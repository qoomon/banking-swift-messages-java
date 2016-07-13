package com.qoomon.banking.swift;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by qoomon on 13/07/16.
 */
public class TestUtils {

    public static <T> List<T> collectAll(Callable<T> function) throws Exception {
        List<T> resultList = new LinkedList<>();
        T result;
        while ((result = function.call()) != null) {
            resultList.add(result);
        }
        return resultList;
    }
}
