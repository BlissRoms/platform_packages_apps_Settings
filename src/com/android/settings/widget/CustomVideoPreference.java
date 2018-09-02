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

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;

/**
 * A full width preference that hosts a MP4 video.
 */
public class CustomVideoPreference extends Preference {

    private static final String TAG = "CustomVideoPreference";
    private final Context mContext;

    private Uri mVideoPath;
    private MediaPlayer mMediaPlayer;
    private boolean mAnimationAvailable;
    private boolean mVideoReady;
    private boolean mVideoPaused;

    public CustomVideoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                com.android.settings.R.styleable.CustomVideoPreference,
                0, 0);
        try {
            int animation = attributes.getResourceId(R.styleable.CustomVideoPreference_animation, 0);
            mVideoPath = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(context.getPackageName())
                    .appendPath(String.valueOf(animation))
                    .build();
            mMediaPlayer = MediaPlayer.create(mContext, mVideoPath);
            if (mMediaPlayer != null && mMediaPlayer.getDuration() > 0) {
                setVisible(true);
                setLayoutResource(R.layout.custom_video_preference);

                mMediaPlayer.setOnSeekCompleteListener(mp -> mVideoReady = true);

                mMediaPlayer.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
                mAnimationAvailable = true;
            } else {
                setVisible(false);
            }
        } catch (Exception e) {
            Log.w(TAG, "Animation resource not found. Will not show animation.");
        } finally {
            attributes.recycle();
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        if (!mAnimationAvailable) {
            return;
        }

        final TextureView video = (TextureView) holder.findViewById(R.id.custom_video_texture_view);

        video.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width,
                    int height) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setSurface(new Surface(surfaceTexture));
                    mVideoReady = false;
                    mMediaPlayer.seekTo(0);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width,
                    int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                if (mVideoReady) {
                    if (!mVideoPaused && mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                    }
                }
            }
        });
    }

    @Override
    public void onDetached() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        super.onDetached();
    }

    public void onViewVisible(boolean videoPaused) {
        mVideoPaused = videoPaused;
        if (mVideoReady && mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(0);
        }
    }

    public void onViewInvisible() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public boolean isVideoPaused() {
        return mVideoPaused;
    }

}
