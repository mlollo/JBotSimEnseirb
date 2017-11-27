package enseirb.random;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
import jbotsim.event.StartListener;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DynamicRandom implements StartListener, ClockListener{

    private static final Logger log = Logger.getLogger(DynamicRandom.class);
    private static final String LOGGER = "[Dynamic][Topology]";

    private Topology tp;
    private List<Link> links = new ArrayList<>();
    private List<Link> innerLinks = new ArrayList<>();

    /***
     * Initialisation de l'objet DynamicRandom
     * @param tp nombre de noeuds
     * @param links outer links
     * ***/
    public DynamicRandom(Topology tp, List<Link> links){
        this.tp = tp;
        this.links = links;
        tp.addStartListener(this::onStart);
        tp.addClockListener(this::onClock);
    }

    /***
     * Initialisation de l'objet DynamicRandom
     * @param tp nombre de noeuds
     * @param links outer links
     * @param innerLinks inner links
     * ***/
    public DynamicRandom(Topology tp, List<Link> links, List<Link> innerLinks){
        this.tp = tp;
        this.links = links;
        this.innerLinks = innerLinks;
        tp.addStartListener(this::onStart);
        tp.addClockListener(this::onClock);
    }

    public void onStart(){
        log.info(String.format("%s[onStart] Topology start", LOGGER));

        /*Start seulement si links contient des objets*/
        if (this.links.size() != 0) {
            /*On retire le premier lien sauvegarder dans la liste*/
            this.tp.removeLink(this.links.get(0));

            if (this.innerLinks.size() != 0) {
                /*Si innerLinks contient des objets, on retire le premier lien sauvegarder dans la liste*/
                this.tp.removeLink(this.innerLinks.get(0));
            }
        }
    }

    /***
     * Modifie le graphe dynamiquement tout les slot de temps
     * ***/
    public void onClock(){
        this.dynamicCircularLinks(this.links, false);
        if (this.innerLinks.size() != 0) {
            this.dynamicCircularLinks(this.innerLinks, true);
        }
        //this.onClockLogs();
    }

    /***
     * Retire un connecteur et en ajoute un de façon à ce que le graphe reste connexe
     *
     * @param savedLinks liste des liens à rendre dynamique
     * @param direction sens giratoire
     * ***/
    private void dynamicCircularLinks(List<Link> savedLinks, boolean direction) {
        Random numberRandom = new Random();

        try {
            /*Pour tout les liens dans savedLinks*/
            savedLinks.forEach(linkSaved -> {
                /*Si aucun liens de tp.getLinks() match avec linkSaved alors :*/
                if (this.tp.getLinks().stream().noneMatch(link -> link.equals(linkSaved))) {
                    /*On sauvegarde l'index de linkSaved dans la liste savedLinks*/
                    int linkSavedIndex = savedLinks.indexOf(linkSaved);
                    log.debug(String.format("%s[onClock] link missing : %s", LOGGER, linkSaved));
                    log.debug(String.format("%s[onClock] link missing index : %s", LOGGER, linkSavedIndex));
                    /*En fonction de la direction on teste si cet index n'est pas le premier ou le dernier*/

                    int rand = numberRandom.nextInt(savedLinks.size() - 2) + 1;
                    log.debug(String.format("%s[onClock] rand number : %s", LOGGER, rand));

                    if (direction ? linkSavedIndex > rand : linkSavedIndex < savedLinks.size() - 1 - rand) {
                        /*On retire soit le liens précédent ou suivant cet index présent dans la liste savedLinks*/
                        this.tp.removeLink(savedLinks.get(direction ? linkSavedIndex - 1 - rand: linkSavedIndex + rand));
                    } else {
                        /*On retire soit le dernier ou le premier liens présent dans savedLinks*/
                        this.tp.removeLink(savedLinks.get(direction ? savedLinks.size() - 1 - rand : rand));
                    }

                    /*On ajoute le lien qui ne se trouve pas dans tp.getLinks()*/
                    this.tp.addLink(linkSaved);
                    /*On sort du forEach en déclenchant un exception capter par le try catch*/
                    throw new BreakException();
                }
            });
        } catch (BreakException ignored) { }
    }

    /***
     * Calcul de la densité du graphe
     * @return densité
     * ***/
    public double getDensity() {
        int nbLinks = this.tp.getLinks().size();
        int nbNodes = this.tp.getNodes().size();
        return nbLinks * 2 / (double) (nbNodes * (nbNodes - 1));
    }
}
