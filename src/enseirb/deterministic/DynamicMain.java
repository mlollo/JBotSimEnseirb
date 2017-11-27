package enseirb.deterministic;

import enseirb.algo.AnonymousNode;
import enseirb.algo.NodeLeader;
import enseirb.generator.DynamicTopologyGenerator;
import jbotsim.Topology;
import jbotsim.ui.JViewer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DynamicMain {
    /***
     * Ajouter dans Edit configuration en haut à droite (à gauche du bouton Run) :
     * > Add a new configuration
     * > Application
     * > DynamicMain class : enseirb.deterministic.DynamicMain
     * > VM Options : -DrootLevel=INFO
     * > VM Options : -DrootLevel=DEBUG
     * En command line ça donne :
     * java -DrootLevel=INFO -classpath "/path/log4j.properties:/path/jbotsim.jar:/path/log4j.jar" enseirb.determinister.DynamicMain
     * Choisir entre DEBUG (log détaillé) ou INFO (log minimal)
     * Différents niveaux de log possible : DEBUG, INFO, WARN, ERROR, FATAL
     * ***/
    private static Logger log = Logger.getLogger(DynamicMain.class);
    private static final String LOGGER = "[Dynamic][Main]";

    private static int width = 1920;   /*Resolution de la fenêtre JBotSim*/
    private static int height = 1080;  /*Resolution de la fenêtre JBotSim*/
    private static int nbNodes = 6;   /*Nombre de noeuds*/
    //private static float density = 0.2;   /*Nombre de noeuds*/
    private static double round = DynamicTopologyGenerator.generateRound( 3, 1.000001, 2);

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Topology tp = new Topology(width/2, height/2, false);
        tp.disableWireless();
        //new DynamicDeterministic(tp,
                DynamicTopologyGenerator.generateFairCircle(
                        tp,
                        new NodeLeader(round, 4),
                        generateAnonymousNodeList(nbNodes, round),
                        nbNodes, 0.6, 0.1,3,width/4, height/4, height/8
                );
        //        2
        //);

        log.info(String.format("%s[Init JViewer]", LOGGER));
        tp.setClockSpeed(0);
        tp.start();
        new JViewer(tp);
    }

    public static List<AnonymousNode> generateAnonymousNodeList(int nbNodes, double round) {
        List<AnonymousNode> anonymousNodeList = new ArrayList<>();
        for (int i = 0; i < nbNodes; i++){
            anonymousNodeList.add(new AnonymousNode(round, 4));
        }
        return anonymousNodeList;
    }
}