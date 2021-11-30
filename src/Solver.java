/*
 *  Your test client should take the name of an input file as a command-line
 *  argument and print the minimum number of moves to solve the puzzle and the sequence of
 *  boards from the initial one to the solution
 *
 *  We define a search
 *  node of the game to consist of the following elements: a board, the number of moves made
 *  to reach the board, and a pointer to its parent in the game tree (defined below)
 *
 *
 *
 * 
 *
 * @author Alessandro Viespoli
 * @version
 *
 */

import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.Vector;
//TODO don’t enqueue a neighbor if its board is the same as the board of the previous search node in the game tree.
//TODO controllare se 2 nodi hanno la stessa prio, quale delle 2 devo guardare?
//TODO aggiungere un altra priorità
//TODO vedere e chiedere a Edo se conviene fare tutto in 1 solo oggetto Board

public class Solver {

    private static class SearchNode {
        private Board b;
        private int moves;
        private SearchNode parent;
        private int priority;

        public SearchNode(Board b, int m, SearchNode p) {
            this.b = b;
            moves = m;
            parent = p;
            priority = m + b.manhattan();
        }

        public Board getBoard() { return b; }
        public int getMoves() { return moves; }
        public int getPriority() { return priority; }
        public SearchNode getParent() { return parent; }

        //Problema con generateSons, è come se non li creasse
        public Vector<SearchNode> generateSons() {
            Vector<SearchNode> figli = new Vector<SearchNode>();
            if(b.get0Row() - 1 >= 0) {
                int nRow = b.get0Row() - 1; 
                Board b1 = new Board(b);
                b1.swap0(nRow, b.get0Col());
                if(!b1.isInitial(inizio) || !getParent().toString().equals(b1.toString())) {
                    SearchNode s1 = new SearchNode(b1, getMoves() + 1, this);
                    figli.add(s1);
                } 
            }
            if(b.get0Row() + 1 < b.getLength()) {
                int nRow = b.get0Row() + 1;
                Board b2 = new Board(b);
                b2.swap0(nRow, b.get0Col());
                if(!b2.isInitial(inizio) || !getParent().toString().equals(b2.toString())) {
                    SearchNode s2 = new SearchNode(b2, getMoves() + 1, this);
                    figli.add(s2);
                }
            }
            if(b.get0Col() + 1 < b.getLength()) {
                int nCol = b.get0Col() + 1;
                Board b3 = new Board(b);
                b3.swap0(b.get0Row(), nCol);
                if(!b3.isInitial(inizio)  || !getParent().toString().equals(b3.toString())) {
                    SearchNode s3 = new SearchNode(b3, getMoves() + 1, this);
                    figli.add(s3);
                }
            }
            if(b.get0Col() - 1 >= 0){
                int nCol = b.get0Col() - 1;
                Board b4 = new Board(b);
                b4.swap0(b.get0Row(), nCol);
                if(!b4.isInitial(inizio)  || !getParent().toString().equals(b4.toString())) {
                    SearchNode s4 = new SearchNode(b4, getMoves() + 1, this);
                    figli.add(s4);
                }
            }
            return figli;
        }
    }

    //This class allows the priority queue to know which node has the priority over another node
    private static class BoardComparator implements Comparator<SearchNode> {
        public int compare(SearchNode b1, SearchNode b2) {
            if(b1.getPriority() == b2.getPriority()) return 0;
            else if(b1.getPriority() < b2.getPriority()) return -1;
            return 1;
        }
    }

    //Variabile globale per il goal
    public static String goal = "";
    public static String inizio = "";

    //Method to generate the goal node in string
    public static void generateGoal(int n) {
        for(int i = 1; i < n*n; i++) {
            goal += i + " ";
        }
        goal += 0 + " ";  
    }

    //Generate the root in the game tree
    public static void generateRoot(Board root) { inizio = root.toString(); }

    //is the SearchNode the goal Node?
    public static boolean isGoal(SearchNode gamenode) { 
        return gamenode.getBoard().toString().equals(goal);
    }

    // test client (see below)
    public static void main(String[] args)  throws FileNotFoundException {
        long start = System.nanoTime();
        Scanner in = new Scanner(new FileReader(args[0]));
        int n = in.nextInt();
        generateGoal(n);
        int[] strra = new int[(n*n)];
        for(int i = 0; i < strra.length; i++) {
            strra[i] = in.nextInt();
        }
        in.close();
        int[][] matrix = new int[n][n];
        int k = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = strra[k++];
            } 
        }

        //Resolution of the problem
        Board init_board = new Board(matrix);
        SearchNode x = new SearchNode(init_board, 0, null); // si potrebbe fare new SearchNode(new Board(matrix), 0, null);
        generateRoot(init_board);
        PriorityQueue<SearchNode> q = new PriorityQueue<SearchNode>(new BoardComparator());
        while(!isGoal(x)) {
            Vector<SearchNode> sons = x.generateSons();
            for(int i = 0; i < sons.size(); i++) {
                q.add(sons.elementAt(i));
            }
            x = q.poll(); 
        }

        //Printing the request, go look the proper PDF in the directory
        System.out.println(x.getMoves());
        String[] stampa = new String[x.getMoves() + 1];
        int indx = x.getMoves(); 
        stampa[0] = init_board.toString();
        while(x.getParent() != null) {
            stampa[indx--] = x.getBoard().toString(); 
            x = x.getParent();
        }
        for(int i = 0; i < stampa.length; i++) {
            System.out.println(stampa[i]);
        }
        long finish = System.nanoTime();
        System.out.println((double)(finish - start)/1000000000l);
    
    }
}
