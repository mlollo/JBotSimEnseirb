package enseirb.ic;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;


import static jbotsimx.Connectivity.isConnected;


// création de la topologie pour tester ic d'alessia
public class TopoTest{
   Topology tp10;
    private Node n0;
    private Node n1;
    private Node n2;
    private Node n3;
    private Node n4;
    private Node n5;
    private Node n6;
    private Node n7;


    private Link l1;
    private Link l2;
    private Link l3;
    private Link l4;
    private Link l5;
    private Link l6;
    private Link l7;
    private Link l8;

    private double round;
    private double k = 4;
    //private double c = 1.1 * Math.log(5)/Math.log(2);
    private double c = 3;
    private double delta = 2;


    public TopoTest(){
        //this.round = Math.ceil(Math.log((k/2)*(3*k+7)+ Math.log((k) + (c + 1) * ((Math.pow((double)2*delta,k+1) * (k + 1) * Math.log(k+1)/Math.log((double)2*delta))- Math.pow((double)2*delta,k+1)*Math.log(k+1)/Math.pow(Math.log((double)2*delta),(double)2)))));
        this.round = k* Math.ceil(Math.pow(2*delta,k)*(c +1)*Math.log(k));
        this.tp10 = new Topology(1920,1080,false);
        /*this.n0 = new NodeLeader(3, 3);
        this.n1 = new NodeAnonymous(3, 3);
        this.n2 = new NodeAnonymous(3, 3);
        this.n3 = new NodeAnonymous(3, 3);
        this.n4 = new NodeAnonymous(3, 3);
        this.n5 = new NodeAnonymous(3, 3);*/
        this.n0 = new TauNodeLeader(k,c);
        this.n1 = new TauAnonymousNode(k,c);
        this.n2 = new TauAnonymousNode(k,c);
        this.n3 = new TauAnonymousNode(k,c);
        this.n4 = new TauAnonymousNode(k,c);
        this.n5 = new TauAnonymousNode(k,c);

        /*this.n6 = new NodeAnonymous(round);
        this.n7 = new NodeAnonymous(round);
        */
        /*this.l1 = new Link(n0,n1);
        this.l2 = new Link(n0,n2);
        this.l3 = new Link(n0,n3);
        this.l4 = new Link(n0,n4);
        this.l5 = new Link(n3,n4);
        this.l6 = new Link(n4,n5);
        this.l7 = new Link(n4,n6);
        this.l8 = new Link(n4,n7);
        */


        this.l1 = new Link(n0,n1);
        this.l2 = new Link(n1,n2);
        this.l3 = new Link(n2,n3);
        this.l4 = new Link(n3,n4);
        this.l5 = new Link(n4,n5);


        System.out.println("round " + round);
        System.out.println("c " + c);

    }


    public void addTpology() {
        this.tp10.disableWireless();
        this.tp10.addNode(1200, 550, n0);
        this.tp10.addNode(150, 250, n1);
        this.tp10.addNode(200, 400, n2);
        this.tp10.addNode(380, 600, n3);
        this.tp10.addNode(250, 800, n4);
        this.tp10.addNode(300, 300, n5);

        /*this.tp10.addNode(600, 600, n6);
        this.tp10.addNode(1000, 700, n7);
        */

        this.tp10.addLink(l1);
        this.tp10.addLink(l2);
        this.tp10.addLink(l3);
        this.tp10.addLink(l4);
        this.tp10.addLink(l5);
        /*this.tp10.addLink(l5);
        this.tp10.addLink(l6);
        this.tp10.addLink(l7);
        this.tp10.addLink(l8);
        */

        System.out.println("connexité " + isConnected(tp10));
        System.out.println("connexité " + 1/0.0005);
        this.tp10.setClockSpeed((int)0.1);


        this.tp10.start();



    }




}
