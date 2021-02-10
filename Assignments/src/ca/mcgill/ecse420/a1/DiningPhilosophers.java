package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

    public static void main(String[] args) {

        int numberOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Chopstick[] chopsticks = new Chopstick[numberOfPhilosophers];

        // Instantiate the 5 chopsticks
        for (int i = 0; i < 5; i++) {
            chopsticks[i] = new Chopstick(i);
        }

        // Instantiate the 5 philosophers
        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher(i);  // new philosopher
            philosophers[i].setChopstics(chopsticks);  // seeing the same chopsticks
            philosophers[i]
                .locateChopsticks();     // let them know which chopsticks are the closest
        }

        // Set their thinking and eating time
        philosophers[0].setThinking_time(1000);
        philosophers[0].setEating_time(2000);

        philosophers[1].setThinking_time(1100);
        philosophers[1].setEating_time(1900);

        philosophers[2].setThinking_time(1200);
        philosophers[2].setEating_time(1800);

        philosophers[3].setThinking_time(1300);
        philosophers[3].setEating_time(1700);

        philosophers[4].setThinking_time(1400);
        philosophers[4].setEating_time(1600);

        // Start the threads
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(philosophers[i]);
            threads[i].start();
        }
    }

    public static class Philosopher implements Runnable {
        int philosopher_id;
        int eating_time;
        int thinking_time;
        int left_chopstick;
        int right_chopstick;
        Chopstick[] chopsticks = new Chopstick[5];

        // Constructor
        Philosopher(int id) {
            this.philosopher_id = id;
        }

        //
        public void locateChopsticks() {
            this.left_chopstick = philosopher_id;
            if (philosopher_id == 0) {
                right_chopstick = 4;
            } else {
                right_chopstick = philosopher_id - 1;
            }
        }

        public void setEating_time(int time) {
            this.eating_time = time;
        }

        public void setThinking_time(int time) {
            this.thinking_time = time;
        }

        public void setChopstics(Chopstick[] set) {
            this.chopsticks = set;
        }

        public void think() {
            try {
                // Thinking
                Thread.sleep(thinking_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void hungry() {
            System.out.println(philosopher_id + " is hungry. ");
          
            // pick up with the left hand
            chopsticks[left_chopstick].picked_up(philosopher_id);

            // wait for a while
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // pick up with the right hand
            chopsticks[right_chopstick].picked_up(philosopher_id);

            /* The philosopher is eating */
            System.out.println(philosopher_id + " is eating.");
            
            try {
                Thread.sleep(eating_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /* The philosopher finished eating */
            chopsticks[left_chopstick].finished();
            chopsticks[right_chopstick].finished();
        }

        @Override public void run() {
            while (true) {
                think();
                hungry();
            }
        }


    }


    private static class Chopstick {
        // The location of the  chopstick
        private int chopstick_id;

        // Is this chopstick on someone's hand
        private boolean eating_now = false;

        Chopstick(int id) {
            this.chopstick_id = id;
        }

        public synchronized void picked_up(int id) {
            while (this.eating_now == true) {
                // Someone is using it, so this person can't pick it up
                try {
                    System.out.println(id + " could not pick up the chopstick " + chopstick_id);
                    wait();
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }

            this.eating_now = true;
            System.out.println(id + " picked up the chopstick " + chopstick_id);
        }

        public synchronized void finished() {
            // someone finished using this chopstick
            this.eating_now = false;

            // waiting pool thread out of lock
            notifyAll();
        }
    }

}
