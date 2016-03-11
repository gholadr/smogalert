package co.ghola.backend.service;


        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;

        import co.ghola.backend.entity.AirQualitySample;

public class AirQualitySampleService {

    public static List<AirQualitySample> AirQualitySamples = new ArrayList<AirQualitySample>();

    public AirQualitySample addAirQualitySample(Integer id, String aqi, String message, Date date) throws Exception {
        //Check for already exists
        int index = AirQualitySamples.indexOf(new AirQualitySample(id));
        if (index != -1) throw new Exception("AirQualitySample Record already exists");
        AirQualitySample q = new AirQualitySample(id, aqi, message, date);
        AirQualitySamples.add(q);
        return q;
    }

    public AirQualitySample updateAirQualitySample(AirQualitySample q) throws Exception {
        int index = AirQualitySamples.indexOf(q);
        if (index == -1) throw new Exception("AirQualitySample Record does not exist");
        AirQualitySample currentAirQualitySample = AirQualitySamples.get(index);
        currentAirQualitySample.setAqi(q.getAqi());
        currentAirQualitySample.setMessage(q.getMessage());
        currentAirQualitySample.setDate(q.getDate());
        return q;
    }

    public void removeAirQualitySample(Integer id) throws Exception {
        int index = AirQualitySamples.indexOf(new AirQualitySample(id));
        if (index == -1)
            throw new Exception("AirQualitySample Record does not exist");
        AirQualitySamples.remove(index);
    }

    public List<AirQualitySample> getAirQualitySamples() {
        return AirQualitySamples;
    }

    public List<AirQualitySample> getAirQualitySamplesByDate(Date date) {
        List<AirQualitySample> results = new ArrayList<AirQualitySample>();
        for (AirQualitySample AirQualitySample : AirQualitySamples) {
            if (AirQualitySample.getDate().equals(date)) {
                results.add(AirQualitySample);
            }
        }
        return results;
    }

    public AirQualitySample getAirQualitySample(Integer id) throws Exception {
        int index = AirQualitySamples.indexOf(new AirQualitySample(id));
        if (index == -1)
            throw new Exception("AirQualitySample Record does not exist");
        return AirQualitySamples.get(index);
    }

}

