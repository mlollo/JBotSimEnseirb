package enseirb.deterministe;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
import jbotsim.event.StartListener;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DynamicTopology implements StartListener, ClockListener{

    private static final Logger log = Logger.getLogger(DynamicTopology.class);
    private static final String LOGGER = "[Dynamic][Topology]";

    private Topology tp;
    private List<Link> links = new ArrayList<>();
    private List<Link> innerLinks = new ArrayList<>();

    /***
     * Initialisation de l'objet DynamicTopology
     * @param tp nombre de noeuds
     * @param links outer links
     * ***/
    public DynamicTopology(Topology tp, List<Link> links){
        this.tp = tp;
        this.links = links;
    }

    /***
     * Initialisation de l'objet DynamicTopology
     * @param tp nombre de noeuds
     * @param links outer links
     * @param innerLinks inner links
     * ***/
    public DynamicTopology(Topology tp, List<Link> links, List<Link> innerLinks){
        this.tp = tp;
        this.links = links;
        this.innerLinks = innerLinks;
    }

    public void onStart(){
        log.info(String.format("%s[onStart] Topology start", LOGGER));

        /*Start seulement si links contient des objets*/
        if (this.links.size() != 0) {
            /*On retire le premier lien sauvegarder dans la liste*/
            this.tp.removeLink(this.links.get(0));

            if (this.innerLinks.size() != 0) {
                /*Si innerLinks contient des objets, on retire le premier lien sauvegarder dans la liste*/
                this.tp.removeLink(this.innerLinks.get(0));
            }
        }
    }

    /***
     * Modifie le graphe dynamiquement tout les slot de temps
     * ***/
    public void onClock(){
        this.dynamicCircularLinks(this.links, false);
        if (this.innerLinks.size() != 0) {
            this.dynamicCircularLinks(this.innerLinks, true);
        }
        this.onClockLogs();
    }

    /***
     * Retire un connecteur et en ajoute un de façon à ce que le graphe reste connexe
     *
     * @param savedLinks liste des liens à rendre dynamique
     * @param direction sens giratoire
     * ***/
    private void dynamicCircularLinks(List<Link> savedLinks, boolean direction) {
        try {
            /*Pour tout les liens dans savedLinks*/
            savedLinks.forEach(linkSaved -> {
                /*Si aucun liens de tp.getLinks() match avec linkSaved alors :*/
                if (this.tp.getLinks().stream().noneMatch(link -> link.equals(linkSaved))) {
                    /*On sauvegarde l'index de linkSaved dans la liste savedLinks*/
                    int linkSavedIndex = savedLinks.indexOf(linkSaved);
                    log.debug(String.format("%s[onClock] link missing : %s", LOGGER, linkSaved));
                    log.debug(String.format("%s[onClock] link missing index : %s", LOGGER, linkSavedIndex));
                    /*En fonction de la direction on teste si cet index n'est pas le premier ou le dernier*/
                    if (direction ? linkSavedIndex > 0 : linkSavedIndex < savedLinks.size() - 1) {
                        /*On retire soit le liens précédent ou suivant cet index présent dans la liste savedLinks*/
                        this.tp.removeLink(savedLinks.get(direction ? linkSavedIndex - 1 : linkSavedIndex + 1));
                    } else {
                        /*On retire soit le dernier ou le premier liens présent dans savedLinks*/
                        this.tp.removeLink(savedLinks.get(direction ? savedLinks.size() - 1 : 0));
                    }
                    /*On ajoute le lien qui ne se trouve pas dans tp.getLinks()*/
                    this.tp.addLink(linkSaved);
                    /*On sort du forEach en déclenchant un exception capter par le try catch*/
                    throw new BreakException();
                }
            });
        } catch (BreakException ignored) { }
    }

    /***
     * Calcul de la densité du graphe
     * @return densité
     * ***/
    public double getDensity() {
        int nbLinks = this.tp.getLinks().size();
        int nbNodes = this.tp.getNodes().size();
        return nbLinks * 2 / (double) (nbNodes * (nbNodes - 1));
    }

    /***
     * Cette fonction permet de dire si un graphe est connexe ou non
     * @return boolean
     * ***/
    public boolean isConnexe(Topology tp1) { return explorer(tp1.getNodes(), tp1.getNodes().get(0)) == tp1.getNodes().size(); }


    /***
     * Cette fonction est le parcours en largeur, elle permet de dire si un graphe non orienté est connexe
     * @return densité
     * ***/
    public int explorer(List<Node> nd, Node sommet) {
        // création de la file
        List<Node> nd2 = new ArrayList<>();
        nd2.add(sommet);
        log.debug(String.format("%s[explorer] sommet %s", LOGGER, sommet));
        log.debug(String.format("%s[explorer] Valeur de nd2 %s", LOGGER, nd2));

        // création du marquage, le marquage permet de savoir si on a déja parcouru le noeud dans le graphe
        List<Node> nd3 = new ArrayList<>();
        nd3.add(sommet);
        log.debug(String.format("%s[explorer] Valeur de nd3 %s", LOGGER, nd3));

        while (!nd2.isEmpty()) {
            log.debug(String.format("%s[explorer] dans while", LOGGER));

            // création de la liste des voisins du noeud qu'on est en train de parcourir
            List<Node> nd4 = new ArrayList<>();
            nd4 = nd2.get(0).getNeighbors();
            log.debug(String.format("%s[explorer] Valeur de nd4 %s", LOGGER, nd4));


            // on defile la file
            sommet = nd2.remove(nd2.indexOf(nd2.get(0)));
            log.debug(String.format("%s[explorer] Valeur de nd2 %s", LOGGER, nd2));

            // pour chaque voisin du noeud que l'on parcourt, on regarde si les voisins sont marqués ou non
            for (Node nd6 : nd4) {
                if (nd3.indexOf(nd6) == -1) {
                    log.debug(String.format("%s[explorer] Valeur de nd6 %s", LOGGER, nd6));
                    nd2.add(nd6);
                    nd3.add(nd6);
                }
            }
            log.debug(String.format("%s[explorer] fin du for", LOGGER));

        }

        log.debug(String.format("%s[explorer] ND3333333 %s", LOGGER, nd3));
        log.debug(String.format("%s[explorer] %s", LOGGER, nd3.size()));

        return nd3.size();
    }

    private void onClockLogs() {
        log.debug(String.format("%s[onClock] list links topo : %s", LOGGER, this.tp.getLinks()));
        log.debug(String.format("%s[onClock] list outer links saved : %s", LOGGER, this.links));
        log.debug(String.format("%s[onClock] list inner links saved : %s", LOGGER, this.innerLinks));
        log.debug(String.format("%s[onClock] density : %s", LOGGER, this.getDensity()));
        log.debug(String.format("%s[onClock] est ce que le graphe est connexe %s", LOGGER, isConnexe(this.tp)));
    }
}
