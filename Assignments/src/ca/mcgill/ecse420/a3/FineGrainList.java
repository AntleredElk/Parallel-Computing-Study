package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainList<T>{

    public static void main(String[] args) {
        FineGrainList linkedList = new FineGrainList();
        int a = 5;
        int b = 13;
        int x = 25;
        int y = 65;
        int z = 42;
        addElementsToList(linkedList, a, b, x, y, z);

        Thread thread1 = new Thread(new someTask(linkedList,z));
        Thread thread2 = new Thread(new someTask(linkedList,z));
        Thread thread3 = new Thread(new someTask(linkedList,x));
        Thread thread4 = new Thread(new someTask(linkedList,b));
        Thread thread5 = new Thread(new someTask(linkedList,a));

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        try{
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();

        }catch(InterruptedException e){
            System.out.println("Interrupt error");
        }
    }


    private Node head;

    public static void addElementsToList( FineGrainList linkedList, int a, int b, int x, int y, int z){
        linkedList.add(a);
        linkedList.add(b);
        linkedList.add(x);
        linkedList.add(y);
        linkedList.add(z);
    }

    // Linked List Constructor
    public FineGrainList() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    /**
     *
     * Use volatile keyword to store data into main memory. This ensures data integrity.
     * Fine-Grained Locking requires a locking for each node, so locks were added as a field.
     *
     * @param <T> Object of type T (Generic)
     */
    public class Node<T>{
        volatile T data;
        volatile int key;
        volatile Node next;
        volatile Lock lock;

        public Node(T data){
            this.data = data;
            this.key = data.hashCode();
            next = null;
            lock = new ReentrantLock();
        }
    }
    // Method to add element to a list
    public boolean add(T data) {
        int key = data.hashCode();
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if (curr.key == key) {
                    return false;
                }
                Node newNode = new Node(data);
                newNode.next = curr;
                pred.next = newNode;
                return true;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }
    // Method to remove element from a list
    public boolean remove(T data) {
        Node pred = null, curr = null;
        int key = data.hashCode();
        head.lock.lock();
        try {
            pred = head;
            curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if (curr.key == key) {
                    pred.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    /**
     *
     * The contains method is a repurposed remove method.
     * Remove needs to locate an element to remove it and this logic could be reused for detection.
     *
     * @param data: Checks for data to see if the data is in the list or not
     * @return true or false
     */
    public boolean contains(T data) {
        Node pred = null, curr = null;
        int key = data.hashCode();
        head.lock.lock();
        try {
            pred = head;
            curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if (curr.key == key) {
                    System.out.println("Element "+data+" is in List");
                    return true;
                }
                else{
                    System.out.println("Element "+data+" was not found");
                    return false;
                }
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }
    static class someTask<T> implements Runnable{

        FineGrainList linkedList;
        T data;

        public someTask(FineGrainList linkedList, T data){
            this.linkedList = linkedList;
            this.data = data;
        }
        @Override public void run() {
            linkedList.contains(data);
        }
    }
}
