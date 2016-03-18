package co.ghola.backend.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Entity
public class AirQualitySample {
    @Id
    Long id;

    String aqi;

    String message;

    @Index
    DateTime date;

    private static final Logger log = Logger.getLogger(AirQualitySample.class.getName());

    public AirQualitySample() {
    }

    public AirQualitySample(Long id) {
        super();
        this.id = id;
    }

    public AirQualitySample(String aqi, String message, DateTime date) {
        super();
        //this.id = id;
        this.aqi = aqi;
        this.message = message;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date)  {
        this.date = date;
    }

    @Override
    public String toString() {
        return "AirQualityIndex [id=" + id + ", aqi=" + aqi + ", message="
                + message + ", date="+ date.toString() +"]";
    }

}
