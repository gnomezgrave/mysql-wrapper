package gnomezgrave.mysql;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class holds the details about the database we are trying to connect.
 * After creating an object of this class, we have to call <code>connect()</code> with corresponding parameters, in order to connect to that database.
 * @author Praneeth
 *
 */
public class DB {
    // local variable to keep the host address.

    private String host;

    /**
     * Creates a new <code>DB</code> object.
     * @param host Host address. <var>localhost</var> if the database is in the local machine.
     */
    public DB(String host) {
        this.host = host;
    }
    /**
     * This method will establish a connection the given database using provided username and password.
     * @param database Name of the database
     * @param userName User Name for the database
     * @param password Password for the given user.
     * @return Connection object to communicate with the database.
     * @throws ClassNotFoundException When Driver is not found.
     * @throws SQLException When the server is unreachable.
     */
    public Connection connect(String database, String userName, String password) throws ClassNotFoundException, SQLException {
        Connection con = null;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, userName, password);
        return con;
    }
}
