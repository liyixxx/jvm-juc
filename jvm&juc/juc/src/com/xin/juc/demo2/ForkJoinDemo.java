package com.xin.juc.demo2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

class MyTask extends RecursiveTask<Long> {
    private Long begin ;
    private Long end ;
    private Long result = 0l ;
    private final Long VALUE = 10000L ;

    public MyTask(Long begin, Long end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if((end - begin) <= VALUE){
            for (Long i = begin ; i <= end; i++) {
                result += i ;
            }
        } else {
            Long middle = (begin+end)/2;
            MyTask left = new MyTask(begin, middle);
            MyTask right = new MyTask(middle+1, end);
            left.fork();
            right.fork();
            result = left.join() + right.join() ;
        }
        return result ;
    }
}

public class ForkJoinDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyTask task = new MyTask(100l, 2712345600L);
        ForkJoinPool pool = new ForkJoinPool();

//        pool.execute(task);
        ForkJoinTask<Long> submit = pool.submit(task);
        System.out.println(submit.get());

        pool.shutdown();
    }
}
