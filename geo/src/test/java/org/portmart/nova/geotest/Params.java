package org.portmart.nova.geotest;

import org.geotools.factory.Hints;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GOT.hodor on 2017/9/25.
 */
public class Params {

    /**
     *
     * @return
     */
    public static Map<String, Object> connParams() {
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

    /**
     *
     * @param code
     * @param longitudeFirst
     * @return
     */
    public static CoordinateReferenceSystem crsByEpsgCode(String code, boolean longitudeFirst) {
        try{
            Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, longitudeFirst);
            CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
            return factory.createCoordinateReferenceSystem(code);
        }catch (FactoryException e) {
            e.printStackTrace();
        }
        return null;
    }
}
