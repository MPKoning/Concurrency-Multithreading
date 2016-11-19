package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data_structures.Sorted;

public class CoarseGrainedList<T extends Comparable<T>> implements Sorted<T> {

    private Lock lock = new ReentrantLock();
    Node<T> head = null;

    public void add(T t) {
        Node<T> previous, current;
        lock.lock();
        try{
            if(head == null){
                head = new Node<T>(t);
                return;
            }
            if(t.compareTo(head.content) < 0){
                Node<T> node = new Node<T>(t);
                node.next = head;
                head = node;
                return;

            }
            current = head;
            previous = null;

            while(current != null){
                if(t.compareTo(current.content) > 0){
                    if(current.next==null){
                        current.next = new Node<T>(t);
                        return;
                    }
                    previous = current;
                    current = current.next;
                }else{
                    Node<T> node = new Node<T>(t);
                    node.next = previous.next;
                    previous.next = node;
                    return;
                }
            }

        }catch(UnsupportedOperationException e){
            throw new UnsupportedOperationException();
        }finally{
            lock.unlock();
        }
    }

    public void remove(T t) {
        Node<T> previous, current;
        lock.lock();
        try{
            previous = null;
            current = head;
            while(t.compareTo(current.content) > 0){
                previous = current;
                current = current.next;
            }
            if(t.compareTo(current.content) == 0){
                if(previous == null){
                    head = current.next;
                }else{
                    previous.next = current.next;
                    return;
                }
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
        lock.lock();
        try{
            if(head != null){
                current = head;
                while(current.next != null){
                    list.add(current.content);
                    current = current.next;
                }
                list.add(current.content);
            }

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
