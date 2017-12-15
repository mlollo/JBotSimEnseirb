package enseirb.random;

import enseirb.algo.AnonymousNode;
import enseirb.algo.NodeLeader;
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
    private static int nbNodes = 6;   /*Nombre de noeuds*/

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Topology tp = new Topology(width/2, height/2, false);
        tp.disableWireless();
        DynamicTopologyGenerator.generateDenseCircle(
                tp,
                new NodeLeader(4, 3),
                generateAnonymousNodeList(nbNodes, 4, 3), nbNodes,
                0.5, width/4, height/4, height/8
        );

        //new DynamicNetwork(tp, list);
        //new DynamicNetwork(tp, DynamicTopologyGenerator.generateRing(tp, new NodeLeader(round), AnonymousNode.class, nbNodes, width/4, height/4, height/8), DynamicTopologyGenerator.addInnerRing(tp, nbNodes, 5));

        log.info(String.format("%s[Init JViewer]", LOGGER));
        tp.setClockSpeed(1);
        tp.start();
        new JViewer(tp);
    }

    public static List<AnonymousNode> generateAnonymousNodeList(int nbNodes, double delta, double c) {
        List<AnonymousNode> anonymousNodeList = new ArrayList<>();
        for (int i = 0; i < nbNodes; i++) {
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