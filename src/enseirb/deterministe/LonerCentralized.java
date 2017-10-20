package enseirb.deterministe;

import jbotsim.Link;
import jbotsim.Topology;
import jbotsim.event.ClockListener;

import java.util.List;

public class LonerCentralized implements ClockListener{
    public static boolean START  = true;
    public static int NB_NODE;
    public static Topology tp;
    public static List<Link> links;

    public LonerCentralized(Topology tp, int nbNodes) {
        tp.disableWireless();
        tp.addClockListener(this);
        tp.setClockSpeed(1000);
        NB_NODE = nbNodes;
        LonerCentralized.tp = tp;
        links = tp.getLinks();
        //tp.setDefaultNodeModel(NodeModel.class);
    }

    @Override
    public void onClock() {
        if (START) {
            System.out.println("Start");
            tp.removeLink(links.get(0));
            START = false;
        }
        try {
            links.forEach(linkSaved -> {
                if (tp.getLinks().stream().noneMatch(link -> link.equals(linkSaved))) {
                    int linkSavedIndex = links.indexOf(linkSaved);
                    System.out.println("link missing       : " + linkSaved);
                    System.out.println("link missing index : " + linkSavedIndex);
                    if (linkSavedIndex < NB_NODE - 1) {
                        tp.removeLink(links.get(linkSavedIndex + 1));
                        tp.addLink(linkSaved);
                    } else {
                        tp.removeLink(links.get(0));
                        tp.addLink(linkSaved);
                    }
                    throw new BreakException();
                }
            });
        } catch (BreakException ignored) { }
        System.out.println("list links topo    : " + tp.getLinks());
        System.out.println("list links saved   : " + links);
        System.out.println("density            : " + this.getDensity());
    }

    public double getDensity() {
        int nbLinks = tp.getLinks().size();
        int nbNodes = tp.getNodes().size();
        return nbLinks * 2 / (double)(nbNodes*(nbNodes - 1));
    }
}
