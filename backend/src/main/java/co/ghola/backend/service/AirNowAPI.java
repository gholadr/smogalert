package co.ghola.backend.service;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import co.ghola.backend.entity.AirQualitySample;

public class AirNowAPI extends HttpServlet {
    private static Logger log = Logger.getLogger(RssFetcher.class.getName());
    private static String urlString = "http://www.airnowapi.org/aq/observation/latLong/current/?format=application/json&latitude=10.7331&longitude=106.7908&distance=25&API_KEY=EE429E8C-0842-4918-B2E8-79B20E71DEC8";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Response response = doGetRequest(urlString);
        AirQualitySampleWrapper api =   AirQualitySampleWrapper.getInstance();
        AirQualitySample sample = null;
        String dateObserved = response.jsonPath().getString("DateObserved[0]");
        String hourObserved = response.jsonPath().getString("HourObserved[0]");
        String AQI = response.jsonPath().getString("AQI[0]");
        Map<String, String> message = response.jsonPath().getMap("Category[0]");

        sample = new AirQualitySample(AQI,message.get("Name"),DateUtils.getInstance().dateString2Long(dateObserved.trim() + " " + hourObserved + ":00:00"));

        api.addAirQualitySample(sample);
        log.info(String.valueOf(dateObserved.trim() + "-" + hourObserved + ":" + AQI + " msg:" + message.get("Name")));
    }

    private static Response doGetRequest(String endpoint) {
        RestAssured.defaultParser = Parser.JSON;

        return
                given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                        when().get(endpoint).
                        then().contentType(ContentType.JSON).extract().response();
    }

}
