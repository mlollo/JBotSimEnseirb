package enseirb.random;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsimx.Connectivity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
     * @param density densité de la Topology
     * @param x abscisse du centre du cercle
     * @param y ordonnées du centre du cercle
     * @param radius rayon du cercle
     * ***/
    public static List<Link> generateRing(Topology tp, Node leader, Class<? extends Node> anonymous, int nbNodes, double density, int x, int y, int radius) throws InstantiationException, IllegalAccessException {
        double angle = 2 * Math.PI / nbNodes;
        Random numberRandom = new Random();
        boolean isconnect = false;
        long nbLinks =  Math.round(density * nbNodes*(nbNodes-1) / 2);

        while(!isconnect) {
            for (int k = 0; k < nbNodes; k++) {
                /*Création du k-ieme noeuds*/
                if (k == 0) {
                    tp.addNode(x + radius * Math.cos(angle * k), y + radius * Math.sin(angle * k), leader);
                } else {
                    tp.addNode(x + radius * Math.cos(angle * k), y + radius * Math.sin(angle * k), anonymous.newInstance());
                }
                log.debug(String.format("%s[createGraph] node %s", LOGGER, tp.getNodes().get(k).getID()));
            }
            isconnect = genereteLinks(tp, nbNodes, nbLinks, numberRandom, isconnect);
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
    public static List<Link> generateRing(Topology tp, Class<? extends Node> node, int nbNodes, double density, int x, int y, int radius) throws InstantiationException, IllegalAccessException{
        double angle = 2 * Math.PI / nbNodes;
        Random numberRandom = new Random();
        boolean isconnect = false;
        long nbLinks =  Math.round(density * nbNodes*(nbNodes-1) / 2);

        while(!isconnect) {
            for (int k = 0; k < nbNodes; k++) {
                /*Création du k-ieme noeuds*/
                tp.addNode(x + radius * Math.cos(angle * k), y + radius * Math.sin(angle * k), node.newInstance());
                log.debug(String.format("%s[createGraph] node %s", LOGGER, tp.getNodes().get(k).getID()));
            }
            isconnect = genereteLinks(tp, nbNodes, nbLinks, numberRandom, isconnect);
        }
        /*Return des liens dans l'objet link*/
        return tp.getLinks();
    }

    private static boolean genereteLinks(Topology tp, int nbNodes, long nbLinks, Random numberRandom, boolean isconnect) {
        for ( int link = 0 ; link < nbLinks ; link++) {
            int random1 = numberRandom.nextInt(nbNodes);
            int random2 = numberRandom.nextInt(nbNodes);
            log.debug(String.format("%s rand1 %s", LOGGER,random1));
            log.debug(String.format("%s rand2 %s", LOGGER, random2));
            if (random1 != random2) {
                tp.addLink(
                        new Link(
                                tp.getNodes().get(random1),
                                tp.getNodes().get(random2)
                        )
                );
            }
            if (Connectivity.isConnected(tp)) {
                isconnect = true;
            } else {
                tp.restart();
            }
        }
        return isconnect;
    }
}
