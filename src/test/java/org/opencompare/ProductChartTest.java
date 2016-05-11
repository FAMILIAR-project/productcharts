package org.opencompare;

import org.junit.Before;
import org.junit.Test;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;
import org.opencompare.api.java.extractor.CellContentInterpreter;
import org.opencompare.api.java.impl.PCMFactoryImpl;
import org.opencompare.api.java.io.CSVLoader;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

import org.trimou.engine.locator.FileSystemTemplateLocator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by macher1 on 11/05/2016.
 */
public class ProductChartTest {

    private PCM _pcm;
    private static String chartTargetFolder = "output";

    @Before
    public void mkPokemonPCM() throws IOException {
        CSVLoader csvL = new CSVLoader(
                new PCMFactoryImpl(),
                new CellContentInterpreter(new PCMFactoryImpl()));

        List<PCMContainer> pcms = csvL.load(new File("pcms/pokemon.csv"));
        assertEquals(pcms.size(), 1);
        _pcm = pcms.get(0).getPcm();

        assertNotNull(_pcm);
    }


    @Test
    public void testConsistency() {

        ProductChartBuilder pchart = new ProductChartBuilder(_pcm, "height", "weight");
        assertTrue(pchart.build());

        ProductChartBuilder pchart2 = new ProductChartBuilder(_pcm, "weight", "height");
        assertTrue(pchart2.build());

        ProductChartBuilder pchart3 = new ProductChartBuilder(_pcm, "eight", "height");
        assertFalse(pchart3.build());

        ProductChartBuilder pchart4 = new ProductChartBuilder(_pcm, "weight", "eight");
        assertFalse(pchart4.build());

    }

    @Test
    public void testConsistency2() {

        ProductChartBuilder pchart = new ProductChartBuilder(_pcm, "height", "height");
        assertTrue(pchart.build());

        ProductChartBuilder pchart2 = new ProductChartBuilder(_pcm, "weight", "weight");
        assertTrue(pchart2.build());

        ProductChartBuilder pchart3 = new ProductChartBuilder(_pcm, "weightt", "weight");
        assertFalse(pchart3.build());

    }

    @Test
    public void testTemplate() throws IOException {

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(new FileSystemTemplateLocator(1, "template/", "html"))
                .build();

        String data = "[{\n" +
                "\tx: [1, 2, 3, 4, 5],\n" +
                "\ty: [1, 2, 4, 8, 16] }]";

        String output = engine.getMustache("index").render(data);

        System.err.println("output:" + output);

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index.html"));
        fw.write(output);
        fw.close();

    }

    @Test
    public void testXYTemplate() throws IOException {

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(new FileSystemTemplateLocator(1, "template/", "html"))
                .build();

        ProductChartBuilder pbuilder = new ProductChartBuilder(_pcm, "weight", "height");
        String data = pbuilder.buildData();
        System.err.println("data:" + data);
        /*
        String data = "[{\n" +
                "\tx: [1, 2, 3, 4, 5],\n" +
                "\ty: [1, 2, 4, 8, 16] }]";*/

        String output = engine.getMustache("index").render(data);

        //System.err.println("output:" + output);

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index.html"));
        fw.write(output);
        fw.close();



    }
}
