package com.ticket.service;

public class Lock{

    /**
     * Singleton design pattern to get the obj instance
     */
    static Lock lock = null;
    private Lock() {}

    static Lock getLockObj() {
        if (lock == null) lock = new Lock();
        return lock;
    }

    /**
     * Flag variable to allow only single thread to proceed
     */
    private boolean isLocked = false;

    /**
     * Locks critical section of /the code.
     * @throws InterruptedException
     */
    public synchronized void lock()
            throws InterruptedException{
        while(isLocked){
            wait();
        }
        isLocked = true;
    }

    /**
     * Releases lcok.
     */
    public synchronized void unlock(){
        isLocked = false;
        notify();
    }
}