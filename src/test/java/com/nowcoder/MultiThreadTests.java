package com.nowcoder;

import org.apache.ibatis.javassist.bytecode.ExceptionsAttribute;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LIU ZHAOZHI on 2017-6-7.
 */
class MyThread extends Thread{

    private int tid;


    public MyThread(int tid){
        this.tid=tid;
    }

    @Override
    public void run(){
        try{
            for(int i=0;i<10;i++){
                Thread.sleep(1000);
                System.out.println(String.format("%d:%d",tid,i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable{
    private BlockingQueue<String> q;
    public Consumer(BlockingQueue<String> q){
        this.q = q;
    }
    @Override
    public void run() {
        try{
            while(true){
                System.out.println(Thread.currentThread().getName()+":"+q.take());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

class Producer implements Runnable{
    private BlockingQueue<String> q;
    public Producer (BlockingQueue<String> q){
        this.q = q;
    }
    @Override
    public void run() {
        try{
            for(int i=0;i<100;i++){
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}



public class MultiThreadTests{

    public static void testThread(){
        for(int i=0;i<9;i++){
            new MyThread(i).start();
        }
    }


    //测试sychronized
    public static Object obj = new Object();

    public static void testSychronized1(){
           synchronized (obj){
               try{
                   for(int i=0;i<10;i++) {
                       Thread.sleep(1000);
                       System.out.println(String.format("T3 %d", i));
                   }
               }catch (Exception e){
                    e.printStackTrace();
               }
           }
    }

    public static void testSychronized2(){
        synchronized (obj){
            try{
                for(int i=0;i<10;i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4 %d", i));
                    System.out.println("******************************");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSychronized(){
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSychronized1();
                    testSychronized2();
                }
            }).start();
        }
    }

    public static void testBlockingQueue(){
        BlockingQueue<String> q=new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q),"Consumer1").start();
        new Thread(new Consumer(q),"Consumer2").start();
    }

    private static ThreadLocal<Integer> threadLocalUserIds=new ThreadLocal<>();
    private static int userId;
    public static void testThreadLocal(){
        for(int i=0;i<10;i++){
            final int finalI=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        threadLocalUserIds.set(finalI);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:"+threadLocalUserIds.get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void testExecutor(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10;i++){
                    try{
                        Thread.sleep(1000);
                        System.out.println("Executor:"+i);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        service.shutdown();
        while(!service.isTerminated()){
            try{
                Thread.sleep(1000);
                System.out.println("Wait for termination!");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static int counter=0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void testWithoutAtomic(){
        for(int i=0;i<10;i++){
           new Thread(new Runnable() {
               @Override
               public void run() {
                   try{
                       Thread.sleep(1000);
                       for(int j=0;j<10;j++){
                           counter++;
                           System.out.println(counter);
                       }
                   }catch(Exception e){
                       e.printStackTrace();
                   }
               }
           }).start();

        }
    }

    public static void testWithAtomic(){
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(1000);
                        for(int j=0;j<10;j++){
                            System.out.println(atomicInteger.incrementAndGet());
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    public static void main(String[] argv){
        //testThread();
        //testSychronized();
        //testBlockingQueue();
        //testThreadLocal();
        //testExecutor();
        //testWithoutAtomic();
        testWithAtomic();
    }

}
