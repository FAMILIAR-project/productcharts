package org.opencompare;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by macher1 on 17/05/2016.
 */
public class ProductChartJSON {


    @Test
    public void test1() throws IOException {
        PCMContainer pcmContainer = PCMUtils.loadPCMContainer("pcms/Comparison_of_Nikon_DSLR_cameras_0.pcm");
        PCM pcmN = pcmContainer.getPcm();

        ProductChartBuilder pchart = new ProductChartBuilder(pcmN, "weight", "height", "base_experience");
        String json = pchart.buildJSON();
        System.err.println("json=" + json);

        JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
        assertNotNull(jo);

        assertEquals(8, jo.entrySet().size());


    }

    @Test
    public void test2() throws IOException {
        PCMContainer pcmContainer = PCMUtils.loadCSV("pcms/pokemon.csv").get(0);
        PCM pcmN = pcmContainer.getPcm();

        ProductChartBuilder pchart = new ProductChartBuilder(pcmN, "weight", "height", "base_experience");
        String json = pchart.buildJSON();

        JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
        assertNotNull(jo);

        assertEquals(7, jo.entrySet().size());


    }


}
