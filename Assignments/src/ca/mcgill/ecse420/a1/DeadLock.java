package ca.mcgill.ecse420.a1;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLock {

    public static void main(String[] args) throws InterruptedException {

        /**
         * Run this test a few times. Occasionally, only one thread will enter deadlock while waiting for the other.
         * Other times both threads will enter deadlock while waiting for each other.
         */
        for(int trial = 0; trial < 1; trial++){
            System.out.println("Trial " + (trial+1) + " executing: **********");
            sharedVariable sharedVariable = new sharedVariable();
            Thread thread1 = new Thread(sharedVariable);
            Thread thread2 = new Thread(sharedVariable);
            thread1.start();
            thread2.start();
            try{
                thread1.join();
                thread2.join();
            }catch(InterruptedException e){
                System.out.println("Interrupt error");
            }
        }
    }

    static class sharedVariable implements Runnable{
        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();

        @Override
        public void run(){
            while(true) {
                if (lock1.tryLock()) {
                    System.out.println(Thread.currentThread().getId() + " acquired lock 1.");
                    lock2.lock();
                    // To prevent deadlock, move the code referenced a few lines down.
                    System.out.println(Thread.currentThread().getId() + " acquired lock 2.");
                    lock2.unlock();
                    System.out.println(Thread.currentThread().getId() + " released lock 2.");
                    // The below piece of code can be moved into the above position.
                    lock1.unlock();
                    System.out.println(Thread.currentThread().getId() + " released lock 1.");

                } else if (lock2.tryLock()) {
                    System.out.println(Thread.currentThread().getId() + " acquired lock 2.");
                    lock1.lock();
                    // To prevent deadlock, move the code referenced a few lines down.
                    System.out.println(Thread.currentThread().getId() + " acquired lock 1.");
                    lock1.unlock();
                    System.out.println(Thread.currentThread().getId() + " released lock 1.");
                    // The below piece of code can be moved into the above position.
                    lock2.unlock();
                    System.out.println(Thread.currentThread().getId() + " released lock 2.");

                }
            }
        }

    }
}

