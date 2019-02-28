/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private double currentHighestlevelOfFitness;
    private int noOfmutations = 0;
    private int noOfComputatons = 0;
    private int noOfCrossover = 0;
    //this controls if what we are computing contains integer or binary values
    private int bound = 2;
    private int[] popSizeArray = {6, 10, 20, 50, 100, 150, 200, 250, 300, 350, 400, 4};
    // private int popSize = 100;
    int standardPop = 100;
    //this dictates the length of each individuals/chromosomes
    private int geneLength = 64;
    public int noOfEvaluation = 0;

    public static void main(String[] args) throws CloneNotSupportedException {

        ArrayList<String> result = new ArrayList<>();

        GeneticAlgorithm ga = new GeneticAlgorithm();
//Get the file reference
        Path path = Paths.get("C:\\Users\\GO\\IdeaProjects\\GeneticAlgorithm\\output.txt");

        for (int popsiz : ga.popSizeArray) {

//Use try-with-resource to get auto-closeable writer instance
            for (int m = 0; m < 20; m++) {
                result.add("\n" + popsiz + "\t ");
                ga.resetter();

                Random rn = new Random();
                String fittestChromosome = "";
                //Initialize population
                ga.population.initializePopulation(ga.bound, ga.geneLength, ga.rastrigan, popsiz);

                //Calculate fitness of each chromosome to get the fittest before evolution begins
                ga.fittest = ga.population.calculateFitness(ga.rastrigan);
                ga.currentHighestlevelOfFitness = ga.population.fittest;
                System.out.println("Generation: " + ga.generationCount + " Fittest: " + ga.currentHighestlevelOfFitness);
                fittestChromosome = ga.fittest.getChromosome(ga.rastrigan);
                System.out.println(fittestChromosome);
                //(ga.geneLength * (ga.geneLength + 1) / 2)
                //While population searches for a chromosome with maximum fitness
                ga.switchOverPopulation = (Population) ga.population.clone();
                while (((ga.population.fittest > 0 && ga.rastrigan)
                        || (ga.population.fittest < (ga.geneLength) && !ga.rastrigan))
                ) {
                    ++ga.generationCount;
                    ga.tempPopulation = (Population) ga.population.clone();
                    if (ga.fps) {
                        ga.fitnessProb = ga.tempPopulation.calculateProbFitness(ga.rastrigan);
                    }
                    int beginfrom;
                    if (popsiz == 2) {
                        beginfrom = ga.naturalSelection(false);
                    } else {
                        beginfrom = ga.naturalSelection(true);
                    }
                    //if rastrigan, change the number format to graycode
                    if (ga.rastrigan) {
                        for (int j = 0; j < popsiz; j++) {
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
                    for (; beginfrom < popsiz; beginfrom += 2) {
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
                            ga.switchOverPopulation.saveChromosomes(beginfrom, (ChromosomeSelection) ga.computations.grayToDecimal
                                    (ga.firstPicked).clone());
                            ga.switchOverPopulation.saveChromosomes(beginfrom + 1,
                                    (ChromosomeSelection) ga.computations.grayToDecimal(ga.secondPicked).clone());
                        } else {
                            ga.switchOverPopulation.saveChromosomes(beginfrom, (ChromosomeSelection) ga.computations.grayToBinary(ga.firstPicked, ga.bound).clone());
                            ga.switchOverPopulation.saveChromosomes(beginfrom + 1, (ChromosomeSelection) ga.computations.grayToBinary(ga.secondPicked, ga.bound).clone());
                        }
                    }
                    // moving the new generation into the old generation space
                    ga.population = (Population) ga.switchOverPopulation.clone();

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
                System.out.println("\nno of evaluations " + ga.noOfEvaluation);
                System.out.print("Genes: ");
                System.out.println(fittestChromosome);
                System.out.println("probability of mutation is " + (double) ga.noOfmutations / ga.noOfComputatons);
                System.out.println("probability of cross over is " + (double) ga.noOfCrossover / ga.noOfComputatons);
                result.add(String.valueOf(ga.noOfEvaluation));
            }
        }
//Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(String.valueOf(result));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }


    private void resetter() {
        population = new Population();
        switchOverPopulation = null;
        tempPopulation = null;
        noOfEvaluation = 0;
        generationCount = 1;
        noOfmutations = 0;
        noOfComputatons = 0;
        noOfCrossover = 0;
        currentHighestlevelOfFitness = 0;
        fittest = null;
        secondFittest = null;
        firstPicked = null;
        secondPicked = null;
    }

    //Selection
    int naturalSelection(boolean elitism) throws CloneNotSupportedException {
        if (true) {
            //Select the most fittest chromosome
            fittest = (ChromosomeSelection) tempPopulation.getFittest(rastrigan).clone();

            //Select the second most fittest chromosome
            secondFittest = (ChromosomeSelection) tempPopulation.getSecondFittest().clone();

            switchOverPopulation.saveChromosomes(0, (ChromosomeSelection) fittest.clone());
            switchOverPopulation.saveChromosomes(1, (ChromosomeSelection) secondFittest.clone());
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
    private void tournamentSelection(int popSize, boolean rastrigan) throws CloneNotSupportedException {
        if (rastrigan) {
            firstPicked = (ChromosomeSelection) tempPopulation.randomlyPicked(popSize).clone();
            secondPicked = tempPopulation.randomlyPicked(popSize);
        } else {
            ++noOfEvaluation;
            ChromosomeSelection[] selection = new ChromosomeSelection[4];
            for (int i = 0; i < selection.length; i++) {
                selection[i] = tempPopulation.randomlyPicked(popSize);
            }
            int best = 0;
            for (int i = 1; i < selection.length; i++) {
                ++noOfEvaluation;
                if (selection[i].fitness > selection[i - 1].fitness) {
                    best = i;
                }
            }
            firstPicked = (ChromosomeSelection) computations.binaryToGray(selection[best], bound, rastrigan).clone();
            if (best != selection.length - 1) {
                selection[best] = selection[selection.length - 1];

            }
            for (int i = 1; i < selection.length - 1; i++) {
                ++noOfEvaluation;
                if (selection[i].fitness > selection[i - 1].fitness) {
                    best = i;
                }
            }
            secondPicked = (ChromosomeSelection) computations.binaryToGray(selection[best], bound, rastrigan).clone();
        }
    }

    /**
     * @param rastrigan
     * @param popsiz    tournament selection where 2 chromosomes are randomly picked and the fittest is added again to the population;
     *                  the population size would ideally increase by 2 but it would give more options.
     */
//    private void tournamentSelection(boolean rastrigan, int popsiz) throws CloneNotSupportedException {
//        Population tournamentPopulation = new Population();
//        popSize = popsiz;
//        tournamentPopulation.initializePopulation(bound, geneLength, rastrigan, (2 * popSize));
//        for (int i = 0; i < popsiz; i++) {
//            tournamentPopulation.saveChromosomes(i, (ChromosomeSelection) tempPopulation.getChromosome(i).clone());
//        }
//        for (int i = 0; i < popsiz; i++) {
//            tournamentSelection(popsiz, rastrigan);
//            popSize++;
//            if (firstPicked.fitness > secondPicked.fitness) {
//                tournamentPopulation.saveChromosomes(popSize - 1, (ChromosomeSelection) firstPicked.clone());
//            } else {
//                tournamentPopulation.saveChromosomes(popSize - 1, (ChromosomeSelection) secondPicked.clone());
//            }
//        }
//        popSize = standardPop;
//    }
    private void fPSelection(boolean rastrigan) throws CloneNotSupportedException {
        double rand = new Random().nextDouble();
        double rand2 = new Random().nextDouble();
        // ++noOfEvaluation;
        firstPicked = (ChromosomeSelection) computations.binaryToGray(tempPopulation.getChromosome(positionOfChromosome(rand)), bound, rastrigan).clone();
        secondPicked = (ChromosomeSelection) computations.binaryToGray(tempPopulation.getChromosome(positionOfChromosome(rand2)), bound, rastrigan).clone();
    }

    private int positionOfChromosome(double rand) {
//        if (rand > 0.6) {
//            for (int i = popSize - 1; i > 0; i--) {
//                if (rand > fitnessProb[i]) {
//                    return i + 1;
//                }
//            }
//
//        } else {
//            for (int i = 0; i < popSize; i++) {
//                if (rand < fitnessProb[i]) {
//                    return i;
//                }
//            }
//        }
        return 0;
    }

    //Two point crossover
    private void twoPointCrossover() {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint1 = rn.nextInt(ChromosomeSelection.geneLength);
        int crossOverPoint2 = rn.nextInt(ChromosomeSelection.geneLength);
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
        int crossOverPoint = rn.nextInt(ChromosomeSelection.geneLength);

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
        int crossOverPoint = rn.nextInt(ChromosomeSelection.geneLength);

        //Swap values uniformly among parents
        for (int i = 0; i < ChromosomeSelection.geneLength; i++) {
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
        int mutationPoint = rn.nextInt(ChromosomeSelection.geneLength);

        //Flip values at the mutation point
        firstPicked.genes[mutationPoint] = computations.getRandomAllele(firstPicked.genes[mutationPoint], bound);

        mutationPoint = rn.nextInt(ChromosomeSelection.geneLength);
        secondPicked.genes[mutationPoint] = computations.getRandomAllele(secondPicked.genes[mutationPoint], bound);

    }

}