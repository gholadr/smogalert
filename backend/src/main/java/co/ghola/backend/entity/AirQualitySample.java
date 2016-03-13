package co.ghola.backend.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

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
    Date date;

    private static final Logger log = Logger.getLogger(AirQualitySample.class.getName());

    public AirQualitySample() {
    }

    public AirQualitySample(Long id) {
        super();
        this.id = id;
    }

    public AirQualitySample(String aqi, String message, Date date) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date)  {
        this.date = date;
    }

    @Override
    public String toString() {
        return "AirQualityIndex [id=" + id + ", aqi=" + aqi + ", message="
                + message + ", date="+ date.toString() +"]";
    }

}
