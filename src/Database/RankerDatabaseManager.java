package Database;

import Ranker.Token;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.sql.SQLException;

public class RankerDatabaseManager extends DatabaseManager {
    public RankerDatabaseManager () throws SQLException, IOException, ClassNotFoundException {
        super();
    }

    public void initializeTokenArray(Token[] t) {
        throw new NotImplementedException();
    }

    public void saveTokenArray(Token[] t) {
        throw new NotImplementedException();
    }
}
