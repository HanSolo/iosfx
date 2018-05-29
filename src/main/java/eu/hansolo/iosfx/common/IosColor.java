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

package eu.hansolo.iosfx.common;

import javafx.scene.paint.Color;


public enum IosColor {
    RED(Color.rgb(255, 59, 48), "RED"),
    ORANGE(Color.rgb(255, 149, 0), "ORANGE"),
    YELLOW(Color.rgb(255, 204, 0), "YELLOW"),
    GREEN(Color.rgb(76, 217, 100), "GREEN"),
    TEAL_BLUE(Color.rgb(90, 200, 250), "TEAL-BLUE"),
    BLUE(Color.rgb(0, 122, 255), "BLUE"),
    PURPLE(Color.rgb(88, 86, 214), "PURPLE"),
    PINK(Color.rgb(255, 45, 85), "PINK"),
    WHITE(Color.rgb(255, 255, 255), "WHITE"),
    CUSTOM_GRAY(Color.rgb(229, 239, 244), "CUSTOM-GRAY"),
    LIGHT_GRAY(Color.rgb(229 ,229, 234), "LIGHT-GRAY"),
    LIGHT_GRAY2(Color.rgb(209, 209, 214), "LIGHT-GRAY2"),
    MID_GRAY(Color.rgb(199, 199, 204), "MID-GRAY"),
    GRAY(Color.rgb(142, 142, 147), "GRAY"),
    DARK_GRAY(Color.rgb(102, 102, 102), "DARK-GRAY"),
    BLACK(Color.rgb(0, 0, 0), "BLACK");


    private final Color  COLOR;
    private final String STYLE_CLASS;


    IosColor(final Color COLOR, final String STYLE_CLASS) {
        this.COLOR       = COLOR;
        this.STYLE_CLASS = STYLE_CLASS;
    }


    public Color color() { return COLOR; }

    public String styleClass() { return STYLE_CLASS; }
}
