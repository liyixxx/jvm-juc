package com.xin.jvm.bookDemo;

import java.io.Serializable;

/**
 * @author 柒
 * @date 2020-03-24 21:21
 * @Description: 分派 -- oop -- 多态 -- 重载/重写
 *
 * 静态分派 -- 重载
 * jvm在重载是是通过参数的静态类型而不是实际类型作为判断依据，静态类型在编译器就可以知道
 * 重载方法匹配优先级：char -- int -- long -- character ...
 *
 * 动态分派 -- 重写
 *
 *
 */
public class Dispatch {

    static abstract class Human{
        public abstract void syHello();
    }

    static class Man extends Human{

        @Override
        public void syHello() {
            System.out.println("man: say");
        }
    }

    static class Woman extends Human{

        @Override
        public void syHello() {
            System.out.println("woman: say");
        }
    }

    public void sayHello(Human human){
        System.out.println("human: say");
    }

    public void sayHello(Man man){
        System.out.println("man: say");
    }
    public void sayHello(Woman woman){
        System.out.println("woman: say");
    }

    public static void main(String[] args) {
        // 重载 测试
        Human man = new Man();
        Human woman = new Woman();
        Dispatch dispatch = new Dispatch();
        dispatch.sayHello(man);
        dispatch.sayHello(woman);


        // 重载优先级 测试
        Overload overload = new Overload();
        overload.sayHello('c');

        // 动态分派
        man.syHello();
        woman.syHello();
        woman = new Man();
        woman.syHello();
    }

}

class Overload{
    public  void sayHello(Object obj){
        System.out.println("hello object" + obj);
    }
    /*public  void sayHello(int obj){
        System.out.println("hello int " + obj);
    }*/
    /*public  void sayHello(long obj){
        System.out.println("hello long" + obj);
    }*/
    /*public  void sayHello(Character obj){
        System.out.println("hello Character " + obj);
    }*/
    /*public  void sayHello(char obj){
        System.out.println("hello char" + obj);
    }*/
    public  void sayHello(char... obj){
        System.out.println("hello char ... " + obj);
    }
    public  void sayHello(Serializable obj){
        System.out.println("hello Serializable " + obj);
    }
}