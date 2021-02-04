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
        boolean[] resourcesInUse = {false, false};
        Lock lock = new ReentrantLock();

        synchronized void getResource(){
            if(!resourcesInUse[0]){
                resourcesInUse[0] = true;
                System.out.println(Thread.currentThread().getId() + " secured Resource 0");
            }
            else if(!resourcesInUse[1]){
                resourcesInUse[1] = true;
                System.out.println(Thread.currentThread().getId() + " secured Resource 1");
            }
        }
        synchronized void getNextResource(){
            if (resourcesInUse[0] && !resourcesInUse[1]) {
                resourcesInUse[1] = true;
                resourcesInUse[0] = false;
                System.out.println(Thread.currentThread().getId() + " secured 1 and released 0");
            } else if (resourcesInUse[1] && !resourcesInUse[0]) {
                resourcesInUse[0] = true;
                resourcesInUse[1] = false;
                System.out.println(Thread.currentThread().getId() + " secured 0 and released 1");
            }
            else System.out.println(Thread.currentThread().getId() + " currently in Deadlock");

        }

        @Override
        public void run(){
            getResource();
            getNextResource();
            getResource();
            getNextResource();
        }

    }
}

