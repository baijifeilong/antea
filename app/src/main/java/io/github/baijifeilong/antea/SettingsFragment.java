package io.github.baijifeilong.antea;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by BaiJiFeiLong@gmail.com on 2017/10/16 0:17
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);
    }
}
