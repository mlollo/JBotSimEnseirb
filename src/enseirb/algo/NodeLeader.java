package enseirb.algo;

import jbotsim.Message;
import jbotsim.Node;
import jdk.nashorn.internal.ir.Flags;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class NodeLeader extends Node {

    private double round;
    private double delta;
    private double energy = 0;
    private double c;
    private double k = 4;
    private double nodeNumber;
    private boolean halt;
    private int tempTime;
    private double roundNumber;
    private double notificationNumber;
    private boolean isCorrect = true;
    List<Double> energyArray = new ArrayList();
    private int initialTime;
    private  int counter = 0;
    private boolean once = true;
    private double realRound = 0;
    private PrintWriter myWriter;
    private int kbis = 0;

    public NodeLeader(double delta, double c){
        this.delta = delta;
        this.c = c;
        this.round = getRound(this.k, this.c, this.delta);
        System.out.println("ROUND " + round);

        this.initialTime = 0;
        this.halt = false;
        this.counter = 0;
    }

    public static double getRound(double k, double c, double delta) { return k* Math.ceil(Math.pow(2*delta,k)*(c +1)*Math.log(k)) ; }

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
                int condition = Double.compare((k - 1 - 1 / Math.pow(k, c)), energy);

                if(condition <= 0 && kbis != k){

                    try {
                        kbis = (int) k;
                        FileWriter fw = new FileWriter("src/test.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw);

                        myWriter = new PrintWriter(bw);
                        String myString = "k =" + this.k + " nombre de round = " + (this.getTime() - this.initialTime);
                        myWriter.println(myString);
                        myWriter.close();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }

                }


            } else {

            }


            if ((this.getTime() - initialTime) >= (int)round) {
                //Verification Phase
                if ((this.getTime() - this.tempTime) < (int)roundNumber) {
                    if(message.getFlag().equals("ENERGYVEF")) {
                        energyArray.add(new Double(message.getContent().toString()));
                    }
                    int temp1 = Double.compare((k - 1 - 1 / Math.pow(k, c)), energy);
                    int temp2 = Double.compare( this.k - 1, energy);

                    //System.out.println(" temp 1 ! " + temp1);
                    //System.out.println(" temp 2 ! " + temp2);
                    //System.out.println("valeur des comparaisons");
                   // System.out.println(k - 1 - 1 / Math.pow(k, c));
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

        //
        if(!halt) {
            if ((this.getTime() - this.initialTime) < (int) this.round) {
                if (counter == 0) {
                    counter++;
                } else {
                    this.realRound++;
                    counter = 0;
                }
            }

            if ((this.getTime() - this.initialTime) == (int)round) {
                this.tempTime = this.getTime();
                this.roundNumber = (1 + Math.ceil(k / (1 - (1 / Math.pow(k, this.c)))));

                //System.out.println("ROUND NUMER LEADER " + roundNumber);
                //System.out.println("ROUND temptime LEADER " + tempTime);

            }

            if ((this.getTime() -  initialTime) >= (int)round) {
                if ((int)roundNumber == (this.getTime() - tempTime)) {
                    notificationNumber = this.getTime();
                }


                // Notification Phase
                if((int)roundNumber == (this.getTime() - tempTime)){
                    notificationNumber = this.getTime();

                }

                if ((this.getTime() - this.tempTime) >= (int)roundNumber) {

                    if ((this.getTime() - (int)this.notificationNumber) < (int)k) {
                        if (isCorrect) {
                            sendAll(new Message("halt", "HALT"));
                            halt = true;
                        }
                    }else {
                        if (counter == 1) {
                            k += 1;
                            System.out.println(" LEADER "+getID()+" CHANGEMENT de k=" + k);
                            //round = this.getTime() +round;
                            this.initialTime = this.getTime();
                            energyArray = new ArrayList<>();
                            this.energy = 0;
                            this.isCorrect = true;
                            this.round = (int) getRound(k,delta,c);
                            System.out.println(" ROUND TH " + round + " REAL ROUND "+realRound);
                            counter = 0;
                        } else {
                            counter = 1;
                        }
                    }
                }
            }
        }else{
            nodeNumber = this.k;
            if (once) {
                //sendAll(new Message("halt", "HALT"));
                System.out.println(" leader "+getID()+" node number " +nodeNumber+ " final th round number "+ round + " final real round number " + realRound);
                once = false;
            }
        }

    }

}
