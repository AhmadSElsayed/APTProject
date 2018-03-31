package Database;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {

    private Connection connection;

    public DatabaseManager() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String uri = "jdbc:postgresql://localhost:5432/crawler";
        Properties properties = new Properties();
        properties.setProperty("user","postgres");
        properties.setProperty("password","1234");
        connection = DriverManager.getConnection(uri, properties);
    }

    public void closeConnection() throws SQLException {
        connection.close();
        connection = null;
    }

    public Object executeQuery(String query, String obj){
        Object o = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next())
                o = resultSet.getObject(obj);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return o;
    }

    public void executeUpdate(String query){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void executeBatch(String[] query){
        try {
            Statement statement = connection.createStatement();
            for(String q : query) {
                statement.addBatch(q);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getNextException().getMessage());
        }
    }
}
