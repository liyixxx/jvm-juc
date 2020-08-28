package com.xin.jvm.bookDemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author 柒
 * @date 2020-03-24 21:41
 * @Description: 动态代理 简单示例
 */
public class DynamicProxy {

    interface Hello_In {
        void sayHello();
    }

    static class Hello implements Hello_In {
        @Override
        public void sayHello() {
            System.out.println("hello world");
        }
    }

    static class DymicProxy_T implements InvocationHandler {

        Object original;

        Object bind(Object original) {
            this.original = original ;
            return Proxy.newProxyInstance(original.getClass().getClassLoader()
                    ,original.getClass().getInterfaces(),this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println(" proxy method ...");
            method.invoke(original,args);
            return null ;
        }
    }

    public static void main(String[] args) {
        Hello_In hello = (Hello_In) new DymicProxy_T().bind(new Hello());
        hello.sayHello();

    }
}
