package edu.pdx.cs410J.jscott;

import edu.pdx.cs410J.AbstractFlight;

import java.text.DateFormat;
import java.util.Date;

/**
 * The Flight class defines an object that represents all the necessary data fields for a flight. This includes the
 * flight number, airline name, 3 digit source airport code, departure time, 3 digit destination airport code, and
 * arrival time. The time fields should be formatted prior to input in the form dd/mm/yyyy hh:mm on a 24 hour clock.
 */
public class Flight extends AbstractFlight {

  private int FlightNumber;
  private String Name;
  private String Src;
  private Date DepartTime;
  private String Dest;
  private Date ArriveTime;

  /**
   * The default constructor does not take any arguments and sets default dummy values for each field.
   */
  public Flight(){
    super();
    Name = "Default";
    FlightNumber = 42;
    Src = "ABC";
    DepartTime = new Date();
    Dest = "ABC";
    ArriveTime = new Date();
  }



  /**
   * A constructor with parameters for each data field.
   * @param AirlineName The name of the airline
   * @param FlightValue The flight number and integer value
   * @param SourceAirportCode The three letter code departure airport
   * @param DepartureTime Departure date/time am/pm a date object in SHORT date format
   * @param DestinationCode The three letter code of the arrival airport
   * @param ArrivalTime The arrival date/time am/pm date object in SHORT date format
   */
  public Flight (String AirlineName, int FlightValue, String SourceAirportCode, Date DepartureTime, String DestinationCode, Date ArrivalTime){
    super();
    Name = AirlineName;
    FlightNumber = FlightValue;
    Src = SourceAirportCode;
    DepartTime = DepartureTime;
    Dest =  DestinationCode;
    ArriveTime = ArrivalTime;
  }

  /**
   * A method to get the name of the airline.
   * @return  the name of the airline
   */
  public String getName(){
    return Name;
  }

  /**
   * A method to return the flight number.
   * @return  Flight number
   */
  @Override
  public int getNumber() {
    return FlightNumber;
  }

  /**
   * A method to return the 3 digit source airport code
   * @return  An airport code
   */
  @Override
  public String getSource() {
    return Src;
  }

  /**
   * A method without parameters that returns the departure date and time as a single string
   * @return  the departure time as a String in SHORT date format
   */
  @Override
  public String getDepartureString() {
    int f = DateFormat.SHORT;
    DateFormat df = DateFormat.getDateTimeInstance(f, f);
    return df.format(DepartTime);
  }

  /**
   * A getter for the destination code
   * @return  destination code
   */
  @Override
  public String getDestination() {
    return Dest;
  }

  /**
   * A getter for the arrival date and time as a single string
   * @return  arrival date and time as a String in SHORT date format
   */
  @Override
  public String getArrivalString() {
    int f = DateFormat.SHORT;
    DateFormat df = DateFormat.getDateTimeInstance(f, f);
    return df.format(ArriveTime);
  }

    /**
     * New Method for Project 3 returns a date object
     * @return A date object for departure time
     */
  @Override
  public Date getDeparture() {
    return DepartTime;
  }

    /**
     * A new mehtod for Projet 3 returns a date object
     * @return a date object for arrival time
     */
  @Override
  public Date getArrival() {
    return ArriveTime;
  }
}
