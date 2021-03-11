package ca.mcgill.ecse420.a2;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BakeryLockImplementation {

    final static int numberOfThreads = 5;
    static BakeryLock lock = new BakeryLock(numberOfThreads);
    static int counter = 0;
    static int position = 0;
    static int[] counterArray = new int[numberOfThreads];

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int index = 0; index < numberOfThreads; index++) {

            executor.execute(new someTask());
        }
        executor.shutdown();
        while(!executor.isTerminated()){
            //Wait until executor is finished
        }
        System.out.println("Count order: "+Arrays.toString(counterArray));
    }

    static class BakeryLock implements Lock{
        boolean[] flags;
        int[] labels;
        int max = 0;

        public BakeryLock(int numberOfThreads){
            flags = new boolean[numberOfThreads];
            labels = new int[numberOfThreads];

            for (int i = 0; i < numberOfThreads; i++){
                flags[i] = false;
                labels[i] = 0;
            }
        }

        @Override public void lock() {
            int thread = (int)Thread.currentThread().getId()%numberOfThreads;


            for(int value: labels)
                max = Math.max(max, value);

            flags[thread] = true; // Indicates interest in lock
            labels[thread] = max + 1; // Takes the next "number" in wait system
            System.out.println("Seen by thread " + thread + ": " + Arrays.toString(labels));
            for (int i = 1; i < numberOfThreads; i++){{
                while(thread !=i && flags[i] && labels[thread] >= labels[i]){
                    // Wait here
                    if(labels[thread] == labels[i]){
                        if(thread < i) break;
                    }
                    try {
                        // Sleep to allow other threads CPU resources to run
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }}
        }

        @Override public void unlock() {
            int thread = (int)Thread.currentThread().getId()%numberOfThreads;
            flags[thread] = false;

        }

        /**
         * Implemented dummy methods for the sake of the Interface
         * No functionality for remaining methods
         */

        @Override public void lockInterruptibly() throws InterruptedException {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override public boolean tryLock() {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override public Condition newCondition() {
            throw new java.lang.UnsupportedOperationException();
        }

    }
    static class someTask implements Runnable{

        @Override public void run() {
            int thread = (int)Thread.currentThread().getId()%numberOfThreads;
            // Do not lock the lock to see what happens to counter order
            lock.lock();
            try {
                counter++;
                System.out.println("*** Thread " + thread + " has lock, Counter is " + counter + "...");
                counterArray[position] = counter;
                position++;
            }finally {
                lock.unlock();
            }
        }
    }
}
