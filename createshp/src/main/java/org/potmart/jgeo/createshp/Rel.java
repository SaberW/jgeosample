package org.potmart.jgeo.createshp;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.geotools.data.*;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.store.ContentFeatureStore;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCFeatureSource;
import org.geotools.jdbc.JDBCInsertFeatureWriter;
import org.geotools.referencing.CRS;
import org.geotools.sql.SqlUtil;
import org.h2.tools.Server;
import org.hsqldb.jdbc.JDBCPreparedStatement;
import org.hsqldb.result.Result;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.potmart.jgeo.createshp.table.Field;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.IntStream;

/**
 * Created by GOT.hodor on 2017/8/22.
 */
public class Rel {

    public static void main(String[] args) {
        //region 描述
        /**
         * <p>
         *     H2GIS围栏
         *     1.连接H2
         *     2.创建围栏表
         *          表名称：
         *              不能重复，最好以子系统的名称为前缀；
         *          表配置:
         *              字段、字段类型、主键
         *              字段推荐：
         *                  id -- 唯一约束的键
         *                  workspace -- 专题地图的工作空间
         *                  code -- 专题地图的编码
         *     3.创建动态位置表
         *          表名称:
         *              不能重复，最好以子系统的名称为前缀
         *          表配置：
         *              字段、字段类型、主键
         *              字段推荐：
         *                  id -- 唯一约束的键
         *                  workspace -- 专题地图的工作空间
         *                  code -- 专题地图的编码
         *
         *
         *     4.添加围栏数据
         *          参数：
         *              围栏表名
         *
         *     5.添加动态数据并给出判断结果
         *          指定接口名称
         * </p>
         */
        //endregion
        try{
            //region 连接h2gis
            /**
             * <p>
             *     db type -> h2
             *     database -> 数据库名称
             * </p>
             */
            Date date1 = new Date();
            Server h2server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();



            Map<String,Object> params = new HashMap<>();
            params.put("dbtype", "h2");
            params.put("host", "localhost");
            params.put("port", 9092);
            params.put("database", "some");
            params.put("username", "some");
            params.put("passwd", "some");

            DataStore dataStore = connnectDataStore(params);
            printSeconds(date1, "连接:");
            //endregion

            //region 建表
            Date date2 = new Date();
            String barTableName = "BAR";
            String dynamicTableName = "DYNAMIC_POINT";
            buildTable(dataStore, barTableName, dynamicTableName);
            printSeconds(date2, "建表：");
            //endregion

            outOfBar2(dataStore, barTableName, dynamicTableName);

            /*
            //region 构建数据
            Date date3 = new Date();
            buildData(dataStore, barTableName, dynamicTableName);
            printSeconds(date3, "构建数据：");

            //endregion

            //region 判断
            Date date4 = new Date();
            Map<String, FeatureCollection> result = outOfBar(dataStore, barTableName, dynamicTableName);
            printSeconds(date4, "判断位置：");
            */

            /*
            Date date5 = new Date();
            FeatureJSON featureJSON = new FeatureJSON();
            for (Map.Entry entry : result.entrySet()) {
                if (entry.getValue() != null) {
                    FeatureCollection featureCollection = (FeatureCollection) entry.getValue();
                    System.out.println((String) entry.getKey());
                    System.out.println(featureJSON.toString(featureCollection));
                    System.out.println("-------------------------------------------------------");
                }
            }
            printSeconds(date5, "输出结果：");
            */
            //endregion

            h2server.shutdown();
        }catch (SQLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (FactoryException e) {
            e.printStackTrace();
        }
    }

    //region private

    /**
     * bar fields
     * @return
     */
    private static LinkedList<Field> gatherBarFields() {
        LinkedList<Field> fields = new LinkedList<>();

        String[] names = {"id", "name", "workspace", "code"};
        String[] types = {"long", "string", "string", "string"};

        for (int i=0;i<names.length;i++) {
            String name = names[i];
            String type = types[i];

            Field field = new Field();
            field.setName(name);
            field.setType(type);
            field.setLength(32);
            field.setNillable(false);

            fields.add(field);
        }

        return fields;
    }

    /**
     * dynamic point fields
     * @return
     */
    private static LinkedList<Field> gatherDynamicPointFields() {
        LinkedList<Field> fields = new LinkedList<>();

        String[] names = {"id", "name", "workspace", "code", "date"};
        String[] types = {"long", "string", "string", "string", "date"};

        for (int i=0;i<names.length;i++) {
            String name = names[i];
            String type = types[i];

            Field field = new Field();
            field.setName(name);
            field.setType(type);
            field.setLength(32);
            field.setNillable(false);

            fields.add(field);
        }

        return fields;
    }

    /**
     * bar
     * @param schema
     * @return
     */
    private static FeatureCollection barFeatures(SimpleFeatureType schema) {
        Date date1 = new Date();
        ListFeatureCollection featureCollection = new ListFeatureCollection(schema);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema);
        IntStream.range(0, 200)
                .forEach(i->{
                    featureBuilder.set("id", i);
                    featureBuilder.set("name", "name" + i);
                    featureBuilder.set("workspace", "workspace"+i);
                    featureBuilder.set("code", "code" + i);

                    StringBuilder wkt = new StringBuilder();
                    int x1 = 0 + i * 100;
                    int y1 = 0 + i * 100;
                    int x2 = 50 + i * 100;
                    int y2 = 0 + i * 100;
                    int x3 = 50 + i * 100;
                    int y3 = 50 + i * 100;
                    int x4 = 0 + i * 100;
                    int y4 = 50 + i * 100;
                    wkt.append("POLYGON((")
                            .append(x1).append(" ").append(y1).append(",")
                            .append(x2).append(" ").append(y2).append(",")
                            .append(x3).append(" ").append(y3).append(",")
                            .append(x4).append(" ").append(y4).append(",")
                            .append(x1).append(" ").append(y1)
                        .append("))");
                    featureBuilder.set("geom", wktToGeom(wkt.toString()));

                    SimpleFeature feature = featureBuilder.buildFeature("id"+i);
                    featureCollection.add(feature);
                });

        printSeconds(date1, "构建bar数据:");
        return featureCollection;
    }

    /**
     * dynamic point feature
     * @param schema
     * @return
     */
    private static FeatureCollection dynamicFeatures(SimpleFeatureType schema) {
        Date date1 = new Date();
        ListFeatureCollection featureCollection = new ListFeatureCollection(schema);

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema);
        IntStream.range(0, 10000)
                .forEach(i->{
                    int m = i % 200;
                    featureBuilder.set("id", i);
                    featureBuilder.set("name", "name" + i);
                    featureBuilder.set("workspace", "workspace" + m);
                    featureBuilder.set("code", "code" + m);
                    featureBuilder.set("date", new Date());

                    StringBuilder wkt = new StringBuilder();
                    int x = Double.valueOf(Math.random() * 100).intValue() + m * 100;
                    int y = Double.valueOf(Math.random() * 100).intValue() + m * 100;
                    wkt.append("POINT(")
                            .append(x).append(" ").append(y)
                            .append(")");
                    featureBuilder.set("geom", wktToGeom(wkt.toString()));


                    SimpleFeature feature = featureBuilder.buildFeature("id"+i);
                    featureCollection.add(feature);

                });

        printSeconds(date1, "构建dynamic数据:");
        return featureCollection;
    }

    private static void buildTable(DataStore dataStore, String barTableName, String dynamicTableName) throws IOException, FactoryException {
        /**
         * <p>
         *     1.判断表名是否存在
         *     2.表名不存在先创建表
         * </p>
         *
         */

        //region Bar
        String[] tables = dataStore.getTypeNames();
        boolean isExist = Arrays.asList(tables).contains(barTableName);
        if (!isExist) {
            LinkedList<Field> barFields = gatherBarFields();
            Field barGeometryField = new Field();
            barGeometryField.setName("geom");
            barGeometryField.setType("polygon");

            createTable(dataStore, barTableName, "EPSG:3857", barFields, barGeometryField, barFields.get(0));
        }

        //endregion

        //region Dynamic Point
        String[] dynamicTables = dataStore.getTypeNames();
        boolean isDynamicExist = Arrays.asList(dynamicTables).contains(dynamicTableName);
        if (!isDynamicExist) {
            LinkedList<Field> dynamicFields = gatherDynamicPointFields();
            Field dynamicGeometry = new Field();
            dynamicGeometry.setName("geom");
            dynamicGeometry.setType("point");

            createTable(dataStore, dynamicTableName, "EPSG:3857", dynamicFields, dynamicGeometry, dynamicFields.get(0));
        }

        //endregion
    }

    private static void buildData(DataStore dataStore, String barTableName, String dynamicTableName) throws IOException {
        //region bar features
        SimpleFeatureType barSchema = dataStore.getSchema(barTableName);

        if (barSchema != null) {
            truncateTable(dataStore, barTableName);

            FeatureSource barFeatureSource = dataStore.getFeatureSource(barTableName);

            FeatureCollection bars = barFeatures(barSchema);
            Date date1 = new Date();
            ((ContentFeatureStore)barFeatureSource).addFeatures(bars);
            printSeconds(date1, "bar插入数据：");
        }

        //endregion

        //region dynamic features
        SimpleFeatureType dynamicSchema = dataStore.getSchema(dynamicTableName);
        if (dynamicSchema != null) {
            truncateTable(dataStore, dynamicTableName);

            SimpleFeatureStore dynamicFeatureSource = (SimpleFeatureStore) dataStore.getFeatureSource(dynamicTableName);

            FeatureCollection dynamics = dynamicFeatures(dynamicSchema);
            Date date2 = new Date();
            //((ContentFeatureStore)dynamicFeatureSource).addFeatures(dynamics);


            /*
            Transaction t = new DefaultTransaction("add dynamic");
            try{
                t.putProperty("hints", 9);
                dynamicFeatureSource.setTransaction(t);
                dynamicFeatureSource.addFeatures(dynamics);
                t.commit();
            }catch (IOException f) {
                t.rollback();
            }finally {
                t.close();
            }
            */


            prepareBatch(dataStore, dynamics);



            printSeconds(date2, "dynamic插入数据：");
        }

        //endregion
    }

    /**
     * 全量比较
     * @param dataStore
     * @param bar
     * @param dynamic
     * @return
     */
    private static Map<String, FeatureCollection> outOfBar(DataStore dataStore, String bar, String dynamic) {
        Map<String, FeatureCollection> result = new HashMap<>();

        try{
            SimpleFeatureSource barFeatureSource = dataStore.getFeatureSource(bar);
            SimpleFeatureCollection barFeatureCollection = barFeatureSource.getFeatures(Filter.INCLUDE);

            SimpleFeatureSource dynamicFeatureSource = dataStore.getFeatureSource(dynamic);

            WKTWriter wktWriter = new WKTWriter();

            SimpleFeatureIterator barIterator =  barFeatureCollection.features();
            while (barIterator.hasNext()) {
                SimpleFeature barFeature = barIterator.next();
                String workspace = (String) barFeature.getAttribute("workspace");
                String code = (String)barFeature.getAttribute("code");

                Geometry geometry = (Geometry)barFeature.getAttribute("geom");

                if (geometry != null) {
                    String wkt = wktWriter.write(geometry);

                    StringBuilder sb = new StringBuilder();
                    sb.append("DISJOINT(geom, ")
                            .append(wkt)
                            .append(")")
                            .append(" and workspace='")
                            .append(workspace)
                            .append("'")
                            .append(" and code='")
                            .append(code)
                            .append("'")
                    ;

                    try{
                        SimpleFeatureCollection featureCollection = dynamicFeatureSource.getFeatures(CQL.toFilter(sb.toString()));
                        result.put(workspace + ":" + code, featureCollection);

                    }catch (CQLException e) {
                        result.put(workspace + ":" + code, null);
                        e.printStackTrace();
                    }
                }else{
                    result.put(workspace + ":" + code, null);
                }
            }
            barIterator.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 直接循环的方式判断
     * @param dataStore
     * @param bar
     * @param dynamic
     * @return
     */
    private static Map<String, FeatureCollection> outOfBar2(DataStore dataStore, String bar, String dynamic) {
        try{
            SimpleFeatureType barSchema = dataStore.getSchema("BAR");
            SimpleFeatureType dynamicSchema = dataStore.getSchema("DYNAMIC_POINT");

            FeatureCollection barFC = barFeatures(barSchema);
            FeatureCollection dynamicFC = dynamicFeatures(dynamicSchema);

            Date date1 = new Date();
            Map<String, Feature> barFeatureMap = new HashMap<>();
            Map<String, ListFeatureCollection> resultMap = new HashMap<>();

            FeatureIterator iterator = barFC.features();

            while (iterator.hasNext()) {
                Feature barFeature = iterator.next();

                StringBuilder sb = new StringBuilder();
                sb.append((String)barFeature.getProperty("workspace").getValue())
                        .append(":")
                        .append((String)barFeature.getProperty("code").getValue());

                barFeatureMap.put(sb.toString(), barFeature);

                if (!resultMap.containsKey(sb.toString())) {
                    resultMap.put(sb.toString(), new ListFeatureCollection(dynamicSchema));
                }
            }

            iterator.close();

            FeatureIterator dyIterator = dynamicFC.features();

            while (dyIterator.hasNext()) {
                SimpleFeature dyFeature = (SimpleFeature) dyIterator.next();
                StringBuilder sb = new StringBuilder();
                sb.append((String)dyFeature.getProperty("workspace").getValue())
                        .append(":")
                        .append((String)dyFeature.getProperty("code").getValue());
                Feature barFeature = barFeatureMap.get(sb.toString());

                Geometry barGeometry = (Geometry) barFeature.getDefaultGeometryProperty().getValue();
                Geometry dyGeometry = (Geometry)dyFeature.getDefaultGeometryProperty().getValue();

                if (!barGeometry.contains(dyGeometry)) {
                    ListFeatureCollection outFC = resultMap.get(sb.toString());
                    outFC.add(dyFeature);
                }
            }

            dyIterator.close();
            printSeconds(date1, "判断：");

            /*
            FeatureJSON featureJSON = new FeatureJSON();
            for (Map.Entry res : resultMap.entrySet()) {
                System.out.println((String) res.getKey());
                String s = featureJSON.toString((ListFeatureCollection) res.getValue());
                System.out.println(s);
            }
            */

        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void printSeconds(Date date1, String desc) {
        Date date2 = new Date();
        StringBuilder sb = new StringBuilder();
        sb.append(desc)
                .append(date2.getTime() - date1.getTime())
                .append("ms");
        System.out.println(sb.toString());
    }
    //endregion

    //region public
    //region 连接
    /**
     * 建立连接
     * @param params
     * @return
     * @throws IOException
     */
    public static DataStore connnectDataStore(Map<String, Object> params) throws IOException {
        return DataStoreFinder.getDataStore(params);
    }
    //endregion

    //region 建表

    /**
     * schema
     * @return
     */
    public static SimpleFeatureType createSchema(DataStore dataStore, String tableName, String crsCode,
                                                 LinkedList<Field> fieldList, Field geometry, Field unique) {
        //表名是否存在
        try {
            String[] tables = dataStore.getTypeNames();
            boolean isExist = Arrays.asList(tables).contains(tableName);

            //唯一字段是否包含在属性中
            if (unique != null) {
                if (!fieldList.contains(unique)) {
                    return null;
                }
            }

            //坐标系
            CoordinateReferenceSystem crs = CRS.decode(crsCode);

            if (!isExist) {
                LinkedList<AttributeDescriptor> attributeDescriptors = new LinkedList<>();

                AttributeTypeBuilder attributeTypeBuilder = new AttributeTypeBuilder();
                fieldList.stream()
                        .forEach(field -> {
                            attributeTypeBuilder.setName(field.getName());
                            Class clazz = fieldTypeToBingding(field.getType());
                            attributeTypeBuilder.setBinding(clazz);
                            if (clazz.equals(String.class)) {
                                attributeTypeBuilder.setLength(field.getLength());
                            }
                            if (clazz.equals(Integer.class)) {
                                attributeTypeBuilder.setMaxOccurs(field.getMax());
                                attributeTypeBuilder.setMinOccurs(field.getMin());
                            }
                            attributeTypeBuilder.setNillable(field.isNillable());
                            if (field.getDescription() != null) {
                                attributeTypeBuilder.setDescription(field.getDescription());
                            }
                            if (field.getDefaultValue() != null) {
                                attributeTypeBuilder.setDefaultValue(field.getDefaultValue());
                            }

                            AttributeType attributeType = attributeTypeBuilder.buildType();
                            AttributeDescriptor attributeDescriptor = attributeTypeBuilder.buildDescriptor(field.getName(), attributeType);
                            attributeDescriptors.add(attributeDescriptor);
                        });

                Name geometryName = new NameImpl(geometry.getName());
                attributeTypeBuilder.setCRS(crs);
                attributeTypeBuilder.setName(geometry.getName());
                attributeTypeBuilder.setBinding(fieldTypeToBingding(geometry.getType()));
                attributeTypeBuilder.setNillable(geometry.isNillable());
                if (geometry.getDescription() != null) {
                    attributeTypeBuilder.setDescription(geometry.getDescription());
                }
                if (geometry.getDefaultValue() != null) {
                    attributeTypeBuilder.setDefaultValue(geometry.getDefaultValue());
                }
                GeometryType geometryType = attributeTypeBuilder.buildGeometryType();
                GeometryDescriptor geometryDescriptor = attributeTypeBuilder.buildDescriptor(geometryName, geometryType);

                attributeDescriptors.add(geometryDescriptor);

                Name schemaName = new NameImpl(tableName);
                SimpleFeatureType simpleFeatureType =
                        new SimpleFeatureTypeImpl(schemaName, attributeDescriptors, geometryDescriptor,
                                false, null, null, null);
                return simpleFeatureType;
            }
        }catch (IOException e) {

        }catch (FactoryException e) {

        }

        return null;
    }

    /**
     * 建表
     * @param dataStore 数据库连接对线
     * @param tableName 表名
     * @param fieldList 字段
     * @return
     * @throws IOException
     */
    public static boolean createTable(DataStore dataStore, String tableName, String crsCode,
                                      LinkedList<Field> fieldList, Field geometry, Field unique)
            throws IOException, FactoryException {

        if (dataStore != null){
            SimpleFeatureType simpleFeatureType = createSchema(dataStore, tableName, crsCode, fieldList, geometry, unique);
            if (simpleFeatureType != null) {
                dataStore.createSchema(simpleFeatureType);
                return true;
            }

        }
        return false;
    }
    //endregion

    //region binding
    /**
     * Binding
     * @param type
     * @return
     */
    public static Class fieldTypeToBingding(String type) {
        switch (type) {
            case "string":
                return String.class;
            case "long" :
                return Long.class;
            case "integer" :
                return Integer.class;
            case "double" :
                return Double.class;
            case "float":
                return Float.class;
            case "date":
                return Date.class;
            case "point":
                return Point.class;
            case "multipoint":
                return MultiPoint.class;
            case "linestring" :
                return LineString.class;
            case "multilinestring":
                return MultiLineString.class;
            case "polygon":
                return Polygon.class;
            case "multipolygon":
                return MultiPolygon.class;
            case "geometry":
                return Geometry.class;
            case "envelope":
                return Envelope.class;
            default:
                return Object.class;
        }
    }
    //endregion

    //region 删表
    /**
     * 删表
     * @param dataStore
     * @param tableName
     * @return
     * @throws IOException
     */
    public static boolean deleteTable(DataStore dataStore, String tableName) throws IOException {
        String[] tables = dataStore.getTypeNames();
        boolean isExist = Arrays.asList(tables).contains(tableName);

        if (!isExist) {
            return false;
        }else{
            dataStore.removeSchema(tableName);
            return true;
        }
    }
    //endregion

    //region 清空数据
    public static boolean truncateTable(DataStore dataStore, String tableName) {

        try{
            String[] tableNames = dataStore.getTypeNames();
            if (!Arrays.asList(tableNames).contains(tableName)){
                return false;
            }

            Date date1 = new Date();
            Transaction t = new DefaultTransaction("handle");
            try{
                JDBCDataStore jdbcDataStore = (JDBCDataStore)dataStore;
                t.putProperty("hint", new Integer(8));

                StringBuilder sql = new StringBuilder();

                sql.append("truncate table ")
                        .append(tableName);
                SqlUtil.PreparedStatementBuilder preparedStatementBuilder = SqlUtil.prepare(jdbcDataStore.getConnection(t), sql.toString());
                PreparedStatement preparedStatement = preparedStatementBuilder.statement();
                preparedStatement.execute();
                t.commit();
                printSeconds(date1, tableName + " 清除数据：");
                return true;
            }catch (SQLException e) {
                t.rollback();
            }catch (IOException e) {
                t.rollback();
            }finally {
                t.close();
            }


        }catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //endregion

    //region wkt转几何

    /**
     *
     * @param wkt
     * @return
     */
    public static Geometry wktToGeom(String wkt) {
        try{
            WKTReader wktReader = new WKTReader();
            Geometry geometry = wktReader.read(wkt);
            if (geometry != null) {
                geometry.setSRID(3857);
                return geometry;
            }
            return null;
        }catch (ParseException e) {
            return null;
        }
    }
    //endregion

    /**
     * prepare batch feature collection
     * @param dataStore
     * @param featureCollection
     */
    public static void prepareBatch(DataStore dataStore, FeatureCollection featureCollection) {
        JDBCDataStore jdbcDataStore = (JDBCDataStore)dataStore;
        Transaction t = new DefaultTransaction();
        PreparedStatement preparedStatement = null;
        try{
            FeatureIterator iterator = null;
            try{
                String sql = "insert into DYNAMIC_POINT (id, name, workspace, code, date, geom) values (?,?,?,?,?,?)";
                SqlUtil.PreparedStatementBuilder preparedStatementBuilder = SqlUtil.prepare(jdbcDataStore.getConnection(t), sql);
                preparedStatement = preparedStatementBuilder.statement();
                iterator = featureCollection.features();
                WKBWriter wkbWriter = new WKBWriter();
                while (iterator.hasNext()) {
                    Feature feature = iterator.next();
                    preparedStatement.setLong(1, (Long) feature.getProperty("id").getValue());
                    preparedStatement.setString(2, (String) feature.getProperty("name").getValue());
                    preparedStatement.setString(3, (String) feature.getProperty("workspace").getValue());
                    preparedStatement.setString(4, (String) feature.getProperty("code").getValue());
                    preparedStatement.setDate(5, (java.sql.Date) feature.getProperty("date").getValue());

                    Geometry geometry = (Geometry) feature.getDefaultGeometryProperty().getValue();
                    byte[] wkb = wkbWriter.write(geometry);
                    preparedStatement.setBytes(6, wkb);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                t.commit();
            }catch (IOException e){
                t.rollback();
            }finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (iterator != null) {
                    iterator.close();
                }
                t.close();
            }

        }catch (IOException e){
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     *  geo tools statement
     * @param dataStore
     * @param featureCollection
     */
    public static void geotoolsPrepare(DataStore dataStore, FeatureCollection featureCollection) {
        JDBCDataStore jdbcDataStore = (JDBCDataStore)dataStore;
        Transaction t = new DefaultTransaction();
        PreparedStatement preparedStatement = null;
        try{
            try {
                String sql = "insert into DYNAMIC_POINT (id, name, workspace, code, date, geom) values (?,?,?,?,?,?)";

                String sql2 = "SELECT * FROM DYNAMIC_POINT  WHERE ID > 0";
                Connection connection = jdbcDataStore.getConnection(t);
                preparedStatement = connection.prepareStatement(sql2);
                if (preparedStatement != null) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    System.out.println("some query");
                    return;
                }
            }catch (SQLException e) {
                t.rollback();
            }catch (IOException e) {
                t.rollback();
            }finally {
                t.close();
            }

        }catch (IOException e){

        }
    }
    //endregion

}
