/*
 * Copyright (C) 2024 PenguinOS
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
 * limitations under the License.
 */

package com.bliss.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;

public class ShrinkablePreference extends Preference {

    public ShrinkablePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.bliss_tp_view);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        View itemView = holder.itemView;
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ScaleAnimation shrinkAnimation = new ScaleAnimation(
                            1f, 0.95f,
                            1f, 0.95f,
                            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                            ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                        shrinkAnimation.setFillAfter(true);
                        shrinkAnimation.setDuration(100);
                        v.startAnimation(shrinkAnimation);
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        ScaleAnimation expandAnimation = new ScaleAnimation(
                            0.95f, 1.05f,
                            0.95f, 1.05f,
                            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                            ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                        expandAnimation.setDuration(100);
                        expandAnimation.setFillAfter(false);

                        ScaleAnimation originalSizeAnimation = new ScaleAnimation(
                            1.05f, 1f,
                            1.05f, 1f,
                            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                            ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                        originalSizeAnimation.setDuration(100);
                        originalSizeAnimation.setFillAfter(true);

                        expandAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                v.startAnimation(originalSizeAnimation);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        v.startAnimation(expandAnimation);

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Intent intent = new Intent("android.settings.SYSTEM_UPDATE_SETTINGS");
                            getContext().startActivity(intent);
                        }
                        break;
                }
                return false;
            }
        });
    }
}

