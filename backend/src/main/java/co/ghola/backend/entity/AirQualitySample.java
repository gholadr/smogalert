package co.ghola.backend.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class AirQualitySample {
    @Id
    Long id;

    String aqi;

    String message;

    @Index
    Date date;

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

    public void setDate(Date date)  { this.date = date; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AirQualitySample other = (AirQualitySample) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AirQualityIndex [id=" + id + ", aqi=" + aqi + ", message="
                + message + ", date="+ date.toString() +"]";
    }

}
