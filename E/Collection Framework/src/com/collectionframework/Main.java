package com.collectionframework;

import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(5);
        System.out.println(list.get(2));
        System.out.println(list.size());
        for(int i = 0; i < list.size(); i++){
            System.out.println(list.get(i));
        }
        List<Integer> list1 = List.of(4,5,6,7,8);

    }
}
