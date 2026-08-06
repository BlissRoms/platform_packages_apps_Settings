package com.android.settings.deviceinfo.aboutphone;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemProperties;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settings.R;

public class BlissBannerPreferenceController extends BasePreferenceController {

    public BlissBannerPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        LayoutPreference preference = screen.findPreference(getPreferenceKey());
        if (preference != null) {
            setupBanner(preference);
        }
    }

    private void setupBanner(LayoutPreference preference) {
        ImageView bannerImage = preference.findViewById(R.id.banner_image);
        TextView bannerTitle = preference.findViewById(R.id.banner_title);

        // Make the banner clickable
        preference.findViewById(R.id.banner_image).getRootView().setOnClickListener(v -> {
            new com.android.settings.core.SubSettingLauncher(mContext)
                    .setDestination("com.android.settings.deviceinfo.bliss.BlissVersionSettings")
                    .setSourceMetricsCategory(com.android.internal.logging.nano.MetricsProto.MetricsEvent.DEVICEINFO)
                    .launch();
        });

        // Fetch Wallpaper and apply Blur
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        if (wallpaperDrawable != null && bannerImage != null) {
            bannerImage.setImageDrawable(wallpaperDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bannerImage.setRenderEffect(RenderEffect.createBlurEffect(50f, 50f, Shader.TileMode.CLAMP));
            }
        }

        // Set Dynamic Text
        if (bannerTitle != null) {
            String[] abis = Build.SUPPORTED_ABIS;
            String primaryAbi = abis != null && abis.length > 0 ? abis[0] : "";
            String osName = primaryAbi.contains("arm") ? "BlissRoms" : "BlissOS";
            String blissVersion = SystemProperties.get("ro.bliss.version", "");
            
            String displayText = osName;
            if (!blissVersion.isEmpty()) {
                displayText += " " + blissVersion;
            }
            bannerTitle.setText(displayText);
        }
    }
}
