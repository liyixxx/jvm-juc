package com.xin.juc02;

/**
 * @author 柒
 * 单例模式 DCL双锁检测
 */
public class DCL {

    /** volatile保证可见性和有序性 */
    private volatile static DCL instance ;

    private DCL(){
        System.out.println("thread:"+Thread.currentThread().getName()+" 构造调用");
    }

    public static DCL getInstance(){
        if(instance == null){
            synchronized (DCL.class){
                if(instance == null ){
                    instance =  new DCL();
                }
            }
        }
        return instance ;
    }

}
