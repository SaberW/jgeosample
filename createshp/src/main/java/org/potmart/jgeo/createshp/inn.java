package org.potmart.jgeo.createshp;

import com.vividsolutions.jts.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by GOT.hodor on 2017/9/13.
 */
public class inn {

    public static void main(String[] args) {
        String m = "ads, as, a";
        String m2 = ",,";

        String[] a = m2.split(",");

        String[] b = m.split(",");

        //String[] a = {};
        System.out.println(a.length);
        for(String c:a) {

        }

        for (int t = 0; t < 4; t++) {
            switch (t){
                case 0:
                    System.out.println(t);
                    break;
                case 1:
                case 2:
                case 3:
                    default:
                        System.out.println("aaa");
            }
        }



        List<Callable<Integer>> task = new ArrayList<>();
        for (int i=0; i<b.length;i++) {
            task.add(()->{
                return null;
            });
        }
        System.out.println(task.size());

        List<Map<String, Object>> list = null;

        for (Map<String, Object> item : list) {

        }




    }

}
