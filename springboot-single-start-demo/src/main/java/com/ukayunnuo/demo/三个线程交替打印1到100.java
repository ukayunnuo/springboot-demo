package com.ukayunnuo.demo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class 三个线程交替打印1到100 {

    private static final ReentrantLock lock = new ReentrantLock(true);
    private static final Condition condition1 = lock.newCondition();
    private static final Condition condition2 = lock.newCondition();
    private static final Condition condition3 = lock.newCondition();

    private static int number = 1;

    private static Condition threadIndex = condition1;

    private static final Integer MAX_NUMBER = 100;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> test("T1", condition1, condition2));
        Thread t2 = new Thread(() -> test("T2", condition2, condition3));
        Thread t3 = new Thread(() -> test("T3", condition3, condition1));
        t1.start();
        t2.start();
        t3.start();


    }

    public static void test(String threadName, Condition currentCondition, Condition nextCondition) {
        while (number <= MAX_NUMBER) {
            try {
                lock.lock();
                while (threadIndex != currentCondition && number <= MAX_NUMBER) {
                    currentCondition.await();
                }
                if (number > MAX_NUMBER) {
                    break;
                }
                System.out.println("线程：" + threadName + ": " + number);
                number++;
                threadIndex = nextCondition;
                nextCondition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}
