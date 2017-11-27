package enseirb.generator;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsimx.Connectivity;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

public class DynamicTopologyGenerator {

    private static final Logger log = Logger.getLogger(DynamicTopologyGenerator.class);
    private static final String LOGGER = "[Dynamic][Topology][Generator]";

    public DynamicTopologyGenerator(){ }

    public static double generateRound(double k, double c, double delta) { return k* Math.ceil(Math.pow(2*delta,k)*(c +1)*Math.log(k)); }
    public static double getDensity(Topology tp, int nbNodes) { return (double)tp.getLinks().size() / (double)((nbNodes * (nbNodes - 1)) / 2); }
    /***
     * Créer une constellation de noeuds circulaire
     * Créer un graphe en sauvegardant les connecteurs Link dans la liste links
     * Modifie l'objet Topology tp qui appartient à l'objet DynamicNet
     *
     * @param tp aTopology
     * @param leader Noeud leader de l'algorithme
     * @param anonymous Noeud anonyme de l'algorithme
     * @param nbNodes nombre de noeuds de la Topology
     * @param x abscisse du centre du cercle
     * @param y ordonnées du centre du cercle
     * @param radius rayon du cercle
     * ***/
    public static List<Link> generateCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        generateLinksCircle(tp, nbNodes);
        return tp.getLinks();
    }

    public static List<Link> generateDenseCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, double density, int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        Random numberRandom = new Random();
        boolean isconnect = false;
        long nbLinks =  Math.round(density * nbNodes*(nbNodes-1) / 2);

        while(!isconnect) {
            isconnect = generateLinksDenseCircle(tp, nbNodes, nbLinks, numberRandom, isconnect);
        }
        return tp.getLinks();
    }

    public static List<Link> generateFairCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, double density, double errDensity, int delta,int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        Random numberRandom = new Random();
        boolean isconnect = false;
        long timer = System.currentTimeMillis() + 1500;
        long nbLinks = Math.round(density * nbNodes * (nbNodes - 1) / 2);

        isconnect = generateLinksFairCircle(tp, nbNodes, nbLinks, delta, numberRandom, isconnect);
        while(!isconnect || Math.abs(density - getDensity(tp, nbNodes)) > errDensity) {
            if (timer - System.currentTimeMillis() <= 0 ) {
                log.info(String.format("%s timeout", LOGGER));
                numberRandom = new Random();
                timer = System.currentTimeMillis() + 1500;
            }
            isconnect = generateLinksFairCircle(tp, nbNodes, nbLinks, delta, numberRandom, isconnect);
        }
        return tp.getLinks();
    }


    private static void generateNodesCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, int x, int y, int radius){
        double angle = 2 * Math.PI / nbNodes;
        for (int k = 0 ; k < nbNodes ; k++){
            //*Création du k-ieme noeuds*//*
            if (k == 0) {
                tp.addNode(x + radius * Math.cos(angle*k), y + radius * Math.sin(angle*k), leader);
                log.debug(String.format("%s[createGraph] node leader %s", LOGGER, tp.getNodes().get(k).getID()));
            } else {
                tp.addNode(x + radius * Math.cos(angle*k), y + radius * Math.sin(angle*k), anonymous.get(k));
                log.debug(String.format("%s[createGraph] node anonymous %s", LOGGER, tp.getNodes().get(k).getID()));
            }
        }
    }

    private static boolean generateLinksFairCircle(Topology tp, int nbNodes, long nbLinks, int delta, Random numberRandom, boolean isconnect) {
        for (int link = 0 ; link < nbLinks ; link++) {
            int random1 = numberRandom.nextInt(nbNodes);
            int random2 = numberRandom.nextInt(nbNodes);
            //log.debug(String.format("%s rand1 %s", LOGGER,random1));
            //log.debug(String.format("%s rand2 %s", LOGGER, random2));
            //System.out.println(getDensity(tp, nbNodes));

            if (random1 != random2 && tp.getNodes().get(random1).getNeighbors().size() < delta && tp.getNodes().get(random2).getNeighbors().size() < delta) {
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

    private static void generateLinksCircle(Topology tp, int nbNodes) {
        /*Création du k-ieme liens entre le noeuds k-1 -> k ou k -> 0*/
        for (int k = 0 ; k < nbNodes ; k++) {
            if (k >= 1) {
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
    }

    private static boolean generateLinksDenseCircle(Topology tp, int nbNodes, long nbLinks, Random numberRandom, boolean isconnect) {
        for (int link = 0 ; link < nbLinks ; link++) {
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


    public static List<Link> addRegularInnerLinks(Topology tp, int nbNodes, int jump) {
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
        //*Return des liens intèrieurs*//*
        return innerLinks;
    }
}
