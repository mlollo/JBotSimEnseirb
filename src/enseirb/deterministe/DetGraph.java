package enseirb.deterministe;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
import jbotsim.ui.JViewer;
import java.util.List;

public class DetGraph implements ClockListener {

    private int nbNodes;
    private Topology tp;
    private List<Link> links;
    private int HOP = 0;



    public DetGraph (int nbNodes, Topology tp){
        this.nbNodes = nbNodes;
        this.tp = tp;
        this.tp.disableWireless();
        this.tp.addClockListener(this);
        this.tp.setClockSpeed(1000);
    }

    public Topology createGraph(int nbNodes, Topology tp){
        double angle = 2*Math.PI/nbNodes;
        for ( int k = 0 ; k < nbNodes ; k++){
            tp.addNode(200+100*Math.cos(angle*k), 200+100*Math.sin(angle*k));
            System.out.println(" node " + tp.getNodes().get(k).getID());
            if(k >= 1){
                Link tmp = new Link(tp.getNodes().get(k-1),tp.getNodes().get(k));
                tp.addLink(tmp);
                //links.add(tmp);
            }
            if (k == nbNodes - 1){
                Link tmp = new Link(tp.getNodes().get(0),tp.getNodes().get(k));
                tp.addLink(tmp);
            }

            this.links = tp.getLinks();
            System.out.println(links);
        }
        return tp;

    }



    public void onClock(){
        tp.removeLink((Link)links.get(HOP));
        if (this.HOP == 0) {
            Link link = new Link((Node)tp.getNodes().get(this.nbNodes - 1), (Node)tp.getNodes().get(0));
            System.out.println("links added " + link);
            tp.addLink(link);
            ++this.HOP;
        } else if (this.HOP < this.nbNodes - 1) {
            System.out.println("links added " + links.get(this.HOP - 1));
            tp.addLink((Link)links.get(HOP - 1));
            ++this.HOP;
        } else {
            System.out.println("links added " + links.get(this.HOP - 1));
            tp.addLink((Link)links.get(HOP - 1));
            this.HOP = 0;
        }

    }



}
