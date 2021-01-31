package com.company;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.print("Enter 16 Integers spaced out from one another: ");
        Scanner scan = new Scanner(System.in); //scan input
        String s = scan.nextLine(); //port input to string
        String[] splitStr = s.trim().split("\\s+"); //trim and divide the string by space
        int[] begin = new int[16]; //initialize beginning game board
        ArrayList<Character> Path;
        Extra Helper = new Extra(); //Extra class with some helper functions

        for(int i = 0; i < splitStr.length; i++){  //parse the string to integer and into the board
            begin[i] = Integer.parseInt(splitStr[i]);
        }

        System.out.println("");
        System.out.println("Make a selection: ");
        System.out.println("Enter 1 for Misplaced tiles: ");
        System.out.println("Enter 2 for Manhattan Distance: ");
        System.out.print("Enter > ");
        Scanner type = new Scanner(System.in);
        int selection = scan.nextInt();

        long start = System.currentTimeMillis(); //start timer

        Node root = new Node(begin, selection, 0); //initial Node object for the beginning board and starting Depth of 0
        if(!root.solvable){ //terminates if board is not solvable
            System.out.println("This Puzzle is not solvable");
            System.exit(0);
        }

        if(!root.hasZero){ //terminates if board has no zero
            System.out.println("This Puzzle has no Zero");
            System.exit(0);
        }

        Node solution;  //Node that stores the solution node
        solution = Helper.IDAstar(root); //call getSolution to receive the goal node

        long end = System.currentTimeMillis(); //end timer
        double total = (double)(end-start)/1000; //determine total time
        double FinalMemory = ((double)Helper.getMemory()/1000); //determine aprox memory

        Path = Helper.getPath(solution); //getPath returns the array containing moves made in reverse order

        System.out.println("Initial Board:");
        root.print();
        System.out.println();
        System.out.println("Final Board:");
        solution.print();
        System.out.println();
        System.out.print("Moves: ");
        Helper.ReversePrint(Path); //print path in reverse to get it in the right order
        System.out.println("Number of Nodes Expanded: " + Helper.getNodesExpanded()); //number of total nodes, repeats included,
        // nodes per depth aren't expanded repeatedly as already expanded nodes are controlled to be unexpandable after expanding.
        System.out.println("Total Time: " + total); //total time in second, milisecs
        System.out.println("Aprox Memory Usage: " + (FinalMemory + " KB")); //Aprox final memory, not the most accurate
    }

}
