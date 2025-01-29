package Needed;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlingOptions extends ArgParsing implements OperationsOnDB{
    private static final Logger logger = LoggerFactory.getLogger(HandlingOptions.class);
    protected static CommandLine cmd = null ;

    public HandlingOptions(String[] args, CommandLine cmd) {
        super(args, cmd);
    }
    private final boolean DBOwnerOption = HasOption("ul",cmd) || HasOption("user-locked", cmd);
    private final boolean DBOwnerPasswordOption = HasOption("pl",cmd) || HasOption("password-locked", cmd);

    private final boolean HelpOption = HasOption("h",cmd) || HasOption("help", cmd);
    private final boolean EmptyOption = HasOption("e",cmd) || HasOption("empty", cmd);
    private final boolean AddOperationInitialCondition = HasOption("a", cmd) || HasOption("add",cmd) ;
    private final boolean ShowALLOperationInitialCondition = HasOption("sa", cmd) || HasOption("show-all",cmd) ;
    private final boolean ShowPlatformOperationInitialCondition = HasOption("spf", cmd) || HasOption("show-platform",cmd) ;
    private final boolean ShowCredsOperationInitialCondition = HasOption("sc", cmd) || HasOption("show-credentials",cmd) ;
    private final boolean DeleteOperationInitialCondition = HasOption("d", cmd) || HasOption("delete",cmd) ;

    private final boolean HasPlatformOption = HasOption("pf", cmd) || HasOption("platform",cmd) ;
    private final boolean HasUserNameOption = HasOption("u", cmd) || HasOption("user",cmd) ;
    private final boolean HasPasswordOption = HasOption("p", cmd) || HasOption("password",cmd) ;
    private final boolean HasEmailOption = HasOption("e", cmd) || HasOption("email",cmd) ;

    private final boolean HasModifyUserNameOption= (HasOption("mu",cmd) || HasOption("modify-username",cmd));
    private final boolean HasModifyPasswordOption = (HasOption("mp",cmd) || HasOption("modify-password",cmd));
    private final boolean HasModifyEmailOption = (HasOption("me",cmd) || HasOption("modify-email",cmd));

    private final boolean AddOperationFinalCondition = DBOwnerOption && DBOwnerPasswordOption && AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption &&
            !HasModifyUserNameOption && HasPlatformOption && HasEmailOption && HasUserNameOption && HasPasswordOption && !HelpOption;

    private final boolean DeleteOperationFinalCondition =  DBOwnerOption && DBOwnerPasswordOption && !AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption &&
            !HasModifyUserNameOption && HasPlatformOption && (HasEmailOption || HasUserNameOption) && !HelpOption;

    private final boolean ShowAllOperationFinalCondition = DBOwnerOption && DBOwnerPasswordOption && !AddOperationInitialCondition && ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption &&
            !HasModifyUserNameOption && !HasPlatformOption && !HasEmailOption && !HasUserNameOption && !HasPasswordOption && !HelpOption;

    private final boolean ShowPlatformOperationFinalCondition = DBOwnerOption && DBOwnerPasswordOption && !AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption &&
            !HasModifyUserNameOption && HasPlatformOption && !HasEmailOption && !HasUserNameOption && !HasPasswordOption && !HelpOption;

    private final boolean ShowCredsOperationFinalCondition = DBOwnerOption && DBOwnerPasswordOption && !AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption &&
            !HasModifyUserNameOption && HasPlatformOption && (HasEmailOption || HasUserNameOption) && !HasPasswordOption && !HelpOption;

    private final boolean ModifyUserNameFinalCondition = DBOwnerOption && DBOwnerPasswordOption && !AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption &&
            HasModifyUserNameOption && HasPlatformOption && HasEmailOption && !HasUserNameOption && !HasPasswordOption && !HelpOption;

    private final boolean ModifyPasswordFinalCondition = DBOwnerOption && DBOwnerPasswordOption && !AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && HasModifyPasswordOption &&
            !HasModifyUserNameOption && HasPlatformOption && (HasEmailOption || HasUserNameOption) && !HasPasswordOption && !HelpOption;

    private final boolean ModifyEmailFinalCondition = DBOwnerOption && DBOwnerPasswordOption && !AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && HasModifyEmailOption && !HasModifyPasswordOption &&
            !HasModifyUserNameOption && HasPlatformOption && !HasEmailOption && HasUserNameOption && HasPasswordOption && !HelpOption;

    private final boolean HelpOptionFinalCondition= DBOwnerOption && DBOwnerPasswordOption && ((!AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption &&
            !HasModifyUserNameOption && !HasPlatformOption && !HasEmailOption && !HasUserNameOption && !HasPasswordOption && HelpOption) ||
            (!AddOperationInitialCondition && !ShowALLOperationInitialCondition && !ShowPlatformOperationInitialCondition &&
                    !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption && !HasModifyUserNameOption &&
                    !HasPlatformOption && !HasEmailOption && !HasUserNameOption && !HasPasswordOption && !HelpOption)) ;

    private final boolean Empty = DBOwnerOption && DBOwnerPasswordOption && !AddOperationInitialCondition && !ShowALLOperationInitialCondition &&
            !ShowPlatformOperationInitialCondition && !ShowCredsOperationInitialCondition && !DeleteOperationInitialCondition && !HasModifyEmailOption && !HasModifyPasswordOption &&
            !HasModifyUserNameOption && !HasPlatformOption && !HasEmailOption && !HasUserNameOption && !HasPasswordOption && !HelpOption && EmptyOption;

    public static void FilterOptions (String[] args, String platform, String email, String UserName, String password, CommandLine cmd){

        HandlingOptions Handler = new HandlingOptions(args, cmd) ;

        OperationsOnDB.CreateDB();
        OperationsOnDB.CreatePlatformsTable();
        OperationsOnDB.CreateCredentialsTable();

        if (Handler.AddOperationFinalCondition){
            OperationsOnDB.InsertNewCredentials(platform, email, UserName, password);
            logger.info("Insertion Successful");
        }
        else if (Handler.DeleteOperationFinalCondition){
            OperationsOnDB.DeleteCredentials(platform, email, UserName);
            logger.info("Deletion Successful");
        }
        else if (Handler.ShowAllOperationFinalCondition){
            OperationsOnDB.ShowALL();
            logger.info("Thesse Are All Available Credentials");
        }
        else if (Handler.ShowPlatformOperationFinalCondition){
            OperationsOnDB.ShowPlatformsTable();
            logger.info("Thesse Are All Available Platforms");
        }
        else if (Handler.ShowCredsOperationFinalCondition){
            OperationsOnDB.ShowSpecificCreds(platform, email, UserName);
            logger.info("Thesse Are All Available Credentials");
        }
        else if (Handler.ModifyUserNameFinalCondition) {
            String[] usernameArgs = cmd.getOptionValues("mu");
            String oldUsername = usernameArgs[0];
            String newUsername = usernameArgs[1];
            OperationsOnDB.ModifyUsername(platform, email, oldUsername, newUsername);
        }
        else if (Handler.ModifyPasswordFinalCondition) {
            String[] passwordArgs = cmd.getOptionValues("mp");
            String oldPassword = passwordArgs[0];
            String newPassword = passwordArgs[1];
            OperationsOnDB.ModifyPassword(platform, email, UserName, oldPassword, newPassword);
        }
        else if (Handler.ModifyEmailFinalCondition) {
            String[] emailArgs = cmd.getOptionValues("me");
            String oldEmail = emailArgs[0];
            String newEmail = emailArgs[1];
            OperationsOnDB.ModifyEmail(platform, UserName, oldEmail, newEmail);
        }
        else if (Handler.HelpOptionFinalCondition){
            OperationsOnDB.Helper();
        }
        else if (Handler.Empty){
            OperationsOnDB.TruncateDB();
            logger.info("DataBase is Empty");
        }
        else {
            logger.error("Invalid Options");
            OperationsOnDB.Helper();
            System.exit(-1);
        }

    }
}
