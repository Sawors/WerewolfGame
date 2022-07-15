package com.github.sawors.werewolfgame;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    
    public static void connectInit(){
        try(Connection co = connect()){
            //  Init teams
            //co.createStatement().execute("INSERT INTO game(DATA,VALUE) VALUES "+"("+ArGameData.TIMER +",0), ("+ArGameData.EGG_HOLDER+",[])");
            
        } catch (
                SQLException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
    
    protected static Connection connect(){
        Connection co;
        try{
            String target = "jdbc:sqlite:"+Main.getDbFile().getCanonicalFile();
            co = DriverManager.getConnection(target);
            return co;
        } catch (
                IOException |
                SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
