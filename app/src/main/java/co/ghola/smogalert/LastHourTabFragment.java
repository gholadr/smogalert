package co.ghola.smogalert;

        import android.app.LoaderManager;
        import android.content.CursorLoader;
        import android.content.Loader;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import co.ghola.smogalert.db.DBContract;


/**
 * Created by gholadr on 4/17/16.
 */
public class LastHourTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private String TAG = getClass().getSimpleName();
    private TextView textView;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_last_hour, container, false);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DBContract.AirQualitySample.CONTENT_URI;
        return(new CursorLoader(getContext(),uri, DBContract.PROJECTION, null, null, "ts DESC LIMIT 24"));
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ((SampleAdapter)textView.getAdapter()).setSamples(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        ((SampleAdapter)mRecyclerView.getAdapter()).setSamples(null);
    }

    class SampleAdapter extends RecyclerView.Adapter<RowController> {

        Cursor samples=null;

        @Override
        public RowController onCreateViewHolder(ViewGroup parent, int viewType) {
            return(new RowController(getLayoutInflater()
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
