package co.ghola.backend.service;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.time.format.DateTimeParseException;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;
        import java.util.TimeZone;
        import java.util.logging.Logger;
        import com.google.api.server.spi.config.Api;
        import com.google.api.server.spi.config.ApiMethod;
        import com.google.api.server.spi.config.Named;
        import com.google.api.server.spi.config.Nullable;
        import com.google.api.server.spi.response.CollectionResponse;
        import com.google.api.server.spi.response.ConflictException;
        import com.google.api.server.spi.response.NotFoundException;
        import com.google.appengine.api.datastore.Cursor;
        import com.google.appengine.api.datastore.QueryResultIterator;
        import com.googlecode.objectify.cmd.Query;
        import static co.ghola.backend.service.OfyService.ofy;
        import co.ghola.backend.entity.AirQualitySample;

@Api(name="aqi",version="v1", description="An API to manage famous AirQualitySamples")
public class AirQualitySampleServiceAPI {

    private static final Logger log = Logger.getLogger(AirQualitySampleServiceAPI.class.getName());
    public static List<AirQualitySample> AirQualitySamples = new ArrayList<AirQualitySample>();


    @ApiMethod(name = "insertAQISample")
    public AirQualitySample insertQuote(@Named("aqi") String aqi, @Named("message") String message, @Named("date") String date) throws ParseException{
        AirQualitySample q  =new AirQualitySample();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        //format.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));

        try {
            q.setDate(format.parse(date));
        }
        catch(ParseException e){
            log.severe(e.getMessage());
            throw new ParseException("Error parsing date", e.getErrorOffset());
        }
        q.setAqi(aqi);
        q.setMessage(message);
//Since our @Id field is a Long, Objectify will generate a unique value for us
//when we use put
        ofy().save().entity(q).now();
        return q;
    }
/*
    @ApiMethod(name="update")
    public AirQualitySample updateAirQualitySample(AirQualitySample q) throws NotFoundException {
        int index = AirQualitySamples.indexOf(q);
        if (index == -1)
            throw new NotFoundException("AirQualitySample Record does not exist");
        AirQualitySample currentAirQualitySample = AirQualitySamples.get(index);
        currentAirQualitySample.setAqi(q.getAqi());
        currentAirQualitySample.setMessage(q.getMessage());
        currentAirQualitySample.setDate(q.getDate());
        return q;
    }

    @ApiMethod(name="remove")
    public void removeAirQualitySample(@Named("id") Integer id) throws NotFoundException {
        int index = AirQualitySamples.indexOf(new AirQualitySample(id));
        if (index == -1)
            throw new NotFoundException("AirQualitySample Record does not exist");
        AirQualitySamples.remove(index);
    }*/

    @ApiMethod(name="listAQISamples")
    public CollectionResponse<AirQualitySample> getAirQualitySamples(@Nullable @Named("cursor") String cursorString,
                                                       @Nullable @Named("count") Integer count) {
        Query<AirQualitySample> query = ofy()
                .load()
                .type(AirQualitySample.class)
                .order("-date")
                .filter("date <", new Date());;

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

//Find the next cursor
        if(count != null) {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }
        return CollectionResponse.<AirQualitySample>builder()
                .setItems(records)
                .setNextPageToken(cursorString)
                .build();
    }


    @ApiMethod(name="getAQISample")
    public AirQualitySample getAirQualitySample(@Named("id") Long id) throws NotFoundException {
        int index = AirQualitySamples.indexOf(new AirQualitySample(id));
        if (index == -1)
            throw new NotFoundException("AirQualitySample Record does not exist");
        return AirQualitySamples.get(index);
    }

    //Private method to retrieve a <code>Quote</code> record
    private AirQualitySample findRecord(Long id) {
        return ofy().load().type(AirQualitySample.class).id(id).now();
//or return ofy().load().type(Quote.class).filter("id",id).first.now();
    }

}
