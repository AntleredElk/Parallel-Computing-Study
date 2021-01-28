package chapter30;

import java.util.concurrent.*;

public class ParallelMax {
  private static final int NUMPROCESSORS = 1;  // Specify number of processors (threads) to use.
  
  public static void main(String[] args) {
    // Create a list
    final int N = 100000000;
    int[] list = new int[N];
    for (int i = 0; i < list.length; i++) {
//      list[i] = i;
      list[i] = (int)(Math.random() * N +1);
    }
   
    long startTime = System.currentTimeMillis();
    System.out.println("\nThe maximal number is " + max(list));  // Invoke max().
    long endTime = System.currentTimeMillis();
//    System.out.println("Number of processors is " + 
//      Runtime.getRuntime().availableProcessors()); 
//    System.out.println("Time with " + (endTime - startTime) 
//      + " milliseconds"); 
    System.out.println("Time with " 
 //       + Runtime.getRuntime().availableProcessors() +
        + NUMPROCESSORS +
        " processors: "+ (endTime - startTime) 
        + " milliseconds");
  }
  
  public static int max(int[] list) {
    RecursiveTask<Integer> task = new MaxTask(list, 0, list.length);  // Create a ForkJoinTask.
    ForkJoinPool pool = new ForkJoinPool(NUMPROCESSORS); // Create a ForkJoinPool.
    return pool.invoke(task);  // Execute a task.
  }
 
  private static class MaxTask extends RecursiveTask<Integer> {  // Define a concrete ForkJoinTask.
    private final static int THRESHOLD = 1000;
    private int[] list;
    private int low;
    private int high;

    public MaxTask(int[] list, int low, int high) {
      this.list = list;
      this.low = low;
      this.high = high;
    }

    @Override
    public Integer compute() {  // Perform the task.
      if (high - low < THRESHOLD) {  // Solve a small problem.
        int max = list[0];                  
        for (int i = low; i < high; i++)  
          if (list[i] > max)
            max = list[i];
        return new Integer(max);
      } 
      else {
        int mid = (low + high) / 2;  // Split into two parts.
        RecursiveTask<Integer> left = new MaxTask(list, low, mid);
        RecursiveTask<Integer> right = new MaxTask(list, mid, high);

        right.fork();  // Fork right.
        left.fork();   // Fork left. 
        return new Integer(Math.max(left.join().intValue(),   // Join tasks.
         right.join().intValue()));
        
//        invokeAll(left, right);
//        return new Integer(Math.max(left.join().intValue(),   // Join tasks.
//                     right.join().intValue()));
      }
    }
  }
}



