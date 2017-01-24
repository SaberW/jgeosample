package org.potmart.jgeo.sample.shortestpath;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.structure.Graph;
import org.opengis.feature.Feature;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GOT.hodor on 2017/1/24.
 */
public class ShpUtil {
    /**
     *
     * @param shpPath
     * @return
     */
    public static DataStore getDataStore(String shpPath) {
        try{
            File file = new File(shpPath);
            Map map = new HashMap();
            map.put( "url", file.toURL() );
            DataStore dataStore = DataStoreFinder.getDataStore(map);
            return dataStore;
        }catch (Exception e) {
            return null;
        }

    }

    /**
     *
     * @param simpleFeatureCollection
     * @return
     */
    public static Graph buildGraph(SimpleFeatureCollection simpleFeatureCollection) {
        LineStringGraphGenerator lineStringGraphGenerator = new LineStringGraphGenerator();
        FeatureGraphGenerator graphGenerator = new FeatureGraphGenerator(lineStringGraphGenerator);
        FeatureIterator iterator = simpleFeatureCollection.features();
        try{
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                graphGenerator.add(feature);
            }

            Graph graph = graphGenerator.getGraph();
            return graph;
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            iterator.close();
        }
        return null;
    }
}
