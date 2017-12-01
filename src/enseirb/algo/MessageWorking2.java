package enseirb.algo;

import jbotsim.Message;
import jbotsim.Node;

public class MessageWorking2 extends Node {


    @Override
    public void onMessage (Message message) {

    }



    @Override
    public void onClock () {

        if(this.getTime() == 1){

            sendAll(new Message(13, "ENERGY"));
            System.out.println(" j'envoie a " + this.getTime());
        }

    }

}
