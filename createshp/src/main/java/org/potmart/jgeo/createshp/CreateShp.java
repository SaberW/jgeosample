package org.potmart.jgeo.createshp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.factory.Hints;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 2017/3/12.
 */
public class CreateShp {

    public static void main(String[] args) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("fid", "Long");
        fields.put("name", "String");
        fields.put("type", "String");
        fields.put("the_geom", "Point");
        write("F:/MapWorkspace/shp/myshp/forest.shp", "EPSG:4326");
    }

    public static void write2(String filepath,
                             String name ,
                             String crs,
                             Map<String, String> fields,
                             String charset,
                             long rows) {

    }

    public static void write(String filepath, String crsCode) {
        try {
            //创建shape文件对象
            File file = new File(filepath);
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put( ShapefileDataStoreFactory.URLP.key, file.toURI().toURL() );
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);

            //定义图形信息和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            tb.add("the_geom", Point.class);
            tb.add("fid", Long.class);
            tb.add("name", String.class);
            tb.add("type", String.class);
            tb.add("coord_x", Double.class);
            tb.add("coord_y", Double.class);
            CoordinateReferenceSystem coordinateReferenceSystem = readCRSFromEPSGCode(crsCode);
            tb.setCRS(coordinateReferenceSystem);
            tb.setName("forest");
            ds.createSchema(tb.buildFeatureType());
            ds.setCharset(Charset.forName("utf-8"));

            double[] bound3857 = {-20037508.34,-20037508.34,20037508.34,20037508.34};
            double[] bound4326 = {-180, -90, 90, 180};

            //设置Writer
            String[] trees = {"水杉-乔木", "白桦-乔木", "香樟-乔木", "油松-乔木", "其他-灌木"};
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT.AUTO_COMMIT);
            //写下一条
            long max = 1000000;
            for (long i=0; i < max; i++) {
                SimpleFeature feature = writer.next();
                double x = 0;
                double y = 0;
                if (crsCode.equals("EPSG:4326")){
                    //region world wide
                    /*
                    x = -180 + Math.random() * 360;
                    y = -80 + Math.random() * 160;
                    */
                    //endregion

                    //region  china x 73 - 135  y 17 - 54
                    x = 73 + Math.random() * 62;
                    y = 17 + Math.random() * 37;
                    //endregion
                }else {
                    x = -20037508.34 + Math.random()*400000000;
                    y = -20037508.34 + Math.random()*400000000;
                }

                feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(x, y)));
                feature.setAttribute("fid", i);
                Double treeIdx = Double.parseDouble("" + Math.floor(Math.random()*5));
                String tree = trees[treeIdx.intValue()];
                String[] treeStr = tree.split("-");
                feature.setAttribute("name", treeStr[0]+i);
                feature.setAttribute("type", treeStr[1]);
                feature.setAttribute("coord_x", x);
                feature.setAttribute("coord_y", y);
            }
            /*
            SimpleFeature feature = writer.next();
            feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(116.123, 39.345)));
            feature.setAttribute("fid", 1234567890l);
            feature.setAttribute("name", "点");
            feature = writer.next();
            feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(116.456, 39.678)));
            feature.setAttribute("fid", 1234567891l);
            feature.setAttribute("name", "点2");
            */
            writer.write();
            writer.close();
            ds.dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CoordinateReferenceSystem readCRSFromEPSGCode(String code) throws FactoryException {
        Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
        CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem(code);
        return crs;
    }

    public static Class fieldValueClassType(String typeClass) {
        switch (typeClass) {
            case "String":
                return String.class;
            case "Integer":
                return Integer.class;
            case "Long":
                return Long.class;
            case "Point":
                return Point.class;
            default:
                return String.class;
        }
    }
}
