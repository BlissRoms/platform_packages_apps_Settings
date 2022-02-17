/*
 * Copyright (C) 2018 The Android Open Source Project
 * Copyright (C) 2022 The Calyx Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.settings.accounts;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.android.settings.core.TogglePreferenceController;
import com.android.settings.Utils;

public class CrossProfileSharingPreferenceController extends TogglePreferenceController {

    private static final String TAG = "CrossProfileSharingPreferenceController";

    private final IPackageManager mPackageManager;
    private final DevicePolicyManager mDevicePolicyManager;

    private UserHandle mManagedUser;
    private UserHandle mParentUser;

    public CrossProfileSharingPreferenceController(Context context, String key) {
        super(context, key);
        // Set default managed profile for the current user, otherwise isAvailable will be false and
        // the setting won't be searchable.
        UserManager userManager = context.getSystemService(UserManager.class);
        mManagedUser = Utils.getManagedProfile(userManager);
        if (userManager != null) {
            mParentUser = userManager.getProfileParent(mManagedUser);
        }
        mPackageManager = AppGlobals.getPackageManager();
        mDevicePolicyManager = context.getSystemService(DevicePolicyManager.class);
    }

    @VisibleForTesting
    void setManagedUser(UserHandle managedUser) {
        mManagedUser = managedUser;
        UserManager userManager = mContext.getSystemService(UserManager.class);
        if (userManager != null) {
            mParentUser = userManager.getProfileParent(mManagedUser);
        }
    }

    @Override
    public int getAvailabilityStatus() {
        if (mManagedUser != null && mParentUser != null) {
            return AVAILABLE;
        }

        return DISABLED_FOR_USER;
    }

    @Override
    public boolean isChecked() {
        if (mManagedUser == null || mParentUser == null) {
            return false;
        }
        for (String action : new String[]{Intent.ACTION_SEND, Intent.ACTION_SEND_MULTIPLE}) {
            Intent intent = new Intent(action).addCategory(Intent.CATEGORY_DEFAULT).setType("*/*");
            try {
                boolean canForwardToParent = mPackageManager.canForwardTo(intent,
                        intent.resolveTypeIfNeeded(mContext.getContentResolver()),
                        mManagedUser.getIdentifier(), mParentUser.getIdentifier());
                boolean canForwardToProfile = mPackageManager.canForwardTo(intent,
                        intent.resolveTypeIfNeeded(mContext.getContentResolver()),
                        mParentUser.getIdentifier(), mManagedUser.getIdentifier());
                if (!canForwardToParent || !canForwardToProfile) {
                    return false;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Could not query intent forwarding", e);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        if (mManagedUser == null || mParentUser == null) {
            return false;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SEND);
        intentFilter.addAction(Intent.ACTION_SEND_MULTIPLE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            intentFilter.addDataType("*/*");
            ComponentName profileOwner = mDevicePolicyManager.getProfileOwnerAsUser(mManagedUser);
            String ownerPackage = null;
            if (profileOwner != null) {
                ownerPackage = profileOwner.getPackageName();
            }
            if (isChecked) {
                mPackageManager.addCrossProfileIntentFilter(intentFilter, ownerPackage,
                        mParentUser.getIdentifier(), mManagedUser.getIdentifier(), 0);
                mPackageManager.addCrossProfileIntentFilter(intentFilter, ownerPackage,
                        mManagedUser.getIdentifier(), mParentUser.getIdentifier(), 0);
            } else {
                mPackageManager.removeCrossProfileIntentFilter(intentFilter, ownerPackage,
                        mParentUser.getIdentifier(), mManagedUser.getIdentifier(), 0);
                mPackageManager.removeCrossProfileIntentFilter(intentFilter, ownerPackage,
                        mManagedUser.getIdentifier(), mParentUser.getIdentifier(), 0);
            }
            return true;
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e(TAG, "Malformed mime type", e);
            return false;
        } catch (NullPointerException e) {
            Log.e(TAG, "System service not available", e);
            return false;
        } catch (RemoteException e) {
            Log.e(TAG, "Could not add/remove cross profile intent filter", e);
            return false;
        }
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return 0; // TODO Actual res
    }
}
