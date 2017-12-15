package enseirb.deterministic;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
import jbotsim.event.StartListener;
import jbotsimx.Connectivity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DynamicNetwork implements StartListener, ClockListener{

    private static final Logger log = Logger.getLogger(DynamicNetwork.class);
    private static final String LOGGER = "[Deterministic][Dynamic][Network]";

    private Topology tp;
    private Link savedLink;
    private int dynamicRound;
    private boolean isDynamicGraph;
    private boolean isRandomDynamicNetwork;
    private Random r;

    /***
     * Initialisation de l'objet DynamicNetwork
     * @param tp nombre de noeuds
     * ***/
    public DynamicNetwork(Topology tp, int dynamicRound){
        this.tp = tp;
        tp.addStartListener(this);
        tp.addClockListener(this);
        this.dynamicRound = dynamicRound;
        this.isDynamicGraph = true;
        this.isRandomDynamicNetwork = false;
        this.r = new Random();
    }

    /***
     * Initialisation de l'objet DynamicNetwork
     * @param tp nombre de noeuds
     * ***/
    public DynamicNetwork(Topology tp, int dynamicRound, boolean random){
        this.tp = tp;
        tp.addStartListener(this);
        tp.addClockListener(this);
        this.dynamicRound = dynamicRound;
        this.isDynamicGraph = true;
        this.isRandomDynamicNetwork = random;
        this.r = new Random();
    }

    public void onStart(){
        log.info(String.format("%s[onStart] Topology start", LOGGER));

        /*On retire le premier lien sauvegarder dans la liste*/
        this.savedLink = this.tp.getLinks().get(this.isRandomDynamicNetwork ? r.nextInt(tp.getNodes().size()) : 0);
        this.tp.removeLink(this.savedLink);
        boolean isconnect = Connectivity.isConnected(this.tp);
        int i = 0;
        while (!isconnect && i < this.tp.getLinks().size()) {
            this.tp.addLink(this.savedLink);
            this.savedLink = this.tp.getLinks().get(this.isRandomDynamicNetwork ? r.nextInt(tp.getNodes().size()) : 0);
            this.tp.removeLink(this.savedLink);
            isconnect = Connectivity.isConnected(this.tp);
            i++;
        }
        if (i == this.tp.getLinks().size() - 1) {
            this.tp.addLink(this.savedLink);
            this.isDynamicGraph = false;
            log.info(String.format("%s[onStart] this graph can't be dynamic", LOGGER));
        } else {
            log.debug(String.format("%s[onStart] link saved : %s", LOGGER, this.savedLink));
            log.debug(String.format("%s[onStart] links : %s", LOGGER, this.tp.getLinks()));
        }
    }

    /***
     * Modifie le graphe dynamiquement tout les slot de temps
     * ***/
    public void onClock(){
        if (this.isDynamicGraph) {
            if (tp.getTime() != 0 && tp.getTime() % this.dynamicRound == 0) {
                this.dynamicCircularLinks();
            }
        }
    }

    /***
     * Retire un connecteur et en ajoute un de façon à ce que le graphe reste connexe
     *
     * ***/
    private void dynamicCircularLinks() {
        this.tp.addLink(this.savedLink);
        boolean isconnect = false;
        Link testLink = null;
        while(!isconnect) {
            testLink = tp.getLinks().get(this.isRandomDynamicNetwork ? r.nextInt(tp.getNodes().size()) : 0);
            if (!testLink.equals(savedLink)) {
                this.tp.removeLink(testLink);
                if (Connectivity.isConnected(this.tp)) {
                    this.savedLink = testLink;
                    log.debug(String.format("%s[onClock] link saved : %s", LOGGER, this.savedLink));
                    log.debug(String.format("%s[onClock] links : %s", LOGGER, this.tp.getLinks()));
                    isconnect = true;
                } else {
                    this.tp.addLink(testLink);
                    log.debug(String.format("%s[onClock] topology not connected link added : %s", LOGGER, testLink));
                    log.debug(String.format("%s[onClock] links : %s", LOGGER, this.tp.getLinks()));
                }
            } else {
                this.tp.removeLink(testLink);
                this.tp.addLink(testLink);
                log.debug(String.format("%s[onClock] testLink same as linkSaved : %s", LOGGER, testLink));
            }
        }
    }
}
