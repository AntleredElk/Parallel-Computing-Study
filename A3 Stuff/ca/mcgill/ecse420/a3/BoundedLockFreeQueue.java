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


public class BoundedLockFreeQueue<T> {
    // ReentrantLock enqLock, deqLock;
    // Condition notEmptyCondition, notFullCondition;
    AtomicInteger size;

    T[] array;

    ReentrantLock enqLock = new ReentrantLock();
    ReentrantLock deqLock = new ReentrantLock();

    Condition notFullCondition = enqLock.newCondition();
    Condition notEmptyCondition = deqLock.newCondition();

    AtomicInteger head = new AtomicInteger(0);
    AtomicInteger tail = new AtomicInteger(0);


    public BoundedLockFreeQueue(int size) {
        array = (T[]) new Object[size];
        this.size = new AtomicInteger(size);
    }

    public void enqueue(T item) {
        int sizex = size.get();
        while (sizex <= 0 || size.compareAndSet(sizex, sizex-1)) {
            sizex = size.get();
        }

        int enq = tail.getAndIncrement();
        array[enq % array.length] = item;



        enqLock.lock();
        try {
            while ( (tail.get() - head.get()) == array.length) {
                try {
                    notFullCondition.await();
                } catch (InterruptedException ie) { }
            }
            array[tail.get() % array.length] = item;
            tail.incrementAndGet();

            if (tail.get() - head.get() == 1) {
                notEmptyCondition.signalAll();
            }
        } finally {
            enqLock.unlock();
        }
    }

    public T dequeue() {
        deqLock.lock();
        try {
            while ( (tail.get() - head.get()) == 0) {
                try {
                    notEmptyCondition.await();
                } catch (InterruptedException ie) { }
            }

            T result = array[head.get() % array.length];
            head.incrementAndGet();

            if ( (tail.get() - head.get()) == (array.length - 1) ) {
                notFullCondition.signalAll();
            }
            return result;
        } finally {
            deqLock.unlock();
        }

    }
}
