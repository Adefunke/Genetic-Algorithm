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
public class Population {
    ChromosomeSelection[] chromosomes;
    double[] fitnessProb;
    double fittest = 0;
    int maxFit;
    int populationSize = 0;

    //Initialize population
    public void initializePopulation(int bound, int geneLength, boolean rastrigin, int popSize) {
        fitnessProb = new double[popSize];
        chromosomes = new ChromosomeSelection[popSize];
        populationSize = popSize;
        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i] = new ChromosomeSelection(bound, geneLength, rastrigin);
        }
    }

    /**
     * @param index
     * @param chromosome
     * saves a chromosome that has probably undergone change or is new
     */
    public void saveChromosomes(int index, ChromosomeSelection chromosome) {
        chromosomes[index] = chromosome;
    }

    /**
     * @param popSize
     * @return
     * randomly pick within the array
     */
    public ChromosomeSelection randomlyPicked(int popSize) {
        return chromosomes[new Random().nextInt(popSize)];
    }

    public ChromosomeSelection getChromosome(int index) {
        return chromosomes[index];
    }

    /**
     * @param rastrigin
     * @return  fittest chromosome
     */
    public ChromosomeSelection getFittest(boolean rastrigin) {
        if (!rastrigin) {
            maxFit = 0;
            for (int i = 0; i < chromosomes.length; i++) {
                if (chromosomes[maxFit].fitness <= chromosomes[i].fitness) {
                    maxFit = i;
                }
            }
        } else {
            maxFit = 0;
            for (int i = 0; i < chromosomes.length; i++) {
                if (chromosomes[maxFit].fitness >= chromosomes[i].fitness && chromosomes[maxFit].fitness > 0) {
                    maxFit = i;
                }
            }
        }
        fittest = chromosomes[maxFit].fitness;
        return chromosomes[maxFit];
    }

    /**
     * @return second fittest chromosome when requested for via elitism
     */
    public ChromosomeSelection getSecondFittest() {
        int maxFit2 = 0;
        for (int i = 0; i < chromosomes.length; i++) {
            if (chromosomes[maxFit2].fitness < chromosomes[i].fitness && i != maxFit) {
                maxFit2 = i;
            }
        }
        return chromosomes[maxFit2];
    }

    /**
     * @param rastrigin
     * @return
     * Calculates the fitness of each chromosome
     */

    public ChromosomeSelection calculateFitness(boolean rastrigin) {

        for (int i = 0; i < chromosomes.length; i++) {
            if (!rastrigin) {
                chromosomes[i].calcFitness();
            } else {
                chromosomes[i].calcFitnessRas();
            }
        }
        return getFittest(rastrigin);
    }//Calculate cumulative fitness of all the chromosomes

    /**
     * @param rastrigin
     * @return calculates the cumulative fitness of each member
     */
    public double calculateCumulativeFitness(boolean rastrigin) {
        double totalFitness = 0.0;
        for (int i = 0; i < chromosomes.length; i++) {
            if (!rastrigin) {
                totalFitness += chromosomes[i].calcFitness();
                fitnessProb[i] = totalFitness;
            } else {
                totalFitness += chromosomes[i].calcFitnessRas();
                fitnessProb[i] = totalFitness;
            }
        }
        return totalFitness;
    }

    /**
     * @param rastrigin
     * @return
     * calculates the cdf's probability
     */
    public double[] calculateProbFitness(boolean rastrigin) {
        double totalFitness = calculateCumulativeFitness(rastrigin);
        for (int i = 0; i < chromosomes.length; i++) {
            fitnessProb[i] = fitnessProb[i] / totalFitness;

        }
        return fitnessProb;
    }

}
