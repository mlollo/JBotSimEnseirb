package enseirb.algo;

import jbotsim.Message;
import jbotsim.Node;

import java.io.*;

public class TauNodeLeader extends Node {

    private double delta = 2;
    private double energy = 0;
    private double c;
    private double k;
    private boolean halt;
    private int nbRound;
    private int nbRoundtest;
    private int counter;
    private PrintWriter myWriter;

    public TauNodeLeader(double k, double c){

        this.nbRound = 0;
        this.nbRoundtest = 2;
        this.halt = false;
        this.counter=0;
        this.c = c;
        this.k = k;

    }





    @Override
    public void onMessage (Message message){

        if (!halt) {
            // Collection Phase
            // pour le moment le node avec un id d 0 correspond au node leader. Je récupère ll'ernergie des noeuds sur un nombre de round donné



            int temp1 = Double.compare((k - 1 - 1 / Math.pow(k, c)), energy);
            System.out.println("temp1 " + energy);
            System.out.println("temp1 " + temp1);

            if (message.getFlag().equals("ENERGY")) {
                //System.out.println("le node : " + message.getSender()  + " dit : " + message.getContent() + " au node " + message.getDestination() );
                // System.out.println("contenu" + new Double(message.getContent().toString());
                energy = energy + new Double(message.getContent().toString());
                System.out.println("energy leader " + energy);


            }

            if(temp1 <= 0){
                System.out.println("borne inf bal " + (k - 1 - 1 / Math.pow(k, c)));
                System.out.println("nb round bal "+ nbRound);
                System.out.println("time "+ this.getTime());
                halt = true;
                try {
                    FileWriter fw = new FileWriter("src/test.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);

                    myWriter = new PrintWriter(bw);
                    String myString = "k =" + this.k + " nombre de round = " + nbRound;
                    myWriter.println(myString);
                    myWriter.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }

            }else{
               // if (counter == 0) {
                 //   nbRoundtest++;
                //}
                //System.out.println("node leader " + nbRound);
                //System.out.println("borne inf " + (k - 1 - 1 / Math.pow(k, c)));

            }
        }






    }

    @Override
    public void onClock (){




        if (counter == 0) {
            counter++;
            nbRound++;
            nbRoundtest++;
        } else {
            counter = 0;
        }










    }

}
