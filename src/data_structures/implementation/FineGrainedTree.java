package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import data_structures.Sorted;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {

    private TreeNode<T> root = null;
    private TreeNode<T> current = null;
    private TreeNode<T> parent = null;
    private Lock headLock = new ReentrantLock();

    public void add(T t) {
        headLock.lock();
        if(root == null) {
            root = new TreeNode<T>(t);
            headLock.unlock();
        } else {
            root = addNode(root, null, t);
        }
    }

    private TreeNode<T> addNode(TreeNode<T> node, TreeNode<T> parent, T t) {
        if(parent == null) {
            headLock.unlock();
            parent = new TreeNode<T>(null);
            parent.lock();

        }
        if(node == null) {
            node = new TreeNode<T>(t);
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
            root = removeNode(root, null, t);
        }
    }

    private TreeNode<T> removeNode(TreeNode<T> node, TreeNode<T> parent, T t) {
        if(node == null) {
            return node;
        }
        node.lock();
        if(parent == null) {
            headLock.unlock(); 
            parent = new TreeNode<T>(null);
            parent.lock();
        } 

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
            
            node.t = minValue(node.right);//two children, get inorder successor
            node.right = removeNode(node.right, node, node.t);//delete inorder successor
            parent.unlock();
        }
        
        return node;
    }

    private T minValue(TreeNode<T> node) {
        T min = node.t;
        TreeNode<T> next = new TreeNode<T>(null);

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

    private void addToArray(TreeNode<T> root, ArrayList<T> result) {
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

class TreeNode<T> {
    T t;
    TreeNode<T> left;
    TreeNode<T> right;
    ReentrantLock nodelock;
    public TreeNode(T t) {
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

