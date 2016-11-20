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
            root = addNode(root, t);
       } finally {
            lock.unlock();
        }
    }

    private Node<T> addNode(Node<T> node, T t) {
        if(node == null) {
            node = new Node<T>(t);
        } else {
            if(t.compareTo(node.t) <= 0) {
                node.left = addNode(node.left, t);
            } else {
                node.right = addNode(node.right, t);
            }
        }
        return node;
    }

    public void remove(T t) {
        lock.lock();
        try {
            root = removeNode(root, t);
        } finally {
            lock.unlock();
        }
    }

    private Node<T> removeNode(Node<T> node,T t) {
        if(node == null) return node;
        else if(t.compareTo(node.t) < 0) {
            node.left = removeNode(node.left, t);
        } else if(t.compareTo(node.t) > 0) {
            node.right = removeNode(node.right, t);
        } else { //remove the node
            if(node.left == null) { //if only one child/no childs
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }

            node.t = minValue(node.right);//two children, get inorder successor
            node.right = removeNode(node.right, node.t);//delete inorder successor
        }
    
        return node;
    }

    private T minValue(Node<T> node) {
        T min = node.t;
        while(node.left != null) {
            min = node.left.t;
            node = node.left;
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
