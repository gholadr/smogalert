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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import co.ghola.smogalert.R;
import co.ghola.smogalert.db.DBContract;
import co.ghola.smogalert.utils.BaseTask;
import co.ghola.smogalert.utils.Constants;

/**
 * Created by alecksjohansson on 8/1/16.
 */
public class StatisticFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private ImageView imageView;
    public TextView tvTime;
    public TextView tvAQI;
    private Typeface mTypeFace;
    private AsyncTask task = null;


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
        EventBus.getDefault().register(this);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        mTypeFace  = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


    }
    public class LoadCursorTask extends BaseTask<Integer> {

        LoadCursorTask(Context ctxt) {
            super(ctxt);
        }

        @Override
        public void onPostExecute(Cursor result) {
            if (result.getCount() > 0) {
                result.moveToPosition(0);
                DateTime d = new DateTime((result.getLong(DBContract.COLUMN_IDX_TS) * 1000), DateTimeZone.UTC);
                String mTimeText = d.toString("hh:mm aaa");
                tvTime.setTypeface(mTypeFace);
                tvTime.setText(mTimeText);

            }
            task = null;
        }



        @Override
        protected Cursor doInBackground(Integer... params) {
            int post = params[0].intValue();
            return (doQuery(post));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(String text) {
        if (task == null)
            task = new LoadCursorTask(getContext()).execute(new Integer(Constants.LAST_7_DAYS));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (task == null)
            task = new LoadCursorTask(getActivity()).execute(new Integer(Constants.LAST_HOUR));
    }
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_layout, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.myimg);
        Glide.with(getActivity()).load(R.drawable.statistic).fitCenter().into(mImageView);
        tvAQI = (TextView) view.findViewById(R.id.tvAQI);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvAQI.setTypeface(mTypeFace);
        tvAQI.setText(getContext().getResources().getString(R.string.swipe));
        return view;
    }

}