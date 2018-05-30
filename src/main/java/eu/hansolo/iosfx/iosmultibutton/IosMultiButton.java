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

package eu.hansolo.iosfx.iosmultibutton;

import eu.hansolo.iosfx.events.IosEvent;
import eu.hansolo.iosfx.events.IosEventListener;
import eu.hansolo.iosfx.events.IosEventType;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * User: hansolo
 * Date: 29.05.18
 * Time: 09:03
 */
@DefaultProperty("children")
public class IosMultiButton extends Region {
    public               enum                                     Type { CHECKBOX, ADD, DELETE, CHECK_MARK, DOT, SMALL_DOT, INFO, PLUS }
    public  static final Color                                    DEFAULT_SELECTED_COLOR  = Color.rgb(0, 122, 255);
    private static final double                                   PREFERRED_WIDTH         = 22;
    private static final double                                   PREFERRED_HEIGHT        = 22;
    private static final double                                   MINIMUM_WIDTH           = 11;
    private static final double                                   MINIMUM_HEIGHT          = 11;
    private static final double                                   MAXIMUM_WIDTH           = 1024;
    private static final double                                   MAXIMUM_HEIGHT          = 1024;
    private static final StyleablePropertyFactory<IosMultiButton> FACTORY                 = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private        final IosEvent                                 SELECTED_EVT            = new IosEvent(IosMultiButton.this, IosEventType.SELECTED);
    private        final IosEvent                                 DESELECTED_EVT          = new IosEvent(IosMultiButton.this, IosEventType.DESELECTED);
    private        final IosEvent                                 PRESSED_EVT             = new IosEvent(IosMultiButton.this, IosEventType.PRESSED);
    private        final IosEvent                                 RELEASED_EVT            = new IosEvent(IosMultiButton.this, IosEventType.RELEASED);
    private static final PseudoClass                              CHECKBOX_PSEUDO_CLASS   = PseudoClass.getPseudoClass("checkbox");
    private static final PseudoClass                              ADD_PSEUDO_CLASS        = PseudoClass.getPseudoClass("add");
    private static final PseudoClass                              DELETE_PSEUDO_CLASS     = PseudoClass.getPseudoClass("delete");
    private static final PseudoClass                              CHECK_MARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("checkmark");
    private static final PseudoClass                              DOT_PSEUDO_CLASS        = PseudoClass.getPseudoClass("dot");
    private static final PseudoClass                              SMALL_DOT_PSEUDO_CLASS  = PseudoClass.getPseudoClass("smalldot");
    private static final PseudoClass                              INFO_PSEUDO_CLASS       = PseudoClass.getPseudoClass("info");
    private static final PseudoClass                              PLUS_PSEUDO_CLASS       = PseudoClass.getPseudoClass("plus");
    private static final PseudoClass                              SELECTED_PSEUDO_CLASS   = PseudoClass.getPseudoClass("selected");
    private        final StyleableProperty<Color>                 selectedColor;
    private              double                                   size;
    private              double                                   width;
    private              double                                   height;
    private              Circle                                   circle;
    private              Region                                   icon;
    private              Pane                                     pane;
    private              Type                                     _type;
    private              ObjectProperty<Type>                     type;
    private              boolean                                  _selected;
    private              BooleanProperty                          selected;
    private              List<IosEventListener>                   listeners;
    private              BooleanBinding                           showing;
    private              ChangeListener<Boolean>                  showingListener;
    private              EventHandler<MouseEvent>                 pressedHandler;
    private              EventHandler<MouseEvent>                 releasedHandler;
    private              HashMap<String, Property>                settings;


    // ******************** Constructors **************************************
    public IosMultiButton() {
        this(new HashMap<>());
    }
    public IosMultiButton(final Map<String, Property> SETTINGS) {
        _type           = Type.CHECKBOX;
        _selected       = false;
        selectedColor   = FACTORY.createStyleableColorProperty(IosMultiButton.this, "selectedColor", "-selected-color", s -> s.selectedColor, DEFAULT_SELECTED_COLOR);
        listeners       = new CopyOnWriteArrayList<>();
        showingListener =  (o, ov, nv) -> { if (nv) { applySettings(); } };
        pressedHandler  = e -> {
            fireIosEvent(PRESSED_EVT);
            if (Type.CHECKBOX == getType() && !isDisabled()) { setSelected(!isSelected()); }
        };
        releasedHandler = e -> fireIosEvent(RELEASED_EVT);
        settings        = new HashMap<>(SETTINGS);

        initGraphics();
        registerListeners();
        applySettings();
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

        getStyleClass().add("ios-multi-button");

        circle = new Circle(PREFERRED_HEIGHT * 0.5);
        circle.getStyleClass().add("circle");

        icon = new Region();
        icon.getStyleClass().setAll("icon");
        icon.setMouseTransparent(true);

        pane = new Pane(circle, icon);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
        addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
        selectedColorProperty().addListener(o -> icon.setStyle(String.join("", "-selected-color: ", (getSelectedColor()).toString().replace("0x", "#"))));
        if (null != getScene()) {
            setupBinding();
        } else {
            sceneProperty().addListener((o1, ov1, nv1) -> {
                if (null == nv1) { return; }
                if (null != getScene().getWindow()) {
                    setupBinding();
                } else {
                    sceneProperty().get().windowProperty().addListener((o2, ov2, nv2) -> {
                        if (null == nv2) { return; }
                        setupBinding();
                    });
                }
            });
        }
    }

    private void setupBinding() {
        showing = Bindings.selectBoolean(sceneProperty(), "window", "showing");
        showing.addListener(showingListener);
    }

    private void applySettings() {
        if (settings.isEmpty()) { return; }
        for (String key : settings.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) settings.get(key)).get();
                setPrefSize(dim.getWidth(), dim.getHeight());
            } else if ("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) settings.get(key)).get();
                setMinSize(dim.getWidth(), dim.getHeight());
            } else if ("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) settings.get(key)).get();
                setMaxSize(dim.getWidth(), dim.getHeight());
            } else if ("prefWidth".equals(key)) {
                setPrefWidth(((DoubleProperty) settings.get(key)).get());
            } else if ("prefHeight".equals(key)) {
                setPrefHeight(((DoubleProperty) settings.get(key)).get());
            } else if ("minWidth".equals(key)) {
                setMinWidth(((DoubleProperty) settings.get(key)).get());
            } else if ("minHeight".equals(key)) {
                setMinHeight(((DoubleProperty) settings.get(key)).get());
            } else if ("maxWidth".equals(key)) {
                setMaxWidth(((DoubleProperty) settings.get(key)).get());
            } else if ("maxHeight".equals(key)) {
                setMaxHeight(((DoubleProperty) settings.get(key)).get());
            } else if ("scaleX".equals(key)) {
                setScaleX(((DoubleProperty) settings.get(key)).get());
            } else if ("scaleY".equals(key)) {
                setScaleY(((DoubleProperty) settings.get(key)).get());
            } else if ("layoutX".equals(key)) {
                setLayoutX(((DoubleProperty) settings.get(key)).get());
            } else if ("layoutY".equals(key)) {
                setLayoutY(((DoubleProperty) settings.get(key)).get());
            } else if ("translateX".equals(key)) {
                setTranslateX(((DoubleProperty) settings.get(key)).get());
            } else if ("translateY".equals(key)) {
                setTranslateY(((DoubleProperty) settings.get(key)).get());
            } else if ("padding".equals(key)) {
                setPadding(((ObjectProperty<Insets>) settings.get(key)).get());
            } // Control specific settings
            else if ("selectedColor".equals(key)) {
                setSelectedColor(((ObjectProperty<Color>) settings.get(key)).get());
            } else if("type".equals(key)) {
                setType(((ObjectProperty<Type>) settings.get(key)).get());
            }
        }

        if (settings.containsKey("selected")) { setSelected(((BooleanProperty) settings.get("selected")).get()); }

        settings.clear();
        if (null == showing) { return; }
        showing.removeListener(showingListener);
    }

    public void dispose() {
        removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
        removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
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

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public boolean isSelected() { return null == selected ? _selected : selected.get(); }
    public void setSelected(final boolean SELECTED) {
        if (null == selected) {
            fireIosEvent(SELECTED ? SELECTED_EVT : DESELECTED_EVT);
            _selected = SELECTED;
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, SELECTED);
        } else {
            selected.set(SELECTED);
        }
    }
    public BooleanProperty selectedProperty() {
        if (null == selected) {
            selected = new BooleanPropertyBase(_selected) {
                @Override protected void invalidated() {
                    fireIosEvent(get() ? SELECTED_EVT : DESELECTED_EVT);
                    pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return IosMultiButton.this; }
                @Override public String getName() { return "selected"; }
            };
        }
        return selected;
    }

    public Color getSelectedColor() { return selectedColor.getValue(); }
    public void setSelectedColor(final Color COLOR) { selectedColor.setValue(COLOR); }
    public ObjectProperty<Color> selectedColorProperty() { return (ObjectProperty<Color>) selectedColor; }

    public Type getType() { return null == type ? _type : type.get(); }
    public void setType(final Type TYPE) {
        if (null == type) {
            _type = TYPE;
            adjustStyle();
        } else {
            type.set(TYPE);
        }
    }
    public ObjectProperty<Type> typeProperty() {
        if (null == type) {
            type = new ObjectPropertyBase<Type>(_type) {
                @Override protected void invalidated() { adjustStyle(); }
                @Override public Object getBean() { return IosMultiButton.this; }
                @Override public String getName() { return "type"; }
            };
            _type = null;
        }
        return type;
    }

    protected HashMap<String, Property> getSettings() { return settings; }

    private void adjustStyle() {
        switch(getType()) {
            case ADD       : pseudoClassStateChanged(ADD_PSEUDO_CLASS, true);break;
            case DELETE    : pseudoClassStateChanged(DELETE_PSEUDO_CLASS, true);break;
            case CHECK_MARK: pseudoClassStateChanged(CHECK_MARK_PSEUDO_CLASS, true);break;
            case DOT       : pseudoClassStateChanged(DOT_PSEUDO_CLASS, true);break;
            case SMALL_DOT : pseudoClassStateChanged(SMALL_DOT_PSEUDO_CLASS, true);break;
            case INFO      : pseudoClassStateChanged(INFO_PSEUDO_CLASS, true);break;
            case PLUS      : pseudoClassStateChanged(PLUS_PSEUDO_CLASS, true);break;
            case CHECKBOX  :
            default:
                pseudoClassStateChanged(CHECKBOX_PSEUDO_CLASS, true);
                pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected());
                break;
        }
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
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            circle.setCenterX(size * 0.5);
            circle.setCenterY(size * 0.5);

            icon.setPrefSize(size, size);
        }
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return IosMultiButton.class.getResource("ios-multibutton.css").toExternalForm();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return FACTORY.getCssMetaData(); }
    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() { return FACTORY.getCssMetaData(); }
}
