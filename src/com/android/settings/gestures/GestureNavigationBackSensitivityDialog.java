/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.settings.gestures;

import static android.os.UserHandle.USER_CURRENT;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

import com.aosip.internal.util.NavBarUtils;

/**
 * Dialog to set the back gesture's sensitivity in Gesture navigation mode.
 */
public class GestureNavigationBackSensitivityDialog extends InstrumentedDialogFragment {
    private static final String TAG = "GestureNavigationBackSensitivityDialog";
    private static final String KEY_BACK_SENSITIVITY = "back_sensitivity";

    public static void show(SystemNavigationGestureSettings parent, int sensitivity) {
        if (!parent.isAdded()) {
            return;
        }

        final GestureNavigationBackSensitivityDialog dialog =
                new GestureNavigationBackSensitivityDialog();
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_BACK_SENSITIVITY, sensitivity);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(parent, 0);
        dialog.show(parent.getFragmentManager(), TAG);
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.SETTINGS_GESTURE_NAV_BACK_SENSITIVITY_DLG;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final IOverlayManager overlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_back_gesture_sensitivity, null);
        final SeekBar seekBar = view.findViewById(R.id.back_sensitivity_seekbar);
        seekBar.setProgress(getArguments().getInt(KEY_BACK_SENSITIVITY));
        Switch mNavBarGesturalHideNav = view.findViewById(R.id.nav_bar_gestural_hide_nav_switch);
        boolean mIsGesturalNavBarHidden = NavBarUtils.isGesturalNavBarHidden(getContext(), USER_CURRENT);
        if (SystemNavigationPreferenceController.isEdgeToEdgeEnabled(getContext())) {
            OverlayInfo ovInfo = null;
            try {
                ovInfo = overlayManager.getOverlayInfo(NavBarUtils.NAV_BAR_GESTURAL_HIDE_NAV_OVERLAY, USER_CURRENT);
            } catch (RemoteException e) { }
            mIsGesturalNavBarHidden = mIsGesturalNavBarHidden && ovInfo != null && (ovInfo.state == OverlayInfo.STATE_ENABLED);
        }
        mNavBarGesturalHideNav.setChecked(NavBarUtils.isGesturalNavBarHidden(getContext(), USER_CURRENT));
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.back_sensitivity_dialog_title)
                .setMessage(R.string.back_sensitivity_dialog_message)
                .setView(view)
                .setPositiveButton(R.string.okay, (dialog, which) -> {
                    int sensitivity = seekBar.getProgress();
                    getArguments().putInt(KEY_BACK_SENSITIVITY, sensitivity);
                    SystemNavigationGestureSettings.setBackSensitivity(getActivity(),
                            overlayManager, sensitivity);
                    final boolean mNavBarGesturalHideNavEnabled = mNavBarGesturalHideNav.isChecked();
                    if (SystemNavigationPreferenceController.isEdgeToEdgeEnabled(getContext())) {
                        if (NavBarUtils.setGesturalNavBarHiddenOverlay(overlayManager, USER_CURRENT, mNavBarGesturalHideNavEnabled)) {
                            Settings.System.putIntForUser(getContext().getContentResolver(),
                                    Settings.System.NAV_BAR_GESTURAL_HIDE_NAV, mNavBarGesturalHideNavEnabled ? 1 : 0, USER_CURRENT);
                        }
                    }
                })
                .create();
    }
}
