package com.xin.juc.demo2;

import com.xin.juc.pojo.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StreamDemo {

    public static void main(String[] args) {
        User u1 = new User(11,"a",23);
        User u2 = new User(12,"b",24);
        User u3 = new User(13,"c",22);
        User u4 = new User(14,"d",28);
        User u5 = new User(16,"e",26);
        List<User> list = Arrays.asList(u1, u2, u3, u4, u5);
        Stream<String> stream = list.stream()
                .filter(user -> user.getId() % 2 == 0 && user.getAge() > 24)
                .map(user -> user.getName().toUpperCase())
                .sorted((user1, user2) -> -user1.compareTo(user2))
                .limit(1);
        new ArrayList<>();
        stream.forEach(System.out::println);
    }
}
