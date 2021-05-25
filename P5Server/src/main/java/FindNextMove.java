import java.util.*;

public class FindNextMove extends Thread {

    private String[] init_board;
    private ArrayList<Node> movesList;

    //default
    FindNextMove() {}

    public boolean Valid(String[] board, int space){
        if(board[space].equals("X") || board[space].equals("O")){
            return false;
        }
        return true;
    }

    public int getNextMove(String[] board, String diff) {
        synchronized (this) {
            MinMax initState = new MinMax(board);
            movesList = initState.findMoves();

            Random rand = new Random();
            Random RandomMove = new Random();
            int randNum = 1 + rand.nextInt(9);
            int randMove = RandomMove.nextInt(9);

            for (int x = 0; x < movesList.size(); x++) {
                Node temp = movesList.get(x);

                //NOTE: 10 = win, 0 = tie
                if (diff.equals("Expert")) {  //impossible mode have fun
                    if (temp.getMinMax() == 10 || temp.getMinMax() == 0) {
                        //returns the next best possible move that leads to a tie or win
                        return temp.getMovedTo();
                    }
                }
                if (diff.equals("Medium")) {
                    if (randNum <= 5 && temp.getMinMax() == 10 || temp.getMinMax() == 0) { //50% of the time return winning or tie move
                        return temp.getMovedTo();
                    }
                    else{ //else return losing move
                        while(!Valid(board, randMove)){
                            randMove = RandomMove.nextInt(9);
                        }
                        return randMove + 1;
                    }
                }
                if (diff.equals("Easy")) {
                    if (randNum <= 8 && temp.getMinMax() == 10 || temp.getMinMax() == 0) {  //80% of the time return winning or tie move
                        return temp.getMovedTo();
                    }
                    while(!Valid(board, randMove)){
                        randMove = RandomMove.nextInt(9);
                    }
                    return randMove + 1;
                }
            }
            return 0;
        }
    }
    /**
     * This class contains the min/max algorithm for selecting the optimal move for a computer opponent. Method findMoves will take a list of all
     * possible moves by the X opponent, follow each move to all of it's possible conclusions, and give it a value of 10 if it can result in a win, 0 if
     * it can not win but can result in a tie and -10 if it will result in a loss. It accomplishes this by recursive calls to the methods Min and Max.
     *
     * @author Mark Hallenbeck
     *
     * Copyright© 2014, Mark Hallenbeck, All Rights Reservered.
     *
     */

    public class MinMax {

        private String[] initState;		//contains the initial state as a string array

        private Node initNode;			//node for the initial state

        private ArrayList<Node> stateList;	//a list to contain all possible moves for X


        /**
         * constructor for the class, sets all data members
         * @param init
         */
        MinMax(String[] init)
        {
            initState = init;			//set the initial state string array

            setInitNode();				//set the initial state node

            stateList = createStateList("max", initNode);		//get list of all X moves from initial state

            setStateList_MinMaxValues(stateList, 1);			//set initial state minMax values for X moves

        }

        /**
         * this method will take the stateList and use the method Max to set all the nodes minMaxValues
         * @return
         */
        ArrayList<Node> findMoves()
        {
            for(int x = 0; x < stateList.size(); x++) 				//for each node in the state list, find it's min/max number
            {
                Node temp = stateList.get(x);					//get node off list

                if(temp.getMinMax() == -1)					//if the state is not won or tied
                {
                    //call min which will find the ultimate minmax value for the node and
                    //perculate it up

                    stateList.remove(x);			//remove node to replace it with new value

                    temp.setMinMax(Min(temp));

                    stateList.add(x,temp);			//add the node back
                }
            }

            return stateList;
        }

        /**
         * sets the first node, the board passed in
         */
        private void setInitNode()
        {
            initNode = new Node(initState, 0);
        }

        /**
         * Takes a string telling whether to expand X or Y and a state Node and
         * Creates the list of possible moves by X or Y
         * @param minOrMax, stateNode
         */
        private ArrayList<Node> createStateList(String minOrMax, Node stateNode)
        {

            ArrayList<Node> stateList = new ArrayList<Node>();		//array list to hold all possible moves

            String[] initState = stateNode.getInitStateString();	//string of initial state of board

            if(minOrMax == "max")		//list of all X moves
            {
                //loop through the array, set each new state(X move) to a node and put in the list

                for(int x = 0 ; x < initState.length ; x++)
                {
                    if(initState[x].equals("b"))								//its a blank space
                    {
                        //create new array with X in initState[x]

                        String[] copyInit = new String[initState.length];		//new string array for the new state

                        System.arraycopy(initState, 0, copyInit, 0, initState.length);	//copy in values to new state array

                        copyInit[x] = "X";										//change "b" to X at proper index

                        Node temp = new Node(copyInit, x+1);				//make a Node out of it

                        stateList.add(temp);								//add it to list
                    }
                }
            }//end of if "max"

            else				//else get list of all of O's moves
            {
                //loop through the array, set each new state(Y move) to a node and put in the list
                for(int x = 0 ; x < initState.length ; x++)
                {
                    if(initState[x].equals("b"))		//its a blank space
                    {
                        //create new array with X in [x]

                        String[] copyInit = new String[initState.length];		//new string array for the new state

                        System.arraycopy(initState, 0, copyInit, 0, initState.length);	//copy in values to new state array

                        copyInit[x] = "O";		//change "b" to O at proper index

                        Node temp = new Node(copyInit, x+1);	//make a Node out of it

                        stateList.add(temp);	//add it to list
                    }
                }

            }

            return stateList;

        }//end of createStateList

        /**
         * prints out each state in the arrayList
         * @param list
         */
        void printList(ArrayList<Node> list)
        {
            for(int x = 0; x < list.size(); x++ )		//get a node from the list
            {
                Node temp = list.get(x);

                String[] tempString = temp.getInitStateString();
                System.out.println("state " + x + ": ");

                for(int y = 0; y< tempString.length; y++)		//print out the string array for that node
                {
                    System.out.print(tempString[y] + " ");

                    if(y == 2 || y == 5)
                    {
                        System.out.print("\n");
                    }
                }
                System.out.println(" ");
            }
        }//end of print list

        /**
         * sets the min/max values for each of the state nodes in the list, x_or_y or 1 are values for X, 0 for Y
         * @param theStateList
         * @param x_or_y
         */
        void setStateList_MinMaxValues(ArrayList<Node> theStateList, int x_or_y)
        {
            if(x_or_y == 1)						//set the minMax values for X
            {
                for(int x =0; x < theStateList.size(); x++)
                {
                    Node temp = theStateList.get(x);

                    temp.setMinMax_for_X();
                }
            }
            else									//set the minMax values for O
            {
                for(int x =0; x < theStateList.size(); x++)
                {
                    Node temp = theStateList.get(x);

                    temp.setMinMax_for_O();
                }

            }
        }

        /**
         * goes through a list of nodes and prints out the min/max value of each
         * @param list
         */
        void print_minMax(ArrayList<Node> list)
        {
            for(int x = 0; x < list.size(); x++ )
            {
                Node temp = list.get(x);

                System.out.print("state " + x + ": minMax: "+temp.getMinMax()+ "\n");
            }

        }

        /**
         * recursive function that works with Max. Takes a state node, creates a list of possible moves,
         * Evaluates the min/max value of those moves. If not a win or a tie, sends it to Max.
         * After all nodes are evaluated, if there is a -10 min/max value in any of the nodes, returns -10, if
         * no -10 returns 0, else returns 10 (means all moves are losing moves
         * @param state
         * @return
         */
        int Min(Node state)
        {
            boolean is_a_win = false, is_a_tie = false;

            //create list of possible moves
            ArrayList<Node> listOfStates = createStateList("min", state);

            //set min max for all nodes in list
            setStateList_MinMaxValues(listOfStates, 0);

            //if list size == 1 return minmax value of that node
            if(listOfStates.size() == 1)
            {
                Node temp = listOfStates.get(0);
                return temp.getMinMax();
            }
            //if the list has a -10 return -10
            for(int x = 0; x < listOfStates.size(); x++)
            {
                Node temp = listOfStates.get(x);

                if(temp.getMinMax() == -10)
                {
                    return -10;
                }
            }

            //otherwise, loop through the list and pass nodes with a value of -1 to max
            for(int y = 0; y < listOfStates.size(); y++)
            {
                Node temp = listOfStates.get(y);					//get node off list

                if(temp.getMinMax() == -1)					//if the state is not won or tied
                {
                    //call max which will find the ultimate minmax value for the node and
                    //perculate it up

                    listOfStates.remove(y);			//remove from list to replace after minmax is set
                    temp.setMinMax(Max(temp));
                    listOfStates.add(y,temp);			//add it back with new minmax
                }
            }

            //after loop, check values if there is a -10 return -10 else if a 0 return 0 else return 10
            for(int i = 0; i < listOfStates.size(); i++)
            {
                Node temp = listOfStates.get(i);

                if(temp.getMinMax() == -10)
                {
                    is_a_win = true;
                }
                if(temp.getMinMax() == 0)
                {
                    is_a_tie = true;
                }
            }

            if(is_a_win == true)
            {
                return -10;
            }
            else if(is_a_tie == true)
            {
                return 0;
            }
            else
            {
                return 10;
            }
        }

        /**
         * recursive function that works with Min. Takes a state node, creates a list of possible moves,
         * Evaluates the min/max value of those moves. If not a win or a tie, sends it to Max.
         * After all nodes are evaluated, if there is a -10 min/max value in any of the nodes, returns 10, if
         * no 10 returns 0, else returns -10 (means all moves are losing moves)
         * @param state
         * @return
         */
        int Max(Node state)
        {

            boolean is_a_win = false, is_a_tie = false;

            //create list of possible moves
            ArrayList<Node> listOfStates = createStateList("max", state);

            //set minmax for all nodes in list
            setStateList_MinMaxValues(listOfStates, 1);

            //if list size is 1 return minmax value of that node
            if(listOfStates.size() == 1)
            {
                Node temp = listOfStates.get(0);
                return temp.getMinMax();
            }

            //if list has a 10 return 10
            for(int x = 0; x < listOfStates.size(); x++)
            {
                Node temp = listOfStates.get(x);

                if(temp.getMinMax() == 10)
                {
                    return 10;
                }
            }

            //otherwise, loop through the list and pass nodes with a value of -1 to min
            for(int y = 0; y < listOfStates.size(); y++)
            {
                Node temp = listOfStates.get(y);					//get node off list

                if(temp.getMinMax() == -1)					//if the state is not won or tied
                {
                    //call max which will find the ultimate minmax value for the node and
                    //perculate it up

                    listOfStates.remove(y);
                    temp.setMinMax(Min(temp));
                    listOfStates.add(y,temp);
                }
            }

            //after loop, check values if there is a 10 return 10 else if a 0 return 0 else -10
            for(int i = 0; i < listOfStates.size(); i++)
            {
                Node temp = listOfStates.get(i);

                if(temp.getMinMax() == 10)
                {
                    is_a_win = true;
                }
                if(temp.getMinMax() == 0)
                {
                    is_a_tie = true;
                }
            }

            if(is_a_win == true)
            {
                return 10;
            }
            else if(is_a_tie == true)
            {
                return 0;
            }
            else
            {
                return -10;
            }
        }
    }

    /**
     * This class is used to store a state of a tic tac toe puzzle in the form of a string as well as a min/max value
     * Methods are included to set the min/max value depending on whose turn it is, X or O
     * @author Mark Hallenbeck
     *
     * Copyright© 2014, Mark Hallenbeck, All Rights Reservered.
     *
     */

    public class Node {

        private String[] state;

        private int minMaxValue;

        private int movedTo;

        Node(String[] stateOfPuzzle, int move)
        {
            state = stateOfPuzzle;

            movedTo = move;

            minMaxValue = -1;
        }

        int getMovedTo()
        {
            return movedTo;
        }

        /**
         * checks for all the ways that O can win and sets minmax to -10. If it is a draw, sets it to 0
         */
        void setMinMax_for_O()
        {

            if(checkForDraw())
            {
                minMaxValue = 0;
            }

            if(state[0].equals("O") && state[1].equals("O") && state[2].equals("O")) //horizontal top
            {
                minMaxValue = -10;
            }

            if(state[3].equals("O") && state[4].equals("O") && state[5].equals("O"))//horizontal middle
            {
                minMaxValue = -10;
            }

            if(state[6].equals("O") && state[7].equals("O") && state[8].equals("O"))//horizontal bottom
            {
                minMaxValue = -10;
            }

            if(state[0].equals("O") && state[3].equals("O") && state[6].equals("O"))//vert right
            {
                minMaxValue = -10;
            }

            if(state[1].equals("O") && state[4].equals("O") && state[7].equals("O"))//vert middle
            {
                minMaxValue = -10;
            }

            if(state[2].equals("O") && state[5].equals("O") && state[8].equals("O"))//vert left
            {
                minMaxValue = -10;
            }

            if(state[0].equals("O") && state[4].equals("O") && state[8].equals("O"))// diag from top left
            {
                minMaxValue = -10;
            }

            if(state[2].equals("O") && state[4].equals("O") && state[6].equals("O"))// diag from top right
            {
                minMaxValue = -10;
            }

        }

        /**
         * checks for all the ways that X can win and sets minmax to 10. If a draw, sets minmax to 0
         */
        void setMinMax_for_X()
        {
            if(checkForDraw())
            {
                minMaxValue = 0;
            }

            if(state[0].equals("X") && state[1].equals("X") && state[2].equals("X")) //horizontal top
            {
                minMaxValue = 10;
            }

            if(state[3].equals("X") && state[4].equals("X") && state[5].equals("X"))//horizontal middle
            {
                minMaxValue = 10;
            }

            if(state[6].equals("X") && state[7].equals("X") && state[8].equals("X"))//horizontal bottom
            {
                minMaxValue = 10;
            }

            if(state[0].equals("X") && state[3].equals("X") && state[6].equals("X"))//vert right
            {
                minMaxValue = 10;
            }

            if(state[1].equals("X") && state[4].equals("X") && state[7].equals("X"))//vert middle
            {
                minMaxValue = 10;
            }

            if(state[2].equals("X") && state[5].equals("X") && state[8].equals("X"))//vert left
            {
                minMaxValue = 10;
            }

            if(state[0].equals("X") && state[4].equals("X") && state[8].equals("X"))// diag from top left
            {
                minMaxValue = 10;
            }

            if(state[2].equals("X") && state[4].equals("X") && state[6].equals("X"))// diag from top right
            {
                minMaxValue = 10;
            }

        }

        void setMinMax(int x)
        {
            minMaxValue = x;
        }

        /**
         * check the state to see if it is a draw (no b's in the string only X and O)
         * @return true if its a draw, false if not
         */
        boolean checkForDraw()
        {
            for(int x = 0; x < state.length; x++)
            {
                if(state[x].equals("b"))
                {
                    return false;
                }
            }

            return true;
        }
        int getMinMax()
        {
            return minMaxValue;
        }

        String[] getInitStateString()
        {
            return state;
        }

    }


}
