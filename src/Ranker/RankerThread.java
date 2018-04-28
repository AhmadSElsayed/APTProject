package Ranker;

import Database.RankerDatabaseManager;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

class RankerThread extends Thread {
    private final Socket clientSocket;
    private final RankerDatabaseManager databaseManager;
    RankerThread(Socket clientSocket) throws SQLException, IOException, ClassNotFoundException {
        this.clientSocket = clientSocket;
        this.databaseManager = new RankerDatabaseManager();
    }

    @Override
    public void run(){
        try {
            Scanner sc = new Scanner(clientSocket.getInputStream());
            while(sc.hasNext()) {
                long documentID = Long.parseLong(sc.nextLine());
                Token[] t = Token.parseString(sc.nextLine());
                databaseManager.initializeTokenArray(t);
                RankerLogic.lda(t);
                databaseManager.saveTokenArray(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
