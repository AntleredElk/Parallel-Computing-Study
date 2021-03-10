package ca.mcgill.ecse420.a2;

import ca.mcgill.ecse420.a1.MatrixMultiplication;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilterLockImplementation {

    final static int numberOfThreads = 5;
    static FilterLock lock = new FilterLock(numberOfThreads);
    static int[] levelsTracker = new int[numberOfThreads];
    static int[] victimsTracker = new int[numberOfThreads];

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int index = 0; index < numberOfThreads; index++) {

            executor.execute(new someTask());
        }
        executor.shutdown();
        while(!executor.isTerminated()){
            //Wait until executor is finished
        }
    }
    static class FilterLock implements Lock {
        int[] levels;
        int[] victims;

        // Constructor for Filter
        public FilterLock(int numberOfThreads) {
            levels = new int[numberOfThreads];
            victims = new int[numberOfThreads];

            for (int i = 1; i < numberOfThreads; i++){
                levels[i] = 0;
            }
        }

        @Override public void lock(){
            int thread = (int)Thread.currentThread().getId()%numberOfThreads;

            for (int level = 1; level < numberOfThreads ; level++){
                levels[thread] = level; // Thread declares interest some level
                victims[level] = thread; //Threads declares itself the victim
                System.out.println("Desired level by Threads: " + Arrays.toString(levels));
                for (int i = 1; i < numberOfThreads; i++){
                    while(thread != i && levels[i] >= levels[thread] && victims[level] == thread){
                        // Wait here
                        try {
                            // Sleep to allow other threads CPU resources to run
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

        @Override public void unlock(){
            int thread = (int)Thread.currentThread().getId()%numberOfThreads;
            levels[thread] = 0;
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
            lock.lock();
            System.out.println("*** Thread " + Thread.currentThread().getId()%numberOfThreads + " has lock and is busy...");
            lock.unlock();
        }
    }
}
