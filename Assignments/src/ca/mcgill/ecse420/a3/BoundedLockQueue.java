package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Assignment 3 Solution
 * - 3.1. Design a bounded lock-based queue implementation using an array instead of a linked list.
 *   Allow parallelism by using two separate locks for head and tail.
 * - Solution adapted from the code on textbook page 231.
 */


public class BoundedLockQueue<T> {
    // ReentrantLock enqLock, deqLock;
    // Condition notEmptyCondition, notFullCondition;
    // AtomicInteger size;
    // volatile Node head, tail;

    private T[] array;

    ReentrantLock enqLock = new ReentrantLock();
    ReentrantLock deqLock = new ReentrantLock();

    Condition notFullCondition = enqLock.newCondition();
    Condition notEmptyCondition = deqLock.newCondition();

    int head = 0;
    int tail = 0;


    public BoundedLockQueue(int size) {
        array = (T[]) new Object[size];
    }

    public void enqueue(T item) {
        enqLock.lock();
        try {
            while ( (tail - head) == array.length) {
                try {
                    notFullCondition.await();
                } catch (InterruptedException ie) { }
            }
            array[tail % array.length] = item;
            tail++;

            if (tail - head == 1) {
                notEmptyCondition.signalAll();
            }
        } finally {
            enqLock.unlock();
        }
    }

    public T dequeue() {
        deqLock.lock();
        try {
            while ( (tail - head) == 0) {
                try {
                    notEmptyCondition.await();
                } catch (InterruptedException ie) { }
            }

            T result = array[head % array.length];
            head++;

            if ( (tail - head) == (array.length - 1) ) {
                notFullCondition.signalAll();
            }
            return result;
        } finally {
            deqLock.unlock();
        }

    }


}
