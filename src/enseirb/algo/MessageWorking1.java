package enseirb.algo;

import jbotsim.Message;
import jbotsim.Node;

public class MessageWorking1 extends Node {

    private  int energy;

    public void MessageWorking1(){
        this.energy=0;

    }

    @Override
    public void onMessage (Message message) {

        if (message.getFlag() == "ENERGY"){
            System.out.println("message reçu");
            System.out.println(this.getTime());
            energy = energy + new Integer(message.getContent().toString());
            System.out.println("mon energy " + energy);


        }

    }



    @Override
    public void onClock () {

    }




}
