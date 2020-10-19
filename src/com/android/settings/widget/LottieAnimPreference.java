package com.oneplus.settings.system;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R;
import com.oneplus.settings.utils.OPUtils;

public class LottieAnimPreference extends Preference implements View.OnClickListener {
    private LottieAnimationView mLottieView;
    private ImageView mPlayButton;
    private Context mContext;
    private int resid = R.layout.system_animation;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

    public LottieAnimPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        new Handler() {
            public void handleMessage(Message message) {
                if (message.what == 0) {
                    if (mLottieView.isAnimating()) {
                        LottieAnimPreference.stopAnim();
                    } else {
                        LottieAnimPreference.startAnim();
                    }
                }
            }
        };
        mContext = context;
        setLayoutResource(resid);
    }

    private void startAnim() {
        mPlayButton.setVisibility(View.VISIBLE);
        mLottieView.resumeAnimation();
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        mPlayButton = (ImageView) preferenceViewHolder.findViewById(R.id.video_play_button);
        mLottieView = (LottieAnimationView) preferenceViewHolder.findViewById(R.id.mLottieView);
        mContext.getContentResolver();
        mLottieView.loop(true);
        mPlayButton.setVisibility(View.VISIBLE);
        mLottieView.resumeAnimation();
    }

    public void onClick(View view) {
        mHandler.sendEmptyMessage(0);
    }

    public void stopAnim() {
        LottieAnimationView lottieAnimationView = mLottieView;
        if (lottieAnimationView != null) {
            lottieAnimationView.pauseAnimation();
        }
        ImageView imageView = mPlayButton;
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public LottieAnimPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, 0);
        new Handler() {
            public void handleMessage(Message message) {
                if (message.what == 0) {
                    if (mLottieView.isAnimating()) {
                        LottieAnimPreference.stopAnim();
                    } else {
                        LottieAnimPreference.startAnim();
                    }
                }
            }
        };
        mContext = context;
        setLayoutResource(resid);
    }

    public LottieAnimPreference(Context context) {
        super(context, (AttributeSet) null);
        new Handler() {
            public void handleMessage(Message message) {
                if (message.what == 0) {
                    if (mLottieView.isAnimating()) {
                        LottieAnimPreference.stopAnim();
                    } else {
                        LottieAnimPreference.startAnim();
                    }
                }
            }
        };
        mContext = context;
        setLayoutResource(resid);
    }
}
