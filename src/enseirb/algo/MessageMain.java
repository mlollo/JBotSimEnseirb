package enseirb.algo;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

public class MessageMain {

    public static void main(String[] args) {



        Topology tp = new Topology(1920,1080,false);

        Node n0 = new MessageWorking1();
        Node n1 = new MessageWorking2();
        Node n2 = new MessageWorking2();

        Link l1 = new Link(n0,n1);
        Link l2 = new Link(n1,n2);

        tp.disableWireless();

        tp.addNode(1200, 550, n0);
        tp.addNode(800, 550, n1);
        tp.addNode(400, 550, n2);

        tp.addLink(l1);
        tp.addLink(l2);

        tp.start();

        new JViewer(tp);

        int toto= Double.compare(3,4);
        System.out.println("babane" +toto);

    }
}
