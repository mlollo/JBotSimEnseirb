package enseirb.deterministic;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DynamicTopologyGenerator {

    private static final Logger log = Logger.getLogger(DynamicTopologyGenerator.class);
    private static final String LOGGER = "[Dynamic][Topology][Generator]";

    public DynamicTopologyGenerator(){ }

    public static double generateRound(double k, double c, double delta) { return k* Math.ceil(Math.pow(2*delta,k)*(c +1)*Math.log(k)); }

    /***
     * Créer une constellation de noeuds circulaire
     * Créer un graphe en sauvegardant les connecteurs Link dans la liste links
     * Modifie l'objet Topology tp qui appartient à l'objet DynamicTopology
     *
     * @param tp aTopology
     * @param leader Noeud leader de l'algorithme
     * @param anonymous Noeud anonyme de l'algorithme
     * @param nbNodes nombre de noeuds de la Topology
     * @param x abscisse du centre du cercle
     * @param y ordonnées du centre du cercle
     * @param radius rayon du cercle
     * ***/
    public static List<Link> generateRing(Topology tp, Node leader, Class<? extends Node> anonymous, int nbNodes, int x, int y, int radius) throws InstantiationException, IllegalAccessException {
        double angle = 2 * Math.PI / nbNodes;

        for ( int k = 0 ; k < nbNodes ; k++){
            /*Création du k-ieme noeuds*/
            if (k == 0) {
                tp.addNode(x + radius * Math.cos(angle*k), y + radius * Math.sin(angle*k), leader);
                log.debug(String.format("%s[createGraph] node leader %s", LOGGER, tp.getNodes().get(k).getID()));
            } else {
                tp.addNode(x + radius * Math.cos(angle*k), y + radius * Math.sin(angle*k), anonymous.newInstance());
                log.debug(String.format("%s[createGraph] node anonymous %s", LOGGER, tp.getNodes().get(k).getID()));
            }

            /*Création du k-ieme liens entre le noeuds k-1 -> k ou k -> 0*/
            if(k >= 1){
                tp.addLink(
                        new Link(
                                tp.getNodes().get(k - 1),
                                tp.getNodes().get(k)
                        )
                );
            }
            if (k == nbNodes - 1) {
                tp.addLink(
                        new Link(
                                tp.getNodes().get(k),
                                tp.getNodes().get(0)
                        )
                );
            }
        }
        /*Return des liens dans l'objet link*/
        return tp.getLinks();
    }

    /***
     * Créer une constellation de noeuds circulaire
     * Créer un graphe en sauvegardant les connecteurs Link dans la liste links
     * Modifie l'objet Topology tp qui appartient à l'objet DynamicTopology
     *
     * @param x abscisse du centre du cercle
     * @param y ordonnées du centre du cercle
     * @param radius rayon du cercle
     * ***/
    public static List<Link> generateRing(Topology tp, Class<? extends Node> node, int nbNodes, int x, int y, int radius) throws InstantiationException, IllegalAccessException{
        double angle = 2 * Math.PI / nbNodes;

        for ( int k = 0 ; k < nbNodes ; k++){
            /*Création du k-ieme noeuds*/
            tp.addNode(x + radius * Math.cos(angle*k), y + radius * Math.sin(angle*k), node.newInstance());
            log.debug(String.format("%s[generateRing] node %s", LOGGER, tp.getNodes().get(k).getID()));

            /*Création du k-ieme liens entre le noeuds k-1 -> k ou k -> 0*/
            if(k >= 1){
                tp.addLink(
                        new Link(
                                tp.getNodes().get(k - 1),
                                tp.getNodes().get(k)
                        )
                );
            }
            if (k == nbNodes - 1) {
                tp.addLink(
                        new Link(
                                tp.getNodes().get(k),
                                tp.getNodes().get(0)
                        )
                );
            }
        }
        /*Return des liens*/
        return tp.getLinks();
    }


    /***
     * Créer des connecteurs entre certains noeuds du cercle afin de créer un cercle intèrieur
     * Modifie l'objet Topology tp
     * Method à appeler après generateRing
     *
     * @param jump nombre de noeuds sauter pour trouver le noeuds destinataire du prochain liens
     * ***/
    public static List<Link> addInnerRing(Topology tp, int nbNodes, int jump) {
        List<Link> innerLinks = new ArrayList<>();

        for (int i = 0; i < nbNodes - nbNodes / jump + 1; i = i + nbNodes / jump) {
            if (i < nbNodes - nbNodes / jump) {
                Link innerLink = new Link(
                        tp.getNodes().get(i),
                        tp.getNodes().get(i + nbNodes / jump)
                );
                tp.addLink(innerLink);
                innerLinks.add(innerLink);
            }
            if (i == nbNodes - nbNodes / jump) {
                Link innerLink = new Link(
                        tp.getNodes().get(i),
                        tp.getNodes().get(0)
                );
                tp.addLink(innerLink);
                innerLinks.add(innerLink);
            }
        }
        /*Return des liens intèrieurs*/
        return innerLinks;
    }
}
