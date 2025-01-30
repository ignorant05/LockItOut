package Needed;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlingOptions extends ArgParsing implements OperationsOnDB{
    private static final Logger logger = LoggerFactory.getLogger(HandlingOptions.class);
    protected CommandLine cmd = null ;

    public HandlingOptions(String[] args) {
        super(args);
    }
    private final boolean DBOwnerOption = HasOption("ul", this.getCommandLine()) || HasOption("user-lock", this.getCommandLine());
    private final boolean DBOwnerPasswordOption = HasOption("pl",this.getCommandLine()) || HasOption("password-lock", this.getCommandLine());

    private final boolean HelpOption = HasOption("h",this.getCommandLine()) || HasOption("help", this.getCommandLine());
    private final boolean EmptyOption = HasOption("e",this.getCommandLine()) || HasOption("empty", this.getCommandLine());
    private final boolean AddOperationInitialCondition = HasOption("a", this.getCommandLine()) || HasOption("add",this.getCommandLine()) ;
    private final boolean ShowALLOperationInitialCondition = HasOption("sa", this.getCommandLine()) || HasOption("show-all",this.getCommandLine()) ;
    private final boolean ShowPlatformOperationInitialCondition = HasOption("spf", this.getCommandLine()) || HasOption("show-platform",this.getCommandLine()) ;
    private final boolean ShowCredsOperationInitialCondition = HasOption("sc", this.getCommandLine()) || HasOption("show-credentials",this.getCommandLine()) ;
    private final boolean DeleteOperationInitialCondition = HasOption("d", this.getCommandLine()) || HasOption("delete",this.getCommandLine()) ;

    private final boolean HasPlatformOption = HasOption("pf", this.getCommandLine()) || HasOption("platform",this.getCommandLine()) ;
    private final boolean HasUserNameOption = HasOption("u", this.getCommandLine()) || HasOption("user",this.getCommandLine()) ;
    private final boolean HasPasswordOption = HasOption("p", this.getCommandLine()) || HasOption("password",this.getCommandLine()) ;
    private final boolean HasEmailOption = HasOption("e", this.getCommandLine()) || HasOption("email",this.getCommandLine()) ;

    private final boolean HasModifyUserNameOption= (HasOption("mu",this.getCommandLine()) || HasOption("modify-username",this.getCommandLine()));
    private final boolean HasModifyPasswordOption = (HasOption("mp",this.getCommandLine()) || HasOption("modify-password",this.getCommandLine()));
    private final boolean HasModifyEmailOption = (HasOption("me",this.getCommandLine()) || HasOption("modify-email",this.getCommandLine()));

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

    public static void FilterOptions (String[] args, CommandLine cmd, String platform, String email, String UserName, String password,
                                      String oldPassword, String newPassword, String oldUsername, String newUsername, String oldEmail, String newEmail){

        HandlingOptions Handler = new HandlingOptions(args) ;
        ArgParsing c = new ArgParsing(args) ;

        //OperationsOnDB.CreateDB(cmd);
        OperationsOnDB.CreatePlatformsTable(cmd);
        OperationsOnDB.CreateCredentialsTable(cmd);

        if (Handler.AddOperationFinalCondition){
            OperationsOnDB.InsertNewCredentials(platform, email, UserName, password, cmd);
            logger.info("Insertion Successful");
        }
        else if (Handler.DeleteOperationFinalCondition){
            OperationsOnDB.DeleteCredentials(platform, email, UserName,cmd);
            logger.info("Deletion Successful");
        }
        else if (Handler.ShowAllOperationFinalCondition){
            OperationsOnDB.ShowALL(cmd);
            logger.info("Thesse Are All Available Credentials");
        }
        else if (Handler.ShowPlatformOperationFinalCondition){
            OperationsOnDB.ShowPlatformsTable(cmd);
            logger.info("Thesse Are All Available Platforms");
        }
        else if (Handler.ShowCredsOperationFinalCondition){
            OperationsOnDB.ShowSpecificCreds(platform, email, UserName,cmd);
            logger.info("Thesse Are All Available Credentials");
        }
        else if (Handler.ModifyUserNameFinalCondition) {
            OperationsOnDB.ModifyUsername(platform, email, oldUsername, newUsername,cmd);
        }
        else if (Handler.ModifyPasswordFinalCondition) {
            OperationsOnDB.ModifyPassword(platform, email, UserName, oldPassword, newPassword,cmd);
        }
        else if (Handler.ModifyEmailFinalCondition) {
            OperationsOnDB.ModifyEmail(platform, UserName, oldEmail, newEmail,cmd);
        }
        else if (Handler.HelpOptionFinalCondition){
            OperationsOnDB.Helper();
        }
        else if (Handler.Empty){
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
