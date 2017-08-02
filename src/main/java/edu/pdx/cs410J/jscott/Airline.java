package edu.pdx.cs410J.jscott;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

import java.util.*;

/**
 * The Airline Class was written for project 1 in order to represent an airline object and all flights associated with
 * that Airline. Each Airline contans a name field and a list of flights. The Airline class contains method
 * implementations to override the abstraxt parent class AbstractAirline.
 */
public class Airline extends AbstractAirline{

    private static String name;
    private static List<AbstractFlight> flightList;

    /**
     * Constructor takes the airline name as an argument and generates an empty list of flights. Must use the addFlight()
     * method to add flights to the airline object.
     * @param airlineName
     */
    public Airline (String airlineName){
      name = airlineName;
      flightList = new ArrayList<>();
    }

    //Default Constructor
    public Airline(){
        name = "";
        flightList = new ArrayList<>();
    }

    /**
     * Copy Constructor
     * @param airline
     */
    //Copy constructor
    public Airline (AbstractAirline airline){
        name = airline.getName();
        flightList = new ArrayList<>(airline.getFlights());
    }

    /**
     * Method that returns the name of the airline.
     * @return  The name of the airline
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Method to add a flight to the list of flights for an airline object. Takes an Flight object as an argument.
     * @param abstractFlight
     */
    @Override
    public void addFlight(AbstractFlight abstractFlight) {
        flightList.add(abstractFlight);
        this.sort();
    }

    /**
     * Method that retuns a Collection object representing the list of flights.
     * @return  a collection object that is a list of flight objects.
     */
    @Override
    public Collection getFlights() {
        return flightList;
    }

    /**
     * Method added for Project 3 to sort by Airport Source Code. If aiport code is equal sorts by Departure time.
     * Uses @FlightComparator object
     */
    public static void sort(){
        Collections.sort(flightList, new FlightComparator());
    }

    /**
     * Added for project 4 to change or add name of airline in servlet.
     * @param newName sets the name of the Airline
     */
    public void addName(String newName){
        name = newName;
    }

}
