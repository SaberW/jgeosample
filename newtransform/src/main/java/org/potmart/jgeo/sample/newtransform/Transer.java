package org.potmart.jgeo.sample.newtransform;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Created by on 2016/12/30.
 */
public class Transer
{
    public static void main(String[] args) {
        try {
            //创建wkt
            StringBuffer sb = new StringBuffer();
            sb.append("POINT(")
                    .append("30 120")
                    .append(")");

            //创建工厂、reader、原始geometry
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
            WKTReader wktReader = new WKTReader(geometryFactory);

            Geometry geometry = wktReader.read(sb.toString());

            //创建coordinate reference system
            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");

            //检查经纬度谁在前面
            if (CRS.getAxisOrder(sourceCRS) == CRS.AxisOrder.LON_LAT) {
                System.out.println("axis order : lon lat");
            }else {
                System.out.println("axis order : lat lon");
            }

            //转换坐标
            MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
            Geometry targetGeometry = JTS.transform(geometry, mathTransform);

            System.out.println("target : " + targetGeometry.toString());

        }catch (ParseException pe) {
            pe.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
