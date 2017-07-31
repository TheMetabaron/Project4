package edu.pdx.cs410J.jscott;

import edu.pdx.cs410J.AbstractFlight;

import java.util.Comparator;
import java.util.Date;

/**
 * Class Flight Comparator builds a comparator that compares two Flight Objects. This is used
 * to lexicographically order the Flight Objects wihtin an Airline object for Porject 3.
 * @author Scott Jones
 */
public class FlightComparator implements Comparator<AbstractFlight> {

    /**
     * Takes two Flight Obejcts and returns a value based on lexicographical ordering.
     * If equal, the tiebraker is Departure Time. Otherwise they are qequal.
     * @param o1 A Flight Object
     * @param o2 Another Flight Object
     * @return -1 if o1 Source code is lexicogrpahically less, 1 if Source is lexicogrpahically equal. Tiebreaker is departuretime. If still equal returns 0.
     *
     */
    @Override
    public int compare (AbstractFlight o1, AbstractFlight o2){
        String airport1 = o1.getSource();
        String airport2 = o2.getSource();

        int compare = airport1.compareToIgnoreCase(airport2);
        if(compare < 0){
            //this is lexicographically less than the argument string
            return -1;
        }
        else if(compare > 0){
            //This is lexicographically greater than the argument string
            return 1;
        }
        else {
            // If airports are equal compare departure times
            Date departure1 = o1.getDeparture();
            Date departure2 = o2.getDeparture();
            if(departure1.before(departure2)){
                return -1;
            }
            else if (departure1.after(departure2)){
                return 1;
            }
            //if departure times are equal, do nothing
        }
        return 0;
    }

}
