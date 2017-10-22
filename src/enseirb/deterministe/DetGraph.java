package enseirb.deterministe;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
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


    /***
     * Initialisation de l'objet DetGraph
     * @param nbNodes nombre de noeuds
     * @param width largeur de la fenêtre
     * @param height hauteur de la fenêtre
     * ***/
    public DetGraph (int nbNodes, int width, int height){
        this.nbNodes = nbNodes;
        this.tp = new Topology(width, height);
        this.tp.disableWireless();
        this.tp.addClockListener(this);
        this.tp.setClockSpeed(1000);
    }

    /***
     * Créer une constellation de noeuds circulaire
     * Créer un graphe en sauvegardant les connecteurs Link dans la liste links
     * Modifie l'objet Topology tp qui appartient à l'objet DetGraph
     *
     * @param x abscisse du centre du cercle
     * @param y ordonnées du centre du cercle
     * @param radius rayon du cercle
     * ***/
    public void createGraph(int x, int y, int radius){
        double angle = 2 * Math.PI / nbNodes;
        for ( int k = 0 ; k < nbNodes ; k++){
            /*Création du k-ieme noeuds*/
            this.tp.addNode(x + radius * Math.cos(angle*k), y + radius * Math.sin(angle*k));
            log.debug(String.format("%s[createGraph] node %s", LOGGER, this.tp.getNodes().get(k).getID()));
            /*Création du k-ieme liens entre le noeuds k-1 -> k ou k -> 0*/
            if(k >= 1){
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
        /*Sauvegarde des liens dans l'objet link*/
        this.links = this.tp.getLinks();
        boolean test = isConnex(this.tp);

        log.info(String.format("%s[createGraph] est ce que le graphe est connexe %s", LOGGER, test));
    }

    /***
     * Créer des connecteurs entre certains noeuds du cercle afin de créer un cercle intèrieur
     * Les connecteurs Link dans la liste innerLinks
     * Modifie l'objet Topology tp qui appartient à l'objet DetGraph
     *
     * @param jump nombre de noeuds sauter pour trouver le noeuds destinataire du prochain liens
     * ***/
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
     * Modifie le graphe dynamiquement tout les slot de temps
     * ***/
    public void onClock(){
        if (this.START) {
            /*Start seulement si links contient des objets*/
            if (this.links.size() != 0) {
                log.info(String.format("%s[onClock] start",LOGGER));
                /*On retire le premier lien sauvegarder dans la liste*/
                this.tp.removeLink(this.links.get(0));
                if (this.innerLinks.size() != 0) {
                    /*Si innerLinks contient des objets, on retire le premier lien sauvegarder dans la liste*/
                    this.tp.removeLink(this.innerLinks.get(0));
                }
                this.START = false;
            }
        } else {
            dynamicCircularLinks(this.links, false);
            if (innerLinks.size() != 0) {
                dynamicCircularLinks(this.innerLinks, true);
            }
            log.debug(String.format("%s[onClock] list links topo : %s", LOGGER, this.tp.getLinks()));
            log.debug(String.format("%s[onClock] list outer links saved : %s", LOGGER, this.links));
            log.debug(String.format("%s[onClock] list inner links saved : %s", LOGGER, this.innerLinks));
            log.info(String.format("%s[onClock] density : %s", LOGGER, this.getDensity()));
            boolean graphConnex = isConnex(this.tp);
            log.info(String.format("%s[onClock] est ce que le graphe est connexe %s", LOGGER, graphConnex));
        }
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
}
