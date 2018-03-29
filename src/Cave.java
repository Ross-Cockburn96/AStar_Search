import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//this class decides what colour the cave should be and draws the calls the paint method of the cave's neighbouring arrows

public class Cave {
    private int xcoord;
    private int ycoord;
    private int caveID;
    private final int scale = 60;
    private List<Cave> neighbours;
    private boolean partOfSolution = false;
    private List<Cave> previousSolutionNodes;
    private Cave prevNodeInSolution = null;
    private boolean inMem = false;
    private static final double ysize = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    public Cave(int x, int y){  //constructor for the cave object, caves represent nodes
        setX(x);    //sets the x coordinates of the cave
        setY(y);    //sets the y coordinates of the cave
        neighbours  = new ArrayList<>();    //each cave has a list of caves that are connected to it, called neighbours
        previousSolutionNodes = new ArrayList<>();  //each cave keeps a history of neighbour nodes that were historically part of the solution in previous search states
    }
    public void setCaveID(int id){
        this.caveID = id;
    }
    public int getID(){
        return caveID;
    }
    public void setX(int x){
        xcoord = x;
    }
    public void setY(int y){
        ycoord = y;
    }
    public int getX(){
        return xcoord;
    }
    public int getY(){
        return ycoord;
    }
    private static final int screenBuffer = 100;
    public void markAsInMemory() {    //if the node is in memory (but not necessarily part of the solution in the current state of the search) we set a boolean to represent it being in memory
        inMem = true;
    }
    public void addPreviousSolutionNode(Cave c){
        previousSolutionNodes.add(c);
    }
    public void removePreviousSolutionNode(Cave c){
        previousSolutionNodes.remove(c);
    }
    public List<Cave> getPreviousSolutionNodes(){
        return previousSolutionNodes;
    }
    public void addNeighbour(Cave cave){
        neighbours.add(cave);
    }
    public List<Cave> getNeighbours(){
        return neighbours;
    }
    public void setPrevNodeInSolution(Cave c){
        prevNodeInSolution = c;
    }
    public Cave getPrevNodeInSolution(){
        return prevNodeInSolution;
    }
    @Override
    public String toString(){
        return("(" + xcoord + "," + ycoord+")");
    }
    public void setIsSolutionBoolean(Boolean bool){
        partOfSolution = bool;
    }
    public void paintCave(Graphics2D g){
        if(!partOfSolution){      //if statements determine what colour the node should be in the gui
            if(inMem){  //if the node is not part of the current-state solution but it is in memory then we colour it blue
                g.setColor(Color.BLUE);
            }else{
                g.setColor(Color.RED);  //if the node is not part of the current-state solution and is not in memory(i.e was never a solution in any search state) we colour it red
            }
        }else{
            g.setColor(Color.BLACK);    //if the node is part of the current-state solution then we colour it black
        }
        g.fill(new Rectangle(getX()*scale+screenBuffer,(int)ysize-(getY()*scale)-screenBuffer,10,10));
        String str = (Integer.toString(this.getID()));
        g.setFont(new Font("TimesRoman",Font.BOLD,16));
        g.drawString(str,getX()*scale+screenBuffer+5,(int)ysize -(getY()*scale)-screenBuffer-15);

        for(Cave c : getNeighbours()){  //for each neighbour of the the cave we create and draw an arrow
            Arrow arrow = new Arrow(this,c);
            arrow.paintArrow(g);
        }
    }

}
