package enseirb.ic;

import enseirb.main.Main;
import jbotsim.Message;
import jbotsim.Node;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class NodeLeader extends Node {

    private double round;
    private double delta;
    private double energy;
    private double c;
    private double k ;
    private double nodeNumber;
    private boolean halt;
    private int tempTime;
    private double roundNumber;
    private double notificationNumber;
    private boolean isCorrect;
    private boolean wasCorrect;
    private List<Double> energyArray;
    private int initialTime;
    private boolean once;
    private double realRound;

    private static Logger log = Logger.getLogger(Main.class);
    private static final String LOGGER = "[Incremental Counting][Node   Leader]";

    public NodeLeader(double delta, double c){
        this.delta = delta;
        this.energy = 0;
        this.energyArray = new ArrayList();
        this.c = c;
        this.k = 4;
        this.round = getRound(this.k, this.c, this.delta);
        this.realRound = 0;
        this.initialTime = 0;
        this.isCorrect = true;
        this.wasCorrect = false;
        this.once = true;
        this.halt = false;
        //log.info(String.format("%s[Collection Phase] Number of iterations %s for k=%s", LOGGER, this.round, this.k));
        //System.out.println("ROUND " + round);
    }

    private static double getRound(double k, double c, double delta) { return k* Math.ceil(Math.pow(2*delta,k)*(c +1)*Math.log(k)) ; }
    public static double getICNumberOfIterations(double nbNodes, double delta, double c) {
        double iterations = 0;
        for(int k=3; k <= nbNodes; k++) {
            iterations += k* Math.ceil(Math.pow(2*delta,k)*(c +1)*Math.log(k));
        }
        return iterations;
    }

    @Override
    public void onMessage (Message message){

        if (!halt) {
            // Collection Phase
            // pour le moment le node avec un id d 0 correspond au node leader. Je récupère ll'ernergie des noeuds sur un nombre de round donné
            if ( (this.getTime() - this.initialTime) < (int)round && message.getFlag().equals("ENERGY")) {
                //System.out.println("le node : " + message.getSender()  + " dit : " + message.getContent() + " au node " + message.getDestination() );
                // System.out.println("contenu" + new Double(message.getContent().toString());
                energy = energy + new Double(message.getContent().toString());
                //System.out.println("energy leader " + energy);
            }

            // Verification Phase
            if ((this.getTime() - initialTime) >= (int)round) {
                if ((this.getTime() - this.tempTime) < (int)roundNumber) {
                    if(message.getFlag().equals("ENERGYVEF")) {
                        energyArray.add(new Double(message.getContent().toString()));
                    }
                    int temp1 = Double.compare((k - 1 - 1 / Math.pow(k, c)), energy);
                    int temp2 = Double.compare( this.k - 1, energy);

                    //System.out.println(" temp 1 ! " + temp1);
                    //System.out.println(" temp 2 ! " + temp2);
                    //System.out.println("valeur des comparaisons");
                    //System.out.println(k - 1 - 1 / Math.pow(k, c));
                    //System.out.println(energy);
                    //System.out.println(k -1);
                    //System.out.println(temp1);
                    //System.out.println(temp2);

                    if ((temp1 <= 0) && (temp2 >= 0)) {

                        for (int j = 0; j < energyArray.size(); j++) {
                            //System.out.println(" valeur des energy array !" + energyArray.get(j));

                            if (energyArray.get(j) > 1 / Math.pow(k, c)) {

                                this.isCorrect = false;
                                //System.out.println(" isCorrect ! " + isCorrect);
                            }
                        }
                    } else {
                        this.isCorrect = false;
                    }
                   // System.out.println("valeur isCorrect " + isCorrect);
                }
            }
        }
    }

    @Override
    public void onClock (){
        if (wasCorrect && this.getTime() % 2 == 1) {
            sendAll(new Message("halt", "HALT"));
            halt = true;
        }
        if(!halt) {
            // Collection Phase
            if ((this.getTime() - this.initialTime) < (int) this.round && this.getTime() != 0) {
                this.realRound++;
            }

            if ((this.getTime() - this.initialTime) == (int) round) {
                this.realRound++;
                this.tempTime = this.getTime();
                this.roundNumber = 2*(1 + Math.ceil(k / (1 - (1 / Math.pow(k, this.c)))));

                //System.out.println("LEADER " + this.getID() + " ROUND NUMBER " + roundNumber);
                //System.out.println("ROUND temptime LEADER " + tempTime);
            }

            if ((this.getTime() - initialTime) >= (int) round) {
                if ((this.getTime() - tempTime) == (int) roundNumber) {
                    notificationNumber = this.getTime();
                }

                // Notification Phase
                if ((this.getTime() - this.tempTime) >= (int) roundNumber) {
                    if ((this.getTime() - (int) this.notificationNumber) < (int) k) {
                        if (isCorrect && this.getTime() % 2 == 1) {
                            sendAll(new Message("halt", "HALT"));
                            halt = true;
                        } else if (isCorrect && this.getTime() % 2 == 0) {
                            wasCorrect = true;
                        }
                    } else {
                        k += 1;
                        //round = this.getTime() +round;
                        this.initialTime = this.getTime();
                        energyArray = new ArrayList<>();
                        this.energy = 0;
                        this.isCorrect = true;
                        this.round = (int) getRound(k, delta, c);
                        this.realRound = 0;
                        log.info(String.format("%s[Notification Phase] Number of iterations %s for k=%s", LOGGER, this.round, this.k));
                        log.info(String.format("%s[Notification Phase] Leader    %s CHANGEMENT de k=%s", LOGGER, this.getID(), this.k));
                    }
                }
            }
        }else{
            // Notification Phase
            nodeNumber = this.k;
            if (once) {
                //sendAll(new Message("halt", "HALT"));
                log.info(String.format("%s[Notification Phase] Node %s Leader counts %s nodes in Topology", LOGGER, this.getID(), this.nodeNumber));
                log.info(String.format("%s[Notification Phase] Number of total iterations %s", LOGGER, getICNumberOfIterations(this.nodeNumber, this.delta, this.c)));
                once = false;
            }
        }

    }
}
