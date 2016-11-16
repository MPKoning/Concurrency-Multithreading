package data_structures;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WorkerThread<T extends Comparable<T>> extends Thread {
    private final int id;
    private final Sorted<T> sorted;
    private final T[] itemsToAdd;
    private final T[] itemsToRemove;
    private final int workTime;
    private final boolean doDebug;
    private final CyclicBarrier barrier;

    public WorkerThread(int id, Sorted<T> list, T[] itemsToAdd,
            T[] itemsToRemove, int workTime, CyclicBarrier barrier,
            boolean debug) {
        this.sorted = list;
        this.id = id;
        this.itemsToAdd = itemsToAdd;
        this.itemsToRemove = itemsToRemove;
        this.workTime = workTime;
        this.barrier = barrier;
        this.doDebug = debug;
    }

    @Override
    public void run() {
        // First: add my items.
        for (T t : itemsToAdd) {
            doWork();
            sorted.add(t);
        }

        // Barrier, and possibly print result.
        // To test inter-operability of add() and remove(), you can comment out
        // this barrier so that some threads may start removing while other
        // threads are still adding.
        try {
            barrier.await();
            if (this.doDebug) {
                if (this.id == 0) {
                    System.out.printf(
                            "Output after adding, before removing:\n%s\n",
                            sorted.toArrayList().toString());
                }
                barrier.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        // Remove my items.
        for (T t : itemsToRemove) {
            doWork();
            sorted.remove(t);
        }
    }

    private void doWork() {
        if (workTime > 0) {
            // Do some work in between operations. workTime indicates the cpu
            // time to be consumed, in microseconds.
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long start = bean.getThreadCpuTime(Thread.currentThread().getId());

            // getThreadCpuTime() returns cpu time in nano seconds.
            long end = start + workTime * 1000;
            while (bean.getThreadCpuTime(Thread.currentThread().getId()) < end)
                ; // busy until we used enough cpu time.
        }
    }
}
