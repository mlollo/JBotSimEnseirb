package enseirb.generator;

import enseirb.ic.NodeAnonymous;
import enseirb.ic.NodeLeader;
import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsimx.Connectivity;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TopologyGenerator {

    private static final Logger log = Logger.getLogger(TopologyGenerator.class);
    private static final String LOGGER = "[Topology][Generator]";

    public TopologyGenerator(){ }

    public static double getDensity(Topology tp, int nbNodes) { return (double)tp.getLinks().size() / (double)((nbNodes * (nbNodes - 1)) / 2); }
    public static double getAverageDelta(Topology tp) {
        int delta = 0;
        for (int i = 0; i < tp.getNodes().size(); i++) {
            delta = tp.getNodes().get(i).getNeighbors().size() + delta;
        }
        return (double)delta/(double)tp.getNodes().size();
    }
    /***
     * Créer une constellation de noeuds circulaire
     * Créer un graphe en sauvegardant les connecteurs Link dans la liste links
     * Modifie l'objet Topology tp qui appartient à l'objet DynamicGraph
     *
     * @param tp aTopology
     * @param leader Noeud leader de l'algorithme
     * @param anonymous Noeud anonyme de l'algorithme
     * @param nbNodes nombre de noeuds de la Topology
     * @param x abscisse du centre du cercle
     * @param y ordonnées du centre du cercle
     * @param radius rayon du cercle
     * ***/
    public static void generateCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        generateLinksCircle(tp, nbNodes);
    }

    public static void generateTwoCircle(Topology tp, Node leader, List<? extends Node> anonymous, Node leader1, List<? extends Node> anonymous1, int nbNodes, int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x + x/2, y, radius);
        generateNodesCircle(tp, leader1, anonymous1, nbNodes, x - x/2, y, radius);
        generateLinksTwoCircle(tp, nbNodes);
    }

    public static void generateLine(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        generateLinksLine(tp, nbNodes);
    }

    public static void generateStar(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, int x, int y, int radius) {
        generateNodesStar(tp, leader, anonymous, nbNodes, x, y, radius);
        generateLinksStar(tp, nbNodes);
    }

    public static void generateDenseCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, double density, long seed, int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        Random numberRandom = (seed == 0) ? new Random() : new Random(seed);
        boolean isconnect = false;
        long nbLinks =  Math.round(density * nbNodes*(nbNodes-1) / 2);
        log.debug(String.format("%s[Dense Circle] nblinks %s density %s", LOGGER, nbLinks, density));
        long count = 0;
        while(!isconnect) {
            generateLinksDenseCircle(tp, nbNodes, nbLinks, numberRandom);
            if (Connectivity.isConnected(tp)) {
                isconnect = true;
            } else {
                tp.clear();
                generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
            }
            count++;
            if (count > 100000){
                log.info(String.format("%s Input Density %s isn't possible with %s nodes", LOGGER, density, nbNodes));
                System.exit(1);
            }
        }
        log.info(String.format("%s[Dense Circle] Output Density %f size links %s", LOGGER, (float)getDensity(tp, nbNodes), tp.getLinks().size()));
    }

    public static void generateRandomDenseCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, double density, double err, long seed, int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        Random numberRandom = (seed == 0) ? new Random() : new Random(seed);
        boolean isconnect = false;
        int minDensity = (int) Math.round(1000 *(density - err));
        int maxDensity = (int) Math.round(1000 *(density + err));
        double randomDensity = ThreadLocalRandom.current().nextInt(minDensity, maxDensity + 1) / (double)1000;
        long nbLinks =  Math.round(randomDensity * nbNodes*(nbNodes-1) / 2);
        log.info(String.format("%s[Dense Circle] nblinks %s density %s err %s input random density %s", LOGGER, nbLinks, density, err, randomDensity));
        long count = 0;
        while(!isconnect) {
            generateLinksDenseCircle(tp, nbNodes, nbLinks, numberRandom);
            if (Connectivity.isConnected(tp)) {
                isconnect = true;
            } else if (Math.abs(getDensity(tp, nbNodes) - randomDensity) > err){
                tp.clear();
                generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
            }
            count++;
            if (count > 100000){
                log.info(String.format("%s Input Density %s isn't possible with %s nodes", LOGGER, density, nbNodes));
                System.exit(1);
            }
        }
        log.info(String.format("%s[Dense Circle] Output Density %f size links %s", LOGGER, (float)getDensity(tp, nbNodes), tp.getLinks().size()));
    }

    public static void generateFairCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, double density, int delta, long seed, int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        long nbMaxLinks = Math.round(nbNodes *(nbNodes - 1)/2);
        long nbLinks = Math.round(density * nbNodes * (nbNodes - 1) / 2);
        Random r = (seed == 0) ? new Random() : new Random(seed);
        boolean isconnect = false;
        long iteration = 0;
        boolean end = false;
        long count = 0;
        while (!end && tp.getLinks().size() < nbLinks || tp.getLinks().size() >= nbLinks && !isconnect) {
            log.debug(String.format("%s[Fair Circle] links size %s nblinks %s nbmaxlinks %s iterations %s isconnect %s end %s links %s", LOGGER, tp.getLinks().size(), nbLinks, nbMaxLinks, iteration, isconnect, end, tp.getLinks()));
            iteration = generateLinksFairCircle(tp, nbNodes, delta, r, iteration);

            if (!isconnect && Connectivity.isConnected(tp)) {
                isconnect = true;
            }
            if (iteration > 4*nbMaxLinks) {
                if (isconnect) {
                    end = true;
                } else if (tp.getLinks().size() >= nbLinks){
                    tp.clear();
                    generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
                    count++;
                    if (count > 100000){
                        //log.debug(String.format("%s[createGraph] clearLinks", LOGGER));
                        log.info(String.format("%s Input Density %s isn't possible with %s nodes", LOGGER, density, nbNodes));
                        System.exit(1);
                    }
                }
            }
        }
        log.info(String.format("%s Output Density %f Average Delta %s Size Links %s", LOGGER, (float)getDensity(tp, nbNodes), getAverageDelta(tp), tp.getLinks().size()));
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

    private static void generateNodesStar(Topology tp, Node leader, List<? extends Node> nodes, int nbNodes, int x, int y, int radius){
        double angle = 2 * Math.PI / (nbNodes - 1);
        tp.addNode(x, y, leader);
        log.debug(String.format("%s[createGraph] node leader 0", LOGGER));
        for (int k = 0 ; k < nbNodes - 1; k++){
            tp.addNode(x + radius * Math.cos(angle*k), y + radius * Math.sin(angle*k), nodes.get(k));
            log.debug(String.format("%s[createGraph] node anonymous %s", LOGGER, tp.getNodes().get(k + 1).getID()));
        }
    }

    private static long generateLinksFairCircle(Topology tp, int nbNodes, double delta, Random r, long iteration) {
        int random1 = r.nextInt(nbNodes);
        int random2 = r.nextInt(nbNodes);
        while (random1 == random2){
            random1 = r.nextInt(nbNodes);
            random2 = r.nextInt(nbNodes);
        }
        if (tp.getNodes().get(random1).getNeighbors().size() < delta && tp.getNodes().get(random2).getNeighbors().size() < delta) {
            Link link12 = new Link(tp.getNodes().get(random1), tp.getNodes().get(random2));
            Link link21 = new Link(tp.getNodes().get(random2), tp.getNodes().get(random1));
            if (!tp.getLinks().contains(link12) && !tp.getLinks().contains(link21)) {
                tp.addLink(link12);
            }
        }
        if (tp.getNodes().get(random1).getNeighbors().size() == delta || tp.getNodes().get(random2).getNeighbors().size() == delta){
            iteration++;
        }
        return iteration;
    }

    private static void generateLinksCircle(Topology tp, int nbNodes) {
        /*Création du k-ieme liens entre le noeuds k-1 -> k ou k -> 0*/
        for (int k = 0 ; k < nbNodes ; k++) {
            if (k >= 1) {
                tp.addLink(new Link(tp.getNodes().get(k - 1), tp.getNodes().get(k)));
            }
            if (k == nbNodes - 1) {
                tp.addLink(new Link(tp.getNodes().get(k), tp.getNodes().get(0)));
            }
        }
    }

    private static void generateLinksTwoCircle(Topology tp, int nbNodes) {
        /*Création du k-ieme liens entre le noeuds k-1 -> k ou k -> 0*/
        generateLinksCircle(tp, nbNodes);
        for (int k = nbNodes ; k < 2*nbNodes ; k++) {
            if (k >= nbNodes + 1) {
                tp.addLink(new Link(tp.getNodes().get(k - 1), tp.getNodes().get(k)));
            }
            if (k == 2*nbNodes - 1) {
                tp.addLink(new Link(tp.getNodes().get(k), tp.getNodes().get(nbNodes)));
            }
        }
    }

    private static void generateLinksLine(Topology tp, int nbNodes) {
        /*Création du k-ieme liens entre le noeuds k-1 -> k ou k -> 0*/
        for (int k = 0 ; k < nbNodes ; k++) {
            if (k >= 1) {
                tp.addLink(new Link(tp.getNodes().get(k - 1), tp.getNodes().get(k)));
            }
        }
    }

    private static void generateLinksStar(Topology tp, int nbNodes) {
        /*Création du k-ieme liens entre le noeuds k-1 -> k ou k -> 0*/
        for (int k = 1 ; k < nbNodes ; k++) {
            tp.addLink(new Link(tp.getNodes().get(0), tp.getNodes().get(k)));
        }
    }

    private static void generateLinksDenseCircle(Topology tp, int nbNodes, long nbLinks, Random numberRandom) {
        int random1;
        int random2;
        for (int link = 0 ; link < nbLinks ; link++) {
            random1 = numberRandom.nextInt(nbNodes);
            random2 = numberRandom.nextInt(nbNodes);
            while (random1 == random2) {
                random1 = numberRandom.nextInt(nbNodes);
                random2 = numberRandom.nextInt(nbNodes);
            }
            tp.addLink(new Link(tp.getNodes().get(random1), tp.getNodes().get(random2)));
        }
    }

    public static void generateTopo(Topology tp, int[] x, int[] y, int[][] link, double delta, double c) {
        if (x.length != y.length) {
            System.out.println("[Dynamic][Topology][Generator] generateTopo : x and y should have the same length");
        }
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < x.length; i++){
            if (i == 0) {
                nodes.add(new NodeLeader(delta, c));
            } else {
                nodes.add(new NodeAnonymous(delta, c));
            }
        }
        nodes.forEach(node -> tp.addNode(x[nodes.indexOf(node)], y[nodes.indexOf(node)], node));
        for (int i = 0; i < link.length; i++) {
            tp.addLink(new Link(nodes.get(link[i][0]), nodes.get(link[i][1])));
        }
    }

    public static void generateTopo0(Topology tp, double delta, double c) {
        int[] x = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
        int[] y = {50, 100, 150, 200, 250, 300, 350, 400, 450, 500};
        int[][] link = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}, {8, 9}};
        generateTopo(tp, x, y, link, delta, c);
    }

    public static void generateTopo1(Topology tp, double delta, double c) {
        int[] x = {100, 100, 100, 100, 150, 150, 150, 50, 50, 50};
        int[] y = {250, 200, 150, 100, 100, 150, 200, 200, 150, 100};
        int[][] link = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {2, 5}, {1, 6}, {1, 7}, {2, 8}, {3, 9}};
        generateTopo(tp, x, y, link, delta, c);
    }
    public static void generateTopo2(Topology tp, double delta, double c) {
        int[] x = {100, 50, 50, 50, 50, 100, 150, 150, 150, 150, 100};
        int[] y = {125, 200, 150, 100, 50, 50, 50, 100, 150, 200, 200};
        int[][] link = {{0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}, {0, 8}, {0, 9}, {0, 10}};
        generateTopo(tp, x, y, link, delta, c);
    }
    public static void generateTopo3(Topology tp, double delta, double c) {
        int[] x = {100, 100, 100, 100, 50, 50, 100, 150, 150, 150};
        int[] y = {250, 200, 150, 100, 100, 50, 50, 50, 100, 150};
        int[][] link = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {3, 5}, {3, 6}, {3, 7}, {3, 8}, {3, 9}};
        generateTopo(tp, x, y, link, delta, c);
    }
    public static void generateTopo4(Topology tp, double delta, double c) {
        int[] x = {150, 200, 200, 200, 250, 100, 125, 100, 50, 100};
        int[] y = {50, 100, 150, 200, 200, 100, 150, 150, 200, 200};
        int[][] link = {{0, 1}, {1, 2}, {2, 3}, {2, 4}, {0, 5}, {5, 6}, {5, 7}, {7, 8}, {7, 9}};
        generateTopo(tp, x, y, link, delta, c);
    }
    public static void generateTopo5(Topology tp, double delta, double c) {
        int[] x = {250, 200, 200, 200, 150, 100, 125, 100, 50, 100};
        int[] y = {200, 100, 150, 200, 50, 100, 150, 150, 200, 200};
        int[][] link = {{4, 1}, {1, 2}, {2, 3}, {0, 2}, {4, 5}, {5, 6}, {5, 7}, {7, 8}, {7, 9}};
        generateTopo(tp, x, y, link, delta, c);
    }
}
