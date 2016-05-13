package org.opencompare;

import org.opencompare.api.java.Cell;
import org.opencompare.api.java.Feature;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.Value;
import org.opencompare.api.java.value.IntegerValue;
import org.opencompare.api.java.value.RealValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by macher1 on 13/05/2016.
 */
public class PCMHelper {

    Logger _log = Logger.getLogger("PCMHelper");

    
    public Collection<String> collectUniformAndNumericalFeatures(PCM pcm) {
        Collection<String> rFts = new ArrayList<String>() ;
        List<Feature> fts = pcm.getConcreteFeatures();
        for (Feature ft : fts) {
            if(checkFeatureNumericalUniformity(ft))
                rFts.add(ft.getName());
        }

        return rFts;
    }

    public boolean checkFeatureNumericalUniformity(Feature ft) {

        List<Cell> cells = ft.getCells();
        Value v0 = null; // first value
        Class<? extends Value> cl = null; // first class, acting as a base for comparison

        boolean isFeatureHomogeneous = true;
        for (Cell c : cells) {
            Value v = c.getInterpretation();

            if (v0 == null) {
                v0 = v;
                cl = v0.getClass();
                if (!(v0 instanceof IntegerValue) && !(v0 instanceof RealValue)) {
                    _log.info("" + ft.getName() + " // " + v0 + " rejected!");
                    isFeatureHomogeneous = false;
                    break;
                }
            }
            else { // pre: cl != null
                // if types of v and v0 differ, then the cell implies that the feature is not homogeneous in terms of types
                if (!cl.equals(v.getClass())) {
                    _log.info("" + ft.getName() + " rejected!");
                    isFeatureHomogeneous = false;
                    break;
                }
            }
        }
        return isFeatureHomogeneous;

    }
}
