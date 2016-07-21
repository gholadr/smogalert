package co.ghola.smogalert.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.ghola.smogalert.R;

/**
 * Created by alecksjohansson on 7/21/16.
 */
public class StatisticFragment extends android.support.v4.app.Fragment {
    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static StatisticFragment newInstance(int page, String title) {
        StatisticFragment mStatisticFragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        mStatisticFragment.setArguments(args);
        return mStatisticFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.third_fragment, container, false);
//        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel3);
//        tvLabel.setText(page + " -- " + title);
        return view;
    }
}