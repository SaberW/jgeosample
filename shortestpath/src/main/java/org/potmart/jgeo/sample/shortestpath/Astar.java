package org.potmart.jgeo.sample.shortestpath;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.path.AStarShortestPathFinder;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;
import org.geotools.graph.traverse.standard.AStarIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Iterator;

/**
 * Created by  on 2017/1/24.
 */
public class Astar {

    public static void main(String[] args) {
        String shpPath  = "F:/MapWorkspace/shp/demo_path/demo_path.shp";
        DataStore dataStore = ShpUtil.getDataStore(shpPath);
        if (dataStore != null) {
            shortest(dataStore);
        }else {
            System.out.println("data store is null");
        }
    }

    private static void shortest(DataStore dataStore) {
        try{
            //由features生成图
            SimpleFeatureSource source = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            SimpleFeatureCollection featureCollection = source.getFeatures();

            Graph graph = ShpUtil.buildGraph(featureCollection);

            Node start = null;
            Node destination = null;

            Iterator iterator = graph.getNodes().iterator();

            while (iterator.hasNext()) {

            }

            if (start == null && destination == null) {
                System.out.println("没有起止点");
                return;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void astarPath(Graph graph, Node start, Node destination) {
        AStarIterator.AStarFunctions aStarFunctions = new AStarIterator.AStarFunctions(destination) {
            @Override
            public double cost(AStarIterator.AStarNode aStarNode, AStarIterator.AStarNode aStarNode1) {
                Edge edge = aStarNode.getNode().getEdge(aStarNode1.getNode());
                SimpleFeature feature = (SimpleFeature) edge.getObject();
                Geometry geometry = (Geometry) feature.getDefaultGeometry();

                return geometry.getLength();
            }

            @Override
            public double h(Node node) {
                return -10;
            }
        };

        AStarShortestPathFinder pathFinder = new AStarShortestPathFinder(graph, start, destination, aStarFunctions);
        try{
            pathFinder.calculate();
            Path path = pathFinder.getPath();
            if (path != null) {
                System.out.println(path.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
