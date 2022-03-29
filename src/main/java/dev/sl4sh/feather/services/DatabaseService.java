package dev.sl4sh.feather.services;

import dev.sl4sh.feather.Permission;
import dev.sl4sh.feather.Service;

import java.sql.*;
import java.util.Optional;

public class DatabaseService implements Service {

    public DatabaseService() {

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


    @Override
    public void loadConfiguration() {

    }

    @Override
    public void writeConfiguration() {

    }

    @Override
    public boolean getServiceState() {
        return false;
    }
}
