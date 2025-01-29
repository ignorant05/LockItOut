package Needed;

import org.apache.commons.cli.* ;

public class ArgParsing {

    private CommandLine cmd;
    private final Options Options = new Options();
    protected OptionGroup Operation = new OptionGroup();

    public ArgParsing(String[] args, CommandLine cmd){
        CommandLineParser Parser = new DefaultParser();
        HelpFormatter Formatter = new HelpFormatter();

        Option DBUser = new Option("ul", "user", true,"parses db user to give you access");
        Option DBPass = new Option("pl", "pass", true,"parses the db password to give you access");
        Option AddCredentials = new Option("a", "add", false,"Add new credentials to database");
        Option DeleteCredentials = new Option("d", "delete", false, "Delete credentials from database");
        Option ModifyUserName = new Option("mu", "modify_username", false, "Modify username");
        Option ModifyPassword = new Option("mp", "modify_password", false, "Modify password");
        Option ModifyEmail = new Option("me", "modify_email", false, "Modify email");
        Option GetCredentials = new Option ("s", "show", false,"Get credentials from database");
        Option GetAllCredentials = new Option ("sa", "show-all", false,"Get All credentials from database");
        Option GetAllPlatforms = new Option ("sp", "show_platforms", false,"Get All platforms from database");
        Option Help = new Option("h", "help", false, "show help window");
        Option Empty = new Option("e", "empty", false,"Truncate database");

        Option Platform = new Option ("pf", "platform", true, "Parse platform");
        Option UserName = new Option("u", "user", true, "Parse Username ");
        Option Password = new Option("p", "pass", true,"Parse password");
        Option email = new Option ("e", "email", true, "Parse email");

        Operation.addOption(AddCredentials);
        Operation.addOption(DeleteCredentials);
        Operation.addOption(GetCredentials);
        Operation.addOption(GetAllCredentials);
        Operation.addOption(GetAllPlatforms);
        Operation.addOption(ModifyUserName);
        Operation.addOption(ModifyPassword);
        Operation.addOption(ModifyEmail);
        Operation.addOption(GetCredentials);
        Operation.addOption(Help);
        Operation.addOption(Empty);
        Operation.setRequired(true);

        DBUser.setRequired(true);
        DBPass.setRequired(true);

        ModifyUserName.setArgs(2);
        ModifyEmail.setArgs(2);
        ModifyPassword.setArgs(2);

        Options.addOptionGroup(Operation);
        Options.addOption(Help);
        Options.addOption(DBUser);
        Options.addOption(DBPass);
        Options.addOption(Platform);
        Options.addOption(UserName);
        Options.addOption(Password);
        Options.addOption(email);

        try {
            this.cmd = Parser.parse(Options, args);
        }catch (ParseException a ){
            System.out.println(a.getLocalizedMessage());
            Formatter.printHelp("Locked", Options);
            System.exit(0);
        }
    }

    public static boolean HasOption (String opt, CommandLine cmd){
        return cmd.hasOption(opt);
    }

    public static String GetOption (String opt, CommandLine cmd){
        return cmd.getOptionValue(opt);
    }

    public CommandLine getCommandLine() {
        return this.cmd;
    }
}
