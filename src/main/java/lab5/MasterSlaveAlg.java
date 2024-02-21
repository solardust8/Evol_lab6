package lab5;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MasterSlaveAlg {

    public static void main_algo(int complexity, boolean isSingleThreaded) {
        int dimension = 100; // dimension of problem
//        int complexity = 1; // fitness estimation time multiplicator
        int populationSize = 100; // size of population
        int generations = 100; // number of generations
        // int eliteCount = (int) 0.05*populationSize;
        double alpha = 0.5;
        double mutation_prob = 0.05D;

        Random random = new Random(); // random

        CandidateFactory<double[]> factory = new MyFactory(dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover(alpha)); // Crossover
        operators.add(new MyMutation(mutation_prob)); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity); // Fitness function

        AbstractEvolutionEngine<double[]> algorithm = new SteadyStateEvolutionEngine<double[]>(
                factory, pipeline, evaluator, selection, populationSize, false, random);


        algorithm.setSingleThreaded(isSingleThreaded);

        algorithm.addEvolutionObserver(new EvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
//                System.out.println("Generation " + populationData.getGenerationNumber() + ": " + bestFit);
//                System.out.println("\tBest solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }
        });

        TerminationCondition terminate = new GenerationCount(generations);
        long startTime = System.currentTimeMillis();
        algorithm.evolve(populationSize, 1, terminate);
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        // System.out.println("Duration (ms): " + duration);

        String algoName = isSingleThreaded ? "single-thread" : "master-slave";

        String tableLine = String.format("| %s | %d | %d | %.3f|", algoName, complexity, duration, ((MultiFitnessFunction) evaluator).bestVal);
        System.out.println(tableLine);
    }

    public static void main(String[] args) {
        System.out.println("| algorithm | complexity | time (ms) | result | ");
        System.out.println("| :- | :- | :- | :- |");


        for (int complexity = 1; complexity <= 5; complexity++) {
            main_algo(complexity, true);
        }

        for (int complexity = 1; complexity <= 5; complexity++) {
            main_algo(complexity, false);
        }
    }
}
