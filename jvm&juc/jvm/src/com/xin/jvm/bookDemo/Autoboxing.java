package com.xin.jvm.bookDemo;

/**
 * @author 柒
 * @date 2020-03-24 21:57
 * @Description: 自动装箱
 *
 * Integer.equals 如果传入的变量是int类型，获取int值，和integer的值进行等值匹配
 */
public class Autoboxing {

    public static void main(String[] args) {
        Integer a = 1 ;
        Integer b = 2 ;
        Integer c = 3 ;
        Integer d = 3 ;
        Integer e = 4 ;
        Integer f = 4 ;
        Long g = 3l ;
        // t
        System.out.println(c == d);
        // t
        System.out.println(e == f);
        // t
        System.out.println((a+b) == c);
        // t
        System.out.println(c.equals(a+b));
        // t
        System.out.println(g == (a+b));
        // f
        System.out.println(g.equals(a+b));
    }
}
