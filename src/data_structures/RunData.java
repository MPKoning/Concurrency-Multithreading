package data_structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

import data_structures.implementation.CoarseGrainedList;
import data_structures.implementation.CoarseGrainedTree;
import data_structures.implementation.FineGrainedList;
import data_structures.implementation.FineGrainedTree;
import data_structures.implementation.LockFreeList;
import data_structures.implementation.LockFreeTree;

public class RunData<T extends Comparable<T>> {

    private final String dataStructure;
    private final int nrItems;
    private final int nrThreads;
    private final T[] itemsToAdd;
    private final T[] itemsToRemove;
    private final int workTime;
    private final boolean debug;

    private final Sorted<T> sorted;

    public RunData(String dataStructure, int nrItems, int nrThreads,
            T[] itemsToAdd, T[] itemsToRemove, int workTime, boolean debug) {
        this.dataStructure = dataStructure;
        this.nrItems = nrItems;
        this.nrThreads = nrThreads;
        this.itemsToAdd = itemsToAdd;
        this.itemsToRemove = itemsToRemove;
        this.workTime = workTime;
        this.debug = debug;

        // Determine and allocate the data structure to be used.

        if (dataStructure.equalsIgnoreCase(Main.CGL)) {
            sorted = new CoarseGrainedList<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.CGT)) {
            sorted = new CoarseGrainedTree<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.FGL)) {
            sorted = new FineGrainedList<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.FGT)) {
            sorted = new FineGrainedTree<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.LFL)) {
            sorted = new LockFreeList<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.LFT)) {
            sorted = new LockFreeTree<T>();
        } else {
            sorted = null;
            Main.exitWithError();
        }
    }

    public void runDataStructure() {

        ArrayList<WorkerThread<T>> workerThreads = new ArrayList<WorkerThread<T>>();
        CyclicBarrier barrier = new CyclicBarrier(nrThreads);

        int sz = nrItems / nrThreads;
        for (int i = 0; i < nrThreads; i++) {
            T[] toAdd = Arrays.copyOfRange(itemsToAdd, i * sz, (i + 1) * sz);
            T[] toRemove = Arrays.copyOfRange(itemsToRemove, i * sz,
                    (i + 1) * sz);
            workerThreads.add(new WorkerThread<T>(i, sorted, toAdd, toRemove,
                    workTime, barrier, debug));
        }

        // Start worker threads
        long start = System.currentTimeMillis();

        for (WorkerThread<T> t : workerThreads) {
            t.start();
        }

        // Wait until worker threads are finished
        for (WorkerThread<T> t : workerThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new Error(
                        "Unexpected InterruptedException. Should not happen.",
                        e);
            }
        }
        long end = System.currentTimeMillis();

        // Report result.
        System.out.println("data structure after removal (should be empty):");
        System.out.println(sorted.toArrayList().toString());
        System.out.println();
        System.out.printf("time: %d ms\n\n", end - start);
    }
}
