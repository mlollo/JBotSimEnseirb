package enseirb.deterministic;

import enseirb.algo.AnonymousNode;
import enseirb.generator.DynamicTopologyGenerator;
import jbotsim.Link;
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
    //private static double round = DynamicTopologyGenerator.generateRound( 3, 1.000001, 2);

    public static void main(String[] args) {
        Topology tp = new Topology(width/2, height/2, false);
        tp.disableWireless();
        List<Link> list = DynamicTopologyGenerator.generateFairCircle(
                tp,
                //new NodeLeader(round, 4),
                //generateAnonymousNodeList(nbNodes, round, 4),
                new Node(),
                generateNodeList(nbNodes),
                nbNodes, 0.6, 0.1,4,width/4, height/4, height/8
        );
        new DynamicNetwork(tp, list, 2);

        log.info(String.format("%s[Init JViewer]", LOGGER));
        tp.setClockSpeed(500);
        tp.start();
        new JViewer(tp);
    }

    public static List<AnonymousNode> generateAnonymousNodeList(int nbNodes, double round, double delta) {
        List<AnonymousNode> anonymousNodeList = new ArrayList<>();
        for (int i = 0; i < nbNodes; i++){
            anonymousNodeList.add(new AnonymousNode(round, delta));
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