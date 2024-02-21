package lab5;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.Random;

public class MyFactory extends AbstractCandidateFactory<double[]> {

    private int dimension;

    public MyFactory(int dimension) {
        this.dimension = dimension;
    }

    private double getRandomDoubleInRange(Random random, int min_possible_val, int max_possible_val) {
        return random.nextDouble() * (max_possible_val - min_possible_val) + min_possible_val;
    }

    public double[] generateRandomCandidate(Random random) {
        double[] solution = new double[dimension];
        int min_possible_val = -5;
        int max_possible_val = 5;

        // x from -5.0 to 5.0
        for (int i = 0; i < dimension; i++)
            solution[i] = getRandomDoubleInRange(random, min_possible_val, max_possible_val);

        return solution;
    }
}


