package org.potmart.jgeo.sample.shortestpath;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.GraphBuilder;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.path.DijkstraShortestPathFinder;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Graphable;
import org.geotools.graph.structure.Node;
import org.geotools.graph.traverse.standard.DijkstraIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by  on 2017/1/24.
 */
public class Dijkstra {

    public static void main(String[] args) {
        String shpPath  = "F:/MapWorkspace/shp/demo_path/demo_path.shp";
        DataStore dataStore = getDataStore(shpPath);
        if (dataStore != null) {
            shortest(dataStore);
        }else {
            System.out.println("data store is null");
        }
    }

    /**
     *
     * @param shpPath
     * @return
     */
    private static DataStore getDataStore(String shpPath) {
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
     * @param dataStore
     */
    private static void shortest(DataStore dataStore) {
        try{
            //由features生成图
            SimpleFeatureSource source = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            SimpleFeatureCollection featureCollection = source.getFeatures();

            Graph graph = buildGraph(featureCollection);
            if (graph == null) return;

            DijkstraIterator.EdgeWeighter edgeWeighter = new DijkstraIterator.EdgeWeighter() {
                public double getWeight(Edge edge) {
                    SimpleFeature feature = (SimpleFeature) edge.getObject();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();
                    return geometry.getLength();
                }
            };

            Node start = null;
            Node destination = null;

            Iterator nodeIterator = graph.getNodes().iterator();
            int i=0;
            while (nodeIterator.hasNext()) {
                Node iNode = (Node) nodeIterator.next();
                Point point = (Point)iNode.getObject();

                if (iNode.getID() == 6) start = iNode;

                if (iNode.getID() == 13) destination = iNode;

                /*

                if (point.getX() == 0 && point.getY() == 0) {
                    start = iNode;
                }
                if (point.getX() == 0 && point.getY() == 0) {
                    destination = iNode;
                }
                */

                i++;
            }

            if (start != null && destination != null) {
                DijkstraShortestPathFinder pathFinder = new DijkstraShortestPathFinder(graph, start, edgeWeighter);
                pathFinder.calculate();

                Path path = pathFinder.getPath(destination);
                double cost = pathFinder.getCost(destination);
                if (path != null) {
                    System.out.println(path.toString());

                }
                System.out.println(cost);

            }


        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param simpleFeatureCollection
     * @return
     */
    private static Graph buildGraph(SimpleFeatureCollection simpleFeatureCollection) {
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
