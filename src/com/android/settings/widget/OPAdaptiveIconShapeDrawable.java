/*
 * Copyright (C) 2021 ShapeShiftOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.settings.widget;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.util.PathParser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.settings.R;

public class OPAdaptiveIconShapeDrawable extends ShapeDrawable {
    public OPAdaptiveIconShapeDrawable() {
    }

    public OPAdaptiveIconShapeDrawable(Resources resources) {
        init(resources);
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        init(r);
    }

    private void init(Resources resources) {
        Path path = new Path(PathParser.createPathFromPathData(
                resources.getString(com.android.internal.R.string.config_icon_mask)));
        getPaint().setAntiAlias(true);
        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setStrokeWidth(3.0f);
        getPaint().setColor(resources.getColor(R.color.settingsHeaderColor));
        setShape(new PathShape(path, 100.0f, 100.0f));
    }
}
