package com.company;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int MemoryUsed = 776;  //aprox of how much a node holds, Java has no sizeof function unable to get the full memory usage number
        System.out.print("Enter 16 Integers spaced out from one another: ");
        Scanner scan = new Scanner(System.in); //scan input
        String s = scan.nextLine(); //port input to string
        String[] splitStr = s.trim().split("\\s+"); //trim and divide the string by space
        int[] begin = new int[16]; //initialize beginning game board

        for(int i = 0; i < splitStr.length; i++){  //parse the string to integer and into the board
            begin[i] = Integer.parseInt(splitStr[i]);
        }

        long start = System.currentTimeMillis(); //start timer

        Node root = new Node(begin); //initial Node object for the beginning board
        if(!root.solvable){ //terminates if board is not solvable
            System.out.println("This Puzzle is not solvable");
            System.exit(0);
        }

        if(!root.hasZero){ //terminates if board has no zero
            System.out.println("This Puzzle has no Zero");
            System.exit(0);
        }

        Node solution = new Node();  //Node that stores the solution node
        LinkedList<Node> Storage = new LinkedList<>();  //Storage that holds all not repeated expansions
        LinkedList<Node> Check = new LinkedList<>();  //Storage for intaking new expansions per loop
        LinkedList<Node> current = new LinkedList<>();  //List for current children being checked
        LinkedList<Character> Path = new LinkedList<>(); //List to store and later reverse the path
        boolean solutionfound = false; //used to terminate for loop later on

        Expand(root); //expand the root

        if(root.children.size() != 0) { //execute if children has been generated
            for (int i = 0; i < root.children.size(); i++) {
                Storage.add(root.children.get(i)); //add onto storage
                current.add(root.children.get(i)); //add to current to begin the looping process
                MemoryUsed += 776; //inc memory usage count
                if(root.children.get(i).goalReached){ //check if the goal is reached already
                    solution = root.children.get(i); //set solution and exit loop
                    System.out.print("Solution");
                    break;
                }
            }
        }

        while(true){ //loop until answer is found
            if(solution.goalReached){ //terminates when solution is found
                break;
            }

            for(int i = 0; i < current.size(); i++){ //double loop to further expand existing children
                Expand(current.get(i));
                for(int j = 0; j < current.get(i).children.size(); j++){
                    if(!Duplicate(Storage, current.get(i).children.get(j))) {  //check for duplicates
                        Check.add(current.get(i).children.get(j)); //add new children needed for checking the next iteration
                        Storage.add(current.get(i).children.get(j));
                        if(current.get(i).children.get(j).goalReached){
                            solution = current.get(i).children.get(j);
                            solutionfound = true; //break out of first loop if found
                            break;
                        }
                    }
                    MemoryUsed += 776; //inc memory usage
                }
                if(solutionfound){ //break out of second loop and the top condition terminates the while loop
                    break;
                }
            }
            current.clear(); //clear the current already checked nodes
            current = (LinkedList<Node>) Check.clone(); //copy all new children onto current
            Check.clear(); //clear to intake new incoming children
        }

        while(solution.parent != null){ //trace the solution back to the parent and extract the path
            Path.add(solution.move);
            solution = solution.parent;
        }

        long end = System.currentTimeMillis(); //end timer
        double total = (double)(end-start)/1000; //determine total time
        double FinalMemory = ((double)MemoryUsed/1000);

        System.out.println("Initial Board:");
        root.print();
        System.out.println();
        System.out.println("Final Board:");
        root.printGoal();
        System.out.println();
        System.out.print("Moves: ");
        ReversePrint(Path);  //function that prints the List in reverse
        System.out.println("Number of Nodes Expanded: " + Storage.size()); //size of nodes expanded, duplicates not included
        System.out.println("Total Time: " + total);
        System.out.println("Aprox Memory Usage: " + (FinalMemory + " KB")); //divide by 1000 to get KB
    }

    public static void Expand(Node x){  //gets 0 position and creates children for all four directions
        int j = x.get0Position();
        x.MoveRight(x.puzzle, j);
        x.MoveLeft(x.puzzle, j);
        x.MoveUp(x.puzzle, j);
        x.MoveDown(x.puzzle, j);
    }

    public static boolean Duplicate(LinkedList<Node> Storage, Node x){ //check if the Node already exist within storage
        int count = 0;
        for(int i = 0; i < Storage.size(); i++){
            for(int j = 0; j < Storage.get(i).puzzle.length; j++) {
                if (Storage.get(i).puzzle[j] == x.puzzle[j]) {
                    count++;
                }
            }
            if(count == 16){ //counter to stop function
                return true;
            }
            else{ //if not found yet reset to 0 for next iteration.
                count = 0;
            }
        }
        return false;
    }

    public static void ReversePrint(LinkedList<Character> x){ //prints in reverse
        for(int i = x.size()-1 ; i >= 0; i--){
            System.out.print(x.get(i));
        }
        System.out.println();
    }

}
