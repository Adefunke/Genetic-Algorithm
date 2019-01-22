/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticalgorithm;


import java.util.Random;

/**
 * @author FAkinola
 */
class ChromosomeSelection {
    double fitness;
    static int geneLength ;
    int[] genes ;
    int boundd;

    ChromosomeSelection(int bound, int geneLength, boolean rastrigin) {
        Random rn = new Random();
        boundd = bound;
        ChromosomeSelection.geneLength = geneLength;
        genes = new int[geneLength];
        //Set genes randomly for each chromosome
        if (rastrigin) {
            for (int i = 0; i < geneLength / 16; i++) {
                genes[i] = (rn.nextInt(91) - 45);
            }
        } else {
            for (int i = 0; i < genes.length; i++) {
                genes[i] = rn.nextInt(bound);
            }
        }

        fitness = 0;
    }

    ChromosomeSelection(int bound, boolean rastrigin) {
        Random rn = new Random();
        genes = new int[geneLength];
        boundd = bound;
        //Set genes randomly for each chromosome
        if (rastrigin) {
            for (int i = 0; i < geneLength / 16; i++) {
                genes[i] = (rn.nextInt(91) - 45);
            }
        } else {
            for (int i = 0; i < genes.length; i++) {
                genes[i] = rn.nextInt(bound);
            }
        }

    }

    /**
     * @param rastrigin
     * @return converts the genes in a chromosome to a string
     */
    String getChromosome(boolean rastrigin) {
        String chromosome = "";
        if (rastrigin) {
            for (int i = 0; i < geneLength / 16; i++) {
                chromosome += (double) getGene(i) / 10;
                if (i < (geneLength / 16) - 1) {
                    chromosome += ",";
                }
            }
        } else {
            getChromosome();
        }
        return chromosome;
    }/**
     * @return converts the genes in a chromosome to a string
     */
    String getChromosome() {
        String chromosome = "";
            for (int i = 0; i < geneLength; i++) {
                chromosome += getGene(i);
            }
        return chromosome;
    }


    /**
     * @return crafted from the aim of one max to have all genes as 1
     */
    double calcFitness() {

        fitness = 0;
        for (int i = 0; i < geneLength; i++) {
            if (genes[i] == boundd - 1) {
                ++fitness;
                //fitness= (geneLength - i) + fitness;
            }
        }
        return fitness;
    }


    /**
     * @return
     * grafted from the rastrigin equation
     */
   double calcFitnessRas() {

        fitness = 0;
        for (int i = 0; i < geneLength / 16; i++) {
            fitness += Math.pow(genes[i] / 10, 2) - (10 * Math.cos(2 * Math.PI * genes[i] / 10));
        }
        fitness += (10 * geneLength / 16);
        return fitness;
    }

   
    int getGene(int index) {
        return genes[index];
    }

    void setGene(int index, int value) {
        genes[index] = value;
    }

}
