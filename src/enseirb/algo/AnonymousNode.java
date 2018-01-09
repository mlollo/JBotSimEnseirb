package enseirb.algo;

import enseirb.deterministic.Main;
import jbotsim.Message;
import jbotsim.Node;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnonymousNode extends Node{

    private double round;
    private double delta ;
    private double energy ;
    private int tempTime;
    private double roundNumber;
    private double notificationNumber;
    private List<Double> energyArray;
    private double k ;
    private boolean halt;
    private boolean haltSent;
    private double c ;
    private double energyMax;
    private double nodeNumber;
    private int initialTime;
    private boolean once;
    private double realRound;
    private boolean booleanMessage = true;

    private static Logger log = Logger.getLogger(Main.class);
    private static final String LOGGER = "[Incremental Counting][NodeAnonymous]";

    public AnonymousNode(double delta, double c) {
        this.delta = delta;
        this.energy = 1;
        this.c = c;
        this.k = 3;
        this.round = getRound(this.k, this.c, this.delta);
        this.energyArray = new ArrayList();
        this.realRound = 0;
        this.initialTime = 0;
        this.once = true;
        this.halt = false;
        this.haltSent = false;
        //System.out.println("ROUND a" + round);
    }

    private static double getRound(double k, double c, double delta) { return 2*k* Math.ceil(Math.pow(2*delta,k)*(c +1)*Math.log(k)); }

    @Override
    public void onMessage (Message message){

        if(!halt) {

            // Collection Phase
            // pour le moment le node avec un id d 0 correspond au node leader. Je récupère ll'ernergie des noeuds sur un nombre de round donné
            if ( (this.getTime() - this.initialTime) < (int)round && message.getFlag().equals("ENERGY")) {
                // System.out.println("le node : " + message.getSender() + " dit : " + message.getContent() + " au node " + message.getDestination());
                // System.out.println("contenu" + new Double(message.getContent().toString());
                this.energy = this.energy + new Double(message.getContent().toString());
                //System.out.println(" anonymous energy " + energy);
            }

            //Verification Phase
            if ((this.getTime() - initialTime) >= (int)round) {
                if ((this.getTime() - this.tempTime) < (int)roundNumber) {
                    if (message.getFlag().equals("ENERGYVEF")) {
                        energyArray.add(new Double(message.getContent().toString()));
                    }
                    for (int j = 0; j < energyArray.size(); j++) {
                        if (energyArray.get(j) > 1 / Math.pow(k, c)) {
                            this.energyMax = energyArray.get(j);
                        }
                    }
                    //System.out.println("APRES NOTIFICATION energyArray " + energyArray);
                }
            }

            // Notification phase
            //if ((this.getTime() - (int)this.notificationNumber) < (int)this.k) {
            if (message.getFlag().equals("HALT")) {
                halt = true;
            }
            //}
        }
    }

    @Override
    public void onClock () {
        if (!halt) {
            // Collection Phase
            if ((this.getTime() - initialTime) < (int) this.round && this.getTime() != 0) {
                this.realRound++;
                double s = this.getNeighbors().size();
                int tmp = Double.compare(energy, 0);
                if (tmp >= 0) {
                    if (this.getTime() % 2 == 1) {
                        sendAll(new Message(this.energy / (2 * (double) this.delta), "ENERGY"));
                        this.energy = this.energy - this.energy * s / (2 * (double) this.delta);
                    }
                }
                // impression du temps de chaque round
            }

            if ((this.getTime() - initialTime) == round) {
                this.realRound++;
                this.tempTime = this.getTime();
                roundNumber =  2*(1 + Math.ceil(this.k / (1 - (1 / Math.pow(this.k, this.c)))));

                //System.out.println("ANONYMOUS " + this.getID() + " ROUND NUMBER " + roundNumber);
                //System.out.println("ROUND temptime ANONYMOUS " + tempTime);
                this.energyMax = this.energy;
            }

            if ((this.getTime() - initialTime) >= (int) round) {
                // Verification Phase
                if (this.getTime() - this.tempTime < (int) roundNumber) {
                    if (this.getTime() % 2 == 1) {
                        sendAll(new Message(this.energyMax, "ENERGYVEF"));
                        //System.out.println(" Envoie energy max" + this.energyMax);
                        //System.out.println("anonymous " + this.getTime());
                    }
                }

                if ((this.getTime() - tempTime) == (int) roundNumber) {
                    notificationNumber = this.getTime();
                }

                // Notification Phase
                if ((this.getTime() - this.tempTime) >= (int) roundNumber) {
                    if ((this.getTime() - (int) this.notificationNumber) < (int) k) {
                        if (halt && this.getTime() % 2 == 1) {
                            sendAll(new Message("halt", "HALT"));
                            haltSent = true;
                        }
                    } else {
                        this.k += 1;
                        log.info(String.format("%s[Notification Phase] Anonymous %s CHANGEMENT de k=%s", LOGGER, this.getID(), this.k));
                        this.initialTime = this.getTime();
                        energyArray = new ArrayList<>();
                        this.energy = 1;
                        //System.out.println(" ROUND KKKKKKKKKKK " + round);
                        //System.out.println(" INITIAL KKKKKKKKKKKKK " + initialTime);
                        this.round = (int) getRound(k, delta, c);
                        this.realRound = 0;
                        //System.out.println(" k anonymous " + k);
                        //System.out.println(" delta anonymous " + round);
                        //System.out.println(" ROUND " + round);
                        //System.out.println(" ROUND " + round);
                    }
                }
            }
        } else {
            // Notification Phase
            nodeNumber = this.k;
            if (once) {
                if (!haltSent && this.getTime() % 2 == 1) {
                    sendAll(new Message("halt", "HALT"));
                    haltSent = true;
                }
                if (haltSent){
                    log.info(String.format("%s[Notification Phase] Node %s Anonymous counts %s nodes in Topology after %s iterations", LOGGER, this.getID(), this.nodeNumber, this.realRound/2));
                    once = false;
                    if (this.getID() == Math.ceil((nodeNumber - 1)/2)) {
                        System.exit(0);
                    }
                }
            }
        }
        //System.out.println(" TEMPS " + this.getTime());
    }
}
