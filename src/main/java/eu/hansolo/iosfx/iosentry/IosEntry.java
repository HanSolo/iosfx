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

package eu.hansolo.iosfx.iosentry;

import eu.hansolo.iosfx.events.IosEvent;
import eu.hansolo.iosfx.events.IosEventListener;
import eu.hansolo.iosfx.events.IosEventType;
import eu.hansolo.iosfx.fonts.Fonts;
import eu.hansolo.iosfx.tools.Helper;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * User: hansolo
 * Date: 25.05.18
 * Time: 06:14
 */
@DefaultProperty("children")
public class IosEntry extends Region {
    private static final double   PREFERRED_WIDTH  = 375;
    private static final double   PREFERRED_HEIGHT = 44;
    private static final double   MINIMUM_WIDTH    = 100;
    private static final double   MINIMUM_HEIGHT   = 10;
    private static final double   MAXIMUM_WIDTH    = 1024;
    private static final double   MAXIMUM_HEIGHT   = 1024;
    private static final double   BUTTON_WIDTH     = 82;
    private        final IosEvent DELETE_ENTRY_EVT = new IosEvent(IosEntry.this, IosEventType.DELETE_ENTRY);
    private              double   size;
    private              double   width;
    private              double   height;
    private              Label    titleLabel;
    private              Label    subtitleLabel;

    private              HBox                     pane;

    private              Node                     leftNode;
    private              String                   _title;
    private              StringProperty           title;
    private              String                   _subtitle;
    private              StringProperty           subtitle;
    private              VBox                     textBox;
    private              Node                     rightNode;
    private              Label                    action;
    private              Label                    delete;

    private              boolean                  _hasDelete;
    private              BooleanProperty          hasDelete;

    private              boolean                  _hasAction;
    private              BooleanProperty          hasAction;
    
    private              Timeline                 timeline;
    private              EventHandler<MouseEvent> draggedHandler;
    private              double                   draggedStartX;

    private              List<IosEventListener> listeners;


    // ******************** Constructors **************************************
    public IosEntry() {
        this(null, "", "", null);
    }
    public IosEntry(final Node LEFT_NODE, final String TITLE, final String SUB_TITLE, final Node RIGHT_NODE) {
        getStylesheets().add(IosEntry.class.getResource("ios-entry.css").toExternalForm());

        leftNode       = LEFT_NODE;
        _title         = TITLE;
        _subtitle      = SUB_TITLE;
        rightNode      = RIGHT_NODE;
        _hasDelete     = true;
        _hasAction     = true;
        timeline       = new Timeline();
        draggedHandler = e -> {
            final EventType<? extends MouseEvent> TYPE = e.getEventType();
            double  translateX = getTranslateX();
            boolean hasAction  = getHasAction();
            boolean hasDelete  = getHasDelete();
            double  x          = e.getSceneX();

            if (TYPE.equals(MouseEvent.MOUSE_PRESSED)) {
                draggedStartX = x;
            } else if (TYPE.equals(MouseEvent.MOUSE_DRAGGED)) {
                double delta = (draggedStartX - x) * -1;
                if (Double.compare(translateX, 0) == 0 && delta > 0 ||
                    Double.compare(x, draggedStartX) == 0 ||
                    (hasAction && hasDelete && Double.compare(translateX, -2 * BUTTON_WIDTH) == 0 && delta < 0) ||
                    ((hasAction && !hasDelete) && Double.compare(translateX, -BUTTON_WIDTH) == 0 && delta < 0) ||
                    ((!hasAction && hasDelete) && Double.compare(translateX, -BUTTON_WIDTH) == 0 && delta < 0)) {
                    return;
                }

                if (getHasAction() && getHasDelete()) {
                    if (delta > 0) {
                        setTranslateX(-2 * BUTTON_WIDTH + Helper.clamp(0, 2 * BUTTON_WIDTH, delta));
                        action.setTranslateX(Helper.clamp(0, BUTTON_WIDTH, delta * 0.75));
                    } else {
                        setTranslateX(Helper.clamp(-2 * BUTTON_WIDTH, 0, delta));
                        action.setTranslateX(Helper.clamp(0, BUTTON_WIDTH, BUTTON_WIDTH + delta * 0.75));
                    }
                } else if (hasAction || hasDelete) {
                    if (delta > 0) {
                        setTranslateX(-BUTTON_WIDTH + Helper.clamp(0, BUTTON_WIDTH, delta));
                    } else {
                        setTranslateX(Helper.clamp(-BUTTON_WIDTH, 0, delta));
                    }
                }
            } else if (TYPE.equals(MouseEvent.MOUSE_RELEASED)) {
                if (Double.compare(x, draggedStartX) == 0 ||
                    hasAction && hasDelete && Double.compare(translateX, -2 * BUTTON_WIDTH) == 0 ||
                    (hasAction || hasDelete) && Double.compare(translateX, -BUTTON_WIDTH) == 0) {
                    return;
                }
                animate();
            }
        };
        listeners      = new CopyOnWriteArrayList<>();

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("ios-entry");
        
        titleLabel = new Label(getTitle());
        titleLabel.getStyleClass().add("title");
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        if (null == getTitle() || getTitle().isEmpty()) { Helper.enableNode(titleLabel, false); }

        subtitleLabel = new Label(getSubtitle());
        subtitleLabel.getStyleClass().add("subtitle");
        subtitleLabel.setAlignment(Pos.CENTER_LEFT);
        subtitleLabel.setMaxWidth(Double.MAX_VALUE);

        if (null == getSubtitle() || getSubtitle().isEmpty()) { Helper.enableNode(subtitleLabel, false); }

        textBox = new VBox(2, titleLabel, subtitleLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        action = new Label("Action");
        action.getStyleClass().add("action");
        action.setFont(Fonts.robotoRegular(18));
        action.setManaged(false);
        action.setVisible(false);

        delete = new Label("Delete");
        delete.getStyleClass().add("delete");
        delete.setFont(Fonts.robotoRegular(18));
        delete.setManaged(false);
        delete.setVisible(false);

        pane = new HBox(15);

        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        HBox.setHgrow(subtitleLabel, Priority.ALWAYS);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        HBox.setHgrow(action, Priority.NEVER);
        HBox.setHgrow(delete, Priority.NEVER);
        HBox.setMargin(action, Insets.EMPTY);
        HBox.setMargin(delete, Insets.EMPTY);

        if (null != getLeftNode())  {
            pane.getChildren().add(getLeftNode());
            HBox.setMargin(getLeftNode(), new Insets(0, 0, 0, 15));
        }
        if (null != textBox)   { pane.getChildren().add(textBox); }
        if (null != getRightNode()) { pane.getChildren().add(getRightNode()); }
        pane.getChildren().addAll(action, delete);
        pane.setAlignment(Pos.CENTER);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        addEventHandler(MouseEvent.MOUSE_PRESSED, draggedHandler);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
        addEventHandler(MouseEvent.MOUSE_RELEASED, draggedHandler);
        delete.setOnMousePressed(e -> fireIosEvent(DELETE_ENTRY_EVT));
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("".equals(PROPERTY)) {

        }
    }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Node getLeftNode() { return leftNode; }
    public void setLeftNode(final Node NODE) { leftNode = NODE; }

    public String getTitle() { return null == title ? _title : title.get(); }
    public void setTitle(final String TITLE) {
        if (null == title) {
            _title = TITLE;
            titleLabel.setText(_title);
        } else {
            title.set(TITLE);
        }
    }
    public StringProperty titleProperty() {
        if (null == title) {
            title = new StringPropertyBase(_title) {
                @Override protected void invalidated() { titleLabel.setText(get());}
                @Override public Object getBean() { return IosEntry.this; }
                @Override public String getName() { return "title"; }
            };
            _title = null;
        }
        return title;
    }

    public String getSubtitle() { return null == subtitle ? _subtitle : subtitle.get(); }
    public void setSubtitle(final String SUB_TITLE) {
        if (null == subtitle) {
            _subtitle = SUB_TITLE;
            subtitleLabel.setText(_subtitle);
        } else {
            subtitle.set(SUB_TITLE);
        }
    }
    public StringProperty subtitleProperty() {
        if (null == subtitle) {
            subtitle = new StringPropertyBase(_subtitle) {
                @Override protected void invalidated() { subtitleLabel.setText(get()); }
                @Override public Object getBean() { return IosEntry.this; }
                @Override public String getName() { return "subtitle"; }
            };
            _subtitle = null;
        }
        return subtitle;
    }

    public Node getRightNode() { return rightNode; }
    public void setRightNode(final Node NODE) { rightNode = NODE; }

    public boolean getHasDelete() { return null == hasDelete ? _hasDelete : hasDelete.get(); }
    public void setHasDelete(final boolean HAS_DELETE) {
        if (null == hasDelete) {
            _hasDelete = HAS_DELETE;
            delete.setManaged(HAS_DELETE);
            delete.setVisible(HAS_DELETE);
            adjustMargins();
        } else {
            hasDelete.set(HAS_DELETE);
        }
    }
    public BooleanProperty hasDeleteProperty() {
        if (null == hasDelete) {
            hasDelete = new BooleanPropertyBase(_hasDelete) {
                @Override protected void invalidated() {
                    delete.setManaged(get());
                    delete.setVisible(get());
                    adjustMargins();
                }
                @Override public Object getBean() { return IosEntry.this; }
                @Override public String getName() { return "hasDelete"; }
            };
        }
        return hasDelete;
    }

    public boolean getHasAction() { return null == hasAction ? _hasAction : hasAction.get(); }
    public void setHasAction(final boolean HAS_ACTION) {
        if (null == hasAction) {
            _hasAction = HAS_ACTION;
            action.setManaged(HAS_ACTION);
            action.setVisible(HAS_ACTION);
            adjustMargins();
        } else {
            hasAction.set(HAS_ACTION);
        }
    }
    public BooleanProperty hasActionProperty() {
        if (null == hasAction) {
            hasAction = new BooleanPropertyBase(_hasAction) {
                @Override protected void invalidated() {
                    action.setManaged(get());
                    action.setVisible(get());
                    adjustMargins();
                }
                @Override public Object getBean() { return IosEntry.this; }
                @Override public String getName() { return "hasAction"; }
            };
        }
        return hasAction;
    }

    public void setActionLabel(final String TEXT) { action.setText(TEXT); }

    public void addOnActionPressed(final EventHandler<MouseEvent> HANDLER) { action.addEventHandler(MouseEvent.MOUSE_PRESSED, HANDLER); }
    public void removeOnActionPressed(final EventHandler<MouseEvent> HANDLER) { action.removeEventHandler(MouseEvent.MOUSE_PRESSED, HANDLER);}

    private void adjustMargins() {
        action.setTranslateX(0);
        if (getHasAction() && getHasDelete()) {
            HBox.setMargin(action, new Insets(0, -15, 0, 0));
            action.setTranslateX(BUTTON_WIDTH);
        } else if (getHasAction() && !getHasDelete()) {
            HBox.setMargin(action, new Insets(0, 0, 0, 0));
        } else if (!getHasAction() && !getHasDelete() && getRightNode() != null) {
            HBox.setMargin(getRightNode(), new Insets(0, 15, 0, 0));
        } else if (null == getLeftNode()) {
            HBox.setMargin(textBox, new Insets(0, 0, 0, 15));
        }
    }

    private void animate() {
        double translateX = getTranslateX();
        if (Double.compare(translateX, 0) == 0) { return; }
        if (getHasAction() && getHasDelete()) {
            if (Double.compare(translateX, -2 * BUTTON_WIDTH) == 0) { return; }
            if (translateX < -BUTTON_WIDTH) {
                animateToShowButtons(true);
            } else {
                animateToHideButtons();
            }
        } else if (getHasAction() || getHasDelete()) {
            if (Double.compare(translateX, -BUTTON_WIDTH) == 0) { return; }
            if (translateX < -BUTTON_WIDTH * 0.5) {
                animateToShowButtons(false);
            } else {
                animateToHideButtons();
            }
        }
    }

    private void animateToShowButtons(final boolean TWO_BUTTONS) {
        KeyValue kvTranslateXStart       = new KeyValue(translateXProperty(), getTranslateX(), Interpolator.EASE_BOTH);
        KeyValue kvTranslateXEnd         = new KeyValue(translateXProperty(), TWO_BUTTONS ? -164 : -82, Interpolator.EASE_BOTH);
        KeyValue kvActionTranslateXStart = new KeyValue(action.translateXProperty(), action.getTranslateX(), Interpolator.EASE_BOTH);
        KeyValue kvActionTranslateXEnd   = new KeyValue(action.translateXProperty(), 0, Interpolator.EASE_BOTH);

        KeyFrame kf0 = TWO_BUTTONS ? new KeyFrame(Duration.ZERO, kvTranslateXStart, kvActionTranslateXStart) : new KeyFrame(Duration.ZERO, kvTranslateXStart);
        KeyFrame kf1 = TWO_BUTTONS ? new KeyFrame(Duration.millis(Helper.ANIMATION_DURATION), kvTranslateXEnd, kvActionTranslateXEnd) : new KeyFrame(Duration.millis(Helper.ANIMATION_DURATION), kvTranslateXEnd);

        timeline.getKeyFrames().setAll(kf0, kf1);
        timeline.play();
    }
    private void animateToHideButtons() {
        KeyValue kvTranslateXStart       = new KeyValue(translateXProperty(), getTranslateX(), Interpolator.EASE_BOTH);
        KeyValue kvTranslateXEnd         = new KeyValue(translateXProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvActionTranslateXStart = new KeyValue(action.translateXProperty(), action.getTranslateX(), Interpolator.EASE_BOTH);
        KeyValue kvActionTranslateXEnd   = new KeyValue(action.translateXProperty(), BUTTON_WIDTH, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvTranslateXStart, kvActionTranslateXStart);
        KeyFrame kf1 = new KeyFrame(Duration.millis(Helper.ANIMATION_DURATION), kvTranslateXEnd, kvActionTranslateXEnd);

        timeline.getKeyFrames().setAll(kf0, kf1);
        timeline.play();
    }


    // ******************** Event Handling ************************************
    public void addOnIosEvent(final IosEventListener LISTENER) { if (!listeners.contains(LISTENER)) { listeners.add(LISTENER); } }
    public void removeOnIosEvent(final IosEventListener LISTENER) { if (listeners.contains(LISTENER)) { listeners.remove(LISTENER); } }

    private void fireIosEvent(final IosEvent EVENT) {
        listeners.forEach(listener -> listener.onIosEvent(EVENT));
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            if (getHasAction() && getHasDelete()) {
                pane.setPrefSize(width + BUTTON_WIDTH + BUTTON_WIDTH, PREFERRED_HEIGHT);
                pane.setMaxSize(width + BUTTON_WIDTH + BUTTON_WIDTH, PREFERRED_HEIGHT);
            } else if (getHasAction() || getHasDelete()) {
                pane.setPrefSize(width + BUTTON_WIDTH, PREFERRED_HEIGHT);
                pane.setMaxSize(width + BUTTON_WIDTH, PREFERRED_HEIGHT);
            } else {
                pane.setPrefSize(width, PREFERRED_HEIGHT);
                pane.setMaxSize(width, PREFERRED_HEIGHT);
            }

            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            action.setPrefSize(BUTTON_WIDTH, PREFERRED_HEIGHT);

            delete.setPrefSize(BUTTON_WIDTH, PREFERRED_HEIGHT);

            redraw();
        }
    }

    private void redraw() {

    }
}
