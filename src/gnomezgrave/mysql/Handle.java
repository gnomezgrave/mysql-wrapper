package gnomezgrave.mysql;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * This class holds the manipulation functions of the JDBC driver. All the methods are designed in a way that is too much easy to use.
 * setDatabase(Connection connection) should be called and the target database should be set before using these methods.
 * @author PNP
 */
public class Handle {

    // local connection
    private static Connection con;

    /**
     * This method sets the <code>Connection</code> object to this class. You <b>have to</b> use this method to set a Database before using this class.
     * @param a connection Connection object, with all data about the database to be connected. You can use the <code>connect()</code> method in <code>DB</code> class to get an Object for this.
     * @throws SQLException When the we can't connect to the database using given <code>Connection</code> object.
     */
    public static void setDatabase(Connection connection) throws SQLException {
        con = connection;
    }

    /**
     *
     * @return Connection object containing all the details about the current connected database.
     */
    public static Connection getDatabase() {
        return con;
    }

    /**
     * When <b>all</b> the values are provided corresponding to a all the fields of a table, this method will automatically add those values to the table.
     * <br/><br/>
     * Example :<br/>
     * Let's assume that there is a MySQL table named 'Student' as following.
     * <table border=1>
     *  <tr>
     *      <th>No</th>
     *      <th>Name</th>
     *      <th>Age</th>
     *  </tr>
     *  <tr>
     *      <td></td>
     *      <td></td>
     *      <td></td>
     *  </tr>
     * </table>
     * <br/>
     * Then you have to call this method like this.<br/> <code>
     * <pre>
     * Object obj[] = {"St-001","Steve",25};
     * try{
     *   Handle.setData("Student",obj); 
     * }catch(SQLException ex){
     * }</pre></code>
     * @param table Name of the MySQL table
     * @param sourceTexts Values to <b/>all the fields</b> in the <b>correct order</b> as in the MySQL definition.
     * @throws SQLException
     */
    public static void setData(String table, Object[] sourceTexts) throws SQLException {
        ResultSet rst = con.createStatement().executeQuery("select * from " + table);
        ResultSetMetaData sm = rst.getMetaData();
        PreparedStatement preparedStatement;
        int numberOfFields = sm.getColumnCount();
        if (numberOfFields == sourceTexts.length) {
            String query = "insert into " + table + " values(";
            for (int i = 0; i < sourceTexts.length - 1; i++) {
                query += "?,";
            }
            query += "?)";

            preparedStatement = con.prepareStatement(query);
            for (int i = 0; i < numberOfFields; i++) {
                preparedStatement.setObject(i + 1, sourceTexts[i]);
            }
        } else {
            String query = "insert into " + table + "(";
            for (int i = 0; i < sourceTexts.length - 1; i++) {
                query += sm.getColumnName(i + 1) + ",";
            }
            query += sm.getColumnName(sourceTexts.length) + ") values(";

            for (int i = 0; i < sourceTexts.length - 1; i++) {
                query += "?,";
            }
            query += "?)";

            preparedStatement = con.prepareStatement(query);
            /*
            for (int i = 0; i < sourceTexts.length; i++) {
            preparedStatement.setObject(i + 1, sm.getColumnName(i + 1));
            }*/
            for (int i = 0; i < sourceTexts.length; i++) {
                preparedStatement.setObject(i + 1, sourceTexts[i]);
            }
        }

        preparedStatement.executeUpdate();
    }

    /**
     * This method will execute a given query.
     * @param query MySQL query
     * @throws SQLException
     */
    public static void setData(String query) throws SQLException {
        con.createStatement().executeUpdate(query);
    }

    /**
     * This method will delete records from a MySQL table when the criteria is provided.
     * Example :<br/>
     * Let's assume that there is a MySQL table named 'Student' as following.
     * <table border=1>
     *  <tr>
     *      <th>No</th>
     *      <th>Name</th>
     *      <th>Age</th>
     *  </tr>
     *  <tr>
     *      <td>St-001</td>
     *      <td>Steve</td>
     *      <td>25</td>
     *  </tr>
     * </table>
     * <br/>
     * If you want to delete <b>Steve</b> from the database, then you have to call this method like this.<br/> <code>
     * <pre>
     * Object fields[] = {"No" , "Name" , "Age"};
     * Object values[] = {"St-001" , "Steve" , 25};
     * try{
     *   Handle.deleteData("Student",fields,values);
     * }catch(SQLException ex){
     * }</pre></code>
     * @param table Name of the MySQL table
     * @param sourceTexts Fields of the corresponding MySQL table, which we want to use for matching data for deletion.
     * @param criteriaTexts Values to each field mentioned if <var>sourceTexts</var> in corresponding order. This should have the same number of elements as <var>sourceTexts</var>.
     * @throws SQLException
     */
    public static void deleteData(String table, String[] sourceFields, String[] criteriaTexts) throws SQLException {
        PreparedStatement preparedStatement;
        String query = "delete from " + table + " where ";
        if (sourceFields.length == 1) {
            query += sourceFields[0] + " = ?";
        } else {
            for (int i = 0; i < sourceFields.length - 1; i++) {
                query += sourceFields[i] + " = ? && ";
            }
            query += sourceFields[sourceFields.length - 1] + " = ?";
        }
        preparedStatement = con.prepareStatement(query);
        for (int i = 0; i < sourceFields.length; i++) {
            preparedStatement.setObject(i + 1, criteriaTexts[i]);
        }
        preparedStatement.executeUpdate();
    }

    /**
     * This method will give you a <code>ResultSet</code>, when a query is given.
     * @param query MySQL query
     * @return a <code>ReusultSet</code> object with output records of the given query.
     * @throws SQLException
     */
    public static ResultSet getdata(String query) throws SQLException {
        return con.createStatement().executeQuery(query);
    }

    /**
     * This method will collect values from a particular field of a given table and add those into a Vector.
     * @param table Name of the MySQL table.
     * @param column Name of the field in the <var>table</var>
     * @return an Vector object containing the values. <code>null</code> if the given <var>table</var> or <var>column</var> does not exist.
     */
    public static Vector<Object> getdata(String table, String column) {
        Vector<Object> v = null;
        try {
            String query = "select " + column + " from " + table;
            v = new Vector<Object>();
            ResultSet rst = getdata(query);
            try {
                while (rst.next()) {
                    v.add(rst.getObject(column));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Handle.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Handle.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return v;
        }
    }

    /**
     * This method will update a table, when the criteria fields & values are given.
     * Example :<br/>
     * Let's assume that there is a MySQL table named 'Student' as following.
     * <table border=1>
     *  <tr>
     *      <th>No</th>
     *      <th>Name</th>
     *      <th>Age</th>
     *  </tr>
     *  <tr>
     *      <td>St-001</td>
     *      <td>Steve</td>
     *      <td>25</td>
     *  </tr>
     * </table>
     * <br/>
     * If you want to update the name of <b>Steve</b> to Bill, then you have to call this method like this.<br/> <code>
     * <pre>
     * String field = "name";
     * String value = "Steve";
     * Object fields[] = {"name"};
     * Object values[] = {"Bill"};
     * try{
     *   Handle.updateData("Student",field,value,fields,values);
     * }catch(SQLException ex){
     * }</pre></code>
     * @param table Name of the MySQL table.
     * @param keyField Name of the field to match.
     * @param keyText Value that should match with <var>keyField</var>.
     * @param fields Fields to be updated.
     * @param sourceTexts Values for the fields in <var>fields</var>. This should have the same number of elements as <var>fields</var>.
     * @throws SQLException
     */
    public static void update(String table, String keyField, String keyText, String[] fields, Object[] sourceTexts) throws SQLException {
        String query = "update " + table + " set ";
        if (fields.length == 1) {
            query += fields[0] + " = ?";
        } else {
            for (int i = 0; i
                    < fields.length - 1; i++) {
                query += fields[i] + " = ?" + " , ";
            }
            query += fields[fields.length - 1] + " = ?";
        }
        query += " where " + keyField + "=?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        for (int i = 0; i
                < fields.length; i++) {
            preparedStatement.setObject(i + 1, sourceTexts[i]);
        }
        preparedStatement.setObject(fields.length + 1, keyText);
        preparedStatement.executeUpdate();

    }

    /**
     * This method will update all the columns in a table, when the criteria fields & values are given.
     * Example :<br/>
     * Let's assume that there is a MySQL table named 'Student' as following.
     * <table border=1>
     *  <tr>
     *      <th>No</th>
     *      <th>Name</th>
     *      <th>Age</th>
     *  </tr>
     *  <tr>
     *      <td>St-001</td>
     *      <td>Steve</td>
     *      <td>25</td>
     *  </tr>
     * </table>
     * <br/>
     * If you want to update the name of <b>Steve</b> to Bill, then you have to call this method like this.<br/> <code>
     * <pre>
     * String field = "name";
     * String value = "Steve";
     * Object values[] = {"ST-001","Bill",25};
     * try{
     *   Handle.updateData("Student",field,value,values);
     * }catch(SQLException ex){
     * }</pre></code>
     * @param table Name of the MySQL table.
     * @param keyField Name of the field to match.
     * @param keyText Value that should match with <var>keyField</var>.
     * @param sourceTexts Values for the fields in <var>fields</var>. This should have <b> all </b>the fields in the table.
     * @throws SQLException
     */
    public static void update(String table, String keyField, String keyText, Object[] sourceTexts) throws SQLException {
        String query = "update " + table + " set ";
        ResultSetMetaData rm = getdata("select * from " + table).getMetaData();
        Object[] fields = new Object[rm.getColumnCount()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = rm.getColumnName(i + 1);
        }
        if (fields.length == 1) {
            query += fields[0] + " = ?";
        } else {
            for (int i = 0; i
                    < fields.length - 1; i++) {
                query += fields[i] + " = ?" + " , ";
            }
            query += fields[fields.length - 1] + " = ?";
        }
        query += " where " + keyField + "=?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        for (int i = 0; i
                < fields.length; i++) {
            preparedStatement.setObject(i + 1, sourceTexts[i]);
        }
        preparedStatement.setObject(fields.length + 1, keyText);
        preparedStatement.executeUpdate();

    }

    /**
     * This will return an ImageIcon from a given MySQL query.
     * @param query MySQL query.
     * @return An ImageIcon object or <code>null</code> if the query is faulty or query returns no records.
     */
    public static ImageIcon getImage(String query) {
        try {
            ResultSet rst = getdata(query);
            if (rst.next()) {
                byte[] b = rst.getBytes(1);
                return new ImageIcon(b);
            }
        } catch (Exception ex) {
            Logger.getLogger(Handle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method checks whether a given value already exists in the table.
     * @param table MySQL table name.
     * @param field Field to check.
     * @param key Value for that <var>field</var>.
     * @return <code>true</code> if the value if in the table, <code>false</code> if not.
     */
    public static boolean ifContainDuplicates(String table, String field, String key) {
        String query = "select * from " + table + " where " + field + " = '" + key + "'";
        return ifContainDuplicates(query);
    }

    /**
     * This method checks whether a given value (from a query) already exists in the table.
     * @param query MySQL query to process.
     * @return  <code>true</code> if the value if in the table, <code>false</code> if not.
     */
    public static boolean ifContainDuplicates(String query) {
        try {
            ResultSet rst = getdata(query);
            return rst.next();
        } catch (SQLException ex) {
            Logger.getLogger(Handle.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * This method is used to generate the <b>subsequent</b>. next id for a given field in a table. This method will also detect the empty slots in between.
     * @param table MySQL table.
     * @param column Field to check.
     * @param prefix Prefix of the id. Ex: "ST-"
     * @param numberOfMinDigit The minimum number of digits to be in a right side of the id.
     * @return The next id for the field.
     */
    public static String getNextID(String table, String column, String prefix, int numberOfMinDigit) {
        String model = "";
        for (int i = 0; i < numberOfMinDigit; i++) {
            model += "0";
        }
        int id = 1;

        int l = prefix.length();
        ResultSet rst = null;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(model.length());
        nf.setGroupingUsed(false);

        try {
            String first = prefix + model.substring(0, model.length() - 1) + 1;
            rst = Handle.getdata("select " + column + " from " + table + " where " + column + "='" + first + "'");
            if (rst.next()) {
                rst = Handle.getdata("select " + column + " from " + table + " order by 1");
                if (rst.next()) {
                    model = rst.getString(1);
                    rst.beforeFirst();
                    while (rst.next()) {
                        id = Integer.parseInt(rst.getString(1).substring(l));
                        String next = prefix + nf.format(++id);
                        ResultSet temp = Handle.getdata("select " + column + " from " + table + " where " + column + "='" + next + "'");
                        if (!temp.next()) {
                            break;
                        }
                    }
                }
            }
            model = prefix + nf.format(id);
        } catch (SQLException ex) {
            Logger.getLogger(Handle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return model;

    }
}
