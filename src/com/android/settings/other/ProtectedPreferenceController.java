/*
 * Copyright (C) 2016 The Android Open Source Project
 * Copyright (C) 2023 The Calyx Institute
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

package com.android.settings.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;

import androidx.annotation.IntDef;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.development.OnActivityResultListener;
import com.android.settings.password.ChooseLockSettingsHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A controller that prompts for strong authentication (i.e. PIN entry) on clicks and/or
 * preference changes, such as toggles.
 *
 * For preference change handling, no modification of the Preference is required.
 *
 * For click handling, involved Preferences must override onClick() to do nothing, and they may
 * need to provide a new method (callable by the controller) to call super.onClick() or take some
 * other action. Finally, controllers implementing this class should override onAuthorizedAction()
 * to call the Preference's new method, if any, or to do all of the handling themselves.
 */
public abstract class ProtectedPreferenceController extends OtherOptionsPreferenceController
        implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin,
        OnActivityResultListener {

    @IntDef({ActionType.CLICK, ActionType.PREFERENCE_CHANGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionType {
        int CLICK = 0;
        int PREFERENCE_CHANGE = 1;
    }

    private class Request {
        public @ActionType int action;
        public Object newValue;

        public Request(@ActionType int action, Object newValue) {
            this.action = action;
            this.newValue = newValue;
        }
    }

    // Arbitrary request code start point and range for ChooseLockSettingsHelper,
    // to ensure the chosen request codes are not used elsewhere, without the
    // maintenance burden of hard-coding request codes in every subclass.
    private static final int REQUEST_CODE_START = 1924;
    private static final int MAX_REQUESTS = 42;
    private static int nextRequestCode = REQUEST_CODE_START;
    private synchronized static int getNextRequestCode() {
        final int requestCode = nextRequestCode++;
        if (nextRequestCode > (REQUEST_CODE_START + MAX_REQUESTS)) {
            nextRequestCode = REQUEST_CODE_START;
        }
        return requestCode;
    }

    private ArrayMap<Integer, Request> mPendingRequests = new ArrayMap<>();

    private final Activity mActivity;
    private final DashboardFragment mFragment;

    protected Preference mPreference;
    protected boolean mHandleClicks;

    public ProtectedPreferenceController(Context context, Activity activity,
            DashboardFragment fragment, boolean handleClicks) {
        super(context);

        mActivity = activity;
        mFragment = fragment;
        mHandleClicks = handleClicks;
    }

    public abstract String getPreferenceKey();

    protected abstract void onAuthorizedAction(@ActionType int action, Object newValue);

    // Can be overridden to return false in certain situations, e.g. Developer Options enabled.
    // newValue will be null for ActionType.CLICK.
    protected boolean isAuthorizationNeeded(@ActionType int action, Object newValue) {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        mPreference = screen.findPreference(getPreferenceKey());
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (mHandleClicks && preference.equals(mPreference)) {
            if (!acquireAuthorizationIfNeeded(ActionType.CLICK, null)) {
                // Authorization not needed, so action is implicitly authorized.
                onAuthorizedAction(ActionType.CLICK, null);
            }
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mPreference)) {
            if (acquireAuthorizationIfNeeded(ActionType.PREFERENCE_CHANGE, newValue)) {
                // Authorization needed; do not accept the change yet.
                return false;
            }
            onAuthorizedAction(ActionType.PREFERENCE_CHANGE, newValue);
        }
        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Request request = mPendingRequests.remove(requestCode);
        if (request != null) {
            if (resultCode == Activity.RESULT_OK) {
                onAuthorizedAction(request.action, request.newValue);
            }
            return true;
        }
        return false;
    }

    /** Returns true if authorization is in progress, false if not needed. **/
    private boolean acquireAuthorizationIfNeeded(@ActionType int action, Object newValue) {
        if (isAuthorizationNeeded(action, newValue)) {
            final int requestCode = getNextRequestCode();
            mPendingRequests.put(requestCode, new Request(action, newValue));
            if (showKeyguardConfirmation(requestCode)) {
                return true;
            }
            // No credential set.
            mPendingRequests.remove(requestCode);
        }
        return false;
    }

    /** Obtain keyguard confirmation from the user. Returns false if the user has no credential. */
    private boolean showKeyguardConfirmation(int requestCode) {
        final ChooseLockSettingsHelper.Builder builder =
                new ChooseLockSettingsHelper.Builder(mActivity, mFragment);
        return builder.setRequestCode(requestCode)
                .setTitle(((Preference) mPreference).getTitle())
                .show();
    }
}
