<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:fb="http://ogp.me/ns/fb#" >
 <head>
    <meta property="og:image" content="http://i.imgur.com/sN1B51f.png" />
 <head/>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.ghola.backend.entity.AirQualitySample" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ page import="org.joda.time.*" %>

<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the Greetings belonging to the selected Guestbook.
      List<AirQualitySample> aqis = ObjectifyService.ofy()
          .load()
          .type(AirQualitySample.class) // We want only Greetings
          .order("-ts")       // Most recent first - date is indexed.
          .limit(1)             // Only show 5 of them.
          .list();
    if (aqis.isEmpty()) {
%>
<center>
  <p>No Samples</p>
</center>
<%
    } else {

      // Look at all of our greetings
        for (AirQualitySample aqi : aqis) {
            String airQualityIndex;
            String message;
            String timeStamp;
            DateTime d = new DateTime(aqi.getTimestamp() * 1000, DateTimeZone.UTC);
            String dateText =d.toString("MM/dd");
            String timeText =d.toString("h a");
            String datetimeText = String.format("%s at %s", dateText, timeText);
            pageContext.setAttribute("airQualityIndex", "Air Quality: "+aqi.getAqi()+" AQI");
            pageContext.setAttribute("message", "Level: "+aqi.getMessage());
            pageContext.setAttribute("timeStamp","Time: "+datetimeText);
            pageContext.setAttribute("location","Location: "+"Ho Chi Minh City");
            pageContext.setAttribute("by","AQI sample measured by air quality sensor from the US Embassy");
%>
<center>
  <img src="http://i.imgur.com/sN1B51f.png" alt="Icon" width="128" height="128">
  <p><b>${fn:escapeXml(airQualityIndex)}</b></p>
  <blockquote>${fn:escapeXml(message)}</blockquote></P>
  <blockquote>${fn:escapeXml(timeStamp)}</blockquote></P>
  <blockquote>${fn:escapeXml(location)}</blockquote></P>
  <blockquote>${fn:escapeXml(by)}</blockquote></P>
</center>
<%
        }
    }
%>
</html>
