package co.ghola.smogalert.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import co.ghola.smogalert.R;

/**
 * Created by alecksjohansson on 8/1/16.
 */
public class Statistic2Fragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private TextView mAQITextView;
    private ImageView imageView;
    public TextView tvTime;
    public TextView tvAQI;
    private Handler mHandler = new Handler();

    // newInstance constructor for creating fragment with arguments
    public static Statistic2Fragment newInstance(int page, String title) {
        Statistic2Fragment mStatistic2Fragment = new Statistic2Fragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        mStatistic2Fragment.setArguments(args);
        return mStatistic2Fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        imageView = (ImageView) view.findViewById(R.id.imageBackground);
        super.onViewCreated(view, savedInstanceState);


    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_layout, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.myimg);
        Glide.with(getActivity()).load(R.drawable.statistic_highres).fitCenter().into(mImageView);
        tvAQI = (TextView) view.findViewById(R.id.tvAQI);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                tvAQI.setText("Swipe to view detail about 7 days ");
                SharedPreferences pref = getActivity().getPreferences(0);
                String timeText =null ;
                String text = pref.getString("dateText",timeText);
                tvTime.setText(text);
            }
        });
        return view;
    }

}