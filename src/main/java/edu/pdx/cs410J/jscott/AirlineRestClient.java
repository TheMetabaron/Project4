package edu.pdx.cs410J.jscott;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send key/value pairs.
 */
public class AirlineRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "airline";
    private static final String SERVLET = "flights";

    private final String url;


    /**
     * Creates a client to the airline REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public AirlineRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    /**
     *
     * Returns all keys and values from the server
     */

    public Map<String, String> getAllKeysAndValues() throws IOException {
      Response response = get(this.url);
      return Messages.parseKeyValueMap(response.getContent());
    }


    /**
     * Returns all flights from the server. Used by Project4 to search for matching flights
     * @return a array list collection of flights
     * @throws IOException if unable to get url
     */
    public Airline getAllFlights() throws IOException{
        Response response = get(this.url);
        //TODO: Implement -create an airline with returned flight info
        return Messages.parseFlights(response.getContent());
    }

    /**
     * Returns the value for the given key
     */
    public String getValue(String key) throws IOException {
      Response response = get(this.url, "key", key);
      throwExceptionIfNotOkayHttpStatus(response);
      String content = response.getContent();
      return Messages.parseKeyValuePair(content).getValue();
    }

    public String searchForSrcDest(String name, String src, String dest) throws IOException {
        Response response = get(this.url, "name", name, "src", src, "dest", dest);
        throwExceptionIfNotOkayHttpStatus(response);
        return response.getContent();
    }

    public void addFlight(String [] flightInfo) throws IOException{
        Response response = postToMyURL("name", flightInfo[0], "flightNumber", flightInfo[1], "src", flightInfo[2],
                                "departTime", flightInfo[3] + " " + flightInfo[4] + " " + flightInfo[5],
                                "dest", flightInfo[6], "arriveTime", flightInfo[7] + " " + flightInfo[8] + " " + flightInfo[9]);
        throwExceptionIfNotOkayHttpStatus(response);
    }

    @VisibleForTesting
    Response postToMyURL(String... keysAndValues) throws IOException {
      return post(this.url, keysAndValues);
    }

    public void removeAllMappings() throws IOException {
      Response response = delete(this.url);
      throwExceptionIfNotOkayHttpStatus(response);
    }

    private Response throwExceptionIfNotOkayHttpStatus(Response response) {
      int code = response.getCode();
      if (code != HTTP_OK) {
        throw new RuntimeException(response.getContent());
      }
      return response;
    }

    private class AppointmentBookRestException extends RuntimeException {
      public AppointmentBookRestException(int httpStatusCode) {
        super("Got an HTTP Status Code of " + httpStatusCode);
      }
    }
}
