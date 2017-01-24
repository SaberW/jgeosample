package org.potmart.jgeo.sample.newtransform;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GOT.hodor on 2017/1/9.
 */
public class Conn {

    public static void main(String[] args) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put( "dbtype", "postgis");
        params.put( "host", "localhost");
        params.put( "port", 6432);
        params.put( "schema", "public");
        params.put( "database", "database");
        params.put( "user", "postgres");
        params.put( "passwd", "postgres");

        try{
            DataStore store = DataStoreFinder.getDataStore(params);
            if (store == null) {
                System.out.println("data store = null");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
