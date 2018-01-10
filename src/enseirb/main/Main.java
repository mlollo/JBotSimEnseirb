package enseirb.main;

import enseirb.ic.NodeAnonymous;
import enseirb.ic.NodeLeader;
import enseirb.ic.TauAnonymousNode;
import enseirb.dynamicity.DynamicNetwork;
import enseirb.generator.TopologyGenerator;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JClock;
import jbotsim.ui.JViewer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    /***
     * Ajouter dans Edit configuration en haut à droite (à gauche du bouton Run) :
     * > Add a new configuration
     * > Application
     * > Main class : enseirb.main.Main
     * > VM Options : -DrootLevel=INFO
     * > VM Options : -DrootLevel=DEBUG
     * En command line ça donne :
     * java -DrootLevel=INFO -classpath "/path/log4j.properties:/path/jbotsim.jar:/path/log4j.jar" enseirb.determinister.Main
     * Choisir entre DEBUG (log détaillé) ou INFO (log minimal)
     * Différents niveaux de log possible : DEBUG, INFO, WARN, ERROR, FATAL
     * ***/
    private static Logger log = Logger.getLogger(Main.class);
    private static final String LOGGER = "[Dynamic][Main]";

    public static void main(String[] args) {
        int width = 2*1920;   /*Resolution de la fenêtre JBotSim*/
        int height = 2*1080;  /*Resolution de la fenêtre JBotSim*/
        int nbNodes = 6;   /*Nombre de noeuds*/
        int delta = 4; /*Nombre de voisins maximum que chaque peut posséder*/
        double c = 3;
        double density = 0.6; /*Density target*/

        Topology tp = new Topology(width/2, height/2, false);
        tp.disableWireless();

        /*Topology Circle*/
        //TopologyGenerator.generateCircle(tp, new NodeLeader(nbNodes,delta), generateAnonymousNodeList(nbNodes,delta,delta), nbNodes,width/4, height/4, height/8);
        //TopologyGenerator.generateCircle(tp, new Node(), generateNodeList(nbNodes), nbNodes,width/4, height/4, height/8);
        //TopologyGenerator.generateTwoCircle(tp, new Node(), generateNodeList(nbNodes),  new Node(), generateNodeList(nbNodes), nbNodes,width/4, height/4, height/8);

        /*Topology Line*/
        //TopologyGenerator.generateLine(tp, new NodeLeader(nbNodes,delta), generateAnonymousNodeList(nbNodes,delta,delta), nbNodes,width/4, height/4, height/8);
        //TopologyGenerator.generateLine(tp, new Node(), generateNodeList(nbNodes), nbNodes,width/4, height/4, height/8);

        /*Topology Star*/
        //TopologyGenerator.generateStar(tp, new NodeLeader(nbNodes,delta), generateAnonymousNodeList(nbNodes,delta,delta), nbNodes,width/4, height/4, height/8);
        //TopologyGenerator.generateStar(tp, new Node(), generateNodeList(nbNodes), nbNodes,width/4, height/4, height/8);

        /*Topology Circle with a density parameter*/
        //TopologyGenerator.generateDenseCircle(tp, new NodeLeader(nbNodes,delta), generateAnonymousNodeList(nbNodes,delta,delta), nbNodes, density, width/4, height/4, height/8);
        //TopologyGenerator.generateDenseCircle(tp, new Node(), generateNodeList(nbNodes), nbNodes, density, width/4, height/4, height/8);

        /*Topology Circle with a random density parameter*/
        //TopologyGenerator.generateRandomDenseCircle(tp, new NodeLeader(nbNodes,delta), generateAnonymousNodeList(nbNodes,delta,delta), nbNodes, density, width/4, height/4, height/8);
        //TopologyGenerator.generateRandomDenseCircle(tp, new Node(), generateNodeList(nbNodes), nbNodes, density, 0.1, width/4, height/4, height/8);

        /*Topology Randomly Circle with a density parameter and a parameter delta : each nodes respect a limit of neighbors delta*/
        TopologyGenerator.generateRandomFairCircle(tp, new NodeLeader(delta,c), generateAnonymousNodeList(nbNodes,delta,c), nbNodes, density, delta, width/4, height/4, height/8);
        //TopologyGenerator.generateRandomFairCircle(tp, new TauNodeLeader(nbNodes,delta), generateTauAnonymousNodeList(nbNodes,nbNodes,delta), nbNodes, density, delta, width/4, height/4, height/8);
        //TopologyGenerator.generateRandomFairCircle(tp, new Node(), generateNodeList(nbNodes), nbNodes, density, delta, width/4, height/4, height/8);

        /*Topology specific or with a list of parameters*/
        //int[] x = {100, 100, 100, 100, 150, 150, 200, 300};int[] y = {250, 200, 150, 100, 100, 150, 300, 300};int[][] link = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {2, 5}, {5, 6}};
        //TopologyGenerator.generateTopo(tp, x, y, link, 3, 3);
        //TopologyGenerator.generateTopo0(tp, 3,3);

        /*Add Dynamic Links to the Topology*/
        new DynamicNetwork(tp, 2, true, true, delta, 3);
        log.info(String.format("%s[Incremental Counting] Number of total iterations %s for %s nodes", LOGGER, NodeLeader.getICNumberOfIterations(nbNodes, delta, c), nbNodes));


        /*Options for decrease the clock as fast as possible and other options*/
        tp.setClockModel(JClock.class);
        tp.setClockSpeed(0);
        /*Start the clock and view the Topology*/
        log.info(String.format("%s[Init JViewer]", LOGGER));
        tp.start();
        new JViewer(tp);
        //System.out.println(tp.getTime());
    }

    public static List<NodeAnonymous> generateAnonymousNodeList(int nbNodes, double delta, double c) {
        List<NodeAnonymous> anonymousNodeList = new ArrayList<>();
        for (int i = 0; i < nbNodes; i++){
            anonymousNodeList.add(new NodeAnonymous(delta, c));
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

    public static List<TauAnonymousNode> generateTauAnonymousNodeList(int nbNodes, double k, double delta) {
        List<TauAnonymousNode> anonymousNodeList = new ArrayList<>();
        for (int i = 0; i < nbNodes; i++){
            anonymousNodeList.add(new TauAnonymousNode(k, delta));
        }
        return anonymousNodeList;
    }
}