package co.ghola.backend.service;


        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.time.format.DateTimeParseException;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;
        import java.util.logging.Logger;

        import com.google.api.server.spi.config.Api;
        import com.google.api.server.spi.config.ApiMethod;
        import com.google.api.server.spi.config.Named;
        import com.google.api.server.spi.response.NotFoundException;
        import co.ghola.backend.entity.AirQualitySample;

@Api(name="qirqualitysampleapi",version="v1", description="An API to manage famous AirQualitySamples")
public class AirQualitySampleServiceAPI {

    private static final Logger log = Logger.getLogger(AirQualitySampleServiceAPI.class.getName());
    public static List<AirQualitySample> AirQualitySamples = new ArrayList<AirQualitySample>();

    @ApiMethod(name="add")
    public AirQualitySample addAirQualitySample(@Named("id") Integer id, @Named("aqi") String aqi, @Named("message") String message, @Named("date") String date) throws ParseException,NotFoundException {
        //Check for already exists
        int index = AirQualitySamples.indexOf(new AirQualitySample(id));
        if (index != -1) throw new NotFoundException("AirQualitySample Record already exists");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH);
        Date d = null;
        try {
            d = format.parse(date);
        }
        catch(ParseException e){
            log.severe(e.getMessage());
            throw new ParseException("Error parsing date", e.getErrorOffset());
        }
        log.info(d.toString());
        AirQualitySample q = new AirQualitySample(id, aqi, message, d);
        AirQualitySamples.add(q);
        return q;
    }

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
    }

    @ApiMethod(name="list")
    public List<AirQualitySample> getAirQualitySamples() {
        return AirQualitySamples;
    }

    @ApiMethod(name="listByDate")
    public List<AirQualitySample> getAirQualitySamplesByDate(@Named("date") Date date) {
        List<AirQualitySample> results = new ArrayList<AirQualitySample>();
        for (AirQualitySample AirQualitySample : AirQualitySamples) {
            if (AirQualitySample.getDate().equals(date)) {
                results.add(AirQualitySample);
            }
        }
        return results;
    }

    @ApiMethod(name="getAirQualitySample")
    public AirQualitySample getAirQualitySample(@Named("id") Integer id) throws NotFoundException {
        int index = AirQualitySamples.indexOf(new AirQualitySample(id));
        if (index == -1)
            throw new NotFoundException("AirQualitySample Record does not exist");
        return AirQualitySamples.get(index);
    }

}
