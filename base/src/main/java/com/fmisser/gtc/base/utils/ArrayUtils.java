package com.fmisser.gtc.base.utils;

import lombok.SneakyThrows;
import org.springframework.data.util.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author by fmisser
 * @create 2021/6/1 3:29 下午
 * @description TODO
 */
public class ArrayUtils {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> List<T> deepCopy(List<T> src) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        return (List<T>) in.readObject();
    }


    public static Pair<String, Integer> getCommonTotal(List<String> list) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        for (String item : list) {
            if (map.containsKey(item)) {
                map.put(item, map.get(item).intValue() + 1);
            } else {
                map.put(item, new Integer(1));
            }
        }

        Iterator<String> keys = map.keySet().iterator();

      if(keys.hasNext()){
          while (keys.hasNext()) {
              String key = keys.next();
              System.out.print(key + ":" + map.get(key).intValue() + ", ");

              return Pair.of(key, map.get(key).intValue());
          }
      }else{
          return Pair.of("",0);
      }
        return null;
    }


    public static int getCount(List<String> list,String key){
        int i=0;
        for(String item : list){
            if(item.equals(key)){
                i++;
            }
        }

        return i;
    }
}