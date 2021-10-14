package dev.sl4sh.feather.db;

import dev.sl4sh.feather.permissions.Permission;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DatabaseManager {

    public DatabaseManager(){

        try{

            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/feather","feather","secretpass");
            Statement statement = connection.createStatement();

        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }

    public Optional<Permission> loadPermission(String command){
        return Optional.empty();
    }


}
