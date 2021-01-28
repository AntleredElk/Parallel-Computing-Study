package chapter30;

//import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;

public class TaskThreadDemo {
  public static void main(String[] args) {
    // Create tasks
    Runnable printA = new PrintChar('a', 1000);
    Runnable printB = new PrintChar('b', 1000);
    Runnable print100 = new PrintNum(100);

    System.out.println("printA instance of Runnable: " + (printA instanceof Runnable));
    
    // Create threads
    Thread thread1 = new Thread(printA);
    Thread thread2 = new Thread(printB);
    Thread thread3 = new Thread(print100);
    
//    thread1.setPriority(Thread.MAX_PRIORITY);

    
    System.out.println("main thread priority: " +Thread.currentThread().getPriority());
    System.out.println("thread1 priority: " + thread1.getPriority());
    System.out.println("thread2 priority: " + thread2.getPriority());
    System.out.println("thread3 priority: " + thread3.getPriority());


    // Start threads
    thread1.start();
    thread2.start();
    thread3.start();
    
  
    
  }
}

// The task for printing a specified character a specified number of times
class PrintChar implements Runnable {
  private char charToPrint; // The character to print
  private int times; // The times to repeat

  /** Construct a task with specified character and number of
   *  times to print the character
   */
  public PrintChar(char c, int t) {
    charToPrint = c;
    times = t;
  }

  /** Override the run() method to tell the system
   *  what the task is to perform
   */
  public void run() {
    for (int i = 0; i < times; i++) {
//      System.out.print(charToPrint);
      System.out.print(charToPrint + "\n");
    }
  }
}

// The task class for printing number from 1 to n for a given n
class PrintNum implements Runnable {
  private int lastNum;

  /** Construct a task for printing 1, 2, ... i */
  public PrintNum(int n) {
    lastNum = n;
  }

  /** Tell the thread how to run */               
  public void run() {
    
      for (int i = 1; i <= lastNum; i++) {
//        System.out.print(" " + i);
        System.out.print(i + "\n");
      }
  }        

}
   
   

