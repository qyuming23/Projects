
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


public class Server {

    int Count = 1;
    private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback, callback2;
    private int port;
    FindNextMove FNM;

    Server(Consumer<Serializable> call, int p) {
        this.port = p;
        callback = call;
        server = new TheServer();
        server.start();
        FNM = new FindNextMove();
    }

    public ArrayList<Integer> readScore(){
        ArrayList<Integer> ret = new ArrayList<>();
        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i).clientInfo.points != 0){
                ret.add(clients.get(i).clientInfo.points);
            }
        }

        for(int i = 0; i < ret.size() - 1; i++){
            for(int j = 0; j < ret.size() - i - 1; j++){
                if (ret.get(j) < ret.get(j+1)) {
                    int temp = ret.get(j+1);
                    ret.set(j+1, j);
                    ret.set(j, temp);
                }
            }
        }

        return ret;
    }


    public class TheServer extends Thread {

        public void run() {

            try (ServerSocket mysocket = new ServerSocket(port);) {
                callback.accept("Using Port: " + port);
                callback.accept("Waiting on players to join...");

                int id = 0;
                while (true) {
                    synchronized (this) {
                        //keeps track of new players joining the server and starts a new thread of that client
                        ClientThread c = new ClientThread(mysocket.accept(), Count);
                        callback.accept("Player " + Count + " has joined!");
                        clients.add(c);
                        c.start();

                        callback2.accept("Number of clients: "+ clients.size());
                        Count++;
                    }

                }
            }//end of try
            catch (Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of while

    }


    public class ClientThread extends Thread {

        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        GameInfo clientInfo;


        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
            clientInfo = new GameInfo();

        }


        public void run() {

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            } catch (Exception e) {
                System.out.println("Streams not open");
            }


            while (true) {

                try {
                    //get game play information from players
                    this.clientInfo = (GameInfo) in.readObject();

                    ArrayList<Integer> temp = readScore();

                    for(int i = 0; i < clients.size(); i++){
                        clients.get(i).clientInfo.TopScores = temp;
                    }

                    if(clientInfo.inGame) {
                        //server decides which is the best next move to make
                        //and sends it back to client
                        int i = FNM.getNextMove(clientInfo.board, clientInfo.Difficulty);
                        callback.accept("Server made move at: "+i);
                        sendNextMove(i);
                    }


                } catch (Exception e) {
                    synchronized (this) {
                        callback.accept("Player " + count + " quit the game.");
                        clients.remove(this);
                        callback2.accept("Number of clients: "+ clients.size());
                        break;
                    }
                }
            }
        }//end of run

        void sendNextMove(int i) {
            clientInfo.serverMove = i - 1;
            clientInfo.board[i-1] = "X";

            //checks if the move that was made is the winning move
            clientInfo.serverWon = checkForWin(clientInfo);

            //keeps track of number of moves (cant exceed 9)
            //client goes first so move 9 will be made by them, therefore client code handles what happens
            clientInfo.ServerMoveCount++;

            try {
                out.reset();
                out.writeObject(clientInfo);

            }catch (Exception e){}
        }
    }


    public boolean checkForWin(GameInfo info) {

        //utility function that checks for every possible winning board for player

        //checks rows of X's
        if (info.board[0].equals("X") && info.board[1].equals("X") && info.board[2].equals("X")) {
            return true;
        }

        if (info.board[3].equals("X") && info.board[4].equals("X") && info.board[5].equals("X")) {
            return true;
        }

        if (info.board[6].equals("X") && info.board[7].equals("X") && info.board[8].equals("X")) {
            return true;
        }

        //checks columns of X's
        if (info.board[0].equals("X") && info.board[3].equals("X") && info.board[6].equals("X")) {
            return true;
        }

        if (info.board[1].equals("X") && info.board[4].equals("X") && info.board[7].equals("X")) {
            return true;
        }

        if (info.board[2].equals("X") && info.board[5].equals("X") && info.board[8].equals("X")) {
            return true;
        }

        //check diagonals of X's
        if (info.board[0].equals("X") && info.board[4].equals("X") && info.board[8].equals("X")) {
            return true;
        }

        if (info.board[2].equals("X") && info.board[4].equals("X") && info.board[6].equals("X")) {
            return true;
        }

        return false;
    }


    void setCall(Consumer<Serializable> call) {
        this.callback2 = call;
    }
}