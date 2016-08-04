package co.ghola.smogalert.fragments;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.api.client.json.JsonParser;
import com.google.common.eventbus.Subscribe;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import co.ghola.smogalert.MainActivity;
import co.ghola.smogalert.R;

/**
 * Created by alecksjohansson on 7/21/16.
 */
public class Summary2Fragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private String mSummaryText;

    // newInstance constructor for creating fragment with arguments
    public static Summary2Fragment newInstance(int page, String title) {
        Summary2Fragment mSummary2Fragment = new Summary2Fragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        mSummary2Fragment.setArguments(args);
        return mSummary2Fragment;
    }
    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 1);
        title = getArguments().getString("SummaryFragment");
        //Get Data from Activity
        getData();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.summary_fragment, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.background2);
        Glide.with(getActivity()).load(R.drawable.background_test_2).centerCrop().into(mImageView);
        TextView mTextView = (TextView) view.findViewById(R.id.tvShareText);
        mTextView.setText(mSummaryText);
        Log.d("Text","TEXT"+mSummaryText);
        return view;
    }
    public void getData()
    {
        String strtext= "";
        SharedPreferences pref = getActivity().getPreferences(0);
        mSummaryText= pref.getString("sharekey",strtext);
        Log.d("JSON",mSummaryText);
//        JSONObject jsonObject = convertJSON(mSummaryText);
//        try {
//           mSummaryText = jsonObject.getString("blurb");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
    public JSONObject convertJSON (String input){

        JSONObject ref = new JSONObject();

        String processing = input.trim();
        String[] stored = processing.split("#");
        try {
            ref.put("msr",stored[0].trim());
            ref.put("aqi",stored[1].trim());
            ref.put("blurb",stored[2].trim());
            ref.put("usEmbassyText",stored[3].trim());
            ref.put("dateTimeText",stored[4].trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ref;
    }
}