package co.ghola.smogalert.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import co.ghola.smogalert.R;
import co.ghola.smogalert.chart.DayAxisValueFormatter;
import co.ghola.smogalert.chart.MyAxisValueFormatter;
import co.ghola.smogalert.chart.XYMarkerView;
import co.ghola.smogalert.db.DBContract;
import co.ghola.smogalert.utils.BaseTask;
import co.ghola.smogalert.utils.Constants;

/**
 * Created by alecksjohansson on 7/21/16.
 */
public class OneDayFragment extends android.support.v4.app.Fragment {
    // Store instance variables
    private String title;
    private int page;
    private LineChart mLineChart;
    private AsyncTask task = null;
    List<Integer> integerList = new ArrayList<>();
    List<Integer> InteArray = new ArrayList<>();
    private Handler mHandler = new Handler();


    // newInstance constructor for creating fragment with arguments
    public static OneDayFragment newInstance(int page, String title) {
        OneDayFragment mOneDayFragment = new OneDayFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        mOneDayFragment.setArguments(args);
        return mOneDayFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        EventBus.getDefault().register(this);

    }
    private void setData(int count,List<Integer> input) {
        float start = 1f;

        mLineChart.getXAxis().setAxisMinValue(start);
        mLineChart.getXAxis().setAxisMaxValue(start + count );

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < start + count ; i++) {
            float val = input.get(i).floatValue();
            yVals1.add(new Entry(i + 1f, Math.round(val)));
        }

        LineDataSet set1;

        if (mLineChart.getData() != null &&
                mLineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            Log.d(OneDayFragment.class.getSimpleName(), yVals1.toString());
            set1.setValues(yVals1);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(yVals1,null);
            set1.setColor(Color.WHITE);
            set1.enableDashedLine(0f, 0f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.WHITE);
            data.setDrawValues(false);
            //data.setValueTypeface(mTfLight);
            //data.s(0.9f);
            mLineChart.animateX(2500);
            mLineChart.setData(data);
        }
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.one_day_fragment_detail, container, false);
        mLineChart = (LineChart) view.findViewById(R.id.chart);
        AxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mLineChart);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(23);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);


        AxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mLineChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(1f); // this replaces setStartAtZero(true)
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGridColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        //rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setGridColor(Color.WHITE);
        rightAxis.setAxisLineColor(Color.WHITE);
        rightAxis.setAxisMinValue(1f);
        rightAxis.setSpaceTop(15f);// this replaces setStartAtZero(true)

        Legend l = mLineChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextColor(Color.WHITE);

        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        //Set LineChart Attribute
        mLineChart.setMarkerView(new XYMarkerView(getContext(), xAxisFormatter));
        mLineChart.setGridBackgroundColor(Color.WHITE);
        mLineChart.setBorderColor(Color.WHITE);
        mLineChart.setDescription(null);
        if(mLineChart.isEmpty())
        {
            mLineChart.animate().scaleXBy(0.65f);
            mLineChart.animate().scaleYBy(0.65f);
            mLineChart.setNoDataTextColor(getResources().getColor(R.color.color_white));
            mLineChart.setNoDataText(getContext().getResources().getString(R.string.no_data));
        }

        mLineChart.setDrawGridBackground(false);
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.animateX(1300);

        return view;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(String text) {
        if (task == null)
            task = new LoadCursorTask(getContext()).execute(new Integer(Constants.LAST_24_HOURS));
    }
    private class LoadCursorTask extends BaseTask<Integer> {
        LoadCursorTask(Context ctxt) {
            super(ctxt);
        }

        @Override
        public void onPostExecute(Cursor result) {
            Boolean scaled = false;
            if (result.getCount() > 0) {
                if(scaled == false)
                {
                    mLineChart.animate().scaleXBy(0f);
                    mLineChart.animate().scaleYBy(0f);
                    scaled = true;
                }
                Log.d("RESULT","COUNT"+result.getCount());
                int size = 24;
                List<Integer> aqis = new ArrayList<>(size);
                if(aqis.size() <= 24)
                {
                    size = result.getCount();
                }
                for (int i = 0; i < size; i++) {
                    result.moveToPosition(i);
                    aqis.add(Integer.parseInt(result.getString(DBContract.COLUMN_IDX_AQI)));

                }
                setData(23,aqis);
//                Observable.from(aqis)
//                        .map(new Func1<String, Integer>() {
//                            @Override
//                            public Integer call(String s) {
//                                return Integer.parseInt(s);
//                            }
//                        })
//                        .buffer(24)
//                        .map(new Func1<List<Integer>, Integer>() {
//                            @Override
//                            public Integer call(List<Integer> integers) {
//                                int sum = 0;
//                                for (int i = 0, size = integers.size(); i < size; i++) {
//                                    sum += integers.get(i);
//                                    Log.d("check",""+integers.get(i));
//                                }
//                                Log.d("check",""+integers.size());
//                                return sum / integers.size();
//                            }
//                        })
//                        .toList()
//                        .subscribe(new Subscriber<List<Integer>>() {
//                            @Override
//                            public void onCompleted() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onNext(List<Integer> integers) {
//                                integerList.addAll(integers);
//                                Log.d("integer",""+integers);
//                                integerList = InteArray;
//                                setData(23,integers);
//
//                                //list average of each 24 elements
//                            }
//                        });
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
            task = new LoadCursorTask(getActivity()).execute(new Integer(Constants.LAST_24_HOURS));
    }
}