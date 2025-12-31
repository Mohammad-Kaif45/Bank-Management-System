package com.kaif;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReenterentExample {
    private final Lock lock = new ReentrantLock();

    public void outerMethod(){
        lock.lock();
        try {
            System.out.println("Outer method");
            innerMethod();
        } finally {
            lock.unlock();
        }
    }

    public void innerMethod(){
        lock.lock();
        try {
            System.out.println("Inner method");

        } finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) {
        ReenterentExample example = new ReenterentExample();
        example.outerMethod();
    }
}
