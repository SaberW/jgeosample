package org.potmart.jgeo.createshp;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.IOException;

/**
 * Created by GOT.hodor on 2017/9/9.
 */
public class MultiRel {

    public static void main(String[] args) {

        String multipolygon = "MULTIPOLYGON(((0 0, 300 0, 300 -300, 0 -300, 0 0), (100 -100,100 -200,200 -200, 200 -100,100 -100)))";

        String polygon = "POLYGON((0 0, 300 0, 300 300, 0 300, 0 0),(100 100,100 200,200 200,200 100,100 100))";

        String hulegon = "POLYGON((0 0, 400 0, 400 100,300 100, 300 200, 400 200,))";

        String point = "POINT(250 250)";

        Geometry rail = readWkt(multipolygon);
        Geometry tag = readWkt(point);

        if (polygon != null && tag != null) {
            System.out.println(rail.contains(tag));
        }

    }

    /**
     *
     * @param wkt
     * @return
     */
    public static Geometry readWkt(String wkt) {
        try{
            WKTReader wktReader = new WKTReader();
            return wktReader.read(wkt);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
