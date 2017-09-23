package org.potmart.jgeo.createshp;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.data.*;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.transform.Definition;
import org.geotools.data.transform.TransformFactory;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.FeatureBuilder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.collection.AbstractFeatureVisitor;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.feature.visitor.FeatureAttributeVisitor;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geojson.GeoJSON;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.geotools.sql.SqlUtil;
import org.geotools.util.NullProgressListener;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by GOT.hodor on 2017/6/21.
 */
public class Conn {

    public static void main(String[] args) {
        Map<String,Object> params = new HashMap<>();
        params.put( "dbtype", "postgis");
        params.put( "host", "127.0.0.1");
        params.put( "port", 6432);
        params.put( "schema", "public");
        params.put( "database", "gis_db");
        params.put( "user", "postgres");
        params.put( "passwd", "postgres");

        try{
            DataStore dataStore= DataStoreFinder.getDataStore(params);
            //tablestructlaksjdasdfasdf
            SimpleFeatureType schemaMypoint = dataStore.getSchema("mypoint");
            CoordinateReferenceSystem crs = schemaMypoint.getGeometryDescriptor().getCoordinateReferenceSystem();
            if (crs != null){
                Collection<GenericName> alais = crs.getAlias();
                alais.size();
            }

            SimpleFeatureSource featureSource = dataStore.getFeatureSource("mypoint");

            /*
            Query query = new Query();
            String cql = "workspace='egms' and code='egms_110'";
            try{
                Filter filter = CQL.toFilter(cql);
                query.setFilter(filter);

                SimpleFeatureCollection collection = featureSource.getFeatures(query);
                FeatureJSON featureJSON = new FeatureJSON();
                String json = featureJSON.toString(collection);
                System.out.println(json);
            }catch (CQLException e) {

            }
            */
            ListFeatureCollection lfc = new ListFeatureCollection(schemaMypoint);

            for (int i=0; i < 30000; i++) {
                SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(schemaMypoint);
                SimpleFeature feature = sfb.buildFeature(null);
                feature.setAttribute("code", "0000"+i);
                feature.setAttribute("name", "0000"+i);
                Point geom = (Point) new WKTReader().read("POINT(120 30)");
                feature.setDefaultGeometry(geom);
                lfc.add(feature);
            }

            Date date1 = new Date();
            SimpleFeatureStore simpleFeatureStore = (SimpleFeatureStore)featureSource;
            Transaction t = new DefaultTransaction("add point");
            try{
                t.putProperty("hints", 10);
                simpleFeatureStore.setTransaction(t);
                simpleFeatureStore.addFeatures(lfc);
                List<FeatureId> list = ((SimpleFeatureStore)featureSource).addFeatures(lfc);
                list.size();
                Date date2 = new Date();
                t.commit();
                System.out.println("插入耗时:" + (date2.getTime()-date1.getTime()) + "ms");
            }catch (IOException e){
                t.rollback();
            }finally {
                t.close();
            }


            /*
            //region 创建表
            String name = "camera";
            if (!Arrays.asList(dataStore.getTypeNames()).contains(name)) {
                try{

                    Name schemaName = new NameImpl(name);
                    final AttributeTypeBuilder attrTypeBuilder = new AttributeTypeBuilder();
                    List<AttributeDescriptor> descriptorList = new LinkedList<>();

                    //几何字段
                    attrTypeBuilder.setNillable(false);
                    attrTypeBuilder.setName("geom");
                    attrTypeBuilder.setBinding(Point.class);
                    attrTypeBuilder.setCRS(CRS.decode("EPSG:4326"));
                    GeometryType geometryType = attrTypeBuilder.buildGeometryType();

                    GeometryDescriptor geometryDescriptor = attrTypeBuilder.buildDescriptor("geom", geometryType);
                    descriptorList.add(geometryDescriptor);

                    //属性字段
                    // name
                    attrTypeBuilder.setName("name");
                    attrTypeBuilder.setBinding(String.class);
                    attrTypeBuilder.setNillable(false);
                    attrTypeBuilder.setDescription("名称");
                    AttributeType nameType = attrTypeBuilder.buildType();
                    AttributeDescriptor nameDescriptor = attrTypeBuilder.buildDescriptor("name", nameType);
                    descriptorList.add(nameDescriptor);

                    //id
                    attrTypeBuilder.setName("id");
                    attrTypeBuilder.setBinding(Long.class);
                    attrTypeBuilder.setNillable(false);
                    attrTypeBuilder.setDescription("名称");
                    AttributeType idType = attrTypeBuilder.buildType();
                    AttributeDescriptor idDescriptor = attrTypeBuilder.buildDescriptor("id", idType);
                    descriptorList.add(idDescriptor);

                    //创建表
                    SimpleFeatureType schema = new SimpleFeatureTypeImpl(schemaName, descriptorList, geometryDescriptor,
                            false, null, null, null);
                    dataStore.createSchema(schema);

                    //为id字段设置唯一约束
                    Transaction t = new DefaultTransaction("handle");
                    t.putProperty("hint", 7);
                    try{
                        String sql = "ALTER TABLE camera ADD CONSTRAINT camera_unique_id UNIQUE (id);";
                        JDBCDataStore jdbcDataStore = (JDBCDataStore)dataStore;
                        SqlUtil.PreparedStatementBuilder preparedStatementBuilder =
                                SqlUtil.prepare(jdbcDataStore.getConnection(t), sql);
                        PreparedStatement preparedStatement = preparedStatementBuilder.statement();
                        preparedStatement.execute();
                        t.commit();
                    }catch (SQLException e){
                        t.rollback();
                    }finally {
                        t.close();
                    }

                }catch (FactoryException fe){

                }
            }
            //endregion


            //原字段名
            String fieldId = "id";
            String fieldName = "name";
            String fieldGeom = "geom";
            //转义字段名
            String defId = "identify";
            String defName = "alias";
            String defGeom = "geom";

            //配置转义
            Expression expressionId = CQL.toExpression(fieldId);
            Expression expressionName = CQL.toExpression(fieldName);
            Expression expressionGeom = CQL.toExpression(fieldGeom);
            Definition definitionId = new Definition(defId, expressionId);
            Definition definitionName = new Definition(defName, expressionName);
            Definition definitionGeom = new Definition(defGeom, expressionGeom);
            List<Definition> definitionList = new ArrayList<>();
            definitionList.add(definitionId);
            definitionList.add(definitionName);
            definitionList.add(definitionGeom);

            //转义
            SimpleFeatureSource cameraFeatureSource = dataStore.getFeatureSource("camera");
            SimpleFeatureSource defCameraFeatureSource =
                    TransformFactory.transform(cameraFeatureSource,
                            "def_camera", definitionList);

            Filter filter = Filter.INCLUDE;
            SimpleFeatureCollection cameras = cameraFeatureSource.getFeatures(filter);
            SimpleFeatureCollection defCameras = defCameraFeatureSource.getFeatures(filter);
            FeatureJSON featureJSON = new FeatureJSON();
            String cameraJSON = featureJSON.toString(cameras);
            System.out.println("原始：\r\n" + cameraJSON);
            String defCameraJSON = featureJSON.toString(defCameras);
            System.out.println("转义：\r\n" + defCameraJSON);

            FeatureCollection bufferCollection = DataUtilities.collection(cameras);
            bufferCollection.accepts(new AbstractFeatureVisitor() {
                @Override
                public void visit(Feature feature) {
                    Geometry geometry = (Geometry) feature.getDefaultGeometryProperty().getValue();
                    Geometry sGeometry;
                    sGeometry = geometry.buffer(100, 8, 1);
                    Iterator iterator = feature.getValue().iterator();
                    while (iterator.hasNext()) {
                        Property property = (Property) iterator.next();
                        if (property.getValue() instanceof Geometry) {
                            property.setValue(sGeometry);
                        }
                    }
                    feature.getDefaultGeometryProperty().setValue(sGeometry);
                }
            }, new NullProgressListener());

            System.out.println("原始：\r\n" + cameraJSON);
            String bufferJSON = featureJSON.toString(bufferCollection);
            System.out.println("缓冲：\r\n" + bufferJSON);

            SimpleFeatureSource deleteSource = dataStore.getFeatureSource("camera");
            Filter deleteFilter = CQL.toFilter("id=2");
            ((SimpleFeatureStore)deleteSource).removeFeatures(deleteFilter);
            */

        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }
        /*
        catch (CQLException e){
            e.printStackTrace();
        }
        */

    }

}
