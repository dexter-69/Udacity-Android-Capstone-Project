package balraj.se.newsflash.Ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import balraj.se.newsflash.R;

/**
 * Created by balra on 19-03-2018.
 */

public class HeadlinesFragment extends Fragment {

    public HeadlinesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.headlines_fragment, container, false);
        TextView textView = view.findViewById(R.id.sample_tv);
        if(textView != null) {
            textView.setText("Headlines Test");
        }

        return view;
    }
}
