package co.ghola.smogalert.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import co.ghola.smogalert.R;
import co.ghola.smogalert.db.DBContract;
import co.ghola.smogalert.utils.BaseTask;
import co.ghola.smogalert.utils.Constants;

/**
 * Created by alecksjohansson on 7/21/16.
 */
public class OneHourFragmentDetail extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private AsyncTask task = null;
    private String mSummaryText;
    private String mAQI;
    private TextView mTextView;
    private Typeface mTypeFace;

    // newInstance constructor for creating fragment with arguments
    public static OneHourFragmentDetail newInstance(int page, String title) {
        OneHourFragmentDetail mSummary2Fragment = new OneHourFragmentDetail();
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
        title = getArguments().getString("OneHourFragment");
        EventBus.getDefault().register(this);
        mTypeFace  = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(String text) {
        if (task == null)
            task = new LoadCursorTask(getContext()).execute(new Integer(Constants.LAST_HOUR));
    }

    private class LoadCursorTask extends BaseTask<Integer> {
        LoadCursorTask(Context ctxt) {
            super(ctxt);
        }

        @Override
        public void onPostExecute(Cursor result) {
            if (result.getCount() > 0) {
                result.moveToPosition(0);
                mAQI= result.getString(DBContract.COLUMN_IDX_AQI);
                mSummaryText =returnBlurb(mAQI);
                mTextView.setTypeface(mTypeFace);
                mTextView.setText(mSummaryText);

            }
            task = null;
        }



        @Override
        protected Cursor doInBackground(Integer... params) {
            int post = params[0].intValue();
            return (doQuery(post));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (task == null)
            task = new LoadCursorTask(getActivity()).execute(new Integer(Constants.LAST_HOUR));
    }

    public String returnBlurb(String aqi) {
        if (aqi != null || aqi != "") {
            Integer convertedAqi = Integer.parseInt(aqi);
            if (convertedAqi.intValue() > 151) {
                return getContext().getResources().getString(R.string.unhealthy_blurb);
            } else if (convertedAqi.intValue() > 100) {
                return getContext().getResources().getString(R.string.sensitive_blurb);
            } else if (convertedAqi.intValue() > 51) {
                return getContext().getResources().getString(R.string.moderate_blurb);
            } else {
                return getContext().getResources().getString(R.string.good_blurb);
            }
        }
        return "";
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.one_hour_fragment_detail, container, false);
         mTextView = (TextView) view.findViewById(R.id.tvShareText);
        return view;
    }

}