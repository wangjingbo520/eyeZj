package com.l.eyescure.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.LinkedList;

/**
 * Created by jerryco on 2016/8/3.
 */
public class DeferredHandler {
    private LinkedList<Runnable> mQueue = new LinkedList<Runnable>();
    private Impl mHandler = null;

    private class Impl extends Handler {
        public Impl() {

        }

        public Impl(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            Runnable r;
            synchronized (mQueue) {
                if (mQueue.size() == 0) {
                    return;
                }
                r = mQueue.removeFirst();
            }
            r.run();
            synchronized (mQueue) {
                scheduleNextLocked();
            }
        }
    }

    public DeferredHandler() {
        mHandler = new Impl();
    }

    public DeferredHandler(Looper looper) {
        mHandler = new Impl(looper);
    }

    /**
     * Schedule runnable to run after everything that's on the queue right now.
     */
    public void post(Runnable runnable) {

        synchronized (mQueue) {
            mQueue.add(runnable);
            if (mQueue.size() == 1) {
                scheduleNextLocked();
            }
        }
    }

    public void cancelRunnable(Runnable runnable) {
        synchronized (mQueue) {
            while (mQueue.remove(runnable)) {
            }
        }
    }

    public void cancel() {
        synchronized (mQueue) {
            mQueue.clear();
        }
    }

    /**
     * Runs all queued Runnables from the calling thread.
     */
    public void flush() {
        LinkedList<Runnable> queue = new LinkedList<Runnable>();
        synchronized (mQueue) {
            queue.addAll(mQueue);
            mQueue.clear();
        }
        for (Runnable p : queue) {
            p.run();
        }
    }

    void scheduleNextLocked() {
        if (mQueue.size() > 0) {
            mHandler.sendEmptyMessage(1);
        }
    }
}
