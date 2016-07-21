package co.ghola.smogalert.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import co.ghola.smogalert.R;

/**
 * Created by alecksjohansson on 7/21/16.
 */
public class SummaryFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;


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

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.myimg);
        Glide.with(this).load(R.drawable.cloud).into(imageView);
        //extView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
        //tvLabel.setText(page + " -- " + title);
        String shareText = EventBus.getDefault().getStickyEvent(String.class);
        Toast.makeText(getActivity(),shareText,Toast.LENGTH_LONG).show();
        return view;
    }
}