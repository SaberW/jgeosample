package org.potmart.jgeo.createshp.visitor;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.collection.AbstractFeatureVisitor;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by GOT.hodor on 2017/8/22.
 */
public class BarrierFeatureVisitor extends AbstractFeatureVisitor {
    Map<String, ListFeatureCollection> featureMap = new HashMap<>();

    Map<String, ListFeatureCollection> warnMap = new HashMap<>();

    public BarrierFeatureVisitor() {

    }

    @Override
    public void visit(Feature feature) {
        Geometry geometry = (Geometry) feature.getDefaultGeometryProperty().getValue();
        String type = (String) feature.getProperty("type").getValue();

        ListFeatureCollection peoples = featureMap.get(type);
        if (peoples == null) {
            return;
        }
        ListFeatureCollection warns = warnMap.get(type);

        if (warns == null) {
            warns = new ListFeatureCollection(peoples.getSchema());
            warnMap.put(type, warns);
        }

        SimpleFeatureIterator it = peoples.features();
        while (it.hasNext()) {
            SimpleFeature people = it.next();
            Geometry peoplePosition = (Geometry) people.getDefaultGeometry();
            if (!geometry.contains(peoplePosition)) {
                warns.add(people);
            }
        }

    }

    public void addFeatuerCollection(String key, ListFeatureCollection listFeatureCollection) {
        featureMap.put(key, listFeatureCollection);
        warnMap.put(key, new ListFeatureCollection(listFeatureCollection.getSchema()));
    }

    public void addFeature(String key, SimpleFeature feature) {
        ListFeatureCollection featureCollection = featureMap.get(key);
        if (featureCollection == null) {
            return;
        }

        featureCollection.add(feature);
    }

    public void removeFeature(String key, SimpleFeature feature) {
        ListFeatureCollection featureCollection = featureMap.get(key);
        if (featureCollection == null) {
            return;
        }

        featureCollection.remove(feature);
    }

    public Map<String, ListFeatureCollection> getWarnMap() {
        return warnMap;
    }

    public void setWarnMap(Map<String, ListFeatureCollection> warnMap) {
        this.warnMap = warnMap;
    }
}
