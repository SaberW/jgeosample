package org.potmart.jgeo.sample.oldtransform;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Created by  on 2016/12/28.
 */



public class Transer {

    protected static String strWKTMercator = "PROJCS[\"World_Mercator\","
            + "GEOGCS[\"GCS_WGS_1984\","
                + "DATUM[\"WGS_1984\","
                    + "SPHEROID[\"WGS_1984\",6378137,298.257223563]],"
                + "PRIMEM[\"Greenwich\",0],"
                + "UNIT[\"Degree\",0.017453292519943295]],"
                + "PROJECTION[\"Mercator_1SP\"],"
            + "PARAMETER[\"False_Easting\",0],"
            + "PARAMETER[\"False_Northing\",0],"
            + "PARAMETER[\"Central_Meridian\",0],"
            + "PARAMETER[\"latitude_of_origin\",0],"
            + "UNIT[\"Meter\",1]]";

    protected static String epsg3857 = "PROJCS[\"WGS 84 / Pseudo-Mercator\",\n" +
            "    GEOGCS[\"WGS 84\",\n" +
            "        DATUM[\"WGS_1984\",\n" +
            "            SPHEROID[\"WGS 84\",6378137,298.257223563,\n" +
            "                AUTHORITY[\"EPSG\",\"7030\"]],\n" +
            "            AUTHORITY[\"EPSG\",\"6326\"]],\n" +
            "        PRIMEM[\"Greenwich\",0,\n" +
            "            AUTHORITY[\"EPSG\",\"8901\"]],\n" +
            "        UNIT[\"degree\",0.0174532925199433,\n" +
            "            AUTHORITY[\"EPSG\",\"9122\"]],\n" +
            "        AUTHORITY[\"EPSG\",\"4326\"]],\n" +
            "    PROJECTION[\"Mercator_1SP\"],\n" +
            "    PARAMETER[\"central_meridian\",0],\n" +
            "    PARAMETER[\"scale_factor\",1],\n" +
            "    PARAMETER[\"false_easting\",0],\n" +
            "    PARAMETER[\"false_northing\",0],\n" +
            "    UNIT[\"metre\",1,\n" +
            "        AUTHORITY[\"EPSG\",\"9001\"]],\n" +
            "    AXIS[\"X\",EAST],\n" +
            "    AXIS[\"Y\",NORTH],\n" +

            "    AUTHORITY[\"EPSG\",\"3857\"]]";

    protected static String epsg900913 = "PROJCS[\"Google Maps Global Mercator\",\n"+
            "    GEOGCS[\"WGS 84\",\n"+
            "        DATUM[\"WGS_1984\",\n"+
            "            SPHEROID[\"WGS 84\",6378137,298.257223563,\n"+
            "                AUTHORITY[\"EPSG\",\"7030\"]],\n"+
            "            AUTHORITY[\"EPSG\",\"6326\"]],\n"+
            "        PRIMEM[\"Greenwich\",0,\n"+
            "            AUTHORITY[\"EPSG\",\"8901\"]],\n"+
            "        UNIT[\"degree\",0.01745329251994328,\n"+
            "            AUTHORITY[\"EPSG\",\"9122\"]],\n"+
            "        AUTHORITY[\"EPSG\",\"4326\"]],\n"+
            "    PROJECTION[\"Mercator_2SP\"],\n"+
            "    PARAMETER[\"standard_parallel_1\",0],\n"+
            "    PARAMETER[\"latitude_of_origin\",0],\n"+
            "    PARAMETER[\"central_meridian\",0],\n"+
            "    PARAMETER[\"false_easting\",0],\n"+
            "    PARAMETER[\"false_northing\",0],\n"+
            "    UNIT[\"Meter\",1],\n"+

            "    AUTHORITY[\"EPSG\",\"900913\"]]";


    protected static String epsg3395 = "PROJCS[\"WGS 84 / World Mercator\",\n" +
            "    GEOGCS[\"WGS 84\",\n" +
            "        DATUM[\"WGS_1984\",\n" +
            "            SPHEROID[\"WGS 84\",6378137,298.257223563,\n" +
            "                AUTHORITY[\"EPSG\",\"7030\"]],\n" +
            "            AUTHORITY[\"EPSG\",\"6326\"]],\n" +
            "        PRIMEM[\"Greenwich\",0,\n" +
            "            AUTHORITY[\"EPSG\",\"8901\"]],\n" +
            "        UNIT[\"degree\",0.0174532925199433,\n" +
            "            AUTHORITY[\"EPSG\",\"9122\"]],\n" +
            "        AUTHORITY[\"EPSG\",\"4326\"]],\n" +
            "    PROJECTION[\"Mercator_1SP\"],\n" +
            "    PARAMETER[\"central_meridian\",0],\n" +
            "    PARAMETER[\"scale_factor\",1],\n" +
            "    PARAMETER[\"false_easting\",0],\n" +
            "    PARAMETER[\"false_northing\",0],\n" +
            "    UNIT[\"metre\",1,\n" +
            "        AUTHORITY[\"EPSG\",\"9001\"]],\n" +
            "    AXIS[\"Easting\",EAST],\n" +
            "    AXIS[\"Northing\",NORTH],\n" +
            "    AUTHORITY[\"EPSG\",\"3395\"]]";

    protected static String openlayers900913 = "PROJCS[\"WGS84 / Simple Mercator\", GEOGCS[\"WGS 84\",\n" +
            "DATUM[\"WGS_1984\", SPHEROID[\"WGS_1984\", 6378137.0, 298.257223563]], \n" +
            "PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\", 0.017453292519943295], \n" +
            "AXIS[\"Longitude\", EAST], AXIS[\"Latitude\", NORTH]],\n" +
            "PROJECTION[\"Mercator_1SP\"], \n" +
            "PARAMETER[\"latitude_of_origin\", 0.0], PARAMETER[\"central_meridian\", 0.0], \n" +
            "PARAMETER[\"scale_factor\", 1.0], PARAMETER[\"false_easting\", 0.0], \n" +
            "PARAMETER[\"false_northing\", 0.0], UNIT[\"m\", 1.0], AXIS[\"x\", EAST],\n" +
            "AXIS[\"y\", NORTH], AUTHORITY[\"EPSG\",\"900913\"]]";

    public static void main(String[] args) {
        System.out.println("start");

        StringBuffer sb = new StringBuffer();
        sb.append("POINT(")
                .append("120.15 30.17085")
                .append(")");
        StringBuffer sb1 = new StringBuffer();
        sb1.append("POINT(")
                .append("30.17085 120.15")
                .append(")");
        // [13375036.818811823, 3525529.9764870754]
        Geometry geometry = null;
        Geometry geometry1 = null;
        WKTReader wktReader = null;
        GeometryFactory geometryFactory = null;

        geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        wktReader = new WKTReader(geometryFactory);

        try {
            geometry = wktReader.read(sb.toString());

            System.out.println("GEOD坐标 : " + geometry.toString());

            CoordinateReferenceSystem crs = CRS.parseWKT(openlayers900913);

            MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs);

            if (CRS.getAxisOrder(crs) == CRS.AxisOrder.LON_LAT) {
                System.out.println("axis order : lon lat");
            }else {
                System.out.println("axis order : lat lon");
            }

            Geometry target = JTS.transform(geometry, transform);

            String s = target.toString();

            System.out.println("PROJ坐标 : " + s);

            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");

            if (CRS.getAxisOrder(sourceCRS) == CRS.AxisOrder.LON_LAT) {
                System.out.println("axis order : lon lat");
            }else {
                System.out.println("axis order : lat lon");
            }

            geometry1 = wktReader.read(sb1.toString());

            System.out.println("GEOD坐标： " + geometry1.toString());

            MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS, false);

            Geometry target1 = JTS.transform(geometry1, mathTransform);

            String s1 = target1.toString();

            System.out.println("PROJ坐标 : " + s1);



        }catch (ParseException e) {
            e.printStackTrace();

        }catch (Exception e) {
            e.printStackTrace();

        }




    }

}
