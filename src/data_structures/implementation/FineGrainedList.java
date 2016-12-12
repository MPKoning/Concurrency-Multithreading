package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data_structures.Sorted;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {

    private Lock lock = new ReentrantLock();
    //We use a dummy head
    T dummy = null;
    Node<T> head = new Node<T>(dummy);

    public void add(T t) { //Adds node to list
        Node<T> newNode = new Node<T>(t);
        head.lock();
        if(head.next == null){ //check if list is empty
            head.next = newNode;
            head.unlock();
            return;
        }
        Node<T> previous;
        previous = head;
        try{
            Node<T> current = previous.next;
            current.lock();
            try{
                while(t.compareTo(current.content) > 0){ //loop through list until values are bigger than data of new node
                    if(current.next == null){ //special case if node needs to be added to end of list
                        break;
                    }else{
                        previous.unlock();
                        previous = current;
                        current = current.next;
                    }
                    current.lock();
                }
                newNode.next = current;
                previous.next = newNode;
            }finally{
                current.unlock();
            }
        }finally{
            previous.unlock();
        }
    }

    public void remove(T t) { //remove node from list
        Node<T> previous = null, current = null;
        head.lock();
        try{
            previous = head;
            current = previous.next;
            current.lock();
            try{
                while(t.compareTo(current.content) != 0){
                    previous.unlock();
                    previous = current;
                    current = current.next;
                    current.lock();
                }
                if(t.compareTo(current.content) == 0){
                    previous.next = current.next;
                }
            }finally{
                current.unlock();
            }
        }finally{
            previous.unlock();
        }
    }

    public ArrayList<T> toArrayList() { // returns list
        ArrayList<T> list = new ArrayList<T>();
        if(head.next == null){ //list is empty
            return list;
        }
        Node<T> current = head.next;
        while(current.next != null){
                list.add(current.content);
                current = current.next;
        }
        list.add(current.content);
        return list;
    }

    private class Node<T>{ //subclass node to keep data
        public T content;
        public Node<T> next;
        private Lock lock;

        Node(T t){
            content = t;
            lock = new ReentrantLock();
        }
        // lock and unlock methods
        public void lock(){
            lock.lock();
        }

        public void unlock(){
            lock.unlock();
        }
    }
}
