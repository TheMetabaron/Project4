package edu.pdx.cs410J.jscott;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by scottjones on 7/22/17.
 */
public class DateTimeParser {

    public DateTimeParser(){}

    public static Date parse(String dateTime){

        Date result = null;
        int format = DateFormat.SHORT;
        DateFormat df = DateFormat.getDateTimeInstance(format, format);

        try{
            result = df.parse(dateTime);
        } catch(ParseException ex) {
            System.err.println("Error: Bad date format. Please use the format mm/dd/yyy hh:mm am/pm. You entered - " + dateTime);
            System.exit(1);
        }
        return result;
    }
}
