package com.kun.dto;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class White {

    public static void main(String[] args) {
        MyCopyOnWriteList<Integer> list = new MyCopyOnWriteList<>();
        list.add(2);
        list.add(3);


    }


}

class MyCopyOnWriteList<E>{
    private transient  volatile Object[] element;
    final static int INIT_CAPACITY = 5;
    public MyCopyOnWriteList(){
        element = new Object[INIT_CAPACITY];
    }
    public void add(E e){
        ReentrantLock lock  = new ReentrantLock();
        lock.lock();
        try{
            int len = size();
            Object[] newElement = Arrays.copyOf(element,len+1);
            newElement[len] = e;
            Arrays.stream(newElement).forEach(System.out::println);
        }finally {
            lock.unlock();
        }
    }

    public int size(){
        int ans = 0;
        for(Object o:element){
            if(o!=null)
                ++ans;
        }
        return ans;
    }


}
