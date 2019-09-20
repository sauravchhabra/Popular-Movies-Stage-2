package com.sauravchhabra.popularmoviesstage2;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Public class to execute the command given to it in a handler
 */
public class AppExecutors {

    private static final Object SINGLETON = new Object();
    private final Executor mDiskIO;
    private final Executor mMainThread;
    private final Executor mNetworkIO;
    private static AppExecutors sInstance;

    /**
     * Constructor to initialize the values of Executors
     *
     * @param diskIO     local executor
     * @param mainThread local executor
     * @param networkIO  local executor
     */
    public AppExecutors(Executor diskIO, Executor mainThread, Executor networkIO) {
        mDiskIO = diskIO;
        mMainThread = mainThread;
        mNetworkIO = networkIO;
    }

    /**
     * This method makes sure that only one instance of a job is running
     *
     * @return the instance of the AppExecutor
     */
    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (SINGLETON) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    // Public getters
    public Executor getDiskIO() {
        return mDiskIO;
    }

    public Executor getMainThread() {
        return mMainThread;
    }

    public Executor getNetworkIO() {
        return mNetworkIO;
    }

    /**
     * Inner class to invoke the thread
     */
    private static class MainThreadExecutor implements Executor {
        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }
    }
}
