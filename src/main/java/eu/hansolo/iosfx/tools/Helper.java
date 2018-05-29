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

package eu.hansolo.iosfx.tools;

import javafx.scene.Node;

import java.util.HashMap;


public class Helper {
    public static final int ANIMATION_DURATION = 75;

    public static final void enableNode(final Node NODE, final boolean ENABLE) {
        NODE.setVisible(ENABLE);
        NODE.setManaged(ENABLE);
    }

    public static final <T, U> HashMap copy(final HashMap<T, U> ORIGINAL) {
        HashMap<T, U> COPY = new HashMap<>();
        ORIGINAL.entrySet().forEach(entry -> COPY.put(entry.getKey(), entry.getValue()));
        return COPY;
    }

    public static final double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }
}
