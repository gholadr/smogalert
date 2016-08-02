package co.ghola.smogalert.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
 * Created by alecksjohansson on 7/21/16.
 */
public class SummaryFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private ImageView imageView;
    public TextView tvTime;
    public TextView tvAQI;
    private Handler mHandler = new Handler();

    // newInstance constructor for creating fragment with arguments
    public static SummaryFragment newInstance(int page, String title) {
        SummaryFragment mSummaryFragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        mSummaryFragment.setArguments(args);
        return mSummaryFragment;
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
        Glide.with(getContext()).load(R.drawable.bg1).centerCrop().into(imageView);
        super.onViewCreated(view, savedInstanceState);


    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.myimg);
        Glide.with(getActivity()).load(R.drawable.cloud).fitCenter().into(mImageView);
        imageView = (ImageView) view.findViewById(R.id.imageBackground);
        tvAQI = (TextView) view.findViewById(R.id.tvAQI);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        Runnable thisThread = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 15000);
                String shareText = EventBus.getDefault().getStickyEvent(String.class);
                Calendar calendar = Calendar.getInstance();
                String am_pm;
                int hours = calendar.get( Calendar.HOUR );
                if(Integer.parseInt(shareText) > 150)
                {
                    Glide.with(getContext()).load(R.drawable.ninja1).centerCrop().into(imageView);
                }
                if(Integer.parseInt(shareText) < 149)
                {
                    if( calendar.get( Calendar.AM_PM ) == 0 ){
                        am_pm = "AM";
                        if(hours >= 6 && am_pm.equals("AM")) {
                            Glide.with(getContext()).load(R.drawable.bg1).centerCrop().into(imageView);
                        }
                        else
                        {
                            Glide.with(getContext()).load(R.drawable.sandiegonight).centerCrop().into(imageView);
                        }

                    }
                    else{
                        am_pm = "PM";
                        if(hours >= 6 && am_pm.equals("PM")) {
                            Glide.with(getContext()).load(R.drawable.sandiegonight).centerCrop().into(imageView);
                            System.out.println("welcome");
                        }
                        else
                        {
                            Glide.with(getContext()).load(R.drawable.bg1).centerCrop().into(imageView);
                        }

                    }
                }

                tvAQI.setText(shareText + " AQI");
                String timeText = null;
                SharedPreferences pref = getActivity().getPreferences(0);
                String text = pref.getString("dateText",timeText);
                tvTime.setText(text);
            }

        };
        thisThread.run();
        return view;
    }

}