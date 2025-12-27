package com.kaif.executionframework;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static long factorial(int n) throws InterruptedException {
        Thread.sleep(1000);
        long result = 1;
        for(int i = 1; i <= n; i++){
            result *= i;
        }
        return result;
    }
    public static void main(String[] args)  {

        long stratTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for(int i = 1; i < 10; i++){
            int finalI = i;
            executor.submit(() -> {

                try {
                   long  result = factorial(finalI);
                    System.out.println(result);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
        }
        executor.shutdown();
//        Thread[] threads = new Thread[9];
//
//        for(int i = 1; i < 10; i++){
//            int finalI = i;
//            threads[i - 1] = new Thread(
//                    () -> {
//                        try {
//                            long result = factorial(finalI);
//                            System.out.println(result);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//            );
//            threads[i - 1].start();
//        }
//        for(Thread thread : threads){
//            try {
//                thread.join();
//            } catch (InterruptedException e){
//                Thread.currentThread().interrupt();
//            }
//        }


//        System.out.println("Total time : " + (System.currentTimeMillis() - stratTime));
    }
}
