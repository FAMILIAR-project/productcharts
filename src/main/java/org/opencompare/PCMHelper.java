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
import java.util.Optional;
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

    public Optional<Double> max(PCM pcm, String ftName) {
        ComparisonDoubleOperation cmp = (Double a, Double b) -> - (a.compareTo(b));
        return _maxMin(pcm, ftName, cmp);
    }

    interface ComparisonDoubleOperation {
        int operation(Double a, Double b);
    }

    public Optional<Double> min(PCM pcm, String ftName) {
        ComparisonDoubleOperation cmp = (Double a, Double b) -> (a.compareTo(b)) ;
        return _maxMin(pcm, ftName, cmp);
    }

    /**
     * compute the max or the min (depends on 'cmp') of values given a feature name...
     * only applies if values are of types Integer or Real
     * @param pcm
     * @param ftName
     * @param cmp
     * @return
     */
    private Optional<Double> _maxMin(PCM pcm, String ftName, ComparisonDoubleOperation cmp) {
        Feature ft = PCMUtils.getFeature(pcm, ftName);
        if (ft == null)
            return Optional.empty();
        List<Cell> cells = ft.getCells();
        if (cells.size() == 0)
            return Optional.empty();


        double max = 0.0 ;
        boolean firstTime = true;

        for (Cell c : cells) {
            Value v = c.getInterpretation();
            double cValue = 0;
            if (!(v instanceof IntegerValue) && !(v instanceof RealValue)) {
                _log.info("" + ftName + " // " + v + " rejected (not a numerical value)!");
                return Optional.empty();
            }



            if (v instanceof IntegerValue) {
                cValue = ((IntegerValue) v).getValue();
            }

            else if (v instanceof RealValue) {
                cValue = ((RealValue) v).getValue();
            }

            // init the max/min
            if (firstTime) {
                max = cValue;
                firstTime = false;
            }

            if (cmp.operation(max, cValue) > 0)
                    max = cValue;

        }

        return Optional.of(max);
    }
}
