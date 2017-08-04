package edu.pdx.cs410J.jscott;

import edu.pdx.cs410J.AbstractFlight;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * The main class that parses the command line and communicates with the
 * Airline server using REST.
 */
public class Project4 {

    public static void main(String... args) {

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
                } else if ((args[j]).equalsIgnoreCase("-search")){
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
            System.err.println("Error: Missing command line arguments. Expected: name, flightNumber, src, departTime, dest, arriveTime");
            System.exit(1);
        }

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
            AirlineRestClient client = new AirlineRestClient(flags[3], port);
            String responseReturnString;

            try {
                //If -search flag included
                if (flags[6] != null) {
                    // search for flights for a specific src/dest
                    if(commands[3] == null){
                        // Post the src/departTimeString pair
                        try {
                            responseReturnString = client.searchForSrcDest(commands[0], commands[1], commands[2]);
                            System.out.println(responseReturnString);
                        } catch (RuntimeException ex){
                            System.err.println(ex.getMessage());
                            System.exit(1);
                        }
                    }
                    //if src and dest not specified issue error
                    else{
                        System.err.println("Error: To many arguments, -search command must include name, src and dest only");
                        System.exit(1);
                    }
                    //If no -search flag then add flight
                } else{
                    //Check command line arguments
                    checkCommandLineArguments(commands);
                    //Convert FlightValue to int
                    int flightValue = 0;
                    try {
                        flightValue = Integer.parseInt(commands[1]);
                    } catch (NumberFormatException ex) {
                        System.err.println("Second argument must be an integer: " + commands[1]);
                        System.exit(1);
                    }
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
                    client.addFlight(commands);

                    //-print flag prints description of the new flight
                    if(flags[0] != null) {
                        //make flight and print
                        Flight flight = new Flight(commands[0], flightValue, commands[2], departureDate, commands[6], arrivalDate);
                        if (flags[0] != null) {
                            System.out.println(flight.toString());
                        }
                    }
                }

            }   catch (IOException ex) {
                  error("While contacting server: " + ex.getMessage());
                  return;
            }
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
    

    /**
     * This method prints an explanation of the program functionality.
     */
    private static void printReadMe() {
        System.out.println("***********************************************************\n" +
                "* README FOR PROJECT FOUR: A REST-ful AIRLINE WEB SERVICE *\n" +
                "***********************************************************\n");
        System.out.println("This program was written by Scott Jones for CS510J\n");
        System.out.println("The purpose of this program is to simulate an Airline booking application.\n" +
                "The current functionality includes the ability to enter the information for\n" +
                "a flight at the command line and that information will be entered into the\n" +
                "fundamental Airline and Flight objects.\n" +
                "In this project you will extend your airline application to support an airline " +
                "server that provides REST-ful web services to an airline client.\n" +
                "The list of Flights is ordered based on Departure airport and Departure time.\n");
        System.out.println("USAGE\n\n" +
                "java -jar target/airline.jar [options] <args>\n\n" +
                "This program expects the following arguments in the order listed\n" +
                "name\t\t\tThe name of the airline\n" +
                "flightNumber\t\tThe flight number\n" +
                "src\t\t\tThree letter code of the departure airport\n" +
                "departTime\t\tTThe departure date and time (see note)\n" +
                "dest\t\t\tThree letter code of the arrival airport\n" +
                "arriveTime\t\tArrival date and time (see note)\n");
        System.out.println("note: Date and time should be in the format: mm/dd/yyyy hh:mm am/pm");
        System.out.println("\nOptions:\n" +
                "-host hostname\t\tHost computer on which the server runs\n" +
                "-port port \t\tPort on which the server is listening\n" +
                "-print\t\t\tPrints a description of the new flight\n" +
                "-README\t\t\tPrints a README for this project and exits\n" +
                "-search\t\t\tSearch for flights\n" +
                "If the -search option is provided, only the name, src and dest \n" +
                "are required. The program will print  out all of the direct flights " +
                "that originate at the src airport and terminate at the dest airport.\n");
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
