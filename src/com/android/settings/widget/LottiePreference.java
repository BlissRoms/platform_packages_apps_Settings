/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R;

public class LottiePreference extends Preference {

    private static final String TAG = "LottiePreference";

    private Context mContext;

    private LottieAnimationView mLottieView;
    private int mAnimationId;
    private ImageView mPlayButton;
    boolean mAnimationAvailable;

    public LottiePreference(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public LottiePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setLayoutResource(R.layout.system_animation);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mLottieView = (LottieAnimationView) holder.findViewById(R.id.lottie_view);
        mPlayButton = (ImageView) holder.findViewById(R.id.video_play_button);

        mLottieView.loop(true);

        if (mLottieView != null) {
            mAnimationAvailable = true;
        }

    }

    @Override
    public void onDetached() {
        super.onDetached();
    }

    public boolean isAnimationAvailable() {
        return mAnimationAvailable;
    }

    /**
     * Called from {@link LottiePreferenceController} when the view is onResume
     */
    public void onViewVisible() {
        mLottieView.resumeAnimation();
    }

    /**
     * Called from {@link LottiePreferenceController} when the view is onPause
     */
    public void onViewInvisible() {
        mLottieView.pauseAnimation();
    }
}
