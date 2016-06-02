package org.opencompare;

import com.google.gson.*;
import org.opencompare.api.java.*;
import org.opencompare.api.java.value.IntegerValue;
import org.opencompare.api.java.value.RealValue;
import play.api.libs.json.Json;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by macher1 on 11/05/2016.
 */
public class ProductChartBuilder {



    private final static Logger _log = Logger.getLogger("ProductChartBuilder");




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


    public String buildJSON() {

       JsonObject jsonData = _buildJSON();
        Gson gson = new Gson(); // new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonData);
    }

    private JsonObject _buildJSON() {

        JsonObject jsonData = new JsonObject();
        Collection<JsonObject> allFts = new HashSet<JsonObject>();

        Collection<Feature> fts  = new PCMHelper().collectUniformAndNumericalFts(_pcm);
        for (Feature ft : fts) {

            String ftName = ft.getName(); //_quote(ft.getName());
            List<Cell> cells = ft.getCells();

            StringBuilder sb = new StringBuilder();
            JsonArray ja = new JsonArray();
            cells.stream().forEach(c -> ja.add(c.getContent()));
            jsonData.add(ftName, ja);
        }


      return jsonData;
    }

    private String _quote(String name) {
        return "\'" + name + "\'";
    }

    public String buildData() {
        return buildJSON();
    }



    public String getY() {
        return _y;
    }

    public String getX() {
        return _x;
    }

    public String getZ() { return _z;  }
}
