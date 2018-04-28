package Database;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

class DatabaseManager {

    private Connection connection;

    DatabaseManager() throws ClassNotFoundException, SQLException, IOException {
        Properties configuration = new Properties();
        configuration.load(new FileInputStream("Database.config"));

        Class.forName(configuration.getProperty("class"));
        String uri = configuration.getProperty("connection");
        Properties properties = new Properties();
        properties.setProperty("user",configuration.getProperty("user"));
        properties.setProperty("password",configuration.getProperty("password"));
        connection = DriverManager.getConnection(uri, properties);
        connection.setAutoCommit(false);
    }

    public void closeConnection() throws SQLException {
        connection.close();
        connection = null;
    }

    Object executeQuery(String query, String obj){
        Object o = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            connection.commit();
            while (resultSet.next())
                o = resultSet.getObject(obj);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return o;
    }

    void executeUpdate(String query){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    Object[] executeQueryList(String query, String obj){
        ArrayList<Object> o = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            connection.commit();
            while (resultSet.next())
                o.add(resultSet.getObject(obj));
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return o.toArray(new Object[0]);
    }

    @Deprecated
    void executeBatch(String[] query){
        try {
            Statement statement = connection.createStatement();
            for(String q : query) {
                statement.addBatch(q);
            }
            statement.executeBatch();
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getNextException().getMessage());
        }
    }
}
