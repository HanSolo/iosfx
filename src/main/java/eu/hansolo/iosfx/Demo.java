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

package eu.hansolo.iosfx;

import eu.hansolo.iosfx.common.IosColor;
import eu.hansolo.iosfx.iosentry.IosEntry;
import eu.hansolo.iosfx.iosentry.IosEntryCell;
import eu.hansolo.iosfx.ioslistview.IosListView;
import eu.hansolo.iosfx.iosmultibutton.IosMultiButton;
import eu.hansolo.iosfx.iosmultibutton.IosMultiButton.Type;
import eu.hansolo.iosfx.iosmultibutton.IosMultiButtonBuilder;
import eu.hansolo.iosfx.iosswitch.IosSwitch;
import eu.hansolo.iosfx.iosswitch.IosSwitchBuilder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Demo extends Application {
    private ObservableList<IosEntry> entries;
    private IosListView              listView;
    private IosEntry                 entry1;
    private IosEntry                 entry2;
    private IosEntry                 entry3;
    private IosEntry                 entry4;
    private IosEntry                 entry5;
    private IosEntry                 entry6;
    private IosEntry                 entry7;
    private IosEntry                 entry8;


    @Override public void init() {
        entry1 = createIosEntry("Title 1", "Subtitle 1", createMultiButton(Type.SMALL_DOT, IosColor.PURPLE.color(), false), createSwitch(IosColor.PURPLE.color(), false), true, true);
        entry2 = createIosEntry("Title 2", "Subtitle 2", createMultiButton(Type.ADD, IosColor.GREEN.color(), false), createSwitch(IosColor.PINK.color(), true), true, false);
        entry3 = createIosEntry("Title 3", "Subtitle 3", createMultiButton(Type.DELETE, IosColor.RED.color(), false), createSwitch(IosColor.GREEN.color(), false), false, false);
        entry4 = createIosEntry("Title 4", "Subtitle 4", createMultiButton(Type.DOT, IosColor.ORANGE.color(), false), createSwitch(IosColor.ORANGE.color(), true), false, true);
        entry5 = createIosEntry("Title 5", "Subtitle 5", createMultiButton(Type.INFO, IosColor.BLUE.color(), false), createMultiButton(Type.CHECKBOX, IosColor.GREEN.color(), false), false, false);
        entry6 = createIosEntry("Title 6", "Subtitle 6", createMultiButton(Type.PLUS, IosColor.ORANGE.color(), false), createMultiButton(Type.ADD, IosColor.GREEN.color(), false), false, false);
        entry7 = createIosEntry("Title 7", "Subtitle 7", null, createMultiButton(Type.DELETE, IosColor.GREEN.color(), false), false, false);
        entry8 = createIosEntry("Title 8", "Subtitle 8", createMultiButton(Type.DOT, IosColor.GREEN.color(), false), createMultiButton(Type.CHECK_MARK, IosColor.BLUE.color(), true), false, false);

        entry1.setActionLabel("Press");

        entries = FXCollections.observableArrayList();
        entries.addAll(entry1, entry2, entry3, entry4, entry5, entry6, entry7, entry8);

        listView = new IosListView(entries);
        listView.setPrefWidth(375);
        listView.setPlaceholder(new Label("No entries loaded"));
        listView.setCellFactory(p -> new IosEntryCell());

        registerListeners();
    }

    private void registerListeners() {
        entry1.addOnActionPressed(e -> System.out.println("entry1 pressed"));
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(listView);
        //pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);
        //scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("iOS FX");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private IosEntry createIosEntry(final String TITLE, final String SUB_TITLE, final Node LEFT_NODE, final Node RIGHT_NODE, final boolean HAS_ACTION, final boolean HAS_DELETE) {
        IosEntry entry = new IosEntry(LEFT_NODE, TITLE, SUB_TITLE, RIGHT_NODE);
        entry.setHasAction(HAS_ACTION);
        entry.setHasDelete(HAS_DELETE);
        return entry;
    }

    private IosSwitch createSwitch(final Color SELECTED_COLOR, final boolean SHOW_ON_OFF_TEXT) {
        IosSwitch iosSwitch = IosSwitchBuilder.create()
                                              .minSize(51, 31)
                                              .maxSize(51, 31)
                                              .prefSize(51, 31)
                                              .showOnOffText(SHOW_ON_OFF_TEXT)
                                              .selectedColor(null == SELECTED_COLOR ? IosSwitch.DEFAULT_SELECTED_COLOR : SELECTED_COLOR)
                                              .build();
        return  iosSwitch;
    }

    private IosMultiButton createMultiButton(final Type MULTI_BUTTON_TYPE, final Color SELECTED_COLOR, final boolean SELECTED) {
        IosMultiButton iosMultiButton = IosMultiButtonBuilder.create()
                                                             .type(MULTI_BUTTON_TYPE)
                                                             .selectedColor(SELECTED_COLOR)
                                                             .selected(SELECTED)
                                                             .build();
        return iosMultiButton;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
