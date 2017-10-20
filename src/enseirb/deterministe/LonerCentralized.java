package enseirb.deterministe;

import jbotsim.Link;
import jbotsim.Topology;
import jbotsim.event.ClockListener;

import java.util.ArrayList;
import java.util.List;

public class LonerCentralized implements ClockListener{
    public static int HOP  = 0;
    public static int NB_NODE;
    public static Topology tp;
    public static List<Link> links;

    public LonerCentralized(Topology tp, int nbNodes) {
        tp.disableWireless();
        tp.addClockListener(this);
        tp.setClockSpeed(1000);
        NB_NODE = nbNodes;
        this.tp = tp;
        this.links = tp.getLinks();
        //tp.setDefaultNodeModel(NodeModel.class);
    }

    @Override
    public void onClock() {
        System.out.println("Hello " + HOP);
        System.out.println("list links " + tp.getLinks());
        System.out.println("list links saved " + links);

        System.out.println("links removed " + links.get(HOP));
        tp.removeLink(links.get(HOP));
        if (HOP == 0) {
            Link link = new Link(tp.getNodes().get(NB_NODE-1), tp.getNodes().get(0));
            System.out.println("links added " + link);
            tp.addLink(link);
            HOP++;
        } else if (HOP < NB_NODE - 1) {
            System.out.println("links added " + links.get(HOP - 1));
            tp.addLink(links.get(HOP-1));
            HOP++;
        } else {
            System.out.println("links added " + links.get(HOP - 1));
            tp.addLink(links.get(HOP-1));
            HOP = 0;
        }
    }
}
