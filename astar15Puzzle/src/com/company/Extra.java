package com.company;
import java.util.ArrayList;
import java.util.LinkedList;

public class Extra {
    private int MemoryUsed = 776;
    private int NodesExpanded = 0;

    public Node astar(Node root){
        LinkedList<Node> OpenList = new LinkedList<>();  //list to contain Node not already expanded upon
        LinkedList<Node> ClosedList = new LinkedList<>();  //list to contain Nodes already visited and expanded upon
        OpenList.add(root);  //add the initial node

        while (OpenList.size() != 0) {  //loop until OpenList is empty
            int currentLowestIndex = lowestF(OpenList); //get the current node with the lowest F value
            Node current = OpenList.get(currentLowestIndex);  //set current node to the result
            OpenList.remove(currentLowestIndex);  //pop it off the list

            if (!current.alreadyExpanded) { //no need to expand whats already expanded
                current.Expand();  //expand
                MemoryUsed += current.children.size() * 776; //update memory usage
                NodesExpanded += current.children.size(); //update nodes expanded
            }

            for (int i = 0; i < current.children.size(); i++) { //loop through the children nodes of current

                if (current.children.get(i).goalReached) { //if goal is reached
                    return current.children.get(i);
                }

                if (OpenListCheck(OpenList, current.children.get(i))) { //if the current child's board configuration already exist and contains a higher f value
                    continue;
                }

                if (!ClosedListCheck(ClosedList, current.children.get(i))) { //At this point, this node is definitely not in OpenList so if its not in ClosedList, push into OpenList
                    OpenList.add(current.children.get(i));
                   }
                }


                ClosedList.add(current); //add current into closed list
            }
        return null;
    }



    public boolean OpenListCheck(LinkedList<Node> List, Node current){  //checks open list for identical board and also compares the F values
        for(int i = 0; i < List.size(); i++){
            if(samePuzzle(List.get(i).puzzle, current.puzzle)){
                if(List.get(i).getF() < current.getF()){  //if OpenList contains an identical board and contain a lower F value then no point of pushing into the OpenList
                    return true;
                }
            }
        }
        return false;
    }

    public boolean ClosedListCheck(LinkedList<Node> List, Node current){  //checks for identical board from closed list
        for(int i = 0; i < List.size(); i++){
            if(samePuzzle(List.get(i).puzzle, current.puzzle)){
                return true;
            }
        }
        return false;
    }

    public boolean samePuzzle(int[] puzzle1, int[] puzzle2){ //checks if the puzzle is the same
        int count = 0;
        for(int i = 0; i < puzzle1.length; i++){
            if(puzzle1[i] == puzzle2[i]){
                count++;
            }
        }
        if(count == 16){
            return true;
        }

        return false;
    }

    public int lowestF(LinkedList<Node> List){ //finds the index of the lowest F value
        int num = 999;
        int index = -55;
        for(int i = 0; i < List.size(); i++){
            if(List.get(i).getF() < num){
                num = List.get(i).getF();
                index = i;
            }
        }

        return index;
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
