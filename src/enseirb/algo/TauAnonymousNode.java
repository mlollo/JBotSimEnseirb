package enseirb.algo;

import enseirb.deterministic.DynamicTopologyGenerator;
import jbotsim.Message;
import jbotsim.Node;

import java.util.ArrayList;
import java.util.List;

public class TauAnonymousNode extends Node {

    private int counter;
    private double delta ;
    private double k ;
    private boolean halt;
    private double c ;
    private int nbRound;
    private int nbRoundtest;
    private double energy;


    public TauAnonymousNode(double k, double c){
        this.delta = 2;
        this.c = c;
        this.k = k;
        this.halt = false;
        this.counter = 0;
        this.nbRoundtest=2;
        this.nbRound=0;
        this.energy = 1;
    }


    @Override
    public void onMessage (Message message){

        if(!halt) {



            int temp1 = Double.compare((k - 1 - 1 / Math.pow(k, c)), energy);

            if (message.getFlag().equals("ENERGY")) {
                //System.out.println("le node : " + message.getSender()  + " dit : " + message.getContent() + " au node " + message.getDestination() );
                // System.out.println("contenu" + new Double(message.getContent().toString());
                energy = energy + new Double(message.getContent().toString());
                //System.out.println("energy anonymous " + energy);


            }

            if(temp1 <= 0){
                //System.out.println("borne inf ole " + (k - 1 - 1 / Math.pow(k, c)));
               // System.out.println("nb round ole " + nbRound);
            }else{
                //System.out.println("node anonymous " + nbRound);
                //System.out.println("borne inf " + (k - 1 - 1 / Math.pow(k, c)));

            }
        }


    }

    @Override
    public void onClock () {

        //

        if (!halt) {

             // System.out.println("okkkk");

                double s = this.getNeighbors().size();

                int tmp = Double.compare(energy, 0);
                if (tmp >= 0) {
                    if (counter == 0) {
                        sendAll(new Message(this.energy / (2 * (double) this.delta), "ENERGY"));
                        this.energy = this.energy - this.energy * s / (2 * (double) this.delta);
                        counter++;
                        nbRound++;
                       // System.out.println("banane");
                    } else {
                        counter = 0;
                    }
                }


        }
    }
}