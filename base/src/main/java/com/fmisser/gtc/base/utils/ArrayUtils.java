package com.fmisser.gtc.base.utils;

import lombok.SneakyThrows;

import java.io.*;
import java.util.List;

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
        return (List<T>)in.readObject();
    }
}
