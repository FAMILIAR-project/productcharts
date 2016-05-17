package org.opencompare;

import org.junit.Test;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;

import java.io.IOException;

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

        assertEquals(8, json.split("\r\n|\r|\n").length);
        System.err.println("json=" + json);

    }

    @Test
    public void test2() throws IOException {
        PCMContainer pcmContainer = PCMUtils.loadCSV("pcms/pokemon.csv").get(0);
        PCM pcmN = pcmContainer.getPcm();

        ProductChartBuilder pchart = new ProductChartBuilder(pcmN, "weight", "height", "base_experience");
        String json = pchart.buildJSON();

        assertEquals(7, json.split("\r\n|\r|\n").length);
        System.err.println("json=" + json);

    }


}
