package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.database.UserDataType;
import com.github.sawors.werewolfgame.database.UserId;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // |====================================[GIT GUD]=====================================|
    // |                     Reminder for the newbie I'm in SQL :                         |
    // | -> Set  : INSERT into [table]([column]) VALUES([value])                          |
    // | -> Get  : SELECT [column] FROM [table] // WHERE [condition]=[something]          |
    // | -> Edit : UPDATE [table] SET [column] = [value] // WHERE [condition]=[something] |
    // | -> Del  : DELETE FROM [table] WHERE [condition]=[something]                      |
    // |==================================================================================|
    
    public static void connectInit(){
        try(Connection co = connect()){
            //  Init "Users" table
            co.createStatement().execute(linkingDatabaseInitQuery());
            co.createStatement().execute("INSERT into Users VALUES('"+new UserId()+"', 'Sawors', 'f96b1fab-2391-4c41-b6aa-56e6e91950fd', '315237447065927691', '','')");
            
        } catch (
                SQLException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
    //sawors01
    private static Connection connect(){
        Connection co;
        try{
            String target = "jdbc:sqlite:"+ Main.getDbFile().getCanonicalFile();
            co = DriverManager.getConnection(target);
            return co;
        } catch (
                IOException |
                SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String linkingDatabaseInitQuery(){
        return "CREATE TABLE IF NOT EXISTS Users ("
                + UserDataType.USERID+" text NOT NULL PRIMARY KEY,"
                + UserDataType.NAME+" text,"
                + UserDataType.MCUUID+" text UNIQUE,"
                + UserDataType.DISCORDID+" text UNIQUE,"
                + UserDataType.PREFERENCES+" text DEFAULT '[]',"
                + UserDataType.TAGS+" text DEFAULT '[]'"
                + ");"
                ;
    }
}
