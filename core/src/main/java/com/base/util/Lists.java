package com.base.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chenshuai on 2017/11/7.
 */

public class Lists {
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    public static <E> ArrayList<E> newArrayList(int length) {
        return new ArrayList<E>(length);
    }


    public static  ArrayList<String> newTestArrayList() {
        return Lists.newArrayList("","","","","","","","","","","","","","","");
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<E>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    public static <T> T[] toArray(Class<?> cls, ArrayList<T> items) {
        if (items == null || items.size() == 0) {
            return (T[]) Array.newInstance(cls, 0);
        }
        return items.toArray((T[]) Array.newInstance(cls, items.size()));
    }

    public static int computeArrayListCapacity(int arraySize) {

        return saturatedCast(5L + arraySize + (arraySize / 10));
    }

    private static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    public static <E> boolean isFull(E... objects){
        for(int i = 0, len = objects.length; i < len; i++){
            if(objects[i] == null){
                return false;
            }
        }
        return true;
    }

    public static String appendStringByList(List<String> list){
        StringBuffer sb  = new StringBuffer();
        for (int i = 0, len = list.size(); i < len; i++) {
            sb.append(list.get(i));
            if(i != list.size() - 1){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static boolean isEmpty(List list){
        return list == null || list.isEmpty();
    }

}
