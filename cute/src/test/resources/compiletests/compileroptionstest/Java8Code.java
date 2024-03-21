package io.toolisticon.cute.testcases;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Java8Code {
    public static void main(String[] args) {
        Arrays.asList("A", "B", "C").stream().filter(e -> e.equals("B")).collect(Collectors.toSet());
    }
}