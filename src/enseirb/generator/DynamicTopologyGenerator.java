package enseirb.generator;

import enseirb.algo.AnonymousNode;
import enseirb.algo.NodeLeader;
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

    public static double getDensity(Topology tp, int nbNodes) { return (double)tp.getLinks().size() / (double)((nbNodes * (nbNodes - 1)) / 2); }
    /***
     * Créer une constellation de noeuds circulaire
     * Créer un graphe en sauvegardant les connecteurs Link dans la liste links
     * Modifie l'objet Topology tp qui appartient à l'objet DynamicNetwork
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

    public static void  generateFairCircle(Topology tp, Node leader, List<? extends Node> anonymous, int nbNodes, double density, double errDensity, int delta,int x, int y, int radius) {
        generateNodesCircle(tp, leader, anonymous, nbNodes, x, y, radius);
        Random numberRandom = new Random();
        boolean isconnect = false;
        long timer = System.currentTimeMillis() + 1500 + numberRandom.nextInt();
        long nbLinks = Math.round(density * nbNodes * (nbNodes - 1) / 2);

        isconnect = generateLinksFairCircle(tp, nbNodes, nbLinks, delta, numberRandom, isconnect);
        while(!isconnect || Math.abs(density - getDensity(tp, nbNodes)) > errDensity) {
            if (timer - System.currentTimeMillis() <= 0 ) {
                log.info(String.format("%s timeout", LOGGER));
                timer = System.currentTimeMillis() + 1500 + numberRandom.nextInt();
                numberRandom = new Random();
            }
            isconnect = generateLinksFairCircle(tp, nbNodes, nbLinks, delta, numberRandom, isconnect);
        }
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

    public static void generateTopo(Topology tp, int[] x, int[] y, int[][] link, double delta, double c) {
        if (x.length != y.length) {
            System.out.println("[Dynamic][Topology][Generator] generateTopo : x and y should have the same length");
        }
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < x.length; i++){
            if (i == 0) {
                nodes.add(new NodeLeader(delta, c));
            } else {
                nodes.add(new AnonymousNode(delta, c));
            }
        }
        nodes.forEach(node -> {
            tp.addNode(x[nodes.indexOf(node)], y[nodes.indexOf(node)], node);
        });
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
