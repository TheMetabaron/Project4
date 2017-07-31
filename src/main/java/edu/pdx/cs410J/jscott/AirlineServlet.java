package edu.pdx.cs410J.jscott;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.AirportNames;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>Airline</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple key/value pairs.
 */
public class AirlineServlet extends HttpServlet {
  //private final Map<String, String> data = new HashMap<>();
  //private final Map<String, Flight> data = new HashMap<>();
    private Airline airline = new Airline();

  /**
   * Handles an HTTP GET request from a client by writing the value of the key
   * specified in the "key" HTTP parameter to the HTTP response.  If the "key"
   * parameter is not specified, all of the key/value pairs are written to the
   * HTTP response.
   */
  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
      response.setContentType( "text/plain" );
      String uri = request.getRequestURI();
      String lastPart = uri.substring(uri.lastIndexOf('=') + 1, uri.length());

      if(lastPart.contains("&src=")){
          String src = getParameter( "src", request );
          String dest = getParameter( "dest", request );
          writeValue(src, dest, response);
      }
      else {
          writeAllMappings(response);
      }
  }

  /**
   * Handles an HTTP POST request by storing the key/value pair specified by the
   * "key" and "value" request parameters.  It writes the key/value pair to the
   * HTTP response.
   */
  @Override
  protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
      response.setContentType( "text/plain" );

      String airlineName = getParameter( "name", request );
      if (airlineName == null) {
          missingRequiredParameter(response, "name");
          return;
      }
      String flightNumber = (getParameter( "flightNumber", request ));
      if ( flightNumber == null) {
          missingRequiredParameter( response, "flightNumber" );
          return;
      }
      int flightNum = Integer.parseInt(flightNumber);
      String srcCode = (getParameter( "src", request ));
      if ( srcCode == null) {
          missingRequiredParameter( response, "src" );
          return;
      }
      String departTime = getParameter( "departTime", request );
      if ( departTime == null) {
          missingRequiredParameter( response, "departTime" );
          return;
      }
      String destCode = getParameter( "dest", request );
      if (destCode == null) {
          missingRequiredParameter( response, "dest" );
          return;
      }
      String arriveTime = getParameter( "arriveTime", request );
      if ( arriveTime == null) {
          missingRequiredParameter( response, "arriveTime" );
          return;
      }
      DateFormat format = new SimpleDateFormat();
      Date departureDate = new Date();
      Date arrivalDate = new Date();
      try {
          departureDate = format.parse(departTime);
          arrivalDate = format.parse(arriveTime);
      } catch(ParseException ex){
          System.err.println("Error: Parse Exception while parsing string to date in Airline Servlet doPost Method" );
          System.exit(1);
      }

      //Does Airline Exist?
      if(airline.getName().equalsIgnoreCase(airlineName)){
          airline = new Airline(airlineName);
      }
      Flight flight = new Flight(airlineName, flightNum, srcCode, departureDate, destCode, arrivalDate);
      airline.addFlight(flight);

      PrintWriter pw = response.getWriter();
      pw.println("Added flight: " + flight.toString() + "\n" + flight.getName() + " " + flight.getNumber() + " " + flight.getSource() + " "
                    + flight.getDepartureString() + " " + flight.getDestination() + " " + flight.getArrivalString());
      pw.flush();

      response.setStatus( HttpServletResponse.SC_OK);
  }

  /**
   * Handles an HTTP DELETE request by removing all key/value pairs.  This
   * behavior is exposed for testing purposes only.  It's probably not
   * something that you'd want a real application to expose.
   */
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      response.setContentType("text/plain");

      //this.data.clear();

      airline = new Airline();
      PrintWriter pw = response.getWriter();
      pw.println(Messages.allMappingsDeleted());
      pw.flush();

      response.setStatus(HttpServletResponse.SC_OK);

  }

  /**
   * Writes an error message about a missing parameter to the HTTP response.
   *
   * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
   */
  private void missingRequiredParameter( HttpServletResponse response, String parameterName )
      throws IOException
  {
      String message = Messages.missingRequiredParameter(parameterName);
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
  }

  /**
   * Writes the value of the given key to the HTTP response.
   *
   * The text of the message is formatted with
   * {@link Messages#formatKeyValuePair(String, String)}
   */
  private void writeValue(String source, String dest, HttpServletResponse response) throws IOException {
      ArrayList<Flight> flights = new ArrayList<>(airline.getFlights());
      PrintWriter pw = response.getWriter();
      for (Flight f : flights) {
          if (source.equalsIgnoreCase(f.getSource()) && dest.equalsIgnoreCase(f.getSource())) {

              //TODO: needs to be printwriter???
              //pw.println(Messages.formatKeyValuePair(key, value));
              System.out.println("Flight Number:       " + f.getNumber());
              System.out.println("Departure Airport:   " + AirportNames.getName(f.getSource()));
              System.out.println("Departure Date:      " + f.getDeparture().toString());
              System.out.println("Destination Airport: " + AirportNames.getName(f.getDestination()));
              System.out.println("Arrival Date:        " + f.getArrival().toString());
              long duration = f.getArrival().getTime() - f.getDeparture().getTime();
              duration = duration / 1000 / 60;
              System.out.println("Flight Duration:     " + duration + " minutes");
          }
          pw.flush();

          response.setStatus(HttpServletResponse.SC_OK);
      }
  }

  /**
   * Writes all of the key/value pairs to the HTTP response.
   *
   * The text of the message is formatted with
   * {@link Messages#formatKeyValuePair(String, String)}
   */
  private void writeAllMappings( HttpServletResponse response ) throws IOException {
      //PrintWriter pw = response.getWriter();
      //Messages.formatKeyValueMap(pw, data);
      PrettyPrinter prettyPrinter = new PrettyPrinter("-");
      prettyPrinter.dump(airline);
      //pw.flush();

      response.setStatus( HttpServletResponse.SC_OK );
  }

  /**
   * Returns the value of the HTTP request parameter with the given name.
   *
   * @return <code>null</code> if the value of the parameter is
   *         <code>null</code> or is the empty string
   */
  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;

    } else {
      return value;
    }
  }
/*
  @VisibleForTesting
  void setValueForKey(String key, String value) {
      this.data.put(key, value);
  }

  @VisibleForTesting
  String getValueForKey(String key) {
      return this.data.get(key);
  }
*/
}
