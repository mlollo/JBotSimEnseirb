package enseirb.dynamicity;

import jbotsim.Link;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
import jbotsim.event.StartListener;
import jbotsimx.Connectivity;
import org.apache.log4j.Logger;

import java.util.Random;

public class DynamicGraph implements StartListener, ClockListener{

    private static final Logger log = Logger.getLogger(DynamicGraph.class);
    private static final String LOGGER = "[Dynamic][Graph]";

    private Topology tp;
    private Link savedLink;
    private int dynamicRound;
    private boolean isDynamicGraph;
    private boolean isSemiRandom;
    private boolean isRandom;
    private double delta;
    private int linksPerRound;
    private Random r;

    /***
     * Initialisation de l'objet DynamicGraph
     * @param tp nombre de noeuds
     * ***/
    public DynamicGraph(Topology tp, int dynamicRound){ this(tp, dynamicRound, false, false, 0, 0, 0); }
    public DynamicGraph(Topology tp, int dynamicRound, boolean semiRandom){ this(tp, dynamicRound, semiRandom, false, 0, 0, 0); }
    public DynamicGraph(Topology tp, int dynamicRound, boolean semiRandom, boolean random, double delta){ this(tp, dynamicRound, semiRandom, random, delta, 0, 0); }
    public DynamicGraph(Topology tp, int dynamicRound, boolean semiRandom, boolean random, double delta, int linksPerRound){ this(tp, dynamicRound, semiRandom, random, delta, linksPerRound, 0); }
    public DynamicGraph(Topology tp, int dynamicRound, boolean semiRandom, boolean random, double delta, int linksPerRound, long seed){
        this.tp = tp;
        tp.addStartListener(this);
        tp.addClockListener(this);
        this.dynamicRound = dynamicRound;
        this.isDynamicGraph = true;
        this.isSemiRandom = semiRandom;
        this.isRandom = random;
        this.delta = delta;
        this.linksPerRound = linksPerRound;
        this.r = (seed == 0) ? new Random() : new Random(seed);
    }

    public void onStart(){
        log.info(String.format("%s[onStart] Topology start", LOGGER));
        this.initRoundRobinLinks();
    }

    /***
     * Modifie le graphe dynamiquement tout les slot de temps
     * ***/
    public void onClock(){
        //log.info(String.format("%s[onClock] tp.getTime %s", LOGGER, tp.getTime()));
        if (this.isDynamicGraph) {
            if (tp.getTime() != 0 && tp.getTime() % this.dynamicRound == 0) {
                //log.info(String.format("%s[onClock] tp.getTime %s", LOGGER, tp.getTime()));
                if (this.isRandom) {
                    if (this.linksPerRound == 0) {
                        this.addRandomLinks();
                        this.roundRobinLinks();
                    } else {
                        for(int i = 0; i < this.linksPerRound; i++) {
                            this.addRandomLinks();
                            this.roundRobinLinks();
                        }
                    }
                } else {
                    this.tp.addLink(this.savedLink);
                    this.roundRobinLinks();
                }
            }
        }
    }

    /***
     * Retire un connecteur et en ajoute un de façon à ce que le graphe reste connexe
     * On retire le premier lien sauvegarder dans la liste
     *
     * ***/
    private void initRoundRobinLinks(){
        this.savedLink = this.tp.getLinks().get(this.isSemiRandom ? r.nextInt(tp.getNodes().size()) : 0);
        this.tp.removeLink(this.savedLink);
        boolean isconnect = Connectivity.isConnected(this.tp);
        int i = 0;
        while (!isconnect && i < this.tp.getLinks().size()) {
            this.tp.addLink(this.savedLink);
            this.savedLink = this.tp.getLinks().get(this.isSemiRandom ? r.nextInt(tp.getNodes().size()) : 0);
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
     * Retire un connecteur et en ajoute un de façon à ce que le graphe reste connexe
     *
     * ***/
    private void roundRobinLinks() {
        boolean isconnect = false;
        Link testLink = null;
        while (!isconnect) {
            testLink = tp.getLinks().get(this.isSemiRandom ? r.nextInt(tp.getNodes().size()) : 0);
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

    public void addRandomLinks() {
        int nbNodes = this.tp.getNodes().size();
        int random1;
        int random2;
        boolean isconnect = false;
        while (!isconnect) {
            random1 = r.nextInt(nbNodes);
            random2 = r.nextInt(nbNodes);
            while (random1 == random2){
                random1 = r.nextInt(nbNodes);
                random2 = r.nextInt(nbNodes);
            }
            if (this.tp.getNodes().get(random1).getNeighbors().size() < this.delta && this.tp.getNodes().get(random2).getNeighbors().size() < this.delta) {
                Link link12 = new Link(this.tp.getNodes().get(random1), this.tp.getNodes().get(random2));
                Link link21 = new Link(this.tp.getNodes().get(random2), this.tp.getNodes().get(random1));
                if (!this.tp.getLinks().contains(link12) && !this.tp.getLinks().contains(link21)) {
                    this.tp.addLink(link12);
                    if (Connectivity.isConnected(this.tp)) {
                        isconnect = true;
                    } else {
                        this.tp.removeLink(link12);
                    }
                }
            }
        }
    }
}
