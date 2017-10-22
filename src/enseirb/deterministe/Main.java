package enseirb.deterministe;

import jbotsim.ui.JViewer;
import org.apache.log4j.Logger;

public class Main{
    /***
     * Ajouter dans Edit configuration en haut à droite (à gauche du bouton Run) :
     * > Add a new configuration
     * > Application
     * > Main class : enseirb.deterministe.Main
     * > VM Options : -DrootLevel=INFO
     * > VM Options : -DrootLevel=DEBUG
     * En command line ça donne :
     * java -DrootLevel=INFO -classpath "/path/log4j.properties:/path/jbotsim.jar:/path/log4j.jar" enseirb.determinister.Main
     * Choisir entre DEBUG (log détaillé) ou INFO (log minimal)
     * Différents niveaux de log possible : DEBUG, INFO, WARN, ERROR, FATAL
     * ***/
    private static Logger log = Logger.getLogger(Main.class);
    private static final String LOGGER = "[MAIN]";

    public static void main(String[] args) {
        /*Resolution de la fenêtre JBotSim*/
        int width = 1920;
        int height = 1080;
        /*Nombre de noeuds*/
        int nbNodes = 25;

        DetGraph graph = new DetGraph(nbNodes, width, height);
        graph.createGraph(width/2, height/2, height/4);
        //graph.addInnerLinks(5);

        log.info(String.format("%s[Init JViewer]", LOGGER));
        new JViewer(graph.tp);
    }
}
