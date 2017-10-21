package enseirb.deterministe;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

public class Main {

  //  public static Topology tp = new Topology(1920,1080);
   // public static int NB_NODE = 16; // NB_NODE < 18 sinon ça marche plus
   // public static int JUMP = 4;

   /* public static Topology tp = new Topology(1920,1080);
    public static int NB_NODE = 5;
>>>>>>> Stashed changes

    public static void main(String[] args) {
        double angle = 2*Math.PI / NB_NODE;
        for (int i=0; i < NB_NODE; i++) {
            tp.addNode(960+300*Math.cos(i*angle),540+300*Math.sin(i*angle), new Node());
            if (i >= 1) {
                tp.addLink(new Link(tp.getNodes().get(i-1), tp.getNodes().get(i)));
            }
            if (i == NB_NODE-1) {
                tp.addLink(new Link(tp.getNodes().get(i), tp.getNodes().get(0)));
            }
        }
        for (int i=0; i < NB_NODE - NB_NODE/JUMP + 1; i = i + NB_NODE/JUMP) {
            if (i < NB_NODE - NB_NODE/JUMP) {
                tp.addLink(new Link(tp.getNodes().get(i), tp.getNodes().get(i + NB_NODE/JUMP)));
            } if (i == NB_NODE - NB_NODE/JUMP) {
                tp.addLink(new Link(tp.getNodes().get(i), tp.getNodes().get(0)));
            }
        }
        new LonerCentralized(tp, NB_NODE);
        new JViewer(tp);
    }
<<<<<<< Updated upstream
=======

    public int getDensity(Topology tp) {
        int nbLinks = tp.getLinks().size();
        int nbNodes = tp.getNodes().size();
        return nbLinks * 2 / (nbNodes*(nbNodes - 1));
    }
    */



        public static Topology tp = new Topology();
        public static int NB_NODES = 6;


        public static void main(String[] args) {

            DetGraph graph = new DetGraph(NB_NODES, tp);
            tp = graph.createGraph(NB_NODES, tp);
            new JViewer(tp);

        }



}
