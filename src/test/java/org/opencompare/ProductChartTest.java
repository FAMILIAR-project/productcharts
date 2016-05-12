package org.opencompare;

import org.junit.Before;
import org.junit.Test;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;
import org.opencompare.api.java.extractor.CellContentInterpreter;
import org.opencompare.api.java.impl.PCMFactoryImpl;
import org.opencompare.api.java.impl.io.KMFJSONLoader;
import org.opencompare.api.java.io.CSVLoader;
import org.opencompare.api.java.io.PCMLoader;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

import org.trimou.engine.locator.FileSystemTemplateLocator;
import org.trimou.util.ImmutableMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
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
    public void testConsistencty3() {

        ProductChartBuilder pchart = new ProductChartBuilder(_pcm, "weight", "height", "base_experience");
        assertTrue(pchart.build());

        ProductChartBuilder pchart2 = new ProductChartBuilder(_pcm, "weight", "height", "baseexperience");
        assertFalse(pchart2.build());



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

        String data = "" +
                "\tx: [1, 2, 3, 4, 5],\n" +
                "\ty: [1, 2, 4, 8, 16]";

        Mustache mustache = engine.getMustache("index");
        String output = mustache.render(ImmutableMap.<String, Object>of("pcmData", data));

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index0.html"));
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

        ProductChartBuilder pchart = new ProductChartBuilder(_pcm, "weight", "height");
        String data = pchart.buildData();

        /*
        String data = "[{\n" +
                "\tx: [1, 2, 3, 4, 5],\n" +
                "\ty: [1, 2, 4, 8, 16] }]";*/

        Mustache mustache = engine.getMustache("index");
        String output = mustache.render(ImmutableMap.<String, Object>of("pcmData", data,
                "pcmTitle", "Nokia",
                "xFeature", pchart.getX(),
                "yFeature", pchart.getY()
        ));

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index.html"));
        fw.write(output);
        fw.close();



    }


    @Test
    public void testXYZTemplate() throws IOException {

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(new FileSystemTemplateLocator(1, "template/", "html"))
                .build();

        ProductChartBuilder pchart = new ProductChartBuilder(_pcm, "weight", "height", "base_experience");
        String data = pchart.buildData();


        Mustache mustache = engine.getMustache("index");
        String output = mustache.render(ImmutableMap.<String, Object>of("pcmData", data,
                "pcmTitle", "Nokia with Bubble",
                "xFeature", pchart.getX(),
                "yFeature", pchart.getY()
        ));

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index-bubble.html"));
        fw.write(output);
        fw.close();



    }

    @Test
    public void testXYZTemplate2() throws IOException {

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(new FileSystemTemplateLocator(1, "template/", "html"))
                .build();

        ProductChartBuilder pchart = new ProductChartBuilder(_pcm, "base_experience", "weight", "height");
        String data = pchart.buildData();


        Mustache mustache = engine.getMustache("index");
        String output = mustache.render(ImmutableMap.<String, Object>of("pcmData", data,
                "pcmTitle", "Nokia with Bubble",
                "xFeature", pchart.getX(),
                "yFeature", pchart.getY()
        ));

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index-pokemon-bubble.html"));
        fw.write(output);
        fw.close();

    }

    @Test
    public void testXYTemplate3() throws IOException {


        PCM pcmN = _loadPCM("pcms/Comparison_of_Nikon_DSLR_cameras_0.pcm");

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(new FileSystemTemplateLocator(1, "template/", "html"))
                .build();

        ProductChartBuilder pchart = new ProductChartBuilder(pcmN, "Focus points", "Weight (g)");
        String data = pchart.buildData();


        Mustache mustache = engine.getMustache("index");
        String output = mustache.render(ImmutableMap.<String, Object>of("pcmData", data,
                "pcmTitle", "Nokia with Bubble",
                "xFeature", pchart.getX(),
                "yFeature", pchart.getY()
        ));

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index-nokia.html"));
        fw.write(output);
        fw.close();
    }

    @Test
    public void testXYTemplate4() throws IOException {


        PCM pcmN = _loadPCM("pcms/Comparison_of_Nikon_DSLR_cameras_0.pcm");

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(new FileSystemTemplateLocator(1, "template/", "html"))
                .build();

        ProductChartBuilder pchart = new ProductChartBuilder(pcmN, "Megapixel", "Weight (g)"); // argument is Real
        String data = pchart.buildData();

        Mustache mustache = engine.getMustache("index");
        String output = mustache.render(ImmutableMap.<String, Object>of("pcmData", data,
                "pcmTitle", "Nokia",
                "xFeature", pchart.getX(),
                "yFeature", pchart.getY()
        ));

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index-nokia2.html"));
        fw.write(output);
        fw.close();
    }


    @Test
    public void testXYZTemplate3() throws IOException {


        PCMContainer pcmContainer = _loadPCMContainer("pcms/Comparison_of_Nikon_DSLR_cameras_0.pcm");
        PCM pcmN = pcmContainer.getPcm();

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(new FileSystemTemplateLocator(1, "template/", "html"))
                .build();

        ProductChartBuilder pchart = new ProductChartBuilder(pcmN, "Weight (g)", "Metering pixels", "Focus points");
        String data = pchart.buildData();

        String xFeature = pchart.getX();
        String yFeature = pchart.getY();

        Mustache mustache = engine.getMustache("index");
        String output = mustache.render(ImmutableMap.<String, Object>of("pcmData", data,
                "pcmTitle", "Nokia with Bubble",
                "xFeature", xFeature,
                "yFeature", yFeature
                ));

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index-nokia-bubble.html"));
        fw.write(output);
        fw.close();


        _checkOccurencesInHTML(output, data, xFeature, yFeature);
        // TODO: parse the HTML and check the validity or some specific properties (for the oracle)
    }

    private void _checkOccurencesInHTML(String outputHTML, String data, String xFeature, String yFeature) {
        assertTrue(outputHTML.contains(data));
        assertTrue(outputHTML.contains(xFeature));
        assertTrue(outputHTML.contains(yFeature));

    }


    /**
     * A helper method to get a PCM (share the limitation of _loadPCMContainter)
     * @param filename
     * @return
     * @throws IOException
     */
    private PCM _loadPCM(String filename) throws IOException {

        PCMContainer pcmContainer = _loadPCMContainer(filename);
        // Get the PCM
        PCM pcm = pcmContainer.getPcm();

        return pcm ;
    }

    /**
     * A helper method to get a PCMContainer (limited in a sense we only retrieve the 1st element of containers)
     * @param filename
     * @return
     * @throws IOException
     */
    private PCMContainer _loadPCMContainer(String filename) throws IOException {

        // Define a file representing a PCM to load
        File pcmFile = new File(filename);

        // Create a loader that can handle the file format
        PCMLoader loader = new KMFJSONLoader();

        // Load the file
        // A loader may return multiple PCM containers depending on the input format
        // A PCM container encapsulates a PCM and its associated metadata
        List<PCMContainer> pcmContainers = loader.load(pcmFile);

        PCMContainer pcmContainer = pcmContainers.get(0);
        return pcmContainer;
    }


}
