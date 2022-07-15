package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.database.LinkedUser;
import com.github.sawors.werewolfgame.database.UserDataType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

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
            LinkedUser sawors = new LinkedUser("Sawors", UUID.fromString("f96b1fab-2391-4c41-b6aa-56e6e91950fd"),"315237447065927691",null,null);
            saveUserData(sawors);
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
    
    public static void saveUserData(LinkedUser user){
        try(Connection co = connect()){
            co.prepareStatement("INSERT into Users Values('"
            +user.getId()+"','"
            +user.getName()+"','"
            +user.getMinecraftid()+"','"
            +user.getDiscordid()+"','"
            +user.getPreferences()+"','"
            +user.getTags()+"');"
            ).execute();
        } catch(SQLException e){
            try(Connection co = connect()){
                // TODO : add overwrite for confirmation if overwrite detected
                Main.logAdmin("User "+user.getName()+"#"+user.getId()+" already exists, updating it's data...");
                co.prepareStatement("UPDATE Users SET "
                        +UserDataType.NAME+" = '"+user.getName()+"',"
                        +UserDataType.MCUUID+" = '"+user.getMinecraftid()+"',"
                        +UserDataType.DISCORDID+" = '"+user.getDiscordid()+"',"
                        +UserDataType.PREFERENCES+" = '"+user.getPreferences()+"',"
                        +UserDataType.TAGS+" = '"+user.getTags()+"' "+
                        "WHERE "+UserDataType.USERID+" = '"+user.getId()+"'"
                ).execute();
            }catch (SQLException e2){
                e2.printStackTrace();
            }
        }
    }
}
