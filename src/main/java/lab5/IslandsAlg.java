package lab5;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.framework.islands.Migration;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class IslandsAlg {

    public static TimeAndBestFit main_algo(int complexity) {
        int dimension = 100; // dimension of problem
        // int complexity = 1; // fitness estimation time multiplicator
        int populationSize = 100; // size of population
        int generations = 100; // number of generations
        double alpha = 0.5; // arithmetic crossover
        double mutation_prob = 0.05D;
        // int eliteCount = (int) 0.15*populationSize; // number of elite solutions

        Random random = new Random(); // random

        CandidateFactory<double[]> factory = new MyFactory(dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover(alpha)); // Crossover
        operators.add(new MyMutation(mutation_prob)); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity); // Fitness function

        // List<EvolutionEngine<T>> islands,
        //                           Migration<? super T> migration,
        //                           boolean naturalFitness,
        //                           Random rng


        // public IslandEvolution(int islandCount,
        //                           Migration<? super T> migration,
        //                           CandidateFactory<T> candidateFactory,
        //                           EvolutionaryOperator<T> evolutionScheme,
        //                           FitnessEvaluator<? super T> fitnessEvaluator,
        //                           SelectionStrategy<? super T> selectionStrategy,
        //                           Random rng)

        int islandCount = 10;
        int islandPopulationSize = (int) populationSize / islandCount;

        Migration migration = new RingMigration();

        IslandEvolution<double[]> island_model = new IslandEvolution<double[]>(
                islandCount, migration, factory, pipeline, evaluator, selection, random);

        island_model.addEvolutionObserver(new IslandEvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
//                System.out.println("Epoch " + populationData.getGenerationNumber() + ": " + bestFit);
//                System.out.println("\tEpoch best solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }

            public void islandPopulationUpdate(int i, PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
//                System.out.println("Island " + i);
//                System.out.println("\tGeneration " + populationData.getGenerationNumber() + ": " + bestFit);
//                System.out.println("\tBest solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }
        });

        int islandgeneration = generations / islandCount;

        TerminationCondition terminate = new GenerationCount(islandgeneration);

        long startTime = System.currentTimeMillis();
        island_model.evolve(islandPopulationSize, 1, 50, 2, terminate);
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        // System.out.println("Duration (ms): " + duration);

//        String algoName = "island";

        double bestFit =  ((MultiFitnessFunction) evaluator).bestVal;

//        String tableLine = String.format("| %s | %d | %d | %.3f|", algoName, complexity, duration,bestFit);
//        System.out.println(tableLine);

        return new TimeAndBestFit(bestFit, duration);
    }

    public static void main(String[] args) {

        int n_experiments = 10;
        int n_colmlexities = 5;
        String algoName = "island";
        long[] time_sums = new long[n_colmlexities];
        double[] bestFit_sums = new double[n_colmlexities];

        for (int j = 0; j < n_colmlexities; j++) {
            time_sums[j] = 0;
            bestFit_sums[j] = 0;
        }

        for (int i = 0; i < n_experiments; i++) {
            System.out.println("| algorithm | complexity | time (ms) | result | ");
            System.out.println("| :- | :- | :- | :- |");
            for (int complexity = 1; complexity <= n_colmlexities; complexity++) {
                TimeAndBestFit tv = main_algo(complexity);
                time_sums[complexity-1] += tv.time;
                bestFit_sums[complexity-1] += tv.bestFit;

                String tableLine = String.format("| %s | %d | %d | %.3f|", algoName, complexity, tv.time, tv.bestFit);
                System.out.println(tableLine);
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("| algorithm | complexity | time (ms) | result | ");
        System.out.println("| :- | :- | :- | :- |");

        for (int complexity = 1; complexity <= n_colmlexities; complexity++) {
            long duration = time_sums[complexity-1]/n_experiments;
            double bestFit = bestFit_sums[complexity-1]/n_experiments;
            String tableLine = String.format("| %s | %d | %d | %.3f|", algoName, complexity, duration, bestFit);
            System.out.println(tableLine);
        }

    }
}
