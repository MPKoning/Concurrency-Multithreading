package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data_structures.Sorted;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {

    private Lock lock = new ReentrantLock();
    Node<T> head = null;


    public void add(T t) {
        Node<T> newNode = new Node<T>(t);
        if(head == null){
            head = newNode;
            return;
        }
        if(head.next == null){
            if(t.compareTo(head.content) > 0){
                head.next = newNode;
            }else{
                newNode.next = head;
                head = newNode;
            }
            return;
        }
        Node<T> previous;
        head.lock();
        previous = head;
        try{
            Node<T> current = previous.next;
            current.lock();
            try{
                while(t.compareTo(current.content) > 0){
                    if(current.next == null){
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
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                current.unlock();
            }
        }finally{
            previous.unlock();
        }
    }

    public void remove(T t) {
        Node<T> previous = null, current = null;
        head.lock();
        try{
            previous = head;
            current = previous.next;
            current.lock();
            try{
                while(t.compareTo(current.content) > 0){
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

    public ArrayList<T> toArrayList() {
        ArrayList<T> list = new ArrayList<T>();
        Node<T> current;
        if(head != null){
            current = head;
            while(current.next != null){
                list.add(current.content);
                current = current.next;
            }
            list.add(current.content);
        }
    return list;
    }

    private class Node<T>{
        public T content;
        public Node<T> next;
        private Lock lock;

        Node(T t){
            content = t;
            lock = new ReentrantLock();
        }

        public void lock(){
            lock.lock();
        }

        public void unlock(){
            lock.unlock();
        }
    }
}
