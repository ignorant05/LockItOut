package Needed;


import java.sql.*;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface OperationsOnDB {

    String DB_URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:mysql://localhost:3306/?useSSL=false";
    static String GetDBName(CommandLine cmd) {
        return ArgParsing.GetOption("db", cmd);
    }
    static String getDbUser(CommandLine cmd) {
        return ArgParsing.GetOption("ul", cmd);
    }

    static String getDbPassword(CommandLine cmd) {
        return ArgParsing.GetOption("pl", cmd);
    }

    Logger logger = LoggerFactory.getLogger(OperationsOnDB.class);

    static Connection ConnectToDB(CommandLine cmd) {
        try {
            String DB_USER = getDbUser(cmd);
            String DB_PASSWORD = getDbPassword(cmd);
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            logger.error("Connection failed: ");
            throw new RuntimeException(e);
        }
    }

    static void CreateDB(CommandLine cmd){
        String CreateDBQuery = "CREATE DATABASE IF NOT EXISTS "+GetDBName(cmd);
        try (Connection Connect = ConnectToDB(cmd);
             PreparedStatement PreparedStatement1 = Connect.prepareStatement(CreateDBQuery) ;
            Statement Statement = Connect.createStatement();
            PreparedStatement PreparedStatement3 = Connect.prepareStatement("FLUSH PRIVILEGES"))
        {
            PreparedStatement1.executeUpdate();
            Statement.executeUpdate("GRANT ALL PRIVILEGES ON `" + GetDBName(cmd) + "`.* TO '" + getDbUser(cmd) + "'@'localhost'");
            PreparedStatement3.executeUpdate();
        } catch (SQLException e) {
            logger.error("Database creation failed: ");
            throw new RuntimeException(e);
        }
    }
    String CreatePLatformsTableQuery = "CREATE TABLE IF NOT EXISTS PLATFORMS ("
            + "PLATFORM VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE"
            + ") ENGINE=InnoDB";
    static void CreatePlatformsTable(CommandLine cmd){
        try (Connection Connect = ConnectToDB(cmd); Statement Statement = Connect.createStatement()){
            Statement.executeUpdate( "USE "+GetDBName(cmd));
            Statement.executeUpdate(CreatePLatformsTableQuery);
        }catch (SQLException e){
            logger.error("PLATFORMS table creation failed");
            throw new RuntimeException(e);
        }
    }

    static void InsertIntoPlatformsTable(String PLATFORM,CommandLine cmd){
        CreatePlatformsTable(cmd);
        final String InsertIntoPLATFORMSTableQuery = "INSERT IGNORE INTO PLATFORMS (PLATFORM) VALUES (?)";
        try (Connection Connect = ConnectToDB(cmd); PreparedStatement PreparedStatement = Connect.prepareStatement(InsertIntoPLATFORMSTableQuery)){
            PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            PreparedStatement.setString(1, PLATFORM);
            PreparedStatement.executeUpdate();
        }catch (SQLException e){
            logger.error("Insertion failed");
            throw new RuntimeException(e);
        }
    }

    static boolean PlatformExists(String PLATFORM, CommandLine cmd){
        final String GetFromPLATFORMSTableQuery = "SELECT 1 FROM PLATFORMS WHERE PLATFORM = ?";
        try (Connection Connect = ConnectToDB(cmd); PreparedStatement PreparedStatement = Connect.prepareStatement(GetFromPLATFORMSTableQuery)){
            PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            PreparedStatement.setString(1, PLATFORM);
            ResultSet Result = PreparedStatement.executeQuery();
            return Result.next();

        }catch (SQLException e){
            logger.error("Error");
            throw new RuntimeException("Error finding PLATFORM: " + e.getMessage(), e);
        }

    }

    static void DeleteCredentials( String PLATFORM, String EMAIL, String USERNAME, CommandLine cmd){
        String DeleteCredentialsQuery1 ="DELETE FROM Credentials WHERE PLATFORM = ? AND USERNAME = ?";
        String DeleteCredentialsQuery2 ="DELETE FROM Credentials WHERE PLATFORM = ? AND EMAIL = ?";

        if(EMAIL==null && USERNAME==null){
            logger.error("No Matching neither USERNAME nor EMAIL!");
            System.exit(1);
        }

        try (Connection Connect = ConnectToDB(cmd);
             PreparedStatement PreparedStatement1 = Connect.prepareStatement(DeleteCredentialsQuery1);
             PreparedStatement PreparedStatement2 = Connect.prepareStatement(DeleteCredentialsQuery2)){
            PreparedStatement1.executeUpdate( "USE "+GetDBName(cmd));
            PreparedStatement1.setString(1, PLATFORM);
            PreparedStatement2.setString(1, PLATFORM);
            int ErrorsInRows = 0;
            if (EMAIL == null) {
                PreparedStatement1.setString(2, USERNAME);
                ErrorsInRows = PreparedStatement1.executeUpdate();

            } else {
                PreparedStatement2.setString(2, EMAIL);
                ErrorsInRows = PreparedStatement2.executeUpdate();
            }

            if (ErrorsInRows>0) logger.info("Successfully deleted");
            else logger.error("Failed to delete credentials");

        }catch (SQLException e){
            throw new RuntimeException("Error deleting credentials: " + e.getMessage(), e);
        }
    }

    String CreateCredsTableQuery = "CREATE TABLE IF NOT EXISTS Credentials ("
            + "ID INT AUTO_INCREMENT PRIMARY KEY, "
            + "PLATFORM VARCHAR(255) NOT NULL, "
            + "EMAIL VARCHAR(511) NOT NULL, "
            + "USERNAME VARCHAR(255) NOT NULL, "
            + "PASSWORD VARCHAR(255) NOT NULL, "
            + "FOREIGN KEY (PLATFORM) REFERENCES PLATFORMS(PLATFORM) "
            + "ON DELETE CASCADE ON UPDATE CASCADE"
            + ") ENGINE=InnoDB";
    static void CreateCredentialsTable(CommandLine cmd) {
        try (Connection Connect = ConnectToDB(cmd); Statement Statement = Connect.createStatement()){
            Statement.executeUpdate( "USE "+GetDBName(cmd));
            Statement.executeUpdate(CreateCredsTableQuery);
        }catch (SQLException e){
            throw new RuntimeException("Error creating credentials table : "+e.getMessage(), e);
        }
    }

    static void InsertNewCredentials(String PLATFORM, String EMAIL, String USERNAME, String PASSWORD,CommandLine cmd){
        if(PLATFORM == null || EMAIL == null || USERNAME == null || PASSWORD == null) {
            throw new IllegalArgumentException("All credential fields must be non-null");
        }
        if (!PlatformExists(PLATFORM,cmd)) {
            InsertIntoPlatformsTable(PLATFORM,cmd);
        }
        String InsertNewCredsQuery = "INSERT INTO Credentials (PLATFORM, EMAIL, USERNAME, PASSWORD) VALUES (?,?,?,?)";
        try (Connection Connect = ConnectToDB(cmd);PreparedStatement PreparedStatement = Connect.prepareStatement(InsertNewCredsQuery) ){
            PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            PreparedStatement.setString(1, PLATFORM);
            PreparedStatement.setString(2, EMAIL);
            PreparedStatement.setString(3, USERNAME);
            PreparedStatement.setString(4, PASSWORD);
            int ErrorsInRows = PreparedStatement.executeUpdate();

            if (ErrorsInRows>0) logger.info("Successfully Added new credentials");
            else logger.error("Failed to Add new credentials");

        }catch (SQLException e){
            throw new RuntimeException("Error adding credentials to table :" + e.getMessage(), e);
        }
    }

    String ShowALLCredsQuery = "SELECT * FROM Credentials";
    static void ShowALL (CommandLine cmd){
        try (Connection Connect = ConnectToDB(cmd);PreparedStatement PreparedStatement = Connect.prepareStatement(ShowALLCredsQuery)){
            PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            ResultSet ResultSet = PreparedStatement.executeQuery();

            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.println("|        PLATFORM        |                EMAIL                |        USERNAME        |        PASSWORD        |");
            System.out.println("------------------------------------------------------------------------------------------------------------------");

            while (ResultSet.next()) {
                String platform = ResultSet.getString("PLATFORM");
                String email = ResultSet.getString("EMAIL");
                String username = ResultSet.getString("USERNAME");
                String password = ResultSet.getString("PASSWORD");
                System.out.printf("| %-10s | %-40s | %-15s | %-20s%n |\n", platform, email, username, password);
                System.out.println("------------------------------------------------------------------------------------------------------------------");
            }

        }catch (SQLException e){
            throw new RuntimeException("Error showing credential table : "+e.getMessage(), e);
        }
    }

    String ShowPlaformsTableQuery = "SELECT * FROM PLATFORMS";
    static void ShowPlatformsTable(CommandLine cmd){
        try (Connection Connect = ConnectToDB(cmd);PreparedStatement PreparedStatement = Connect.prepareStatement(ShowPlaformsTableQuery)){
            PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            ResultSet ResultSet =PreparedStatement.executeQuery();

            System.out.println("--------------------------");
            System.out.println("|        PLATFORM        |");
            System.out.println("--------------------------");

            while (ResultSet.next()) {
                String platform = ResultSet.getString("PLATFORM");
                System.out.printf("|  %-20s  |\n", platform);
            }
            System.out.println("-------------------------");

        }catch (SQLException e){
            throw new RuntimeException("Error showing platforms table : "+e.getMessage(), e);
        }
    }

    static void ShowSpecificCreds(String PLATFORM, String EMAIL, String USERNAME,CommandLine cmd){
        if (PLATFORM == null){
            logger.error ("PLATFORM is null");
            System.exit(-1);
        }
        if (USERNAME == null && EMAIL==null){
            logger.error ("USERNAME and EMAIL are both null ");
            System.exit(-1);
        }
        String ShowSpecificCredsQuery ;

        if (USERNAME != null && EMAIL != null) {
            ShowSpecificCredsQuery = "SELECT * FROM Credentials WHERE USERNAME = ? AND EMAIL = ?";
        } else if (USERNAME != null) {
            ShowSpecificCredsQuery = "SELECT * FROM Credentials WHERE USERNAME = ?";
        } else {
            ShowSpecificCredsQuery = "SELECT * FROM Credentials WHERE EMAIL = ?";
        }
        try (Connection Connect = ConnectToDB(cmd);PreparedStatement PreparedStatement = Connect.prepareStatement(ShowSpecificCredsQuery)){
            PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            if (USERNAME != null && EMAIL != null) {
                PreparedStatement.setString(1, USERNAME);
                PreparedStatement.setString(2, EMAIL);
            } else if (USERNAME != null) {
                PreparedStatement.setString(1, USERNAME);
            } else {
                PreparedStatement.setString(1, EMAIL);
            }
            ResultSet ResultSet = PreparedStatement.executeQuery();

            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.println("|        PLATFORM        |                 EMAIL                 |        USERNAME        |        PASSWORD        |");
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            while (ResultSet.next()) {
                String platform = ResultSet.getString("PLATFORM");
                String email = ResultSet.getString("EMAIL");
                String username = ResultSet.getString("USERNAME");
                String password = ResultSet.getString("PASSWORD");
                System.out.printf("|  %-20s  |  %-40s  |  %-25s  |  %-30s  |\n", platform, email, username, password);
                System.out.println("------------------------------------------------------------------------------------------------------------------");
            }

        }catch (SQLException e){
            throw new RuntimeException("Error showing credentials : "+e.getMessage(), e);
        }
    }

    static void ModifyEmail(String PLATFORM, String USERNAME, String old_EMAIL, String new_EMAIL,CommandLine cmd){
        if (PLATFORM==null || USERNAME == null || old_EMAIL == null || new_EMAIL == null) {
            throw new IllegalArgumentException("Username, old email, and new email must be provided");
        }
        String ModifyEMAILQuery = "UPDATE Credentials SET EMAIL = ? WHERE PLATFORM = ? AND USERNAME = ? AND EMAIL = ?";
        try (Connection Connect = ConnectToDB(cmd);
             PreparedStatement PreparedStatement = Connect.prepareStatement(ModifyEMAILQuery))
        {   PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            PreparedStatement.setString(1, new_EMAIL);
            PreparedStatement.setString(2, PLATFORM);
            PreparedStatement.setString(3, USERNAME);
            PreparedStatement.setString(4, old_EMAIL);
            int ErrorsInRows = PreparedStatement.executeUpdate();

            if (ErrorsInRows > 0) logger.info("Successfully updated EMAIL for USERNAME: " + USERNAME);
            else logger.error("USERNAME : "+USERNAME+" DOES NOT EXIST");

        }catch (SQLException e){
            logger.error("Email update failed", e);
            throw new RuntimeException("Error updating EMAIL : "+e.getMessage(), e);
        }
    }

    static void ModifyUsername(String PLATFORM, String EMAIL, String old_USERNAME, String new_USERNAME,CommandLine cmd){
        if (PLATFORM==null || old_USERNAME == null || EMAIL == null || new_USERNAME == null) {
            throw new IllegalArgumentException("EMAIL, old Username and new USERNAME must be provided");
        }
        final String ModifyUSERNAMEQuery = "UPDATE Credentials SET USERNAME = ? WHERE PLATFORM = ? AND EMAIL = ? AND USERNAME = ?";
        try(Connection Connect = ConnectToDB(cmd); PreparedStatement PreparedStatement = Connect.prepareStatement(ModifyUSERNAMEQuery)){
            PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            PreparedStatement.setString(1, new_USERNAME);
            PreparedStatement.setString(2, PLATFORM);
            PreparedStatement.setString(3, EMAIL);
            PreparedStatement.setString(4, old_USERNAME);
            int ErrorsInRows = PreparedStatement.executeUpdate();

            if (ErrorsInRows > 0) logger.info("Successfully updated USERNAME for EMAIL : " + EMAIL);
            else logger.error("EMAIL : "+EMAIL+" DOES NOT EXIST");
            OperationsOnDB.DeleteCredentials(PLATFORM,EMAIL,new_USERNAME,cmd);

        }catch (SQLException e){
            throw new RuntimeException("Error updating USERNAME : "+e.getMessage(), e);
        }
    }

    static void ModifyPassword(String PLATFORM, String EMAIL, String USERNAME, String old_PASSWORD, String new_PASSWORD,CommandLine cmd){
        if (PLATFORM==null || USERNAME == null || EMAIL == null || old_PASSWORD== null || new_PASSWORD == null) {
            throw new IllegalArgumentException("Username, EMAIL, old password and new password must be provided");
        }
        final String ModifyPASSWORDQuery ="UPDATE Credentials SET PASSWORD = ? WHERE PLATFORM = ? AND EMAIL = ? AND USERNAME = ? AND PASSWORD = ?";
        try(Connection Connect = ConnectToDB(cmd); PreparedStatement PreparedStatement = Connect.prepareStatement(ModifyPASSWORDQuery)){
            PreparedStatement.executeUpdate( "USE "+GetDBName(cmd));
            PreparedStatement.setString(1, new_PASSWORD);
            PreparedStatement.setString(2, PLATFORM);
            PreparedStatement.setString(3, EMAIL);
            PreparedStatement.setString(4, USERNAME);
            PreparedStatement.setString(5, old_PASSWORD);
            int ErrorsInRows = PreparedStatement.executeUpdate();

            if (ErrorsInRows > 0) logger.info("Successfully updated PASSWORD for EMAIL/USERNAME : " + EMAIL +"/" +USERNAME);
            else logger.error("EMAIL/USERNAME: "+EMAIL+"/"+USERNAME+" DOES NOT EXIST");
            OperationsOnDB.DeleteCredentials(PLATFORM,EMAIL,USERNAME,cmd);

        }catch (SQLException e){
            throw new RuntimeException("Error updating PASSWORD : "+e.getMessage(), e);
        }
    }

    String Description = """
            LockItOut - Credential Management Tool
            
            Usage: java -jar LockItOut.jar -db <database> -ul <db_user> -pl <db_password> [OPERATION] [ARGUMENTS]
            
            Mandatory Authentication:
              -db, --database <database>       Specify the database name.
              -ul, --user <db_user>            Database username.
              -pl, --pass <db_password>        Database password.
            
            Operations:
              -a,  --add                       Add new credentials (requires -pf -e -u -p).
              -d,  --delete                    Delete credentials (requires -pf and either -e or -u).
              -sa, --show-all                  Show all credentials stored in the database.
              -spf, --show-platforms           List all platforms in the database.
              -sc, --show-credentials          Show credentials for a specific platform (requires -pf and -e/-u).
              -mu, --modify-username <old> <new> Modify username.
              -me, --modify-email <old> <new>   Modify email.
              -mp, --modify-password <old> <new> Modify password.
              -t,  --empty                     Delete all stored credentials.
              -h,  --help                      Display this help message.
            
            Arguments:
              -pf, --platform <name>           Specify the platform/service name.
              -u,  --user <username>           Specify the username for an account.
              -e,  --email <email>             Specify the email associated with an account.
              -p,  --pass <password>           Specify the password for an account.
            
            Examples:
              Add credentials:
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -ul root -pl secret -db mydb -a -pf Google -e user@gmail.com -u johndoe -p s3cr3t
                
              Delete credentials:
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -ul root -pl secret -db mydb -d -pf Google -u johndoe
                
              Show all credentials:
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -ul root -pl secret -db mydb -sa
                
              Show credentials for Google:
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -db myDB -ul admin -pl secret -sc -pf Google -e user@mail.com
                
              Show all platforms:
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -ul root -pl secret -db mydb -spf
                
              Change email :
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -ul root -pl secret -db mydb -me old@mail.com new@mail.com -pf Google -u johndoe
              
              Change username:
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -ul root -pl secret -db mydb -mu old_user new_user -pf Google -e user@gmail.com
            
              Change password:
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -ul root -pl secret -db mydb -mp old_pass new_pass -pf Google -e user@gmail.com -u johndoe
                
              Truncate database:
                java -jar out/artifacts/LockedOut_jar/LockedOut.jar -ul root -pl secret -db mydb -t
    """;
    static void Helper(){
        System.out.println(Description);
    }

    String TruncateCredsTableQuery = "TRUNCATE TABLE Credentials";
    String TruncatePLATFORMSTableQuery = "TRUNCATE TABLE PLATFORMS";
    static void TruncateDB(CommandLine cmd){
        try (Connection Connect = ConnectToDB(cmd);
             PreparedStatement PreparedStatement1 = Connect.prepareStatement(TruncateCredsTableQuery);
             PreparedStatement PreparedStatement2 = Connect.prepareStatement(TruncatePLATFORMSTableQuery))
        {   PreparedStatement1.executeUpdate("USE "+GetDBName(cmd));
            PreparedStatement1.executeUpdate();
            PreparedStatement2.executeUpdate();
            logger.info("Successfully truncated PLATFORMS & CREDENTIALS");

        }catch (SQLException e){
            throw new RuntimeException("Error truncating tables : "+e.getMessage(), e);
        }
    }
}
