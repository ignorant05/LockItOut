package org.example;

import Needed.ArgParsing;
import Needed.HandlingOptions;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        ArgParsing parser = new ArgParsing(args);
        CommandLine cmd = parser.getCommandLine();

        String oldUsername = null, newUsername = null;
        String oldPassword = null, newPassword = null;
        String oldEmail = null, newEmail = null;

        if (cmd.hasOption("mu") || cmd.hasOption("modify-username")) {
            String[] usernameArgs = ArgParsing.GetUserOptions("mu", cmd);
            if (usernameArgs != null && usernameArgs.length == 2){
                oldUsername = usernameArgs[0];
                newUsername = usernameArgs[1];
            }
        }

        if (cmd.hasOption("mp") || cmd.hasOption("modify-password")) {
            String[] passwordArgs = ArgParsing.GetUserOptions("mp", cmd);
            if (passwordArgs != null && passwordArgs.length == 2){
                oldPassword = passwordArgs[0];
                newPassword = passwordArgs[1];
            }
        }

        if (cmd.hasOption("me") || cmd.hasOption("modify-email")) {
            String[] emailArgs = ArgParsing.GetUserOptions("me", cmd);
            if (emailArgs != null && emailArgs.length == 2){
                oldEmail = emailArgs[0];
                newEmail = emailArgs[1];
            }
        }

        String platform = ArgParsing.HasOption("pf", cmd) ? ArgParsing.GetOption("pf", cmd) : null;
        String email = ArgParsing.HasOption("e", cmd) ? ArgParsing.GetOption("e", cmd) : null;
        String username = ArgParsing.HasOption("u", cmd) ? ArgParsing.GetOption("u", cmd) : null;
        String password = ArgParsing.HasOption("p", cmd) ? ArgParsing.GetOption("p", cmd) : null;

        HandlingOptions.FilterOptions(args, cmd, platform, email, username, password, oldPassword, newPassword, oldUsername, newUsername, oldEmail, newEmail);
    }
}