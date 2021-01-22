package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = 1;
	private static final int MATRIX_SIZE = 2000;

        public static void main(String[] args) {
		
		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		//double[][] c = {{2,3,2}, {1,4,2},{3,4,1}};
		//double[][] d = {{5,1,1}, {1,3,4},{5,3,4}};

		//printAllMatrices(a,b);
		sequentialMultiplyMatrix(a, b);
		//parallelMultiplyMatrix(a, b);
	}
	
	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {

		double[] elementsInRowA = new double[MATRIX_SIZE];
		double[] elementsInColumnB = new double[MATRIX_SIZE];
		double elementInProductMatrix = 0;
		double[][] productMatrix =  new double[MATRIX_SIZE][MATRIX_SIZE];
		int columnOfB = 0;
		double sum = 0;

		for(int rowOfA = 0; rowOfA < MATRIX_SIZE; rowOfA++){

			for(int columnOfA = 0; columnOfA < MATRIX_SIZE; columnOfA++){
				elementsInRowA[columnOfA] = a[rowOfA][columnOfA];
			}

			for (int rowOfB = 0; rowOfB < MATRIX_SIZE; rowOfB++){
				elementsInColumnB[rowOfB] = b[rowOfB][columnOfB];
			}

			for(int index = 0; index < MATRIX_SIZE; index++){
				sum += elementsInRowA[index]*elementsInColumnB[index];
			}
			productMatrix[rowOfA][columnOfB] = sum;
			sum = 0;
			columnOfB++;

			if(columnOfB < MATRIX_SIZE){
				rowOfA--;
			}
			else columnOfB = 0;
		}
		return productMatrix;
	}
	
	/**
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
		return null;
		
	}
	/**
	 * Populates a matrix of given size with randomly generated integers between 0-10.
	 * @param numRows number of rows
	 * @param numCols number of cols
	 * @return matrix
	 */
	private static double[][] generateRandomMatrix (int numRows, int numCols) {
		double matrix[][] = new double[numRows][numCols];
		for (int row = 0 ; row < numRows ; row++ ) {
			for (int col = 0 ; col < numCols ; col++ ) {
				matrix[row][col] = (double) ((int) (Math.random() * 10.0));
			}
		}
		return matrix;
	}
	/**
	 * @author Edward & Rin
	 * Use this to print the product of matrices A and B
	 * @param matrix_1 of size mxn
	 * @param matrix_2 of size mxp
	 */
	private static void printAllMatrices(double[][] matrix_1, double[][] matrix_2 ){
		System.out.println("Matrix A: ----------");
		printMatrix(matrix_1);
		System.out.println("Matrix B: ----------");
		printMatrix(matrix_2);
		System.out.println("Matrix AB: ----------");
		printMatrix(sequentialMultiplyMatrix(matrix_1,matrix_2));
	}
	private static void printMatrix(double[][] matrix){
		for(int i = 0; i < matrix[0].length; i++){
			for (int j = 0; j < matrix[1].length; j++){
				System.out.print(matrix[i][j]+"\t\t");
			}
			System.out.println("\n");
		}
	}
}
