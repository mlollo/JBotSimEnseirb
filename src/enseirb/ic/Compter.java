package enseirb.ic;

import jbotsim.ui.JViewer;


// classe permettant de test pour l'ic d'alessia
public class Compter {
        public static void main(String[] args) {

            TopoTest toto = new TopoTest();
             toto.addTpology();

            new JViewer(toto.tp10);

        }
    }


