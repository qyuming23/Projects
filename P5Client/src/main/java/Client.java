import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {

    boolean flag;
    private Socket socketClient;
    private String ipAddress;
    private int port;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    GameInfo info;
    Consumer<Serializable> callback;
    Consumer<Serializable> callback2;


    Client(Consumer<Serializable> call, String ip, int p){
        this.ipAddress = ip;
        this.port = p;
        callback = call;
        info = new GameInfo();
        flag = false;
    }

    public void run() {

        try {
            socketClient= new Socket(ipAddress,port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e) {
            flag = true;
            return;
        }

        while(true) {

            try {
                //get information from server
                this.info = (GameInfo) in.readObject();

                if(this.info.TopScores.size() != 0) {
                    if(this.info.TopScores.size() == 1) {
                        this.info.top1 = Integer.toString(this.info.TopScores.get(0));
                    }
                    if(this.info.TopScores.size() == 2) {
                        this.info.top2 = Integer.toString(this.info.TopScores.get(1));
                    }
                    if(this.info.TopScores.size() == 3) {
                        this.info.top3 = Integer.toString(this.info.TopScores.get(2));
                    }
                }
            }
            catch(Exception e) {
                //server has closed, closes socket and ends client connection
                callback.accept("Server has Closed...");
                callback.accept("Return to main menu.");
                close();
                break;
            }
        }

    }

    public void send() {
        try{
            out.reset();
            out.writeObject(info);

        }catch (Exception e) {

        }
    }

    public boolean checkForWin() {

        //utility function that checks for every possible winning board for player

        //checks rows of O's
        if (info.board[0].equals("O") && info.board[1].equals("O") && info.board[2].equals("O")) {
            return true;
        }

        if (info.board[3].equals("O") && info.board[4].equals("O") && info.board[5].equals("O")) {
            return true;
        }

        if (info.board[6].equals("O") && info.board[7].equals("O") && info.board[8].equals("O")) {
            return true;
        }

        //checks columns of O's
        if (info.board[0].equals("O") && info.board[3].equals("O") && info.board[6].equals("O")) {
            return true;
        }

        if (info.board[1].equals("O") && info.board[4].equals("O") && info.board[7].equals("O")) {
            return true;
        }

        if (info.board[2].equals("O") && info.board[5].equals("O") && info.board[8].equals("O")) {
            return true;
        }

        //check diagonals
        if (info.board[0].equals("O") && info.board[4].equals("O") && info.board[8].equals("O")) {
            return true;
        }

        if (info.board[2].equals("O") && info.board[4].equals("O") && info.board[6].equals("O")) {
            return true;
        }

        return false;
    }

    void setCall(Consumer<Serializable> call) {
        this.callback2 = call;
    }

    //used when a client quits game
    public void close() {
        try{
            socketClient.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
