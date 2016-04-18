package co.ghola.smogalert;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.ghola.smogalert.db.DBContract;

public class LastDayTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;


    //private CursRecAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tab_fragment_last_day, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        getLoaderManager().initLoader(0, null, this);
        recyclerView.setAdapter(new SampleAdapter());
        return view;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DBContract.AirQualitySample.CONTENT_URI;
        return(new CursorLoader(getContext(),uri, DBContract.PROJECTION, null, null, "ts DESC LIMIT 24"));
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ((SampleAdapter)recyclerView.getAdapter()).setSamples(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        ((SampleAdapter)recyclerView.getAdapter()).setSamples(null);
    }

    class SampleAdapter extends RecyclerView.Adapter<RowController> {

        Cursor samples=null;

        @Override
        public RowController onCreateViewHolder(ViewGroup parent, int viewType) {
            return(new RowController(getLayoutInflater(new Bundle())
                    .inflate(R.layout.aqi_list_item, parent, false)));
        }

        void setSamples(Cursor samples) {
            this.samples=samples;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RowController holder, int position) {
            samples.moveToPosition(position);
            holder.bindModel(samples);
        }

        @Override
        public int getItemCount() {
            if (samples==null) {
                return(0);
            }

            return(samples.getCount());
        }
    }
}

