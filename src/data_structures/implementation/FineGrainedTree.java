package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import data_structures.Sorted;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {

    private Node<T> root = null;
    private Node<T> current = null;
    private Node<T> parent = null;
    private Lock headLock = new ReentrantLock();

    public void add(T t) {
        headLock.lock();
        if(root == null) {
            root = new Node<T>(t);
            headLock.unlock();
        } else {
            headLock.unlock();
            Node<T> dummy = new Node<T>(null);
            dummy.lock();
            root = addNode(root, dummy, t);
        }
    }

    private Node<T> addNode(Node<T> node, Node<T> parent, T t) {
        if(node == null) {
            node = new Node<T>(t);
            parent.unlock();
        } else {
            node.lock();
            parent.unlock();
            if(t.compareTo(node.t) <= 0) {
                node.left = addNode(node.left, node, t);
            } else {
                node.right = addNode(node.right, node, t);
            }
        }
        return node;
    }

    public void remove(T t) {
        headLock.lock();
        if(root == null) {
            headLock.unlock();
        } else {
            headLock.unlock();
            Node<T> dummy = new Node<T>(null);
            dummy.lock();
            root = removeNode(root, dummy, t);
        }
    }

    private Node<T> removeNode(Node<T> node, Node<T> parent, T t) {
        if(node == null) {
            return node;
        }
        node.lock();
        if(t.compareTo(node.t) < 0) {
            parent.unlock();
            node.left = removeNode(node.left, node, t);
        } else if(t.compareTo(node.t) > 0) {
            parent.unlock();
            node.right = removeNode(node.right, node, t);
        } else { //remove the node
            if(node.left == null) { //if only one child/no childs
                parent.unlock();
                node.unlock();
                return node.right;
            } else if (node.right == null) {
                parent.unlock();
                node.unlock();
                return node.left;
            }
            parent.unlock();
            node.t = minValue(node.right);//two children, get inorder successor
            node.right = removeNode(node.right, node, node.t);//delete inorder successor
        }
        
        return node;
    }

    private T minValue(Node<T> node) {
        T min = node.t;
        Node<T> next = null;
        node.lock();
        while(node.left != null) {
            min = node.left.t;
            next = node.left;
            next.lock();
            node.unlock();
            node = next;
        }
        node.unlock();
        return min;
    }


    public ArrayList<T> toArrayList() {
        headLock.lock();
        ArrayList<T> result = new ArrayList<T>();
        try {
            addToArray(root, result);
        } finally { 
            headLock.unlock();
        }

        return result;
    }

    private void addToArray(Node<T> root, ArrayList<T> result) {
        if(root == null) {
            return;
        } else {
            root.lock();
            addToArray(root.left, result);
            result.add(root.t);
            addToArray(root.right, result);
            root.unlock();
        }
    }
}

class Node<T> {
    T t;
    Node<T> left;
    Node<T> right;
    ReentrantLock nodelock;
    public Node(T t) {
        this.t = t;
        left = null;
        right = null;
        nodelock = new ReentrantLock();
    }

    void lock() {
        nodelock.lock();
    }

    void unlock() {
        nodelock.unlock();
    }
}
