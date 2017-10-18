package io.github.baijifeilong.antea;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by BaiJiFeiLong@gmail.com on 2017/10/15 23:23
 */

public class TestFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onResume() {
        try {
            ((Button) getView().findViewById(R.id.test_text_button)).setText(Utils.encryptString(getString(R.string.test_text), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("password", "123")));
            ((TextView) getView().findViewById(R.id.test_encryption_text_view)).setText(String.format(getString(R.string.test_encryption), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("trigger", getString(R.string.the_default_trigger))));
        } catch (Utils.EncryptionException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), String.format(getString(R.string.error_occurred), e.getMessage()), Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }
}
