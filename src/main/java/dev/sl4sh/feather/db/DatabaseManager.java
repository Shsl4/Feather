package dev.sl4sh.feather.db;

import dev.sl4sh.feather.permissions.Permission;

import java.sql.*;
import java.util.Optional;

public class DatabaseManager {

    //private final Connection connection;

    public DatabaseManager() {

        try {

            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/feather","feather","secretpass");
            Statement statement = connection.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void closeConnection() throws SQLException {

    }

    private void getOrCreateTable(String name, Connection connection) throws SQLException{


        if(!tableExists(name, connection)){



        }



    }

    private boolean tableExists(String name, Connection connection) throws SQLException{
        DatabaseMetaData metadata = connection.getMetaData();
        ResultSet result = metadata.getTables(null, null, "permissions", null);
        return result.next();
    }

    public Optional<Permission> loadPermission(String command){
        return Optional.empty();
    }


}
