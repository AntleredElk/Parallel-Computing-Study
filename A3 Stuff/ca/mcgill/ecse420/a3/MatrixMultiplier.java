package ca.mcgill.ecse420.a3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MatrixMultiplier {

    /**
     * The code is originally written for Assignment 1
     * And modified for this Assignment 3
     *
     * Edward Latulipe-Kang - 260746475
     * Rintaro Nomura - 260781007
     */

    private static final int NUMBER_THREADS = 2;
    private static final int MATRIX_SIZE = 2000;

    public static void main(String[] args) throws InterruptedException {
        // Create two matrices
        double[][] matrix_1 = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);   // 2000 x 2000
        double[][] matrix_2 = generateRandomMatrix(MATRIX_SIZE, 1);     // 2000 x 1 (therefore it's a vector)

        // Parallel Matrix Multiplication
        System.out.println("Parallel Matrix A･B computing... ");

        double millisecond = 1000000.0;
        double sleepTime = 1.0;
        long startParallel = System.nanoTime();
        double[][] res1 = parallelMultiplyMatrix(matrix_1, matrix_2);   // result from parallel multiplier
        long finishParallel = System.nanoTime();
        System.out.println(
            "Parallel Matrix A･B - Time: " + ((finishParallel - startParallel - sleepTime) / millisecond)
                + " ms. ");

        // Sequential Matrix Multiplication
        System.out.println("Sequential Matrix A･B computing...");
        long startSequential = System.nanoTime();
        double[][] res2 = sequentialMultiplyMatrix(matrix_1, matrix_2); // result from sequential multiplier
        long finishSequential = System.nanoTime();
        System.out.println(
            "Sequential Matrix A･B - Time: " + ((finishSequential - startSequential - sleepTime) / millisecond)
                + " ms. ");

        // Confirming that their result do match
        for (int i=0; i<matrix_1.length; i++) {
            for (int j=0; j<matrix_2[0].length; j++) {
                if (res1[i][j] != res2[i][j]) {
                    System.out.println("Doesn't match");
                    break;
                }
            }
            break;
        }
    }

    /**
     * Returns the result of a sequential matrix multiplication
     * Referenced: https://classes.engineering.wustl.edu/cse231/core/index.php/MatrixMultiply
     * @param a is the first matrix
     * @param b is the second matrix
     * @return the result of the multiplication
     */
    public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
        double[] elementsInRowA = new double[MATRIX_SIZE];
        double[] elementsInColumnB = new double[MATRIX_SIZE];
        double[][] productMatrix = new double[a.length][b[0].length];

        int columnOfB = 0;
        double sum = 0;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    productMatrix[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return productMatrix;
    }

    /**
     * Returns the result of a parallel matrix multiplication
     *
     * @param a is the first matrix
     * @param b is the second matrix
     * @return the result of the multiplication
     */
    public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
        double[][] productMatrix = new double[a.length][b[0].length];
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int index = 0; index < NUMBER_THREADS; index++) {
            executor.execute(new singleElementComputation(a, b, productMatrix, index));
            //System.out.println(index);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            //Wait until executor is finished
        }

        return productMatrix;
    }

    /**
     * Populates a matrix of given size with randomly generated integers between 0-10.
     *
     * @param numRows number of rows
     * @param numCols number of cols
     * @return matrix
     */
    private static double[][] generateRandomMatrix(int numRows, int numCols) {
        double matrix[][] = new double[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }

    // Determines one element of the product matrix at position x,y
    public static class singleElementComputation implements Runnable {

        double[][] matrixA;
        double[][] matrixB;
        double[][] matrixC;
        double sum;
        int threadInUse = 0;
        int thread;
        int divisionBlock = MATRIX_SIZE / NUMBER_THREADS;

        public singleElementComputation(double[][] matrixA, double[][] matrixB, double[][] matrixC,
            int thread) {
            this.matrixA = matrixA;
            this.matrixB = matrixB;
            this.matrixC = matrixC;
            this.thread = thread;
        }

        @Override public void run() {
            if (divisionBlock == 0)
                divisionBlock = 1;
            for (int index = thread * divisionBlock;
                 index < (thread + 1) * divisionBlock; index++) {
                if (index >= MATRIX_SIZE)
                    break;
                int row = index;
                for (int columnIndex = 0; columnIndex < matrixC[0].length; columnIndex++) {
                    sum = 0;
                    for (int dualIndex = 0; dualIndex < matrixC.length; dualIndex++) {
                        sum += matrixA[row][dualIndex] * matrixB[dualIndex][columnIndex];
                    }
                    matrixC[row][columnIndex] = sum;
                }
            }
            // If threads and Matrix size aren't divisible then we can make threads take on additional
            // tasks here.
            if (MATRIX_SIZE > NUMBER_THREADS && MATRIX_SIZE % NUMBER_THREADS != 0
                && MATRIX_SIZE != divisionBlock) {
                for (int index = thread + NUMBER_THREADS * divisionBlock;
                     index < (thread + NUMBER_THREADS * divisionBlock + divisionBlock); index++) {
                    if (index >= MATRIX_SIZE)
                        break;
                    int row = index;
                    for (int columnIndex = 0; columnIndex < MATRIX_SIZE; columnIndex++) {
                        sum = 0;
                        for (int dualIndex = 0; dualIndex < MATRIX_SIZE; dualIndex++) {
                            sum += matrixA[row][dualIndex] * matrixB[dualIndex][columnIndex];
                        }
                        matrixC[row][columnIndex] = sum;
                    }
                }
            }
        }
    }

    /**
     * Printing out the matrix
     *
     * @param matrix
     */
    private static void printMatrix(double[][] matrix) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + "\t\t");
            }
            System.out.println("\n");
        }
    }
}

