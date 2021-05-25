import java.io.Serializable;
import java.util.ArrayList;


public class GameInfo implements Serializable {

    String top1, top2, top3, Difficulty;
    int points;
    String[] board;
    boolean inGame;
    int serverMove;
    int clientMoveCount;
    int ServerMoveCount;
    boolean serverWon;
    ArrayList<Integer> TopScores;

    GameInfo() {
        top1 = null;
        top2 = null;
        top3 = null;
        Difficulty = "Easy";
        points = 0;
        board = new String[9];
        inGame = false;
        serverMove = -1;
        clientMoveCount = 0;
        ServerMoveCount = 0;
        serverWon = false;
        TopScores = new ArrayList<>();

        //initiate board
        for (int i = 0; i < 9; i++) {
            board[i] = "b";
        }
    }
}
