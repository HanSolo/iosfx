/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.iosfx.ioslistview;

import eu.hansolo.iosfx.events.IosEvent;
import eu.hansolo.iosfx.events.IosEventListener;
import eu.hansolo.iosfx.iosentry.IosEntry;
import eu.hansolo.iosfx.tools.Helper;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.util.Duration;


public class IosListView extends ListView<IosEntry> implements IosEventListener {
    private Timeline timeline;

    public IosListView() {
        this(FXCollections.observableArrayList());
    }
    public IosListView(final ObservableList<IosEntry> ENTRIES) {
        super(ENTRIES);
        timeline = new Timeline();
        getStylesheets().add(IosListView.class.getResource("ios-listview.css").toExternalForm());
        getStyleClass().add("ios-list-view");

        registerListeners();
    }


    private void registerListeners() {
        getItems().forEach(entry -> entry.addOnIosEvent(IosListView.this));

        getItems().addListener((ListChangeListener<IosEntry>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(addedItem -> addedItem.addOnIosEvent(IosListView.this));
                } else if (change.wasRemoved()) {
                    change.getRemoved().forEach(removedItem -> removedItem.removeOnIosEvent(IosListView.this));
                }
            }
        });
    }

    @Override public void onIosEvent(final IosEvent EVT) {
        switch(EVT.TYPE) {
            case DELETE_ENTRY:
                IosEntry entry = (IosEntry) EVT.SRC;
                KeyValue kvEntryHeightStart = new KeyValue(entry.prefHeightProperty(), entry.getPrefHeight(), Interpolator.EASE_BOTH);
                KeyValue kvEntryHeightEnd   = new KeyValue(entry.prefHeightProperty(), 0, Interpolator.EASE_BOTH);

                KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvEntryHeightStart);
                KeyFrame kf1 = new KeyFrame(Duration.millis(2 * Helper.ANIMATION_DURATION), kvEntryHeightEnd);

                timeline.getKeyFrames().setAll(kf0, kf1);
                timeline.setOnFinished(e -> getItems().remove(entry));
                timeline.play();
                break;
        }
    }
}
