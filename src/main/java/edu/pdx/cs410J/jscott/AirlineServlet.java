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

      //check for matching airline name
      String airlineName = getParameter("name", request);
      if(airlineName == null){
          missingRequiredParameter(response, "name");
          return;
      }
      if(airline.getName() == ""){
          response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Unable to search because no Airline data has been saved yet");
          return;
      }
      //send error if airline names don't match
      if(!airlineName.equalsIgnoreCase(airline.getName())){
          response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Airline name does not match Airline of record");
          return;
      }

      //Check values for search function
      String src = getParameter( "src", request );
      String dest = getParameter( "dest", request );
      String Paramaters = request.getQueryString();
      if(Paramaters == null){
          response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "No paramaters specified, must include at least an airline name");
      }
      if(dest == null && src == null){
        writeAllMappings(response);
      }
      else if(dest == null){
          missingRequiredParameter( response, "dest" );
          return;
      }
      else if (src == null) {
          missingRequiredParameter(response, "src");
          return;
      }
      // else get all flights
      else {
          writeValue(src, dest, response);
      }

      response.setStatus( HttpServletResponse.SC_OK);
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
      Date departureDate;
      Date arrivalDate;
      try {
          departureDate = format.parse(departTime);
          arrivalDate = format.parse(arriveTime);
      } catch(ParseException ex){
          PrintWriter pw = response.getWriter();
          pw.println("Error: Parse Exception while parsing string to date in Airline Servlet doPost Method" );
          pw.flush();
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: Parse Exception while parsing string to date in Airline Servlet doPost Method\"");
          return;
      }

      //Does Airline Exist?
      if(airline == null) {
          airline = new Airline(airlineName);
      }
       else if(!(airline.getName().equalsIgnoreCase(airlineName))){
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
      boolean isThereAMatch = false;
      long duration = 0;
      PrintWriter pw = response.getWriter();
      StringBuilder sb = new StringBuilder();
      sb.append("\nAll Flights for ").append(airline.getName()).append(" between ")
              .append(source).append(" and ").append(dest).append("\n");
      sb.append("\n-------------------------------------------------\n");
      for (Flight f : flights) {
          if (source.equalsIgnoreCase(f.getSource()) && dest.equalsIgnoreCase(f.getDestination())) {
              isThereAMatch = true;
              sb.append("Flight Number:       ").append(f.getNumber());
              sb.append("\nDeparture Airport:   ").append(AirportNames.getName(f.getSource()));
              sb.append("\nDeparture Date:      ").append(f.getDeparture().toString());
              sb.append("\nDestination Airport: ").append(AirportNames.getName(f.getDestination()));
              sb.append("\nArrival Date:        ").append(f.getArrival().toString());
              duration = f.getArrival().getTime() - f.getDeparture().getTime();
              duration = duration /1000/60;
              sb.append("\nFlight Duration:     ").append(duration).append(" minutes");
              sb.append("\n-------------------------------------------------\n");
          }
      }
      //TODO: Print a message if there are no matches
      if(isThereAMatch == false){
          sb.append("No matches found\n\n");
      }
      pw.println(sb.toString());
      pw.flush();
      response.setStatus(HttpServletResponse.SC_OK);
  }

  /**
   * Writes all of the key/value pairs to the HTTP response.
   *
   * The text of the message is formatted with
   * {@link Messages#formatKeyValuePair(String, String)}
   */
  private void writeAllMappings( HttpServletResponse response ) throws IOException {
      PrintWriter pw = response.getWriter();
      StringBuilder sb = new StringBuilder();
      sb.append("All Flights for " + airline.getName() +"\n");
      sb.append("\n-------------------------------------------------\n");
      ArrayList<Flight> flights = new ArrayList<>(airline.getFlights());
      long duration = 0;
      for(Flight f: flights){
          sb.append("Flight Number:       ").append(f.getNumber());
          sb.append("\nDeparture Airport:   ").append(AirportNames.getName(f.getSource()));
          sb.append("\nDeparture Date:      ").append(f.getDeparture().toString());
          sb.append("\nDestination Airport: ").append(AirportNames.getName(f.getDestination()));
          sb.append("\nArrival Date:        ").append(f.getArrival().toString());
          duration = f.getArrival().getTime() - f.getDeparture().getTime();
          duration = duration /1000/60;
          sb.append("\nFlight Duration:     ").append(duration).append(" minutes");
          sb.append("\n-------------------------------------------------\n");
      }
      pw.println(sb);
      pw.flush();
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
