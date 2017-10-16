package io.github.baijifeilong.antea;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by BaiJiFeiLong@gmail.com on 2017/10/16 14:10
 */

public class HelpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        TextView appDescription = view.findViewById(R.id.app_description);
        appDescription.setText(Html.fromHtml(getString(R.string.app_description)));
        appDescription.setMovementMethod(new LinkMovementMethod());
        return view;
    }
}
