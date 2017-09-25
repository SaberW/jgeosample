package org.portmart.nova.geotest;

import com.vividsolutions.jts.geom.Point;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.transform.Definition;
import org.geotools.data.transform.TransformFactory;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.potmart.nova.geo.datastore.DataStoreManager;
import org.potmart.nova.geo.schema.SchemaManager;

import java.io.IOException;
import java.util.*;

/**
 * test
 * Created by GOT.hodor on 2017/9/23.
 */
public class Bane {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty("org.geotools.referencing.forceXY", "true");

        DataStoreManager dataStoreManager = new DataStoreManager();
        String localStoreName = "local_postgis";
        dataStoreManager.storeConnection(localStoreName, Params.connParams());
        DataStore dataStore = dataStoreManager.fetchDataStore(localStoreName);

        if (dataStore != null) {
            SchemaManager localSchemaManager = new SchemaManager(dataStore);
            localSchemaManager.preloadSchemaNames();

            String cameraSchemaName = "camera";
            String buildingSchemaName = "building";
            String broadingSchemaName = "broading";
            String mypolySchemaName = "mypoly";

            SimpleFeatureType featureType = localSchemaManager.fetchSchema(cameraSchemaName);
            if (featureType != null) {
                printMsg(featureType.getTypeName());
            }

            SimpleFeatureSource cameraFeatureSource = featureSourceByName(dataStore, cameraSchemaName);
            SimpleFeatureSource buildingFeatureSource = featureSourceByName(dataStore, buildingSchemaName);
            SimpleFeatureSource broadingFeatureSource = featureSourceByName(dataStore, broadingSchemaName);
            SimpleFeatureSource mypolyFeatureSource = featureSourceByName(dataStore, mypolySchemaName);

            SimpleFeatureSource cameraSource = transformSource(cameraFeatureSource, "camera", cameraDefinitionList());
            SimpleFeatureSource buildingSource = transformSource(buildingFeatureSource, "building", buildingDefinitionList());
            SimpleFeatureSource broadingSource = transformSource(broadingFeatureSource, "broading", broadingDefinitionList());

            SimpleFeatureSource mypolySource = transformSource(mypolyFeatureSource, "mypoly", mypolyDefinitionList());

            try{
                FeatureJSON featureJSON = new FeatureJSON();

                Query query = new Query();
                query.setFilter(CQL.toFilter("id is not null"));
                query.setCoordinateSystemReproject(Params.crsByEpsgCode("EPSG:3857", true));
                SimpleFeatureCollection cameraCollection = cameraFeatureSource.getFeatures(query);
                String cameraJSON = featureJSON.toString(cameraCollection);
                System.out.println(cameraJSON);

                DefaultFeatureCollection defaultFeatureCollection = new DefaultFeatureCollection();

                Query cameraQuery = new Query();
                cameraQuery.setFilter(CQL.toFilter("id > 0"));
                cameraQuery.setCoordinateSystemReproject(Params.crsByEpsgCode("EPSG:3857", true));
                SimpleFeatureCollection camera = cameraSource.getFeatures(cameraQuery);
                System.out.println(featureJSON.toString(camera));

                Query buildingQuery = new Query();
                buildingQuery.setFilter(CQL.toFilter("id > 0"));
                buildingQuery.setCoordinateSystemReproject(Params.crsByEpsgCode("EPSG:3857", true));
                SimpleFeatureCollection building = buildingSource.getFeatures(buildingQuery);
                System.out.println(featureJSON.toString(building));

                Query broadingQuery = new Query();
                broadingQuery.setFilter(CQL.toFilter("id > 0"));
                broadingQuery.setCoordinateSystem(Params.crsByEpsgCode("EPSG:3857", true));
                broadingQuery.setCoordinateSystemReproject(Params.crsByEpsgCode("EPSG:4326", true));
                SimpleFeatureCollection broading = broadingSource.getFeatures(broadingQuery);
                System.out.println(featureJSON.toString(broading));

                defaultFeatureCollection.addAll(camera);
                defaultFeatureCollection.addAll(building);
                defaultFeatureCollection.addAll(broading);

                String geojson = featureJSON.toString(defaultFeatureCollection);
                printMsg(geojson);


                Query mypolyQuery = new Query();
                mypolyQuery.setFilter(CQL.toFilter("code is not null"));
                mypolyQuery.setCoordinateSystem(Params.crsByEpsgCode("EPSG:3857", true));
                mypolyQuery.setCoordinateSystemReproject(Params.crsByEpsgCode("EPSG:4326", true));
                SimpleFeatureCollection mypolyCollection = mypolySource.getFeatures(mypolyQuery);
                printMsg("centroid : \r\n" + featureJSON.toString(mypolyCollection));

            }catch (CQLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     *
     * @param msg
     */
    private static void printMsg(String msg) {
        System.out.println(msg);
    }

    /**
     *
     * @param dataStore
     * @param name
     * @return
     */
    private static SimpleFeatureSource featureSourceByName(DataStore dataStore, String name) {
        try{
            return dataStore.getFeatureSource(name);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param featureSource
     * @param featureSourceName
     * @param definitions
     */
    private static SimpleFeatureSource transformSource(SimpleFeatureSource featureSource, String featureSourceName, List<Definition> definitions) {
        try{
            return TransformFactory.transform(featureSource, featureSourceName, definitions);
        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * building definition
     * @return
     */
    private static List<Definition> buildingDefinitionList() {
        try{
            String geomField = "geom";
            Expression expression = CQL.toExpression("geom");
            CoordinateReferenceSystem crs = Params.crsByEpsgCode("EPSG:3857", true);
            Definition geomDef = new Definition(geomField, expression, Point.class, crs);

            String nameField = "name";
            Expression nameExpression = CQL.toExpression("name");
            Definition nameDef = new Definition(nameField, nameExpression, null, null);

            String idField = "id";
            Expression idExpression = CQL.toExpression("oid");
            Definition idDef = new Definition(idField, idExpression, null, null);

            List<Definition> list = new ArrayList<>();
            list.add(idDef);
            list.add(nameDef);
            list.add(geomDef);

            return list;

        }catch (CQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * camera definition
     * @return
     */
    private static List<Definition> cameraDefinitionList() {
        try{
            String geomField = "geom";
            Expression expression = CQL.toExpression("geom");
            CoordinateReferenceSystem crs = Params.crsByEpsgCode("EPSG:3857", true);
            Definition geomDef = new Definition(geomField, expression, Point.class, crs);

            String nameField = "name";
            Expression nameExpression = CQL.toExpression("name");
            Definition nameDef = new Definition(nameField, nameExpression, null, null);

            String idField = "id";
            Expression idExpression = CQL.toExpression("id");
            Definition idDef = new Definition(idField, idExpression, Double.class, null);

            List<Definition> list = new ArrayList<>();
            list.add(idDef);
            list.add(nameDef);
            list.add(geomDef);

            return list;

        }catch (CQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    private static List<Definition> broadingDefinitionList() {
        try{
            String geomField = "geom";
            Expression expression = CQL.toExpression("geom");
            CoordinateReferenceSystem crs = Params.crsByEpsgCode("EPSG:3857", true);
            Definition geomDef = new Definition(geomField, expression, Point.class, crs);

            String nameField = "name";
            Expression nameExpression = CQL.toExpression("name");
            Definition nameDef = new Definition(nameField, nameExpression, null, null);

            String idField = "id";
            Expression idExpression = CQL.toExpression("id");
            Definition idDef = new Definition(idField, idExpression, null, null);

            List<Definition> list = new ArrayList<>();
            list.add(idDef);
            list.add(nameDef);
            list.add(geomDef);

            return list;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Definition> mypolyDefinitionList() {
        try{
            String geomField = "geom";
            Expression expression = CQL.toExpression("centroid(geom)");
            CoordinateReferenceSystem crs = Params.crsByEpsgCode("EPSG:4326", true);
            Definition geomDef = new Definition(geomField, expression, Point.class, crs);

            String nameField = "name";
            Expression nameExpression = CQL.toExpression("name");
            Definition nameDef = new Definition(nameField, nameExpression, null, null);

            String idField = "code";
            Expression idExpression = CQL.toExpression("code");
            Definition idDef = new Definition(idField, idExpression, null, null);

            List<Definition> list = new ArrayList<>();
            list.add(idDef);
            list.add(nameDef);
            list.add(geomDef);

            return list;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
