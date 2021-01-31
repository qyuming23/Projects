package com.company;
import java.util.ArrayList;
import java.util.LinkedList;

public class Extra {
    private int MemoryUsed = 776;
    private int NodesExpanded = 0;

    public Node IDAstar(Node root){
        int threshold = root.h;

        while(true){
            Node temp = Search(root, threshold); //search until threshold is exceeded
            if(temp.goalReached){  //if solution is found
                return temp;
            }

            if(temp.Min > Integer.MAX_VALUE){  //if f value exceeds max value
                return null;
            }

            threshold = temp.Min; //set new threshold to the min of the greater values
            System.out.println(threshold);
        }
    }

    public Node Search(Node current, int threshold){
        if(current.getF() > threshold){  //greater f value found
            return current;
        }

        if(current.goalReached){ //if solution is found
            return current;
        }

        current.Expand(); //create children

        MemoryUsed += current.children.size() * 776; //update memory usage
        NodesExpanded += current.children.size(); //update nodes expanded

        int Min = Integer.MAX_VALUE; //set min to max for easy resets

        for(int i = 0; i < current.children.size(); i++){
            Node temp = Search(current.children.get(i), threshold); //search all children within threshold
            if(temp.goalReached){ //if goal is reached
                return temp;
            }

            if(temp.getF() < Min && temp.getF() != threshold){ //set new min value and must not be equal to threshold
                Min = temp.getF();
            }
        }

        current.Min = Min; //return the Min back to caller function
        return current;
    }

    public int getMemory(){ //returns final memory count
        return MemoryUsed;
    }

    public int getNodesExpanded(){ //returns final nodes expanded coount
        return NodesExpanded;
    }

    public void ReversePrint(ArrayList<Character> x){ //prints in reverse
        for(int i = x.size()-1 ; i >= 0; i--){
            System.out.print(x.get(i));
        }
        System.out.println();
    }

    public ArrayList<Character> getPath(Node solution){ //stores the path in reverse
        ArrayList<Character> ret = new ArrayList<>();
        while(solution.parent != null){
            ret.add(solution.move);
            solution = solution.parent;
        }
        return ret;
    }
}
