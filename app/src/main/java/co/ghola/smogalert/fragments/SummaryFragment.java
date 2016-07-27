package co.ghola.smogalert.fragments;

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
 * Created by alecksjohansson on 7/21/16.
 */
public class SummaryFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private TextView mAQITextView;
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
        Glide.with(getContext()).load(R.drawable.background_1_test).centerCrop().into(imageView);
        super.onViewCreated(view, savedInstanceState);


    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.myimg);
        Glide.with(getActivity()).load(R.drawable.cloud).fitCenter().into(mImageView);
        tvAQI = (TextView) view.findViewById(R.id.tvAQI);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String shareText = EventBus.getDefault().getStickyEvent(String.class);
                tvAQI.setText(shareText + " AQI");
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                if (minutes < 10) {
                    tvTime.setText("  " + hours + ":" + "0" + minutes);
                } else {
                    tvTime.setText("  " + hours + ":" + minutes);
                }
            }
        });
        return view;
    }

}