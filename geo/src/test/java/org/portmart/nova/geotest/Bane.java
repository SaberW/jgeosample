package org.portmart.nova.geotest;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeatureType;
import org.potmart.nova.geo.datastore.DataStoreManager;
import org.potmart.nova.geo.schema.SchemaManager;

import java.util.HashMap;
import java.util.Map;

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
        DataStoreManager dataStoreManager = new DataStoreManager();
        String localStoreName = "local_postgis";
        dataStoreManager.storeConnection(localStoreName, connParams());

        DataStore dataStore = dataStoreManager.fetchDataStore(localStoreName);

        if (dataStore != null) {
            SchemaManager localSchemaManager = new SchemaManager(dataStore);
            localSchemaManager.preloadSchemaNames();

            String cameraSchemaName = "camera__";
            SimpleFeatureType featureType = localSchemaManager.fetchSchema(cameraSchemaName);
            if (featureType != null) {
                printMsg(featureType.getTypeName());
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
     * @return
     */
    private static Map<String, Object> connParams() {
        Map<String,Object> params = new HashMap<>();
        params.put( "dbtype", "postgis");
        params.put( "host", "127.0.0.1");
        params.put( "port", 6432);
        params.put( "schema", "public");
        params.put( "database", "gis_db");
        params.put( "user", "postgres");
        params.put( "passwd", "postgres");
        return params;
    }
}
