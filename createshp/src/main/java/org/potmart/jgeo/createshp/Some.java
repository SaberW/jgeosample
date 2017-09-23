package org.potmart.jgeo.createshp;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by GOT.hodor on 2017/9/23.
 */
public class Some {

    public static void main(String[] args) {
        String[] eles = {"name", "code", "type"};

        eles = Arrays.asList(eles).stream()
                .filter(key -> !key.equals("name"))
                .collect(Collectors.toList())
                .toArray(new String[eles.length-2]);

        System.out.println(eles[0]);
    }
}
