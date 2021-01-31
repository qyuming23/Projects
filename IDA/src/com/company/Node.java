package com.company;
import java.util.LinkedList;

public class Node {
    int[] puzzle;  //stores the board state of each node.
    int Depth = 0; //stores depth
    int h = 0;
    int type;
    int Min = 0;
    Node parent;  //points back to parent
    LinkedList<Node> children = new LinkedList<>(); //linked list for holding all children
    char move = 'I'; //saves what moves the parent made to get here. I is Root
    int[] goal = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0};  //goal state
    boolean goalReached; //determines whether this state is the goal or not
    boolean solvable = true;  //if solvable
    boolean hasZero = false;
    boolean alreadyExpanded = false;

    Node(int[] root, int type, int Depth){  //constructor automatically runs tests on the board and determines its status
        this.puzzle = root.clone(); //clone board
        goalReached = false;
        zero(); //checks if the board has a 0
        CheckForSolution(this.puzzle); //check if this state is the goal state
        if(!isSolvable(this.puzzle)){  //is it solvable?
            solvable = false;
        }
        if(type == 1){
            h = misplacedTile();
        }
        else if(type == 2) {
            h = ManhattanDistance();
        }
        this.type = type;
        this.Depth = Depth;
    }

    public Node() { //default constructor
        goalReached = false;
    }

    public void MoveRight(int[] list, int x){ //moves to the right
        if((x + 1) % 4 != 0) { //check boundaries
            int[] clone = list.clone(); //clone the board
            clone[x] = clone[x + 1];
            clone[x + 1] = 0;  //swap positions

            Node child = new Node(clone, this.type, this.Depth);  //update child status and add to children list
            child.move = 'R';
            child.Depth++;
            child.parent = this;
            children.add(child);
        }
    }

    public void MoveLeft(int[] list, int x){ //moves to the left
        if(x % 4 > 0) {
            int[] clone = list.clone();
            clone[x] = clone[x - 1];
            clone[x - 1] = 0;

            Node child = new Node(clone, this.type, this.Depth);
            child.move = 'L';
            child.Depth++;
            child.parent = this;
            children.add(child);
        }
    }

    public void MoveUp(int[] list, int x){ //moves up
        if(x - 4 >= 0) {
            int[] clone = list.clone();
            clone[x] = clone[x - 4];
            clone[x - 4] = 0;

            Node child = new Node(clone, this.type, this.Depth);
            child.move = 'U';
            child.Depth++;
            child.parent = this;
            children.add(child);
        }
    }

    public void MoveDown(int[] list, int x){  //moves down
        if(x + 4 < 16) {
            int[] clone = list.clone();
            clone[x] = clone[x + 4];
            clone[x + 4] = 0;

            Node child = new Node(clone, this.type, this.Depth);
            child.move = 'D';
            child.Depth++;
            child.parent = this;
            children.add(child);
        }
    }

    public void print(){  //prints the board
        if(this.puzzle.length == 0){
            return;
        }
        int m = 0;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                System.out.printf("%3d ", this.puzzle[m]);
                m++;
            }
            System.out.println();
        }
    }

    public int get0Position(){  //returns where 0 is
        for(int i = 0; i < this.puzzle.length; i++){
            if(this.puzzle[i] == 0){
                return i;
            }
        }
        return -1;
    }

    public void Expand(){  //expands the children of this node where possible
        if(this.children.size() > 0){
            this.children.clear();
        }
        int x = get0Position();
        MoveRight(this.puzzle, x);
        MoveLeft(this.puzzle, x);
        MoveUp(this.puzzle, x);
        MoveDown(this.puzzle, x);
        alreadyExpanded = true;
    }

    public void CheckForSolution(int[] x){  //checks if the state of the board matches the goal
        int count = 0;
        for(int i = 0; i < x.length; i++){
            if(this.goal[i] == x[i]){
                count++;
            }
        }
        if(count == 16){
            goalReached = true;
        }
    }

    public boolean isSolvable(int[] puzzle) {  //determines solvability by parity
        int parity = 0;
        int gridWidth = (int) Math.sqrt(puzzle.length);
        int row = 0;
        int blankRow = 0;

        for (int i = 0; i < puzzle.length; i++)
        {
            if (i % gridWidth == 0) {
                row++;
            }
            if (puzzle[i] == 0) {
                blankRow = row;
                continue;
            }
            for (int j = i + 1; j < puzzle.length; j++)
            {
                if (puzzle[i] > puzzle[j] && puzzle[j] != 0)
                {
                    parity++;
                }
            }
        }

        if (gridWidth % 2 == 0) {
            if (blankRow % 2 == 0) {
                return parity % 2 == 0;
            } else {
                return parity % 2 != 0;
            }
        } else {
            return parity % 2 == 0;
        }
    }

    public void printGoal(){  //prints the goal state
        if(this.goal.length == 0){
            return;
        }
        int m = 0;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                System.out.printf("%3d ", this.goal[m]);
                m++;
            }
            System.out.println();
        }
    }

    public void zero(){ //checks for zero in the board
        for(int i = 0; i < this.puzzle.length; i++){
            if(this.puzzle[i] == 0){
                this.hasZero = true;
                break;
            }
        }
    }

    public int row(int x){ //function to tell which row the number is supposed to be on
        if(x >= 1 && x <= 4){
            return 1;
        }
        else if(x >= 5 && x <= 8){
            return 2;
        }
        else if(x >= 9 && x <= 12){
            return 3;
        }
        else if(x >= 13 && x <= 16){
            return 4;
        }
        return 0;
    }

    public int Distance(int indexPos, int goalPos){
        int DistanceCount = 0;
        indexPos++;

        while(indexPos != goalPos) { //while indexPos is not in the right position
            if (row(indexPos) < row(goalPos)){  //move up or down depending on the row
                indexPos += 4;
                DistanceCount++;
            }
            else if(row(indexPos) > row(goalPos)){
                indexPos -= 4;
                DistanceCount++;
            }
            else if(row(indexPos) == row(goalPos)){ //if on the same row, move into correct position
                if(indexPos < goalPos){
                    indexPos++;
                    DistanceCount++;
                }
                else if(indexPos > goalPos){
                    indexPos--;
                    DistanceCount++;
                }
            }
        }
        return DistanceCount; //return moves
    }

    public int ManhattanDistance(){  //loops through the entire puzzle and counts the distance of every misplaced tiles to their destination
        int[] DistanceStorage = new int[15];
        int index = 0;
        for(int i = 0; i < this.puzzle.length; i++){
            if(this.puzzle[i] != 0 && this.puzzle[i] != i+1){
                DistanceStorage[index] = Distance(i, this.puzzle[i]);
                index++;
            }
        }

        int sum = 0;
        for(int i = 0; i < DistanceStorage.length; i++){ //sums up the recorded values
            sum += DistanceStorage[i];
        }

        return sum; //return the total distance
    }

    public int misplacedTile(){  //counts number of misplaced tiles
        int count = 0;
        for(int i = 0; i < this.puzzle.length; i++){
            if(this.puzzle[i] != 0) {
                if (this.puzzle[i] != i + 1) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getF(){  //returns f
        return Depth + h;
    }
}
