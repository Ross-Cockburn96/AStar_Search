import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * Created by rosss on 05/03/2018.
 */
public class AStar extends JFrame implements KeyListener {
    private static int FASTEST = 0; //this represents a setting to run the search from start to finish without stopping
    private static int STEP = 1;    //this represents a setting to run the search step by step
    private static JFrame gui;
    private static GUI dc = new GUI();
    private boolean finished = false;   //this is set to true if the search has reached the goal node.

    private String filename;
    private String[] cavContents;
    private int noOfCaves;
    private java.util.List<Cave> caves = new ArrayList<Cave>();

    private HashMap<Cave, Double> hscore;   //maps each cave to its hscore
    private HashMap<Cave, Double> gscore;   //maps each cave to its gscore
    private HashMap<Cave, Double> fscore;   //maps each cave to its fscore
    private HashMap<Cave, Cave> parentMap;  //maps each cave to the previous cave in the solution path.

    private boolean[][] connectivityMatrix;

    private List<Cave> openList;
    private List<Cave> closedList;

    private Cave goalCave;

    private LinkedList<Cave> total_path = new LinkedList<>();   //a list of all the caves that exist in the solution path.
    public AStar(int mode, String cavFile){
        setUp(cavFile);
        if(mode == 0) {
            while (!openList.isEmpty() && (!finished)) {    //loops through search steps until goal node is found
                search();
            }
        }
    }
    public void setFileName(String cavFile) {
        filename = cavFile;
    }

    public String getFileName() {
        return filename;
    }
    public void setCavContents() {    //reads input file and
        try {
            //open input.cav
            BufferedReader br = new BufferedReader(new FileReader(filename));
            //Read the line of comma separated text from the file
            String buffer = br.readLine();
            br.close();
            cavContents = buffer.split(",");


        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    private void setNoOfCaves() {
        noOfCaves = Integer.parseInt(cavContents[0]);
    }

    public int getNoOfCaves() {
        return noOfCaves;
    }

    private void createCavesList(){
        int caveCounter = 1;
        for (int count = 1; count < ((noOfCaves*2) +1); count=count + 2){
            caves.add(new Cave(Integer.parseInt(cavContents[count]),Integer.parseInt(cavContents[count+1])));
            caves.get(caveCounter-1).setCaveID(caveCounter);
            caveCounter++;
        }
    }
    private void search(){
        Cave currentNode = lowestFScore();  //current node is the node that had the lowest scoring f-score out of the nodes held in the open list.
        System.out.println("\n<<< Current Node is: " + currentNode + " >>> \n");

        if (currentNode.equals(goalCave)) {     //if the solution is found then draw the path solution
            finished = true;
            System.out.println("Goal node reached, goal node is  " + currentNode);
            //stop search and update the gui
            reconstructPath(currentNode);
            dc.repaint();
            return;
        }
        if (gscore.get(currentNode)<Double.MAX_VALUE){
            System.out.println("GScore is "+ gscore.get(currentNode));
        }else{
            System.out.println("node does not have a gscore yet");
        }
        System.out.println("HScore is " + hscore.get(currentNode));
        openList.remove(currentNode);   //move current node from the open list to the closed list because it has been evaluated
        System.out.println("Open List contains " + openList);
        closedList.add(currentNode);
        System.out.println("Closed List contains " + closedList);
        double neighbourScore;
        System.out.println("This node has neighbours " + currentNode.getNeighbours());
        for (Cave neighbour : currentNode.getNeighbours()) { //loop iterating through each of the neighbours
            System.out.println("analyising Neighour " + neighbour);
            if (closedList.contains(neighbour)) {   //if the neighbour node has already been evaluated then skip to next neighbour
                continue;
            }
            neighbourScore = gscore.get(currentNode) + euclideanDistance(currentNode, neighbour);   //calculate a provisional gscore of this neigbour
            System.out.println("Provisional gScore of neighbour is "+ neighbourScore);
            if (!openList.contains(neighbour)) {    //add this neighbour to the list of discovered nodes if it isn't already there
                openList.add(neighbour);
            }
            if (neighbourScore >= gscore.get(neighbour)) {  //if the new provisional gscore of the neighbour is greater than the current lowest gscore of the neighbour then we continue because this is not a better path
                continue;
            }
            //after we have found a better path we record the new lowest scores.
            System.out.println("This neighbour is a better choice, adding neighbour " + neighbour + " to path");
            parentMap.put(neighbour, currentNode);
            gscore.put(neighbour, neighbourScore);
            fscore.put(neighbour, gscore.get(neighbour) + hscore.get(neighbour));
        }
        //at the end of one search cycle we repaint the gui

        reconstructPath(currentNode);

        dc.repaint();
    }
    private Cave lowestFScore(){    //method that returns the node that is in the openList with the lowest FScore
        double fScore;
        double lowestScore = Double.MAX_VALUE;  //set the lowest score to a high number
        Cave lowestNode = null;
        for (Cave c : openList) {
            if (fscore.get(c) < lowestScore){
                lowestScore = fscore.get(c);
                lowestNode = c;
            }
        }
        return lowestNode;
    }
    private ArrayList<Cave> neighbourFinder (Cave currentNode){ //takes a node as an argument and returns a list of connected nodes
        ArrayList<Cave> neighbours = new ArrayList<Cave>();
        for(Cave c : caves){
            if (areConnected(caves.indexOf(currentNode),caves.indexOf(c))){
                neighbours.add(c);
            }
        }
        return neighbours;

    }
    private boolean areConnected(int cave1, int cave2) {
        boolean areConnected = connectivityMatrix[cave1 ][cave2 ];
        return areConnected;
    }
    private void reconstructPath(Cave currentNode){ //method takes a node and retraces the route from that node back to the start.
        if(!total_path.isEmpty()){
            for(Cave c : total_path){   //updates the state of the nodes that were previously in the path in that last run through of the search.
                c.setIsSolutionBoolean(false);
                if(c.getPrevNodeInSolution()!=null){
                    c.addPreviousSolutionNode(c.getPrevNodeInSolution()); //if a node was part of a solution then it keeps a record of what nodes it is preceded by
                }
                c.setPrevNodeInSolution(null);
                c.markAsInMemory();
            }
        }
        caves.get(0).setIsSolutionBoolean(true); //the first cave in the cavern is always part of the solution
        total_path.clear(); //clear the solution path list
        total_path.add(currentNode);
        while (parentMap.containsKey(currentNode)){ //loops through the contents of the parentmap
            currentNode.setIsSolutionBoolean(true); //each node that is contained in the map is marked as a solution
            if(currentNode.getPreviousSolutionNodes().contains(parentMap.get(currentNode))){    //a cave should only keep a history of nodes that were in a previous solution if they are not part of the current solution
                currentNode.removePreviousSolutionNode(parentMap.get(currentNode));
            }
            currentNode.setPrevNodeInSolution(parentMap.get(currentNode));  //assigns the node that precedes the current node in the solution path
            currentNode = parentMap.get(currentNode);
            total_path.add(currentNode);
        }
    }

    private void setUp(String cavFile){ //initialise variables when program is executed
        setFileName(cavFile);
        setCavContents();
        setNoOfCaves();
        createCavesList();
        hscore = heuristicScore();
        gscore = new HashMap<>();
        fscore = new HashMap<>();
        parentMap = new HashMap<>();
        dc.setAllCaves(caves);
        connectivityMatrix = connectivityMatrixCalc();
        for (Cave c1 : caves){   //every cave should start with a gscore and fscore with a really high number because the aim is the find the minimum score for each node.
            gscore.put(c1, Double.MAX_VALUE);
            fscore.put(c1, Double.MAX_VALUE);
            for (Cave c2 : neighbourFinder(c1)){    //initialises each cave's neighbours
                c1.addNeighbour(c2);
            }
        }
        gscore.put(caves.get(0), (double)0); //the first cave always has a gscore of 0
        fscore.put(caves.get(0), hscore.get(caves.get(0))); //the first cave's score is the hscore of the node only.
        

        openList = new ArrayList<>();
        closedList = new ArrayList<>();
        goalCave = caves.get(caves.size()-1);   //the goal cave is the last cave in the list of caves
        openList.add(caves.get(0));     //the first caves is the only discovered cave when the program is first executed

    }
    private HashMap<Cave, Double> heuristicScore(){     //calculates the euclidean distance between any node and the goal node
        HashMap<Cave,Double> cavesAndScores = new HashMap<Cave, Double>();
        for (Cave c : caves){
            double distanceToGoal = euclideanDistance(c, caves.get(caves.size()-1));
            cavesAndScores.put(c,distanceToGoal);
        }
        return cavesAndScores;
    }
    private double euclideanDistance(Cave c1, Cave c2){
        int x = c1.getX() - c2.getX();
        int y = c1.getY() - c2.getY();
        return Math.sqrt(x*x + y*y);
    }
    private boolean[][] connectivityMatrixCalc() {
        boolean[][] connected = new boolean[noOfCaves][];

        for (int row = 0; row < noOfCaves; row++) {
            connected[row] = new boolean[noOfCaves];
        }
        //Now read in the data - the starting point in the array is after the coordinates
        int col = 0;
        int row = 0;

        for (int point = (noOfCaves * 2) + 1; point < cavContents.length; point++) {
            //Work through the array

            if (cavContents[point].equals("1")) {
                connected[row][col] = true;
            }else
                connected[row][col] = false;

            row++;
            if (row == noOfCaves) {
                row = 0;
                col++;
            }
        }
        return connected;
    }

    public static void main(String [] args){
        AStar aStar;

        if (Objects.equals(args[0], "fastest")){
            aStar =  new AStar(FASTEST, args[1]);
        }
        if (Objects.equals(args[0], "step")) {
            aStar = new AStar(STEP, args[1]);
        }else{
            System.out.println("Mode " + args[0] + " not valid, running as default - fastest mode");
            aStar = new AStar(FASTEST, args[1]);
        }
        AStar finalAStar = aStar;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                guiSetup(finalAStar);
            }
        });
    }
    public static void guiSetup(AStar aStar){
        gui = new JFrame("AStar Search");   //setup gui in event dispatch thread
        gui.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.add(dc);
        gui.addKeyListener(aStar);
        gui.setVisible(true);
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e){ //when the Enter key is pressed the search goes to the next state
        int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_ENTER && finished == false){
            search();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
