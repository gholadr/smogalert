package co.ghola.backend.service;


        import com.google.api.server.spi.config.Named;
        import com.google.api.server.spi.config.Nullable;
        import com.google.appengine.api.datastore.Cursor;
        import com.google.appengine.api.datastore.QueryResultIterator;

        import com.googlecode.objectify.cmd.Query;

        import org.joda.time.DateTime;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;
        import java.util.logging.Logger;

        import co.ghola.backend.entity.AirQualitySample;

        import static co.ghola.backend.service.OfyService.ofy;

public final class AirQualitySampleWrapper {

    private static List<AirQualitySample> AirQualitySamples = new ArrayList<AirQualitySample>();
    private static final Logger log = Logger.getLogger(AirQualitySampleWrapper.class.getName());
    private static AirQualitySampleWrapper instance = new AirQualitySampleWrapper();

    private  AirQualitySampleWrapper(){

    }
    public static AirQualitySampleWrapper getInstance(){

        return instance;
    }

    public static AirQualitySample addAirQualitySample(AirQualitySample q)  {
        ofy().save().entity(q).now();
        return q;
    }

    public static List<AirQualitySample> getAirQualitySamples(@Nullable String cursorString,
                                                       @Nullable Integer count) {
        Query<AirQualitySample> query = ofy().load()
                .type(AirQualitySample.class)
                .order("-date")
                .filter("date <", new DateTime());
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<AirQualitySample> records = new ArrayList<AirQualitySample>();
        QueryResultIterator<AirQualitySample> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }
        return records;
    }


}

