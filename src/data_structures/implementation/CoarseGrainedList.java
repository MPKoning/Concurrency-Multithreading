package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data_structures.Sorted;

public class CoarseGrainedList<T extends Comparable<T>> implements Sorted<T> {

    private Lock lock = new ReentrantLock();
    Node<T> head;
    int counter;

    public CoarseGrainedList(){
        counter = 0;
    }

    public void add(T t) {
        if(counter == 0){
            head = new Node<T>(t);
            return;
        }
        Node<T> pred, current;
        lock.lock();
        try{
            pred = head;
            current = pred.next;
            while(t.compareTo(current.content) > 0){
                pred = current;
                current = current.next;
            }
            if(t.compareTo(current.content) == 0){
                return;
            }else{
                Node<T> node = new Node<T>(t);
                node.next = current;
                pred.next = node;
                return;
            }

        }catch(UnsupportedOperationException e){
            throw new UnsupportedOperationException();
        }finally{
            counter += 1;
            lock.unlock();
        }
    }

    public void remove(T t) {
        Node<T> pred, current;
        lock.lock();
        if(counter == 0){
            System.out.printf("Element not in list\n");
            return;
        }
        try{
            pred = head;
            current = pred.next;
            while(t.compareTo(current.content) > 0){
                pred = current;
                current = current.next;
            }
            if(t.compareTo(current.content) == 0){
                pred.next = current.next;
                counter -= 1;
                return;
            }else{
                System.out.printf("node not in list\n");
                return;
            }
        }catch(UnsupportedOperationException e){
            throw new UnsupportedOperationException();
        }finally{
            lock.unlock();
        }
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> list = new ArrayList<T>();
        Node<T> current;
        //lock.lock();
        System.out.println("test3");
        try{
            System.out.println("test1");
            current = head;
            while(current.next != null){
                list.add(current.content);
                current = current.next;
                System.out.println("test ");
            }
            list.add(current.content);

        }catch(UnsupportedOperationException e){
            throw new UnsupportedOperationException();
        }finally{
            lock.unlock();
        }
        return list;
    }

    private class Node<T extends Comparable<T>>{
        public T content;
        public Node<T> next;
        Node(T t){
            content = t;
        }
    }
}
