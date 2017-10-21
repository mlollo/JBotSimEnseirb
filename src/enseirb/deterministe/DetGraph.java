package enseirb.deterministe;

import jbotsim.Link;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DetGraph implements ClockListener {

    private static final Logger log = Logger.getLogger(DetGraph.class);
    private static final String LOGGER = "[DET][GRAPH]";
    private boolean START  = true;
    private int nbNodes;
    Topology tp;
    List<Link> links = new ArrayList<>();
    List<Link> innerLinks = new ArrayList<>();

    public DetGraph (int nbNodes, int width, int height){
        BasicConfigurator.configure();
        this.nbNodes = nbNodes;
        this.tp = new Topology(width, height);
        this.tp.disableWireless();
        this.tp.addClockListener(this);
        this.tp.setClockSpeed(1000);
    }

    public void createGraph(int x, int y, int radius){
        double angle = 2 * Math.PI / nbNodes;
        for ( int k = 0 ; k < nbNodes ; k++){
            this.tp.addNode(x + radius * Math.cos(angle*k), y + radius * Math.sin(angle*k));
            log.info(String.format("%s[createGraph] node %s", LOGGER, this.tp.getNodes().get(k).getID()));
            if(k >= 1){
                this.tp.addLink(
                        new Link(
                                this.tp.getNodes().get(k-1),
                                this.tp.getNodes().get(k)
                        )
                );
            }
            if (k == nbNodes - 1){
                this.tp.addLink(
                        new Link(
                                this.tp.getNodes().get(k),
                                this.tp.getNodes().get(0)
                        )
                );
            }
        }
        this.links = this.tp.getLinks();
    }

    public void addInnerLinks(int jump) {
        for (int i = 0; i < nbNodes - nbNodes / jump + 1; i = i + nbNodes / jump) {
            if (i < nbNodes - nbNodes / jump) {
                Link innerLink = new Link(this.tp.getNodes().get(i), this.tp.getNodes().get(i + nbNodes / jump));
                this.tp.addLink(innerLink);
                this.innerLinks.add(innerLink);
            }
            if (i == nbNodes - nbNodes / jump) {
                Link innerLink = new Link(this.tp.getNodes().get(i), this.tp.getNodes().get(0));
                this.tp.addLink(innerLink);
                this.innerLinks.add(innerLink);
            }
        }
    }

    public double getDensity() {
        int nbLinks = this.tp.getLinks().size();
        int nbNodes = this.tp.getNodes().size();
        return nbLinks * 2 / (double)(nbNodes*(nbNodes - 1));
    }

    public void onClock(){
        if (this.START) {
            if (this.links.size() != 0) {
                log.trace(String.format("%s[onClock] start",LOGGER));
                this.tp.removeLink(this.links.get(0));
                if (this.innerLinks.size() != 0) {
                    this.tp.removeLink(this.innerLinks.get(0));
                }
                this.START = false;
            }
        }
        dynamicCircularLinks(this.links, false);
        if (innerLinks.size() != 0) {
            dynamicCircularLinks(this.innerLinks, true);
        }
        log.info(String.format("%s[onClock] list links topo : %s", LOGGER, this.tp.getLinks()));
        log.info(String.format("%s[onClock] list outer links saved : %s", LOGGER, this.links));
        log.info(String.format("%s[onClock] list inner links saved : %s", LOGGER, this.innerLinks));
        log.info(String.format("%s[onClock] density : %s", LOGGER, this.getDensity()));
    }

    private void dynamicCircularLinks(List<Link> savedLinks, boolean direction) {
        try {
            savedLinks.forEach(linkSaved -> {
                if (this.tp.getLinks().stream().noneMatch(link -> link.equals(linkSaved))) {
                    int linkSavedIndex = savedLinks.indexOf(linkSaved);
                    log.info(String.format("%s[onClock] link missing : %s", LOGGER, linkSaved));
                    log.info(String.format("%s[onClock] link missing index : %s", LOGGER, linkSavedIndex));
                    if (direction ? linkSavedIndex > 0 : linkSavedIndex < savedLinks.size() - 1) {
                        this.tp.removeLink(savedLinks.get(direction ? linkSavedIndex - 1 : linkSavedIndex + 1));
                    } else {
                        this.tp.removeLink(savedLinks.get(direction ? savedLinks.size() - 1 : 0));
                    }
                    this.tp.addLink(linkSaved);
                    throw new BreakException();
                }
            });
        } catch (BreakException ignored) { }
    }
}
