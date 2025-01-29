package Needed;
import static Needed.HandlingOptions.cmd;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface OperationsOnDB {
    String DB_URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:mysql://localhost:3306/LockItOut?useSSL=false";
    String DB_USER = ArgParsing.GetOption("ul", cmd);
    String DB_PASSWORD = ArgParsing.GetOption("pl", cmd);

    Logger logger = LoggerFactory.getLogger(OperationsOnDB.class);

    static Connection ConnectToDB(){
        try {
            Connection Connect = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Successfully connected to database");
            return Connect;
        }catch (SQLException e){
            logger.error("Database connection failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    String CreateDBQuery = "CREATE DATABASE IF NOT EXISTS LockedOut";
    String GrantPrevilegesQuery = "GRANT ALL PRIVILEGES ON LockedOut.* TO '?'@'localhost'";
    String FlushQuery = "FLUSH PRIVILEGES";
    static void CreateDB(){
        try (Connection Connect = ConnectToDB();
             PreparedStatement PreparedStatement1 = Connect.prepareStatement(CreateDBQuery) ;
            PreparedStatement PreparedStatement2 = Connect.prepareStatement(GrantPrevilegesQuery);
            PreparedStatement PreparedStatement3 = Connect.prepareStatement(FlushQuery))
        {
            PreparedStatement1.executeUpdate();
            PreparedStatement2.setString(1, DB_USER);
            PreparedStatement2.executeUpdate();
            PreparedStatement3.executeUpdate();
            logger.info("Successfully created database");
        } catch (SQLException e) {
            logger.error("Database creation failed: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    String UseDBQuery = "USE LockedOut";
    String CreatePLatformsTableQuery = "CREATE TABLE IF NOT EXISTS PLATFORMS (PLATFORM TEXT PRIMARY KEY NOT NULL)";
    static void CreatePlatformsTable(){
        try (Connection Connect = ConnectToDB(); Statement Statement = Connect.createStatement()){
            Statement.executeUpdate(UseDBQuery);
            Statement.executeUpdate(CreatePLatformsTableQuery);
            System.out.println("Successfully created PLATFORMS Table");
        }catch (SQLException e){
            logger.error("PLATFORMS table creation failed");
            throw new RuntimeException(e);
        }
    }

    static void InsertIntoPlatformsTable(String PLATFORM){
        CreatePlatformsTable();
        final String InsertIntoPLATFORMSTableQuery = "INSERT IGNORE INTO PLATFORMS (PLATFORM) VALUES (?)";
        try (Connection Connect = ConnectToDB(); PreparedStatement PreparedStatement = Connect.prepareStatement(InsertIntoPLATFORMSTableQuery)){
            PreparedStatement.setString(1, PLATFORM);
            PreparedStatement.executeUpdate();
        }catch (SQLException e){
            logger.error("Insertion failed");
            throw new RuntimeException(e);
        }
    }

    static boolean PlatformExists(String PLATFORM){
        final String GetFromPLATFORMSTableQuery = "SELECT 1 FROM PLATFORMS WHERE PLATFORM = ?";
        try (Connection Connect = ConnectToDB(); PreparedStatement PreparedStatement = Connect.prepareStatement(GetFromPLATFORMSTableQuery)){
            PreparedStatement.setString(1, PLATFORM);
            ResultSet Result = PreparedStatement.executeQuery();
            return Result.next();

        }catch (SQLException e){
            logger.error("Error");
            throw new RuntimeException("Error finding PLATFORM: " + e.getMessage(), e);
        }

    }

    static void DeleteCredentials( String PLATFORM, String EMAIL, String USERNAME){
        String DeleteCredentialsQuery1 ="DELETE FROM Credentials WHERE PLATFORM = ? AND USERNAME = ?";
        String DeleteCredentialsQuery2 ="DELETE FROM Credentials WHERE PLATFORM = ? AND EMAIL = ?";

        if(EMAIL==null && USERNAME==null){
            logger.error("No Matching neither USERNAME nor EMAIL!");
            System.exit(1);
        }

        try (Connection Connect = ConnectToDB();
             PreparedStatement PreparedStatement1 = Connect.prepareStatement(DeleteCredentialsQuery1);
             PreparedStatement PreparedStatement2 = Connect.prepareStatement(DeleteCredentialsQuery2)){
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
            + "PLATFORM TEXT NOT NULL, "
            + "EMAIL TEXT NOT NULL, "
            + "USERNAME TEXT NOT NULL, "
            + "PASSWORD TEXT NOT NULL, "
            + "FOREIGN KEY (PLATFORM) REFERENCES PLATFORMS(PLATFORM) "
            + "ON DELETE CASCADE ON UPDATE CASCADE"
            + ")";
    static void CreateCredentialsTable() {
        try (Connection Connect = ConnectToDB(); Statement Statement = Connect.createStatement()){
            Statement.executeUpdate(CreateCredsTableQuery);
        }catch (SQLException e){
            throw new RuntimeException("Error creating credentials table : "+e.getMessage(), e);
        }
    }

    static void InsertNewCredentials(String PLATFORM, String EMAIL, String USERNAME, String PASSWORD){
        if(PLATFORM == null || EMAIL == null || USERNAME == null || PASSWORD == null) {
            throw new IllegalArgumentException("All credential fields must be non-null");
        }
        if (!PlatformExists(PLATFORM)) {
            InsertIntoPlatformsTable(PLATFORM);
        }
        String InsertNewCredsQuery = "INSERT INTO Credentials (PLATFORM, EMAIL, USERNAME, PASSWORD) VALUES (?,?,?,?)";
        try (Connection Connect = ConnectToDB();PreparedStatement PreparedStatement = Connect.prepareStatement(InsertNewCredsQuery) ){

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
    static void ShowALL (){
        try (Connection Connect = ConnectToDB();PreparedStatement PreparedStatement = Connect.prepareStatement(ShowALLCredsQuery)){
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
            }
            System.out.println("------------------------------------------------------------------------------------------------------------------");

        }catch (SQLException e){
            throw new RuntimeException("Error showing credential table : "+e.getMessage(), e);
        }
    }

    String ShowPlaformsTableQuery = "SELECT * FROM PLATFORMS";
    static void ShowPlatformsTable(){
        try (Connection Connect = ConnectToDB();PreparedStatement PreparedStatement = Connect.prepareStatement(ShowPlaformsTableQuery)){
            ResultSet ResultSet =PreparedStatement.executeQuery();

            System.out.println("--------------------------");
            System.out.println("|        PLATFORM        |");
            System.out.println("--------------------------");

            while (ResultSet.next()) {
                String platform = ResultSet.getString("PLATFORM");
                System.out.printf("|  %-10s  |\n", platform);
            }
            System.out.println("-------------------------");

        }catch (SQLException e){
            throw new RuntimeException("Error showing platforms table : "+e.getMessage(), e);
        }
    }

    static void ShowSpecificCreds(String PLATFORM, String EMAIL, String USERNAME){

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
        try (Connection Connect = ConnectToDB();PreparedStatement PreparedStatement = Connect.prepareStatement(ShowSpecificCredsQuery)){
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
            System.out.println("|        PLATFORM        |                EMAIL                |        USERNAME        |        PASSWORD        |");
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            while (ResultSet.next()) {
                String platform = ResultSet.getString("PLATFORM");
                String email = ResultSet.getString("EMAIL");
                String username = ResultSet.getString("USERNAME");
                String password = ResultSet.getString("PASSWORD");
                System.out.printf("| %-10s | %-40s | %-15s | %-20s%n |\n", platform, email, username, password);
            }
            System.out.println("------------------------------------------------------------------------------------------------------------------");

        }catch (SQLException e){
            throw new RuntimeException("Error showing credentials : "+e.getMessage(), e);
        }
    }

    static void ModifyEmail(String PLATFORM, String USERNAME, String old_EMAIL, String new_EMAIL){
        if (PLATFORM==null || USERNAME == null || old_EMAIL == null || new_EMAIL == null) {
            throw new IllegalArgumentException("Username, old email, and new email must be provided");
        }
        String ModifyEMAILQuery = "UPDATE Credentials SET EMAIL = ? WHERE PLATFORM = ? AND USERNAME = ? AND EMAIL = ?";
        try (Connection Connect = ConnectToDB();
             PreparedStatement PreparedStatement = Connect.prepareStatement(ModifyEMAILQuery))
        {   PreparedStatement.setString(1, new_EMAIL);
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

    static void ModifyUsername(String PLATFORM, String EMAIL, String old_USERNAME, String new_USERNAME){
        if (PLATFORM==null || old_USERNAME == null || EMAIL == null || new_USERNAME == null) {
            throw new IllegalArgumentException("EMAIL, old Username and new USERNAME must be provided");
        }
        final String ModifyUSERNAMEQuery = "UPDATE Credentials SET USERNAME = ? WHERE PLATFORM = ? AND EMAIL = ? AND USERNAME = ?";
        try(Connection Connect = ConnectToDB(); PreparedStatement PreparedStatement = Connect.prepareStatement(ModifyUSERNAMEQuery)){
            PreparedStatement.setString(1, new_USERNAME);
            PreparedStatement.setString(2, PLATFORM);
            PreparedStatement.setString(3, EMAIL);
            PreparedStatement.setString(4, old_USERNAME);
            int ErrorsInRows = PreparedStatement.executeUpdate();

            if (ErrorsInRows > 0) logger.info("Successfully updated USERNAME for EMAIL : " + EMAIL);
            else logger.error("EMAIL : "+EMAIL+" DOES NOT EXIST");

        }catch (SQLException e){
            throw new RuntimeException("Error updating USERNAME : "+e.getMessage(), e);
        }
    }

    static void ModifyPassword(String PLATFORM, String EMAIL, String USERNAME, String old_PASSWORD, String new_PASSWORD){
        if (PLATFORM==null || USERNAME == null || EMAIL == null || old_PASSWORD== null || new_PASSWORD == null) {
            throw new IllegalArgumentException("Username, EMAIL, old password and new password must be provided");
        }
        final String ModifyPASSWORDQuery ="UPDATE Credentials SET PASSWORD = ? WHERE PLATFORM = ? AND EMAIL = ? AND USERNAME = ? AND PASSWORD = ?";
        try(Connection Connect = ConnectToDB(); PreparedStatement PreparedStatement = Connect.prepareStatement(ModifyPASSWORDQuery)){
            PreparedStatement.setString(1, new_PASSWORD);
            PreparedStatement.setString(2, PLATFORM);
            PreparedStatement.setString(3, EMAIL);
            PreparedStatement.setString(4, USERNAME);
            PreparedStatement.setString(5, old_PASSWORD);
            int ErrorsInRows = PreparedStatement.executeUpdate();

            if (ErrorsInRows > 0) logger.info("Successfully updated PASSWORD for EMAIL/USERNAME : " + EMAIL +"/" +USERNAME);
            else logger.error("EMAIL/USERNAME: "+EMAIL+"/"+USERNAME+" DOES NOT EXIST");

        }catch (SQLException e){
            throw new RuntimeException("Error updating PASSWORD : "+e.getMessage(), e);
        }
    }

    String Description = """
    LOCKEDOUT - Credential Management Tool
    
    Usage: java -jar tool.jar -ul <db_user> -pl <db_password> [OPERATION] [ARGUMENTS]
    
    Mandatory Authentication:
      -ul, --user <user>                            Database username
      -pl, --pass <pass>                            Database password
    
    Operations:
      -a,  --add                                    Add new credentials (requires -pf -e -u -p)
      -d,  --delete                                 Delete credentials (requires -pf and either -e or -u)
      -sa, --show-all                               Show all credentials
      -sp, --show-platforms                         List all platforms
      -sc, --show-credentials                       Show credentials for platform (requires -pf and -e/-u)
      -mu, --modify-username <old> <new>            Modify username
      -me, --modify-email <old> <new>               Modify email
      -mp, --modify-password <old> <new>            Modify password
      -e,  --empty                                  Truncate all data
      -h,  --help                                   Show this help

    Arguments:
      -pf, --platform <name>                        Service/platform name (e.g. 'Google')
      -u,  --user <username>                        Account username
      -e,  --email <address>                        Account email
      -p,  --pass <password>                        Account password

    Examples:
      Add credentials:
        java -jar LockItOut.jar -ul root -pl secret -a -pf Google -e user@mail.com -u johndoe -p s3cr3t
      
      Show Gmail credentials:
        -java -jar LockItOut.jar ul root -pl secret -sc -pf Google -e user@mail.com
      
      Change username:
        java -jar LockItOut.jar -ul root -pl secret -mu old_username new_username -pf google -e user@mail.com
    """;
    static void Helper(){
        System.out.println(Description);
    }

    String TruncateCredsTableQuery = "TRUNCATE TABLE Credentials";
    String TruncatePLATFORMSTableQuery = "TRUNCATE TABLE PLATFORMS";
    static void TruncateDB(){
        try (Connection Connect = ConnectToDB();
             PreparedStatement PreparedStatement1 = Connect.prepareStatement(TruncateCredsTableQuery);
             PreparedStatement PreparedStatement2 = Connect.prepareStatement(TruncatePLATFORMSTableQuery))
        {
            PreparedStatement1.executeUpdate();
            PreparedStatement2.executeUpdate();
            logger.info("Successfully truncated PLATFORMS & CREDENTIALS");

        }catch (SQLException e){
            throw new RuntimeException("Error truncating tables : "+e.getMessage(), e);
        }
    }
}
