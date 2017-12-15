package enseirb.deterministic;

import enseirb.algo.AnonymousNode;
import enseirb.algo.NodeLeader;
import enseirb.generator.DynamicTopologyGenerator;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    /***
     * Ajouter dans Edit configuration en haut à droite (à gauche du bouton Run) :
     * > Add a new configuration
     * > Application
     * > Main class : enseirb.deterministic.Main
     * > VM Options : -DrootLevel=INFO
     * > VM Options : -DrootLevel=DEBUG
     * En command line ça donne :
     * java -DrootLevel=INFO -classpath "/path/log4j.properties:/path/jbotsim.jar:/path/log4j.jar" enseirb.determinister.Main
     * Choisir entre DEBUG (log détaillé) ou INFO (log minimal)
     * Différents niveaux de log possible : DEBUG, INFO, WARN, ERROR, FATAL
     * ***/
    private static Logger log = Logger.getLogger(Main.class);
    private static final String LOGGER = "[Dynamic][Main]";

    private static int width = 1920;   /*Resolution de la fenêtre JBotSim*/
    private static int height = 1080;  /*Resolution de la fenêtre JBotSim*/
    private static int nbNodes = 8;   /*Nombre de noeuds*/
    //private static float density = 0.2;   /*Nombre de noeuds*/

    public static void main(String[] args) {
        Topology tp = new Topology(width/2, height/2, false);
        tp.disableWireless();
        DynamicTopologyGenerator.generateFairCircle(
                tp,
                //new NodeLeader(4, 3),
                //generateAnonymousNodeList(nbNodes, 4, 3),
                new Node(),
                generateNodeList(nbNodes),
                nbNodes, 0.6, 4,width/4, height/4, height/8
        );
        //int[] x = {150, 200, 200, 200, 250, 100};
        //int[] y = {50, 100, 150, 200, 200, 100};
        //int[][] link = {{0, 1}, {1, 2}, {2, 3}, {2, 4}, {5, 0}};
        /*int[] x = {250, 200, 200, 200, 150, 100};
        int[] y = {200, 100, 150, 200, 50, 100};
        int[][] link = {{4, 1}, {1, 2}, {2, 3}, {0, 2}, {4, 5}};*/
        /*int[] x = {100, 100, 100, 100, 150, 150};
        int[] y = {250, 200, 150, 100, 100, 150};
        int[][] link = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {2, 5}};
        DynamicTopologyGenerator.generateTopo(tp, x, y, link, 3, 3);*/
        new DynamicNetwork(tp, 2);

        log.info(String.format("%s[Init JViewer]", LOGGER));
        tp.setClockSpeed(500);
        tp.start();
        new JViewer(tp);
    }

    public static List<AnonymousNode> generateAnonymousNodeList(int nbNodes, double delta, double c) {
        List<AnonymousNode> anonymousNodeList = new ArrayList<>();
        for (int i = 0; i < nbNodes; i++){
            anonymousNodeList.add(new AnonymousNode(delta, c));
        }
        return anonymousNodeList;
    }

    public static List<Node> generateNodeList(int nbNodes) {
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < nbNodes; i++){
            nodeList.add(new Node());
        }
        return nodeList;
    }
}