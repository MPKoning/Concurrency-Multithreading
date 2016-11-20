package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import data_structures.Sorted;

public class CoarseGrainedTree<T extends Comparable<T>> implements Sorted<T> {

    private Node<T> root = null;
    private Node<T> current = null;
    private Lock lock = new ReentrantLock();

    public void add(T t) {
        lock.lock();
        try {
            if(root == null) {
                root = new Node<T>(t);
            } else {
                current = root;
                while(current.t != null) {
                    if(current.t.compareTo(t) <= 0) {
                        if(current.left != null) {
                            current = current.left;
                        } else {
                            current.left = new Node<T>(t);
                        }
                    } else {
                        if(current.right != null) {
                            current = current.right;
                        } else {
                            current.right = new Node<T>(t);
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void remove(T t) {
        lock.lock();
        try {
            root = removeNode(root, t);
        } finally {
            lock.unlock();
        }
    }

    private Node<T> removeNode(Node<T> root,T t) {

        if(root == null) return root;
        else if(t.compareTo(root.t) < 0) {
            root.left = removeNode(root.left, t);
        } else if(t.compareTo(root.t) > 0) {
            root.right = removeNode(root.right, t);
        } else { //remove the node
            if(root.left == null) { //if only one child/no childs
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            root.t = minValue(root.right);//two children, get inorder successor
            root.right = removeNode(root.right, root.t);//delete inorder successor
        }
    
        return root;
    }

    private T minValue(Node<T> root) {
        T min = root.t;
        while(root.left != null) {
            min = root.left.t;
            root = root.left;
        }
        return min;
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> result = new ArrayList<T>();
        lock.lock();
        try {
            addToArray(root, result);
        } finally {
            lock.unlock();
        }

        return result;
    }

    private void addToArray(Node<T> root, ArrayList<T> result) {
        if(root == null) {
            return;
        } else {
            addToArray(root.left, result);
            result.add(root.t);
            addToArray(root.right, result);
        }
    }
}

class Node<T> {
    T t;
    Node<T> left;
    Node<T> right;
    public Node(T t) {
        this.t = t;
        left = null;
        right = null;
    }
}
