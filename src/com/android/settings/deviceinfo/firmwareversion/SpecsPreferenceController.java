/*
 * Copyright (C) 2017 The Android Open Source Project
 * Copyright (C) 2020 The "Best Improved Cherry Picked Rom" Project
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
package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.SystemProperties;
import androidx.annotation.VisibleForTesting;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import android.util.Log;
import android.os.Environment;
import com.android.internal.util.MemInfoReader;
import java.io.File;
import android.os.StatFs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecsPreferenceController extends BasePreferenceController {

    @VisibleForTesting
    static final String PROCESSOR_MODEL = "ro.soc.model";
    static String aprox;
    static String aproxRam;
    public SpecsPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    // Get internal storage
    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        double total = (totalBlocks * blockSize)/ 1073741824;
        int lastval = (int) Math.round(total);
            if (lastval > 0  && lastval <= 16){
                aprox = "16";
            } else if (lastval > 16 && lastval <= 32) {
                aprox = "32";
            } else if (lastval > 32 && lastval <= 64) {
                aprox = "64";
            } else if (lastval > 64 && lastval <= 128) {
                aprox = "128";
            } else if (lastval > 128 && lastval <= 256) {
                aprox = "256";
            } else if (lastval > 256) {
                aprox = "256+";
            } else aprox = "null";
        return aprox;
    }

    // Get total mem
    public static String getTotalRAM() {
            MemInfoReader memReader = new MemInfoReader();
            memReader.readMemInfo();
            String aprox;
            double totalmem = memReader.getTotalSize();
            double gb = (totalmem / 1073741824) + 0.1f; // Cause 4gig devices show memory as 3.48 .-.
            int gigs = (int) Math.round(gb);
            if (gigs > 0  && gigs <= 2) {
                aproxRam = "2";
            } else if (gigs > 2 && gigs <= 3) {
                aproxRam = "3";
            } else if (gigs > 3 && gigs <= 4) {
                aproxRam = "4";
            } else if (gigs > 4 && gigs <= 6) {
                aproxRam = "6";
            } else if (gigs > 6 && gigs <= 8) {
                aproxRam = "8";
            } else if (gigs > 8 && gigs <= 12) {
                aproxRam = "12";
            } else if (gigs > 12) {
                aproxRam = "12+";
            } else aproxRam = "null";
        return aproxRam;
    }

    @Override
    public CharSequence getSummary() {
        String memory = getTotalRAM() + "GB | " + getTotalInternalMemorySize() + "GB";
        String cpu = SystemProperties.get(PROCESSOR_MODEL);
        if (!cpu.isEmpty()) {
            return cpu + " | " + memory;
        } else if (cpu.isEmpty()) {
          return "Unknown | " + memory;
        } else {
          return mContext.getString(R.string.unknown);
       }
    }
}
