package co.ghola.backend.service;

/**
 * Created by macbook on 3/12/16.
 */

import com.google.apphosting.api.ApiProxy;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.ghola.backend.entity.AirQualitySample;
import sun.util.logging.resources.logging;

// [START example]
@SuppressWarnings("serial")
public class RssFetcher extends HttpServlet {

    private static AirQualitySampleWrapper api =   AirQualitySampleWrapper.getInstance();

    private List<AirQualitySample> dataStoreList = new ArrayList<>();

    private List<AirQualitySample> rssList = new ArrayList<>();

    private final static String RSS_URL ="http://www.stateair.net/dos/RSS/HoChiMinhCity/HoChiMinhCity-PM2.5.xml";

   // private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

   // private static DateTimeFormatter format =  DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static Logger log = Logger.getLogger(RssFetcher.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        BufferedReader reader = null;

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
                if (sample != null && Integer.valueOf(sample.getAqi().trim())!= -999){
                    rssList.add(sample);
                }
            }
        } catch (FeedException | ParseException e) {
            throw new IOException("Parsing issue, likely date related", e.getCause());
        }

        //Removing duplicates, if any

        List<AirQualitySample> cleanRssList = removeDuplicates(rssList);

        //Persisting samples in Datastore

        dataStoreList = api.getAirQualitySamples(null, 24); //retrieve last 24 hrs only

        Iterator<AirQualitySample> itr = cleanRssList.iterator();

        while (itr.hasNext()) {
            persistAirQualitySample(itr.next());
        }

    }

    private List<AirQualitySample> removeDuplicates(List<AirQualitySample> listWithDuplicates) {
    /* Set of all attributes seen so far */
        Set<Long> attributes = new HashSet<Long>();
    /* All confirmed duplicates go in here */
        List<AirQualitySample> duplicates = new ArrayList<AirQualitySample>();

        for(AirQualitySample sample : listWithDuplicates) {

            if(attributes.contains(sample.getTimestamp())) {

                duplicates.add(sample);
            }

            attributes.add(sample.getTimestamp());
        }

        /* Clean list without any dups */

        listWithDuplicates.removeAll(duplicates);

        return listWithDuplicates;
    }

    private  AirQualitySample createSampleFromRss(String rssStr) throws ParseException{
        String[] arr = rssStr.split(";");
        AirQualitySample sample = null;

        sample = new AirQualitySample(arr[3].trim(), arr[4].trim(), DateUtils.getInstance().dateString2Long(arr[0]));//.withZone(DateTimeZone.forID("Asia/Bangkok")));

        return sample;
    }

    public void persistAirQualitySample( AirQualitySample rssListItem)  {

        boolean isPresent = false;

        Iterator<AirQualitySample> itr = dataStoreList.iterator();

        while (itr.hasNext()) {
            AirQualitySample dataStoreListItem = (AirQualitySample)itr.next();
            log.info("date in Datastore:" + dataStoreListItem.getTimestamp().toString() + " date in rss sample:" + rssListItem.getTimestamp().toString());

            if(dataStoreListItem.getTimestamp().equals(rssListItem.getTimestamp())){
                log.info("present!");
                isPresent = true;
                break;
            }
        }

        if(!isPresent) {

            api.addAirQualitySample(rssListItem);

        }
    }

}