package co.ghola.smogalert;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.tz.UTCProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import co.ghola.backend.aqi.model.AirQualitySample;

/**
 * Created by gholadr on 4/2/16.
 */
public class AQIListItemAdapter extends RecyclerView.Adapter<AQIListItemAdapter.ViewHolder> {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private Calendar calendar = Calendar.getInstance();

    public ArrayList<AirQualitySample> mAQIListItems = new ArrayList<AirQualitySample>();

    public AQIListItemAdapter(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
    }

    @Override
    public AQIListItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.aqi_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AQIListItemAdapter.ViewHolder viewHolder, int i) {
        AirQualitySample aqiListItem = mAQIListItems.get(i);

        DateTime time=new DateTime((aqiListItem.getTimestamp()*1000), DateTimeZone.UTC);

        viewHolder.setText(aqiListItem.getAqi() + " : " + aqiListItem.getMessage() + " : " + time.toString("MMM d  h aa"));
    }

    @Override
    public int getItemCount() {
        return mAQIListItems.size();
    }

    public void addItems(List list){

        this.mAQIListItems.addAll(list);
        notifyItemRangeChanged(0,list.size());
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }

        public void setText(String text) {
            this.text.setText(text);
        }
    }
}
