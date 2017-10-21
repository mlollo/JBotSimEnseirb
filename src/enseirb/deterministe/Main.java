package enseirb.deterministe;

import jbotsim.ui.JViewer;

public class Main{
    public static void main(String[] args) {
        int width = 1920;
        int height = 1080;
        int nbNodes = 25;

        DetGraph graph = new DetGraph(nbNodes, width, height);
        graph.createGraph(width/2, height/2, height/4);
        graph.addInnerLinks(5);
        new JViewer(graph.tp);
    }
}
