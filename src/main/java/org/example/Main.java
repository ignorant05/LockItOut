package org.example;

import Needed.ArgParsing;
import Needed.HandlingOptions;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        ArgParsing parser = new ArgParsing(args, null);
        CommandLine cmd = parser.getCommandLine();
        String platform = ArgParsing.HasOption("pf", cmd) ? ArgParsing.GetOption("pf", cmd) : null;
        String email = ArgParsing.HasOption("e", cmd) ? ArgParsing.GetOption("e", cmd) : null;
        String username = ArgParsing.HasOption("u", cmd) ? ArgParsing.GetOption("u", cmd) : null;
        String password = ArgParsing.HasOption("p", cmd) ? ArgParsing.GetOption("p", cmd) : null;

        HandlingOptions.FilterOptions(args, platform, email, username, password, cmd);
    }
}