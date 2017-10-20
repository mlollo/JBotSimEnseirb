package enseirb.deterministe;

import jbotsim.Message;
import jbotsim.Node;

public class NodeModel extends Node {
    @Override
    public void onStart() {
        System.out.println("Hello everyone I am Node n°" + getID());
        super.onStart();
    }

    @Override
    public void onClock() {
        sendAll("Hello " + getID());
        super.onClock();
    }
}
