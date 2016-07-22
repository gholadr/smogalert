package co.ghola.smogalert.fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import co.ghola.smogalert.MainActivity;
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
        Animation left = AnimationUtils.loadAnimation(getContext(), R.anim.imageslide);
        left.setStartTime(AnimationUtils.currentAnimationTimeMillis());
        left.setRepeatCount(ValueAnimator.INFINITE);
        left.setRepeatMode(ValueAnimator.REVERSE);
        left.setDuration(3000);
        Glide.with(getContext()).load(R.drawable.background1).centerCrop().into(imageView);
        super.onViewCreated(view, savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.myimg);
        Glide.with(getActivity()).load(R.drawable.cloud).fitCenter().into(mImageView);
        TextView tvAQI = (TextView) view.findViewById(R.id.tvAQI);
        String shareText = EventBus.getDefault().getStickyEvent(String.class);
        tvAQI.setText(shareText);



        return view;
    }
}