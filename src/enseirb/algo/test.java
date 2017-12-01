package enseirb.algo;


import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;


import static jbotsimx.Connectivity.isConnected;
import jbotsim.ui.JViewer;


// classe permettant de test pour l'algo d'alessia
public class test {
    public static void main(String[] args) {

        TopoTest toto = new TopoTest();
        toto.addTpology();

        new JViewer(toto.tp10);

    }
}