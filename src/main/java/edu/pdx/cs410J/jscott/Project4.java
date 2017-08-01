package edu.pdx.cs410J.jscott;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * The main class that parses the command line and communicates with the
 * Airline server using REST.
 */
public class Project4 {

    public static final String MISSING_ARGS = "Missing command line arguments";

    public static void main(String... args) {
        String name = null;
        String flightNumber = null;
        String src = null;
        String departTimeString = null;
        String dest = null;
        String arrivalTimeString = null;

        String[] commands = new String[10];
        String[] flags = new String[7];

        int i = 0;
        for (int j = 0; j < args.length; ++j) {
            //Check for flags
            if (args[j].charAt(0) == '-') {
                if ((args[j]).equalsIgnoreCase("-print")) {
                    flags[0] = (args[j]);
                    //call print description of the new flight
                } else if ((args[j]).equalsIgnoreCase("-README")) {
                    flags[1] = (args[j]);
                    //call print a README for this project and exit
                    printReadMe();
                } else if ((args[j]).equalsIgnoreCase("-host")) {
                    flags[2] = (args[j]);
                    //Make sure hostname is included after text file flag
                    if (j + 1 < args.length) {
                        ++j;
                        flags[3] = args[j];
                    } else {
                        System.err.println("Error: Expects argument after -host");
                        System.exit(1);
                    }
                } else if ((args[j]).equalsIgnoreCase("-port")) {
                    flags[4] = (args[j]);
                    //make sure port number is included
                    if (j + 1 < args.length) {
                        ++j;
                        flags[5] = args[j];
                    } else {
                        System.err.println("Error: Expects argument after -port");
                        System.exit(1);
                    }
                } else if ((args[i]).equalsIgnoreCase("-search")){
                    flags[6] = args[j];
                }
                else {
                    System.err.println("Error: Unknown flag " + args[j]);
                    System.exit(1);
                }
            } else {
                if (i > 9) {
                    System.err.println("Error: To many command line arguments. Expected: name, flightNumber, src, departTime, dest, arriveTime");
                    System.exit(1);
                }

                commands[i] = args[j];
                ++i;
            }
        }
        //if -search only name src and dest are required
        if ((flags[6] == null && i < 10) || (i < 3)){
            System.err.println("Error 666: Missing command line arguments. Expected: name, flightNumber, src, departTime, dest, arriveTime");
            System.err.println("i = " + i);
            System.exit(1);
        }

        //Convert FlightValue to int
        int flightValue = 0;
        try {
            flightValue = Integer.parseInt(commands[1]);
        } catch (NumberFormatException ex) {
            System.err.println("Second argument must be an integer: " + commands[1]);
            System.exit(1);
        }
        //Check command line arguments
        checkCommandLineArguments(commands);

        //TODO: DO I need to create a date object here?
        //parse date objects
        Date departureDate = null;
        Date arrivalDate = null;
        int f = DateFormat.SHORT;
        DateFormat df = DateFormat.getDateTimeInstance(f, f);
        try {
            departureDate = df.parse(commands[3] + " " + commands[4] + " " + commands[5]);
            arrivalDate = df.parse(commands[7] + " " + commands[8] + " " + commands[9]);
        } catch (ParseException e) {
            System.err.println("Error: Unable to parse date and times which must be in the format dd/mm/yyyy hh:mm am/pm");
            System.exit(1);
        }
        //------------------------------------


        //if -port parse port number, error check and call AirlineRestClient
        if(flags[2] != null && flags [4] == null){
            System.err.println("Error: -host flag must be accompanied with -port flag");
            System.exit(1);
        }
        if (flags[4] != null) {
            int port;
            if (flags[5] == null) {
                System.err.println("Error: -port flag must be followed by the port number");
                System.exit(1);
            }
            if (flags[2] == null) {
                System.err.println("Error: -port flag must be accompanied by -host flag");
                System.exit(1);
            }
            try {
                port = Integer.parseInt(flags[5]);

            } catch (NumberFormatException ex) {
                usage("Port \"" + flags[5] + "\" must be an integer");
                return;
            }
            AirlineRestClient client = new AirlineRestClient(name, port);

            String message;
            try {
                if (commands[3] == null) {
                    // Print all pretty print all matching flights
                    //TODO: Implement client.getAllFlights?  and messages.parseFlights for Search functionality
                    /*
                    Map<String, String> keysAndValues = client.getAllKeysAndValues();
                    StringWriter sw = new StringWriter();
                    Messages.formatKeyValueMap(new PrintWriter(sw, true), keysAndValues);
                    message = sw.toString();


                } else if (departTimeString == null) {
                    // Print all values of src
                    message = Messages.formatKeyValuePair(src, client.getValue(src));
                    */
                } else{
                    // Post the src/departTimeString pair
                    //TODO: Implement client.addFlight
                    client.addFlight(commands[0]);
                    client.addKeyValuePair(src, departTimeString);
                    message = Messages.mappedKeyValue(src, departTimeString);
                }

            }   catch (IOException ex) {
                  error("While contacting server: " + ex);
                  return;
            }

            //System.out.println(message);
        }

        System.exit(0);
    }

    private static void error(String message) {
        PrintStream err = System.err;
        err.println("** " + message);

        System.exit(1);
    }

    /**
     * Prints usage information for this program and exits
     *
     * @param message An error message to print
     */
    private static void usage(String message) {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java Project4 host port [key] [value]");
        err.println("  host    Host of web server");
        err.println("  port    Port of web server");
        err.println("  key     Key to query");
        err.println("  value   Value to add to server");
        err.println();
        err.println("This simple program posts key/value pairs to the server");
        err.println("If no value is specified, then all values are printed");
        err.println("If no key is specified, all key/value pairs are printed");
        err.println();

        System.exit(1);
    }

//TODO: Update README

    /**
     * This method prints an explanation of the program functionality.
     */
    private static void printReadMe() {
        System.out.println("***********************************************\n" +
                "* README FOR PROJECT THREE: AIRLINE APPLICATION *\n" +
                "***********************************************\n");
        System.out.println("This program was written by Scott Jones for CS510J\n");
        System.out.println("The purpose of this program is to simulate an Airline booking application.\n" +
                "The current functionality includes the ability to enter the information for\n" +
                "a flight at the command line and that information will be entered into the\n" +
                "fundamental Airline and Flight objects.\n" +
                "Added for project 2 is the printFile flag that reads and prints to file\n" +
                "Added for project 3 is the pretty flag that prints an Airline's flights to a text file or standard out.\n" +
                "The list of Flights is now ordered based on Departure airport and Departure time.\n");
        System.out.println("USAGE\n\n" +
                "java edu.pdx.cs410J.jscott.Project3 [options] <args>\n\n" +
                "Command Line Arguments:\n" +
                "This program expects the following arguments in the order listed\n" +
                "name\t\t\tThe name of the airline\n" +
                "flightNumber\tThe flight number\n" +
                "src\t\t\t\tThree letter code of the departure airport\n" +
                "departTime\t\tTThe departure date and time\n" +
                "dest\t\t\tThree letter code of the arrival airport\n" +
                "arriveTime\t\tArrival date and time (24 hour time see note)\n");
        System.out.println("note: Date and time should be in the format: mm/dd/yyyy hh:mm am/pm");
        System.out.println("\nOptions:\n" +
                "-textFile file\t\tWhere to read/write the airline info\n" +
                "-print\t\t\t\tPrints a description of the new flight\n" +
                "-README\t\t\t\tPrints a README for this project and exits\n" +
                "-pretty file\t\tPretty print the airline's lights to\n" +
                "\t\t\t\t\ta text file or standard out (file -)");
        System.exit(4);
    }

    private static void checkCommandLineArguments(String[] commands) {
        //check to make sure airport code is 3 letter (2 and 4)
        if (!commands[2].matches("[a-zA-z]{3}") || !commands[6].matches("[a-zA-z]{3}")) {
            System.err.println("Error: The airport codes must consist of three letters");
            System.err.println("Your entries were: " + commands[2] + " and " + commands[6]);
            System.exit(2);
        } else {
            commands[2] = commands[2].toUpperCase();
            commands[6] = commands[6].toUpperCase();
        }
    }
}
