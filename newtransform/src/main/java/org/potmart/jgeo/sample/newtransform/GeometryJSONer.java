package org.potmart.jgeo.sample.newtransform;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.factory.Hints;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by GOT.hodor on 2017/3/15.
 */
public class GeometryJSONer {
    public static void main(String[] args) {
        String wkt = "POINT(120.231234 30.241234)";
        GeometryJSON geometryJSON = new GeometryJSON(4);
        try {
            WKTReader wktReader = new WKTReader();
            Geometry geometry = wktReader.read(wkt);

            CoordinateReferenceSystem sourceCRS = readCRSFromEPSGCode("EPSG:4326");
            CoordinateReferenceSystem targetCRS = readCRSFromEPSGCode("EPSG:3857");

            MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS, false);
            Geometry targetGeometry = JTS.transform(geometry, mathTransform);

            StringWriter sw = new StringWriter();

            geometryJSON.write(targetGeometry, sw);

            System.out.println(sw.toString());

        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }catch (FactoryException e){
            e.printStackTrace();
        }catch (TransformException e) {
            e.printStackTrace();
        }

    }

    public static CoordinateReferenceSystem readCRSFromEPSGCode(String code) throws FactoryException {
        Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
        CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem(code);
        return crs;
    }
}
