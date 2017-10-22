package enseirb.deterministe;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DetGraph implements ClockListener{

    private static final Logger log = Logger.getLogger(DetGraph.class);
    private static final String LOGGER = "[DET][GRAPH]";
    private boolean START = true;
    private int nbNodes;
    Topology tp;
    List<Link> links = new ArrayList<>();
    List<Link> innerLinks = new ArrayList<>();

    public DetGraph(int nbNodes, int width, int height) {
        BasicConfigurator.configure();
        this.nbNodes = nbNodes;
        this.tp = new Topology(width, height);
        this.tp.disableWireless();
        this.tp.addClockListener(this);
        this.tp.setClockSpeed(1000);
    }

    public void createGraph(int x, int y, int radius) {
        double angle = 2 * Math.PI / nbNodes;
        for (int k = 0; k < nbNodes; k++) {
            this.tp.addNode(x + radius * Math.cos(angle * k), y + radius * Math.sin(angle * k));
            log.info(String.format("%s[createGraph] node %s", LOGGER, this.tp.getNodes().get(k).getID()));
            if (k >= 1) {
                this.tp.addLink(
                        new Link(
                                this.tp.getNodes().get(k - 1),
                                this.tp.getNodes().get(k)
                        )
                );
            }
            if (k == nbNodes - 1) {
                this.tp.addLink(
                        new Link(
                                this.tp.getNodes().get(k),
                                this.tp.getNodes().get(0)
                        )
                );
            }
        }
        this.links = this.tp.getLinks();
        boolean test = isConnex(this.tp);

        System.out.println("est ce que le graphe est connexe " + test);
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
        return nbLinks * 2 / (double) (nbNodes * (nbNodes - 1));
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
        boolean graphConnex = isConnex(this.tp);

        System.out.println("est ce que le graphe est connexe " + graphConnex);
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
        } catch (BreakException ignored) {
        }
    }


    // Cette fonction permet de dire si un graphe est conenxe ou non
    public boolean isConnex(Topology tp1) {

        List<Node> l1 = tp1.getNodes();
        int nbNodes = l1.size();
        int pel = explorer(l1,l1.get(0));

        if (pel == nbNodes){
            return true;
        }
        else {

            return false;
        }
    }


    // Cette fonction est le parcours en largeur, elle permet de dire si un graphe non orienté est connexe
    public int explorer(List<Node> nd, Node sommet) {
        // création de la file
        List<Node> nd2 = new ArrayList<>();
        nd2.add(sommet);
        System.out.println(" sommet " + sommet);
        System.out.println(" Valeur de nd2 " + nd2);

        // création du marquage, le marquage permet de savoir si on a déja parcouru le noeud dans le graphe
        List<Node> nd3 = new ArrayList<>();
        nd3.add(sommet);
        System.out.println(" Valeur de nd3 " + nd3);

        while (nd2.isEmpty() != true) {
            System.out.println(" dans while");

            // création de la liste des voisins du noeud qu'on est en train de parcourir
            List<Node> nd4 = new ArrayList<>();
            nd4 = nd2.get(0).getNeighbors();
            System.out.println(" Valeur de nd4 " + nd4);


            // on defile la file
            sommet = nd2.remove(nd2.indexOf(nd2.get(0)));
            System.out.println(" Valeur de nd2 " + nd2);

            // pour chaque voisin du noeud que l'on parcourt, on regarde si les voisins sont marqués ou non
            for (Node nd6 : nd4) {
                if (nd3.indexOf(nd6) == -1) {
                    System.out.println(" Valeur de nd6 " + nd6);
                    nd2.add(nd6);
                    nd3.add(nd6);
                }
            }
            System.out.println(" fin du for");

        }

        System.out.println(" ND3333333" + nd3);
        System.out.println(nd3.size());

        return nd3.size();

    }
}
