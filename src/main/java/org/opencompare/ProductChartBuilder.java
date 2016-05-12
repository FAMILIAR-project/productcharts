package org.opencompare;

import org.opencompare.api.java.*;
import org.opencompare.api.java.value.IntegerValue;
import org.opencompare.api.java.value.RealValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by macher1 on 11/05/2016.
 */
public class ProductChartBuilder {



    Logger _log = Logger.getLogger("ProductChartBuilder");




    private final PCM _pcm;

    /*
     * feature name for X
     */
    private String _x ;


    /**
     * feature name for Y
     */
    private String _y;

    /**
     * optional
     * feature name for Z (bubble size)
     *
     */
    private String _z;
    // inv: _z != null <=> _withBubble
    private final boolean _withBubble; // true if _z is specified


    public ProductChartBuilder (PCM pcm, String x, String y) {
        _pcm = pcm;
        _x = x;
        _y = y;
        _withBubble = false;
    }

    public ProductChartBuilder (PCM pcm, String x, String y, String z) {
        _pcm = pcm;
        _x = x;
        _y = y;
        _z = z;
        _withBubble = true;

    }

    public boolean build() {

        if (!checkConsistencyX()) {
            _log.warning("Feature " + _x + " is not a valid feature!");
            return false;
        }
        if (!checkConsistencyY()) {
            _log.warning("Feature " + _y + " is not a valid feature!");
            return false;
        }

        if (withBubble() && !checkConsistencyZ()) {
            _log.warning("Feature " + _z + " is not a valid feature!");
            return false;
        }


        return true;

    }

    private boolean withBubble() {
        return _withBubble;
    }


    /** TODO duplicated code on purpose (of course we have to iterate in one shot for checking X and Y)
     *  for locating the specific error (either X or Y)
     * @return
     */
    private boolean checkConsistencyX() {

        List<Feature> fts = _pcm.getConcreteFeatures();
        for (Feature ft : fts) {
            if (ft.getName().equals(_x))
                return true;
        }

        return false;
    }

    private boolean checkConsistencyY() {

        List<Feature> fts = _pcm.getConcreteFeatures();
        for (Feature ft : fts) {
            if (ft.getName().equals(_y))
                return true;
        }

        return false;
    }

    private boolean checkConsistencyZ() {

        List<Feature> fts = _pcm.getConcreteFeatures();
        for (Feature ft : fts) {
            if (ft.getName().equals(_z))
                return true;
        }

        return false;
    }


    public String buildData() {

        StringBuilder sb = new StringBuilder();
        sb.append("x: ");
        sb.append("[");


        List<Product> pdts = _pcm.getProducts();
        Collection<String> xs = new ArrayList<String>();
        for (Product pdt : pdts) {
            Cell c = pdt.findCell(_getFeature(_pcm, _x));
            xs.add(c.getContent());
        }
        sb.append(xs.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));


/*
        List<Feature> fts = _pcm.getConcreteFeatures();
        for (Feature ft : fts) {
            if (ft.getName().equals(_x)) {
                List<Cell> cells = ft.getCells();
                //cells.sort(new CellValueComparator());
                sb.append(cells.stream()
                        .map(c -> c.getContent())
                        .collect(Collectors.joining(", ")));
            }
        }*/
        sb.append("],\n");



        sb.append("y: ");
        sb.append("[");


        Collection<String> ys = new ArrayList<String>();
        for (Product pdt : pdts) {
            Cell c = pdt.findCell(_getFeature(_pcm, _y));
            ys.add(c.getContent());
        }
        sb.append(ys.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));

/*
        for (Feature ft : fts) {
            if (ft.getName().equals(_y)) {
                List<Cell> cells = ft.getCells();
                //cells.sort(new CellValueComparator());
                sb.append(cells.stream()
                        .map(c -> c.getContent())
                        .collect(Collectors.joining(", ")));
            }
        }*/

        sb.append("]");

        if (withBubble()) {
            sb.append(", \n");
            sb.append("marker: { size: ");
            sb.append("[");
            Collection<String> zs = new ArrayList<String>();
            for (Product pdt : pdts) {
                Cell c = pdt.findCell(_getFeature(_pcm, _z));
                zs.add(c.getContent());
            }
            sb.append(zs.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "))); // / 10

            sb.append("],\n");
            sb.append("}");
        }


        return sb.toString();
    }

    private Feature _getFeature(PCM pcm, String ftName) {
        List<Feature> fts = pcm.getConcreteFeatures();
        for (Feature ft : fts) {
            if (ft.getName().equals(ftName)) {
                return ft;
            }
        }
        return null;
    }

    public String getY() {
        return _y;
    }

    public String getX() {
        return _x;
    }
}
