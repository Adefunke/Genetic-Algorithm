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
public class GeneticAlgorithm {
    public ChromosomeSelection[][] chromosomeSelected;
    /**
     * @param args the command line arguments
     */
    private Population population = new Population();
    private Population tempPopulation;
    private Population switchOverPopulation = population;
    private Computations computations = new Computations();
    private ChromosomeSelection fittest;
    private ChromosomeSelection secondFittest;
    private ChromosomeSelection firstPicked;
    private ChromosomeSelection secondPicked;
    private int generationCount = 1;
    private boolean fps = false;
    private double[] fitnessProb;
    private boolean rastrigan = false;
    double currentHighestlevelOfFitness;
    int noOfmutations = 0;
    int noOfComputatons = 0;
    int noOfCrossover = 0;
    //this controls if what we are computing contains integer or binary values
    private int bound = 2;
    int popSize = 100;
    //this dictates the length of each individuals/chromosomes
    private int geneLength = 16;

    public static void main(String[] args) {

        Random rn = new Random();
        String fittestChromosome = "";
        GeneticAlgorithm ga = new GeneticAlgorithm();

        //Initialize population
        ga.population.initializePopulation(ga.bound, ga.geneLength, ga.rastrigan, ga.popSize);

        //Calculate fitness of each chromosome to get the fittest before evolution begins
        ga.fittest = ga.population.calculateFitness(ga.rastrigan);
        ga.currentHighestlevelOfFitness = ga.population.fittest;
        System.out.println("Generation: " + ga.generationCount + " Fittest: " + ga.currentHighestlevelOfFitness);
        fittestChromosome = ga.fittest.getChromosome(ga.rastrigan);
        System.out.println(fittestChromosome);
        //(ga.geneLength * (ga.geneLength + 1) / 2)
        //While population searches for a chromosome with maximum fitness
        while (((ga.population.fittest > 0 && ga.rastrigan) || (ga.population.fittest < (ga.geneLength) && !ga.rastrigan))
                && ga.generationCount < ga.popSize) {
            ++ga.generationCount;
            ga.tempPopulation = ga.population;
            if (ga.fps) {
                ga.fitnessProb = ga.tempPopulation.calculateProbFitness(ga.rastrigan);
            }
            int beginfrom = ga.naturalSelection(new Random().nextBoolean());
            //if rastrigan, change the number format to graycode
            if (ga.rastrigan) {
                for (int j = 0; j < ga.popSize; j++) {
                    for (int i = 0; i < ga.geneLength / 16; i++) {
                        String grayChromosome = ga.computations.decimalToGray(ga.population.getChromosome(j).getGene(i));
                        for (int grayMembers = 16 * i; grayMembers < 16 * (i + 1); grayMembers++) {
                            ga.tempPopulation.getChromosome(j).setGene
                                    (grayMembers, Character.getNumericValue(grayChromosome.charAt(grayMembers - (16 * i))));
                        }
                    }
                }
            }
            //Do the things involved in evolution
            for (; beginfrom < ga.popSize; beginfrom += 2) {
                if (ga.fps) {
                    ga.fPSelection(ga.rastrigan);
                } else {
                    ga.tournamentSelection(ga.tempPopulation.populationSize, ga.rastrigan);
                }
                ++ga.noOfComputatons;
                //crossover with a random and quite high probability
                if (rn.nextInt() % 5 < 4) {
                    ++ga.noOfCrossover;
                    ga.twoPointCrossover();
                }

                //mutate with a random and quite low probability
                if (rn.nextInt() % 23 >= 18) {
                    ++ga.noOfmutations;
                    ga.mutation();
                }
                if (ga.rastrigan) {
                    ga.switchOverPopulation.saveChromosomes(beginfrom, ga.computations.grayToDecimal(ga.firstPicked));
                    ga.switchOverPopulation.saveChromosomes(beginfrom + 1, ga.computations.grayToDecimal(ga.secondPicked));
                } else {
                    ga.switchOverPopulation.saveChromosomes(beginfrom, ga.computations.grayToBinary(ga.firstPicked, ga.bound));
                    ga.switchOverPopulation.saveChromosomes(beginfrom + 1, ga.computations.grayToBinary(ga.secondPicked, ga.bound));
                }
            }
            // moving the new generation into the old generation space
            ga.population = ga.switchOverPopulation;

            //Calculate new fitness value
            ga.fittest = ga.population.calculateFitness(ga.rastrigan);
            ga.currentHighestlevelOfFitness = ga.population.fittest;
            System.out.println("Generation: " + ga.generationCount + " Fittest: " + ga.currentHighestlevelOfFitness);
            fittestChromosome = ga.fittest.getChromosome(ga.rastrigan);
            System.out.println(fittestChromosome);
        }
        //when a solution is found or 100 generations have been produced
        System.out.println("\nSolution found in generation " + ga.generationCount);
        System.out.println("Fitness: " + ga.currentHighestlevelOfFitness);
        System.out.print("Genes: ");
        System.out.println(fittestChromosome);
        System.out.println("probability of mutation is " + (double) ga.noOfmutations / ga.noOfComputatons);
        System.out.println("probability of cross over is " + (double) ga.noOfCrossover / ga.noOfComputatons);
    }

    //Selection
    int naturalSelection(boolean elitism) {
        if (elitism) {
            //Select the most fittest chromosome
            fittest = tempPopulation.getFittest(rastrigan);

            //Select the second most fittest chromosome
            secondFittest = tempPopulation.getSecondFittest();

            switchOverPopulation.saveChromosomes(0, fittest);
            switchOverPopulation.saveChromosomes(1, secondFittest);
            return 2;
        }
        return 0;
    }

    /**
     * @param popSize
     * @param rastrigan this picks two chromosomes randomly. In tournament selection, the norm is to randomly pick k numbers of chromosomes,
     *                  then select the best and return it to the population so as to increase the chance of picking global optimum.
     *                  k can be between 1 and n; Here, I'm picking one random chromosome each then the reproduction process.
     */
    private void tournamentSelection(int popSize, boolean rastrigan) {
        if (rastrigan) {
            firstPicked = tempPopulation.randomlyPicked(popSize);
            secondPicked = tempPopulation.randomlyPicked(popSize);
        } else {
            firstPicked = computations.binaryToGray(tempPopulation.randomlyPicked(popSize), bound, rastrigan);
            secondPicked = computations.binaryToGray(tempPopulation.randomlyPicked(popSize), bound, rastrigan);
        }
    }

    /**
     * @param rastrigan
     * @param popsiz    tournament selection where 2 chromosomes are randomly picked and the fittest is added again to the population;
     *                  the population size would ideally increase by 2 but it would give more options.
     */
    private void tournamentSelection(boolean rastrigan, int popsiz) {
        Population tournamentPopulation = new Population();
        popSize = popsiz;
        tournamentPopulation.initializePopulation(bound, geneLength, rastrigan, (2 * popSize));
        for (int i = 0; i < popsiz; i++) {
            tournamentPopulation.saveChromosomes(i, tempPopulation.getChromosome(i));
        }
        for (int i = 0; i < popsiz; i++) {
            tournamentSelection(popsiz, rastrigan);
            popSize++;
            if (firstPicked.fitness > secondPicked.fitness) {
                tournamentPopulation.saveChromosomes(popSize - 1, firstPicked);
            } else {
                tournamentPopulation.saveChromosomes(popSize - 1, secondPicked);
            }
        }
        if (rastrigan) {
            firstPicked = tournamentPopulation.randomlyPicked(popSize);
            secondPicked = tournamentPopulation.randomlyPicked(popSize);
        } else {
            firstPicked = computations.binaryToGray(tournamentPopulation.randomlyPicked(popSize), bound, rastrigan);
            secondPicked = computations.binaryToGray(tournamentPopulation.randomlyPicked(popSize), bound, rastrigan);
        }popSize=100;
    }

    private void fPSelection(boolean rastrigan) {
        double rand = new Random().nextDouble();
        double rand2 = new Random().nextDouble();
        firstPicked = computations.binaryToGray(tempPopulation.getChromosome(positionOfChromosome(rand)), bound, rastrigan);
        secondPicked = computations.binaryToGray(tempPopulation.getChromosome(positionOfChromosome(rand2)), bound, rastrigan);
    }

    private int positionOfChromosome(double rand) {
        if (rand > 0.6) {
            for (int i = popSize - 1; i > 0; i--) {
                if (rand > fitnessProb[i]) {
                    return i + 1;
                }
            }

        } else {
            for (int i = 0; i < popSize; i++) {
                if (rand < fitnessProb[i]) {
                    return i;
                }
            }
        }
        return 0;
    }

    //Two point crossover
    private void twoPointCrossover() {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint1 = rn.nextInt(population.chromosomes[0].geneLength);
        int crossOverPoint2 = rn.nextInt(population.chromosomes[0].geneLength);
        if (crossOverPoint1 > crossOverPoint2) {
            int temp = crossOverPoint2;
            crossOverPoint2 = crossOverPoint1;
            crossOverPoint1 = temp;
        }
        //Swap values among parents
        for (int i = crossOverPoint1; i < crossOverPoint2; i++) {
            int temp = firstPicked.genes[i];
            firstPicked.genes[i] = secondPicked.genes[i];
            secondPicked.genes[i] = temp;

        }

    }

    //One point crossover
    private void onePointCrossover() {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint = rn.nextInt(population.chromosomes[0].geneLength);

        //Swap values among parents
        for (int i = 0; i < crossOverPoint; i++) {
            int temp = firstPicked.genes[i];
            firstPicked.genes[i] = secondPicked.genes[i];
            secondPicked.genes[i] = temp;

        }

    }

    //Uniform crossover
    private void uniformCrossover() {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint = rn.nextInt(population.chromosomes[0].geneLength);

        //Swap values uniformly among parents
        for (int i = 0; i < population.chromosomes[0].geneLength; i++) {
            int temp = firstPicked.genes[i];
            firstPicked.genes[i] = secondPicked.genes[i];
            secondPicked.genes[i] = temp;
            i++;
        }

    }

    /**
     * picking a random gene and swapping it with its allelle
     */
    private void mutation() {
        Random rn = new Random();

        //Select a random mutation point
        int mutationPoint = rn.nextInt(population.chromosomes[0].geneLength);

        //Flip values at the mutation point
        firstPicked.genes[mutationPoint] = computations.getRandomAllele(firstPicked.genes[mutationPoint], bound);

        mutationPoint = rn.nextInt(population.chromosomes[0].geneLength);
        secondPicked.genes[mutationPoint] = computations.getRandomAllele(secondPicked.genes[mutationPoint], bound);

    }

}
