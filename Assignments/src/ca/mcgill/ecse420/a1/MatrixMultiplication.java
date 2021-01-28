package ca.mcgill.ecse420.a1;

import java.awt.image.ImagingOpException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixMultiplication {

	private static final int NUMBER_THREADS = 3;
	private static final int MATRIX_SIZE = 10;

	public static void main(String[] args) {

		/**
		 * **IMPORTANT** Adjust @param type to TRUE for parallel execution or FALSE for sequential
		 */
		testMatrixMultiplication(true, MATRIX_SIZE, MATRIX_SIZE);

	}

	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 *
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
		double[] elementsInRowA = new double[MATRIX_SIZE];
		double[] elementsInColumnB = new double[MATRIX_SIZE];
		double[][] productMatrix = new double[MATRIX_SIZE][MATRIX_SIZE];

		int columnOfB = 0;
		double sum = 0;

		for (int rowOfA = 0; rowOfA < MATRIX_SIZE; rowOfA++) {

			for (int columnOfA = 0; columnOfA < MATRIX_SIZE; columnOfA++) {
				elementsInRowA[columnOfA] = a[rowOfA][columnOfA];
			}

			for (int rowOfB = 0; rowOfB < MATRIX_SIZE; rowOfB++) {
				elementsInColumnB[rowOfB] = b[rowOfB][columnOfB];
			}

			for (int index = 0; index < MATRIX_SIZE; index++) {
				sum += elementsInRowA[index] * elementsInColumnB[index];
			}
			productMatrix[rowOfA][columnOfB] = sum;
			sum = 0;
			columnOfB++;

			if (columnOfB < MATRIX_SIZE) {
				rowOfA--;
			} else
				columnOfB = 0;
		}
		return productMatrix;
	}

	/**
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 *
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {

		double[][] productMatrix = new double[MATRIX_SIZE][MATRIX_SIZE];

		for (int index = 0; index < NUMBER_THREADS; index++) {
			singleElementComputation test = new singleElementComputation(a, b, productMatrix, index);
			Thread thread = new Thread(test);
			thread.start();
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
		int inUse = 0;
		int row;

		public singleElementComputation(double[][] matrixA, double[][] matrixB, double[][] matrixC,
			int row) {
			this.matrixA = matrixA;
			this.matrixB = matrixB;
			this.matrixC = matrixC;
			this.row = row;
		}

		//TODO look at inUSe method
		@Override public void run() {
			while (row != MATRIX_SIZE) {
				inUse++;
				for (int columnIndex = 0; columnIndex < MATRIX_SIZE; columnIndex++) {
					sum = 0;
					for (int dualIndex = 0; dualIndex < MATRIX_SIZE; dualIndex++) {
						sum += matrixA[row][dualIndex] * matrixB[dualIndex][columnIndex];
					}
					matrixC[row][columnIndex] = sum;
				}
				inUse--;
				while (inUse != 0) {
				}
				row++;
			}
		}
	}
	/**
	 * Use this to test sequential matrix multiplication
	 * @param rows
	 * @param columns
	 */
	private static void testMatrixMultiplication(boolean type, int rows, int columns){
		double[][] matrix_1 = generateRandomMatrix(rows, columns);
		double[][] matrix_2 = generateRandomMatrix(rows, columns);
		printAllMatrices(type, matrix_1, matrix_2);
	}

	/**
	 * Use this to print the product of matrices A and B
	 *
	 * @param matrix_1 of size mxn
	 * @param matrix_2 of size mxp
	 */
	private static void printAllMatrices(boolean type, double[][] matrix_1, double[][] matrix_2) {
		double millisecond = 1000000.0;
		System.out.println("Matrix A: ----------");
		//printMatrix(matrix_1);
		System.out.println("Matrix B: ----------");
		//printMatrix(matrix_2);
		//if(type == true){
		System.out.println("Parallel Matrix AB: ----------");
		long startParallel = System.nanoTime();
		parallelMultiplyMatrix(matrix_1, matrix_2);
		printMatrix(parallelMultiplyMatrix(matrix_1, matrix_2));
		long finishParallel = System.nanoTime();
		System.out.println("Elapsed time: " + ((finishParallel - startParallel) / millisecond));
		//}else {
		System.out.println("Sequential Matrix AB: ----------");
		long startSequential = System.nanoTime();
		sequentialMultiplyMatrix(matrix_1, matrix_2);
		printMatrix(sequentialMultiplyMatrix(matrix_1, matrix_2));
		long finishSequential = System.nanoTime();
		System.out.println("Elapsed time: " + ((finishSequential - startSequential) / millisecond));
		//}
	}

	private static void printMatrix(double[][] matrix) {
		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = 0; j < matrix[1].length; j++) {
				System.out.print(matrix[i][j] + "\t\t");
			}
			System.out.println("\n");
		}
	}
}
