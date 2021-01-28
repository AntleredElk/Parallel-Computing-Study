package chapter30;

import java.util.concurrent.*;


public class AccountWithSynchronizedMethod {

  private static Account account = new Account();

  public static void main(String[] args) {
    ExecutorService executor = Executors.newCachedThreadPool();

    // Create and launch 100 threads
    for (int i = 0; i < 100; i++) {
      executor.execute(new AddAPennyTask(i));
    }

    executor.shutdown();

    // Wait until all tasks are finished
    while (!executor.isTerminated()) {
    }

    System.out.println("What is balance? " + account.getBalance());
  }

  // A thread for adding a penny to the account
  private static class AddAPennyTask implements Runnable {
    int i;
    AddAPennyTask(int i){
      this.i = i;
    }
 
    public void run() {
//      System.out.println("Thread " + i + "running.  Account Balance = " + account.getBalance());
      account.deposit(1);
    }
  }

  // An inner class for account
  private static class Account {
    private int balance = 0;

    public int getBalance() {
      return balance;
    }
    
//    public void deposit(int amount) {
    public synchronized void deposit(int amount) {
      
      int newBalance = balance + amount;
      
      // This delay is deliberately added to magnify the
      // data-corruption problem and make it easy to see.
      try {
        Thread.sleep(0);
      }
      catch (InterruptedException ex) {
      }
      
      

      balance = newBalance; 
      

    }
  }
}
