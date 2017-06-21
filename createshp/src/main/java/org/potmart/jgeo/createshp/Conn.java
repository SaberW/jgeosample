package org.potmart.jgeo.createshp;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GOT.hodor on 2017/6/21.
 */
public class Conn {

    public static void main(String[] args) {
        Map<String,Object> params = new HashMap<>();
        params.put( "dbtype", "postgis");
        params.put( "host", "localhost");
        params.put( "port", 6432);
        params.put( "schema", "public");
        params.put( "database", "gis_db");
        params.put( "user", "postgres");
        params.put( "passwd", "postgres");

        try{
            DataStore dataStore= DataStoreFinder.getDataStore(params);
            //tablestructlaksjdasdfasdf
            SimpleFeatureType schema = dataStore.getSchema("mypoly");
            CoordinateReferenceSystem crs = schema.getGeometryDescriptor().getCoordinateReferenceSystem();
            if (crs != null){
                Collection<GenericName> alais = crs.getAlias();
                alais.size();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
