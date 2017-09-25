package org.portmart.nova.geotest;

import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.FactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geojson.feature.FeatureJSON;
import org.omg.SendingContext.RunTime;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.potmart.nova.geo.datastore.DataStoreManager;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by GOT.hodor on 2017/9/25.
 */
public class CqlFilter {

    public static void main(String[] args) {
        System.setProperty("org.geotools.referencing.forceXY", "true");

        DataStoreManager dataStoreManager = new DataStoreManager();
        String localStoreName = "local_postgis";
        dataStoreManager.storeConnection(localStoreName, Params.connParams());
        DataStore dataStore = dataStoreManager.fetchDataStore(localStoreName);

        if (dataStore != null) {
            FeatureJSON featureJSON = new FeatureJSON();
            String mypolySchemaName = "camera";

            try {
                SimpleFeatureSource mypolySource = dataStore.getFeatureSource(mypolySchemaName);

                Query query = new Query();
                //query.setCoordinateSystem(Params.crsByEpsgCode("EPSG:4326", true));
                //query.setCoordinateSystemReproject(Params.crsByEpsgCode("EPSG:3857", true));
                query.setFilter(buildCameraFilter());

                SimpleFeatureCollection mypolyCollection = mypolySource.getFeatures(query);

                printInstrumentationSize(mypolyCollection);

                out.println("camera:\r\n" + featureJSON.toString(mypolyCollection));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    /**
     *
     * @param object
     */
    public static void printInstrumentationSize(final Object object)
    {
        out.println(
                "Object of type '" + object.getClass() + "' has size of "
                        + InstrumentationAgent.getObjectSize(object) + " bytes.");
    }


    private static Filter buildCameraFilter() {
        try{
            FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

            Filter filter = CQL.toFilter("code is not null");
            Filter filter1 = CQL.toFilter("code = 'my001'");

            List<Filter> list = new ArrayList<>();
            list.add(filter);
            list.add(filter1);

            Filter finalFilter = filterFactory.and(list);
            return finalFilter;
        }catch (CQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
