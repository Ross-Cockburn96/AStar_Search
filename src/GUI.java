
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rosss on 11/03/2018.
 */
public class GUI extends JComponent{
    private Graphics2D g2;
    private List<Cave> allCaves = new ArrayList<>();
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        g2.fill(new Rectangle(30,60,20,10));
        g2.drawString("Paths not taken", 60,70);
        g2.setColor(Color.BLUE);
        g2.fill(new Rectangle(30,90,20,10));
        g2.drawString("Attempted paths, nodes are in memory",60,100 );
        g2.setColor(Color.BLACK);
        g2.fill(new Rectangle(30,120, 20, 10));
        g2.drawString("Current Path/Solution Path", 60,130);

        if (!allCaves.isEmpty()){
            for (int i = 0; i< allCaves.size(); i ++){
                allCaves.get(i).paintCave(g2);
            }
        }
    }
    public void setAllCaves(List<Cave> caves){
        allCaves = caves;
    }

}
