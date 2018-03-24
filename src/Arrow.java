import javax.tools.Tool;
import java.awt.*;

import static java.lang.Math.abs;

/**
 * Created by rosss on 14/03/2018.
 */
public class Arrow {
    private Cave from;
    private Cave to;
    private static int scale = 60;
    private int offsetYPosition = 10;
    private int offsetXPosition = 10;
    private static final double ysize = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final int screenBuffer = 100;

    public Arrow(Cave from, Cave to){ //constructor consists of a origin cave, destination cave and the colour of the cave
        setFrom(from);
        setTo(to);
    }
    public Cave getFrom() {
        return from;
    }

    public void setFrom(Cave from) {
        this.from = from;
    }

    public Cave getTo() {
        return to;
    }

    public void setTo(Cave to) {
        this.to = to;
    }

    public void paintArrow(Graphics2D g){   //method that decides what colour the arrow should be and draws it on the gui
        offsetXPosition = offsetXPosition - (getFrom().getX() - getTo().getX());
        offsetYPosition = offsetYPosition - (getFrom().getY() - getTo().getY());
        try {   //try/catch for null pointer case
            if (getTo().getPrevNodeInSolution().equals(getFrom())) { //the arrow is black if the origin cave is the cave that comes before the destination cave in the current-state solution
                g.setColor(Color.BLACK);
            }else if(getTo().getPreviousSolutionNodes().contains(getFrom())) {  //the arrow is blue if the origin cave came before the destination cave in one of the search's previous states
                g.setColor(Color.BLUE);
            }else { //the arrow is red if the origin cave is not part of the current-state solution and was never part of any previous solution
                g.setColor(Color.RED);
            }
        }catch(NullPointerException e){    //happens in the case where no solution cave comes before the destination cave
            if(getTo().getPreviousSolutionNodes().contains(getFrom())){     //in this situation we mark it as blue if the origin cave was part of the destination's solution path in a previous search state.
                g.setColor(Color.BLUE);
            }else{  //if in all states including current-state the origin was never part of the solution-path of the destination then we draw the arrow as red
                g.setColor(Color.RED);
            }
        }
        if((g.getColor().equals(Color.BLACK))||g.getColor().equals(Color.BLUE)){ //these statements attempt to make the arrows less cluttered (could be improved)
            drawArrowLine(g,(getFrom().getX()*scale)+5+offsetXPosition +screenBuffer,(int)ysize-(getFrom().getY()*scale)+offsetYPosition-screenBuffer,(getTo().getX()*scale)+5+offsetXPosition+screenBuffer,(int)ysize-(getTo().getY()*scale)+offsetYPosition-screenBuffer,15,15);
        }else if (g.getColor().equals(Color.RED)){
            drawArrowLine(g,(getFrom().getX()*scale)+5 +screenBuffer,(int)ysize-(getFrom().getY()*scale)-screenBuffer,(getTo().getX()*scale)+5+screenBuffer,(int)ysize-(getTo().getY()*scale)-screenBuffer,15,15);

        }
    }
    private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {//https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.drawPolygon(xpoints, ypoints, 3);
    }


}
