package org.potmart.jgeo.createshp;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.List;

/**
 * Created by GOT.hodor on 2017/8/18.
 */
public class WktBuildGeom {

    public static void main(String[] args) {
        String wkt = "POINT(120.0 30.0)";
        try{
            WKTReader wktReader = new WKTReader();
            Geometry geometry = wktReader.read(wkt);
            Geometry env = geometry.getEnvelope();
            System.out.println(env.getCentroid());
        }catch (ParseException e){
            e.printStackTrace();
        }

        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.add("id", Long.class);
        simpleFeatureTypeBuilder.add("name", String.class);
        simpleFeatureTypeBuilder.add("code", String.class);
        simpleFeatureTypeBuilder.add("geom", Point.class, 3857);
        SimpleFeatureType schema1 = simpleFeatureTypeBuilder.buildFeatureType();
        ListFeatureCollection fc1 = new ListFeatureCollection(schema1);
        ListFeatureCollection fc2 = new ListFeatureCollection(schema1);

        fc1.addAll(fc2);
    }
}
