package Needed;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlingOptions implements OperationsOnDB{
    private static Logger logger = LoggerFactory.getLogger(HandlingOptions.class);
    private CommandLine cmd;

    public HandlingOptions(CommandLine cmd) {
        this.cmd = cmd;
    }
    private boolean HasOption(String opt) {
        return cmd.hasOption(opt);
    }


    private boolean DBNameOption() {
        return( HasOption("db") || HasOption("database"));
    }
    private boolean DBOwnerOption() {
        return HasOption("ul") || HasOption("user-lock");
    }
    private boolean DBOwnerPasswordOption() {
        return HasOption("pl") || HasOption("password-lock");
    }

    private boolean HelpOption() {
        return HasOption("h") || HasOption("help");
    }
    private boolean EmptyOption() {
        return HasOption("e") || HasOption("empty");
    }
    private boolean AddOperationInitialCondition() {
        return HasOption("a") || HasOption("add");
    }
    private boolean ShowALLOperationInitialCondition() {
        return HasOption("sa") || HasOption("show-all");
    }
    private boolean ShowPlatformOperationInitialCondition() {
        return HasOption("spf") || HasOption("show-platforms");
    }
    private boolean ShowCredsOperationInitialCondition() {
        return HasOption("sc") || HasOption("show-credentials");
    }
    private boolean DeleteOperationInitialCondition() {
        return HasOption("d") || HasOption("delete");
    }

    private boolean HasPlatformOption() {
        return HasOption("pf") || HasOption("platform");
    }
    private boolean HasUserNameOption() {
        return HasOption("u") || HasOption("user");
    }
    private boolean HasPasswordOption() {
        return HasOption("p") || HasOption("password");
    }
    private boolean HasEmailOption() {
        return HasOption("e") || HasOption("email");
    }

    private boolean HasModifyUserNameOption() {
        return (HasOption("mu") || HasOption("modify-username"));
    }
    private boolean HasModifyPasswordOption() {
        return (HasOption("mp") || HasOption("modify-password"));
    }
    private boolean HasModifyEmailOption() {
        return (HasOption("me") || HasOption("modify-email"));
    }

    private boolean AddOperationFinalCondition() {
        return DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption() && AddOperationInitialCondition() && !ShowALLOperationInitialCondition() &&
                !ShowPlatformOperationInitialCondition() && !ShowCredsOperationInitialCondition() && !DeleteOperationInitialCondition() && !HasModifyEmailOption() && !HasModifyPasswordOption() &&
                !HasModifyUserNameOption() && HasPlatformOption () && HasEmailOption() && HasUserNameOption() && HasPasswordOption() && !HelpOption() ;
    }

    private boolean DeleteOperationFinalCondition() {
        return DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption() && !AddOperationInitialCondition() && !ShowALLOperationInitialCondition() &&
                !ShowPlatformOperationInitialCondition() && !ShowCredsOperationInitialCondition() && DeleteOperationInitialCondition() && !HasModifyEmailOption() && !HasModifyPasswordOption() &&
                !HasModifyUserNameOption() && HasPlatformOption() && (HasEmailOption() || HasUserNameOption() && !HelpOption()) ;
    }
    private boolean ShowAllOperationFinalCondition() {
        return (DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption()
                && !AddOperationInitialCondition()
                && ShowALLOperationInitialCondition()
                && !ShowCredsOperationInitialCondition()
                && !DeleteOperationInitialCondition()
                && !HelpOption()) ;
    }

    private boolean ShowPlatformOperationFinalCondition() {
        return (DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption ()
                && !AddOperationInitialCondition()
                && !ShowALLOperationInitialCondition()
                && ShowPlatformOperationInitialCondition()
                && !ShowCredsOperationInitialCondition()
                && !DeleteOperationInitialCondition()
                && !HelpOption());
    }

    private boolean ShowCredsOperationFinalCondition() {
        return (DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption()
                && !AddOperationInitialCondition()
                && !ShowALLOperationInitialCondition()
                && !ShowPlatformOperationInitialCondition()
                && ShowCredsOperationInitialCondition()
                && !DeleteOperationInitialCondition()
                && !HasModifyEmailOption()
                && !HasModifyPasswordOption()
                && !HasModifyUserNameOption()
                && HasPlatformOption()
                && (HasEmailOption() || HasUserNameOption())
                && !HasPasswordOption()
                && !HelpOption());
    }

    private boolean ModifyUserNameFinalCondition() {
        return DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption() && !AddOperationInitialCondition() && !ShowALLOperationInitialCondition() &&
                !ShowPlatformOperationInitialCondition() && !ShowCredsOperationInitialCondition() && !DeleteOperationInitialCondition() && !HasModifyEmailOption() && !HasModifyPasswordOption() &&
                HasModifyUserNameOption() && HasPlatformOption() && HasEmailOption() && !HasPasswordOption() && !HelpOption() ;
    }
    private boolean ModifyPasswordFinalCondition () {
        return DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption() && !AddOperationInitialCondition() && !ShowALLOperationInitialCondition() &&
                !ShowPlatformOperationInitialCondition() && !ShowCredsOperationInitialCondition() && !DeleteOperationInitialCondition() && !HasModifyEmailOption() && HasModifyPasswordOption() &&
                !HasModifyUserNameOption() && HasPlatformOption() && (HasEmailOption() || HasUserNameOption()) && !HasPasswordOption() && !HelpOption ();
    }
    private boolean ModifyEmailFinalCondition () {
        return DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption() && !AddOperationInitialCondition() && !ShowALLOperationInitialCondition() &&
                !ShowPlatformOperationInitialCondition() && !ShowCredsOperationInitialCondition() && !DeleteOperationInitialCondition() && HasModifyEmailOption() && !HasModifyPasswordOption() &&
                !HasModifyUserNameOption() && HasPlatformOption()  && HasUserNameOption() && !HelpOption();
    }

    private boolean HelpOptionFinalCondition() {
        return DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption() && ((!AddOperationInitialCondition() && !ShowALLOperationInitialCondition() &&
                !ShowPlatformOperationInitialCondition() && !ShowCredsOperationInitialCondition() && !DeleteOperationInitialCondition() && !HasModifyEmailOption() && !HasModifyPasswordOption() &&
                !HasModifyUserNameOption() && !HasPlatformOption() && !HasEmailOption() && !HasUserNameOption() && !HasPasswordOption() && HelpOption()) ||
                (!AddOperationInitialCondition() && !ShowALLOperationInitialCondition() && !ShowPlatformOperationInitialCondition() && !ShowCredsOperationInitialCondition() && !DeleteOperationInitialCondition() && !HasModifyEmailOption() && !HasModifyPasswordOption() && !HasModifyUserNameOption() && !HasPlatformOption() && !HasEmailOption() && !HasUserNameOption() && !HasPasswordOption() && !HelpOption()));
    }

    private boolean Empty() {
        return DBNameOption() && DBOwnerOption() && DBOwnerPasswordOption() && !AddOperationInitialCondition() && !ShowALLOperationInitialCondition() &&
                !ShowPlatformOperationInitialCondition() && !ShowCredsOperationInitialCondition() && !DeleteOperationInitialCondition() && !HasModifyEmailOption() && !HasModifyPasswordOption() &&
                !HasModifyUserNameOption() && !HasPlatformOption() && !HasEmailOption() && !HasUserNameOption() && !HasPasswordOption() && !HelpOption() && EmptyOption();
    }

    public static void FilterOptions(String[] args, CommandLine cmd, String platform, String email, String UserName, String password,
                                     String oldPassword, String newPassword, String oldUsername, String newUsername, String oldEmail, String newEmail) {
        HandlingOptions Handler = new HandlingOptions(cmd);

        OperationsOnDB.CreateDB(cmd);
        OperationsOnDB.CreatePlatformsTable(cmd);
        OperationsOnDB.CreateCredentialsTable(cmd);

        if (Handler.AddOperationFinalCondition()){
            OperationsOnDB.InsertNewCredentials(platform, email, UserName, password,cmd);
            logger.info("Insertion Successful");
        }
        else if (Handler.DeleteOperationFinalCondition()){
            OperationsOnDB.DeleteCredentials(platform, email, UserName,cmd);
            logger.info("Deletion Successful");
        }
        else if (Handler.ShowAllOperationFinalCondition()){
            OperationsOnDB.ShowALL(cmd);
            logger.info("Thesse Are All Available Credentials");
        }
        else if (Handler.ShowPlatformOperationFinalCondition()){
            OperationsOnDB.ShowPlatformsTable(cmd);
            logger.info("Thesse Are All Available Platforms");
        }
        else if (Handler.ShowCredsOperationFinalCondition()){
            OperationsOnDB.ShowSpecificCreds(platform, email, UserName,cmd);
            logger.info("Thesse Are All Available Credentials");
        }
        else if (Handler.ModifyUserNameFinalCondition()) {
            OperationsOnDB.ModifyUsername(platform, email, oldUsername, newUsername,cmd);
        }
        else if (Handler.ModifyPasswordFinalCondition()) {
            OperationsOnDB.ModifyPassword(platform, email, UserName, oldPassword, newPassword,cmd);
        }
        else if (Handler.ModifyEmailFinalCondition()) {
            OperationsOnDB.ModifyEmail(platform, UserName, oldEmail, newEmail,cmd);
        }
        else if (Handler.HelpOptionFinalCondition()){
            OperationsOnDB.Helper();
        }
        else if (Handler.Empty()){
            OperationsOnDB.TruncateDB(cmd);
            logger.info("DataBase is Empty");
        }
        else {
            logger.error("Invalid Options");
            OperationsOnDB.Helper();
            System.exit(-1);
        }

    }
}
