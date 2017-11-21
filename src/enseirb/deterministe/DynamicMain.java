package enseirb.deterministe;

import enseirb.algo.AnonymousNode;
import enseirb.algo.NodeLeader;
import jbotsim.Topology;
import jbotsim.Link;
import jbotsim.Node;
import jbotsim.ui.JViewer;
import org.apache.log4j.Logger;

import java.util.List;

public class DynamicMain {
    /***
     * Ajouter dans Edit configuration en haut à droite (à gauche du bouton Run) :
     * > Add a new configuration
     * > Application
     * > DynamicMain class : enseirb.deterministe.DynamicMain
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
    private static int nbNodes = 5;   /*Nombre de noeuds*/
    private static double round = DynamicTopologyGenerator.generateRound( 3, 1.000001, 2);

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Topology tp = new Topology(width/2, height/2, false);
        tp.disableWireless();
        List<Link> links = DynamicTopologyGenerator.generateRing(tp, new NodeLeader(round), AnonymousNode.class, nbNodes, width/4, height/4, height/8);
        DynamicTopology dynamicTopology = new DynamicTopology(tp, links);
        //DynamicTopology dynamicTopology = new DynamicTopology(tp, links, DynamicTopologyGenerator.addInnerRing(tp, nbNodes, 5));
        tp.addClockListener(dynamicTopology::onClock);
        tp.addStartListener(dynamicTopology::onStart);

        log.info(String.format("%s[Init JViewer]", LOGGER));
        tp.setClockSpeed((int)0.01);
        tp.start();
        new JViewer(tp);
    }
}