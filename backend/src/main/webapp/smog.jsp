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
<p>No Samples</p>
<%
    } else {

      // Look at all of our greetings
        for (AirQualitySample aqi : aqis) {
            String airQualityIndex;
            String message;
            String timeStamp;
            DateTime d = new DateTime(aqi.getTimestamp() * 1000, DateTimeZone.UTC);
            String dateText =d.toString("MMMM d");
            String timeText =d.toString("haa");
            String datetimeText = String.format("%s, %s", dateText, timeText);
            pageContext.setAttribute("airQualityIndex", aqi.getAqi());
            pageContext.setAttribute("message", aqi.getMessage());
            pageContext.setAttribute("timeStamp",datetimeText );
%>
<p><b>${fn:escapeXml(airQualityIndex)}</b></p>
<blockquote>${fn:escapeXml(message)}</blockquote></P>
<blockquote>${fn:escapeXml(timeStamp)}</blockquote></P>
<%
        }
    }
%>