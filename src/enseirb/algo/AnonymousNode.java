package enseirb.algo;

import jbotsim.Message;
import jbotsim.Node;

import java.util.ArrayList;
import java.util.List;

public class AnonymousNode extends Node{

    private double round;
    private double delta ;
    private double energy ;
    private int tempTime;
    private double roundNumber;
    private double notificationNumber;
    List<Double> energyArray = new ArrayList();
    private double k ;
    private boolean halt;
    private double c ;
    private double energyMax;
    private double nodeNumber;
    private int counter = 0;
    private int initialTime = 0;
    private boolean once = true;
    private double realRound = 0;
    private boolean booleanMessage = true;

    public AnonymousNode(double delta, double c) {
        this.delta = delta;
        this.energy = 1;
        this.c = c;
        this.k = 4;
        this.round = getRound(this.k, this.c, this.delta);
        this.initialTime = 0;
        this.halt = false;
    }

    public static double getRound(double k, double c, double delta) { return 2*k* Math.ceil(Math.pow(2*delta,k)*(c +1)*Math.log(k)); }

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


            // Verification Phase
            if ((this.getTime() - initialTime) >= (int)round) {
                //Verification Phase
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

        //
        if (!halt) {
            if ((this.getTime() - initialTime) < (int)this.round) {
                double s = this.getNeighbors().size();

                int tmp = Double.compare(energy,0);
                if (tmp >= 0) {
                    if (counter == 0) {
                        sendAll(new Message(this.energy / (2 * (double) this.delta), "ENERGY"));
                        this.energy = this.energy - this.energy * s / (2 * (double) this.delta);
                        counter++;
                    } else {
                        this.realRound++;
                        counter = 0;
                    }
                }


                // impression du temps de chaque round

            }

            // Verification Phase
            if ((this.getTime() - initialTime) == round) {
                this.tempTime = this.getTime();
                roundNumber = 1 + Math.ceil(this.k / (1 - (1 / Math.pow(this.k, this.c))));

                //System.out.println("ROUND NUMER ANONYMOUS " + roundNumber);
                //System.out.println("ROUND temptime ANONYMOUS " + tempTime);
                this.energyMax = this.energy;
            }

            if ((this.getTime() - initialTime) >= (int)round) {
                if (this.getTime() - this.tempTime < (int)roundNumber) {
                    sendAll(new Message(this.energyMax, "ENERGYVEF"));
                    //System.out.println(" Envoie energy max" + this.energyMax);


                }

                if((int)roundNumber == (this.getTime() - tempTime)){
                    notificationNumber = this.getTime();
                }


                if ((this.getTime() - this.tempTime) >= (int)roundNumber) {
                    if ((this.getTime() - this.notificationNumber) < this.k) {
                        if (halt) {
                            sendAll(new Message("halt", "HALT"));
                        }

                    } else {
                        if (counter == 1) {
                            this.k += 1;
                            System.out.println(" ANONYMOUS "+getID()+" CHANGEMENT de k=" + this.k);
                            this.initialTime = this.getTime();
                            energyArray = new ArrayList<>();
                            this.energy = 1;
                            //System.out.println(" ROUND KKKKKKKKKKK " + round);
                            //System.out.println(" INITIAL KKKKKKKKKKKKK " + initialTime);
                            this.round = (int) getRound(k,delta,c);
                            //System.out.println(" k anonymous " + k);
                            //System.out.println(" delta anonymous " + round);
                            //System.out.println(" ROUND " + round);
                            //System.out.println(" ROUND " + round);
                            counter = 0;
                        } else {
                            counter = 1;
                        }
                    }
                }


            }


        } else {
            nodeNumber = this.k;
            if (once) {
                sendAll(new Message("halt", "HALT"));
                System.out.println(" anonymous " +getID()+ " node number " + nodeNumber +" final round number "+realRound);
                once = false;
            }
        }

        //System.out.println(" TEMPS " + this.getTime());

    }
}
