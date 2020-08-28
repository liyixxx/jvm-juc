package com.xin.jvm.bookDemo;

/**
 * @author 柒
 * @date 2020-03-25 8:51
 * @Description:DCL双锁检测 - 单例模式
 * 构造私有化 -- 双锁检测
 *
 */
public class Singleton {


    public static void main(String[] args) {
        Singleton instance = DCLSingleton.getInstance();
        Singleton instance1 = DCLSingleton.getInstance();
        System.out.println(instance == instance1);
    }
}

class DCLSingleton {
    private volatile static Singleton instance ;

    private DCLSingleton() {
    }

    public static Singleton getInstance(){
        if(instance == null){
            synchronized (DCLSingleton.class){
                if(instance == null){
                    instance = new Singleton() ;
                }
            }
        }
        return instance ;
    }
}