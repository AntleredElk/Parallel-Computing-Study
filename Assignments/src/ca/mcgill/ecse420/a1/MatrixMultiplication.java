package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {

	private static final int NUMBER_THREADS = 1000;
	private static final int MATRIX_SIZE = 2000;

	public static void main(String[] args) {

		testMatrixMultiplication(MATRIX_SIZE, MATRIX_SIZE);
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
		ExecutorService executor = Executors.newCachedThreadPool();

		for (int index = 0; index < NUMBER_THREADS; index++) {

			executor.execute(new singleElementComputation(a, b, productMatrix, index));
			//System.out.println(index);
		}
		executor.shutdown();
		while(!executor.isTerminated()){
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
		int divisionBlock = MATRIX_SIZE/NUMBER_THREADS;

		public singleElementComputation(double[][] matrixA, double[][] matrixB, double[][] matrixC,
			int thread) {
			this.matrixA = matrixA;
			this.matrixB = matrixB;
			this.matrixC = matrixC;
			this.thread = thread;
		}

		@Override public void run() {
			if(divisionBlock == 0) divisionBlock = 1;
			for(int index = thread * divisionBlock; index < (thread+1)*divisionBlock; index++ ) {
				if(index >= MATRIX_SIZE) break;
				int row = index;
				for (int columnIndex = 0; columnIndex < MATRIX_SIZE; columnIndex++) {
					sum = 0;
					for (int dualIndex = 0; dualIndex < MATRIX_SIZE; dualIndex++) {
						sum += matrixA[row][dualIndex] * matrixB[dualIndex][columnIndex];
					}
					matrixC[row][columnIndex] = sum;
				}
			}
			//If threads and Matrix size aren't divisible
			if(MATRIX_SIZE > NUMBER_THREADS && MATRIX_SIZE%NUMBER_THREADS != 0 && MATRIX_SIZE != divisionBlock){
				for(int index = thread + NUMBER_THREADS * divisionBlock; index < (thread + NUMBER_THREADS * divisionBlock + divisionBlock); index++ ) {
					if(index >= MATRIX_SIZE) break;
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
	 * Use this to test sequential matrix multiplication
	 * @param rows
	 * @param columns
	 */
	private static void testMatrixMultiplication(int rows, int columns){
		double[][] matrix_1 = generateRandomMatrix(rows, columns);
		double[][] matrix_2 = generateRandomMatrix(rows, columns);
		printAllMatrices(matrix_1, matrix_2);
	}

	/**
	 * Use this to print the product of matrices A and B
	 *
	 * @param matrix_1 of size mxn
	 * @param matrix_2 of size mxp
	 */
	private static void printAllMatrices(double[][] matrix_1, double[][] matrix_2) {
		double millisecond = 1000000.0;
		double sleepTime = 1.0;
		//System.out.println("Matrix A: ----------");
		//printMatrix(matrix_1);
		//System.out.println("Matrix B: ----------");
		//printMatrix(matrix_2);
		//if(type == true){
		System.out.println("Parallel Matrix AB: ----------");
		long startParallel = System.nanoTime();
		parallelMultiplyMatrix(matrix_1, matrix_2);
		//printMatrix(parallelMultiplyMatrix(matrix_1, matrix_2));
		long finishParallel = System.nanoTime();
		System.out.println("Elapsed time: " + ((finishParallel - startParallel - sleepTime) / millisecond));
		//}else {

		System.out.println("Sequential Matrix AB: ----------");
		long startSequential = System.nanoTime();
		sequentialMultiplyMatrix(matrix_1, matrix_2);
		//printMatrix(sequentialMultiplyMatrix(matrix_1, matrix_2));
		long finishSequential = System.nanoTime();
		System.out.println("Elapsed time: " + ((finishSequential - startSequential - sleepTime) / millisecond));
		//}
	}

	private static void printMatrix(double[][] matrix) {
		try
		{
			Thread.sleep(1);
		}
		catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = 0; j < matrix[1].length; j++) {
				System.out.print(matrix[i][j] + "\t\t");
			}
			System.out.println("\n");
		}
	}
}
