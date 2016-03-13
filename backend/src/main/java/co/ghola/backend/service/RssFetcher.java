package co.ghola.backend.service;

/**
 * Created by macbook on 3/12/16.
 */
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.ghola.backend.entity.AirQualitySample;

// [START example]
@SuppressWarnings("serial")
public class RssFetcher extends HttpServlet {

    private static AirQualitySampleWrapper api =   AirQualitySampleWrapper.getInstance();

    private static List<AirQualitySample> AirQualitySamplesInStorage = new ArrayList<AirQualitySample>();

    private static List<AirQualitySample> AirQualitySamples = new ArrayList<AirQualitySample>();

    private final static String RSS_URL ="http://www.stateair.net/dos/RSS/HoChiMinhCity/HoChiMinhCity-PM2.5.xml";

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private static final Logger log = Logger.getLogger(RssFetcher.class.getName());
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter out = resp.getWriter();

        BufferedReader reader = null;

        //format.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));

        try {
            URL url = new URL(RSS_URL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            throw new MalformedURLException();
        }

        // Reading the feed
        SyndFeedInput input = new SyndFeedInput();
        try {
            SyndFeed feed = input.build(reader);
            List entries = feed.getEntries();
            Iterator itEntries = entries.iterator();

            while (itEntries.hasNext()) {
                SyndEntry entry = (SyndEntry) itEntries.next();
                AirQualitySample sample = createSampleFromRss(entry.getDescription().getValue());
                if (sample != null){
                    AirQualitySamples.add(sample);
                }
            }
        } catch (FeedException e) {
            throw new IOException("Parsing issue, likely date related", e.getCause());
        }

        //Removing duplicates, if any

        List<AirQualitySample> AirQualitySamplesWithoutDuplicates = removeDuplicates(AirQualitySamples);

        log.info(String.valueOf(AirQualitySamplesWithoutDuplicates.size()));

        //Persisting samples in Datastore

        AirQualitySamplesInStorage = api.getAirQualitySamples(null, 24); //retrieve last 24 hrs only

        Iterator<AirQualitySample> crunchifyIterator = AirQualitySamples.iterator();

        while (crunchifyIterator.hasNext()) {
            persistAirQualitySample(crunchifyIterator.next());
        }

    }

    private List<AirQualitySample> removeDuplicates(List<AirQualitySample> listWithDuplicates) {
    /* Set of all attributes seen so far */
        Set<Date> attributes = new HashSet<Date>();
    /* All confirmed duplicates go in here */
        List<AirQualitySample> duplicates = new ArrayList<AirQualitySample>();

        for(AirQualitySample sample : listWithDuplicates) {

            if(attributes.contains(sample.getDate())) {

                duplicates.add(sample);
            }

            attributes.add(sample.getDate());
        }
    /* Clean list without any dups */

        listWithDuplicates.removeAll(duplicates);

        return listWithDuplicates;
    }

    private static AirQualitySample createSampleFromRss(String rssStr){
        String[] arr = rssStr.split(";");
        AirQualitySample sample = null;
        try {
             sample = new AirQualitySample(arr[3], arr[4], format.parse(arr[0]));
        }
        catch(ParseException e){
            log.severe(e.getMessage());
        }

        return sample;
    }

    public static void persistAirQualitySample(AirQualitySample sample)  {

        boolean isPresent = false;

        Iterator<AirQualitySample> crunchifyIterator = AirQualitySamplesInStorage.iterator();

        while (crunchifyIterator.hasNext()) {
            Date StoredDate = crunchifyIterator.next().getDate();
            if(crunchifyIterator.next().getDate().compareTo(sample.getDate()) == 0){

                isPresent = true;

                break;
            }
        }

        if(!isPresent) {

            api.addAirQualitySample(sample);

        }
    }

}