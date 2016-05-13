package org.opencompare;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opencompare.api.java.*;
import org.opencompare.api.java.extractor.CellContentInterpreter;
import org.opencompare.api.java.impl.PCMFactoryImpl;
import org.opencompare.api.java.impl.io.KMFJSONLoader;
import org.opencompare.api.java.io.CSVExporter;
import org.opencompare.api.java.io.CSVLoader;
import org.opencompare.api.java.io.PCMLoader;
import org.opencompare.api.java.value.IntegerValue;
import org.opencompare.api.java.value.RealValue;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.FileSystemTemplateLocator;
import org.trimou.util.ImmutableMap;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by macher1 on 12/05/2016.
 */
public class CellUniformityTest {


    Logger _log = Logger.getLogger("CellUniformityTest");




    public PCM mkPokemonPCM() throws IOException {
        CSVLoader csvL = new CSVLoader(
                new PCMFactoryImpl(),
                new CellContentInterpreter(new PCMFactoryImpl()));

        List<PCMContainer> pcms = csvL.load(new File("pcms/pokemon.csv"));
        assertEquals(pcms.size(), 1);
        PCM _pcmPokemon = pcms.get(0).getPcm();

        assertNotNull(_pcmPokemon);
        return _pcmPokemon;
    }


    @Test
    public void testUniformCell() throws IOException {

        String ftName = "height";
        Feature ft = PCMUtils.getFeature(mkPokemonPCM(), ftName);
        assertNotNull(ft);
        assertTrue(_checkFeatureUniformity(ft));

    }



    @Test
    public void testFindUniformCells() throws IOException {

        Collection<String> fts = _collectUniformAndNumericalFeatures(mkPokemonPCM());
        System.err.println(fts);
        assertEquals(7, fts.size());
    }

    @Test
    public void testFindUniformCells2() throws IOException {

        Collection<String> fts = _collectUniformAndNumericalFeatures(PCMUtils.loadPCM("pcms/Comparison_of_Nikon_DSLR_cameras_0.pcm"));
        System.err.println(fts);
        assertEquals(8, fts.size());
    }


    @Ignore
    @Test
    public void testFindUniformCells3() throws IOException {

        Collection<String> fts = _collectUniformAndNumericalFeatures(PCMUtils.loadPCM("pcms/List_of_AMD_graphics_processing_units_37.pcm"));
        System.err.println(fts);
        assertEquals(7, fts.size());
    }


    @Test
    public void testProductChart1() throws Exception {


        String chartTargetFolder = "outputNokia";
        String fileName = "pcms/Comparison_of_Nikon_DSLR_cameras_0.pcm";


        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        _buildRandomProductChart(PCMUtils.loadPCM(fileName), "NokiaRandom2", chartTargetFolder, 2);
        _buildRandomProductChart(PCMUtils.loadPCM(fileName), "NokiaRandom3", chartTargetFolder, 3);
    }


    @Test
    public void testProductChart2() throws Exception {


        String chartTargetFolder = "outputPokemon";

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        _buildRandomProductChart(mkPokemonPCM(), "Pokemon2", chartTargetFolder, 2);
        _buildRandomProductChart(mkPokemonPCM(), "Pokemon3", chartTargetFolder, 3);

    }



    @Test
    public void testProductCharts1() throws Exception {


        String chartTargetFolder = "outputAll";
        int chartDimension = 2; // X, Y, or Z (bubble size)

        _buildRandomProductCharts(chartTargetFolder, chartDimension);

    }

    @Test
    public void testProductCharts2() throws Exception {


        String chartTargetFolder = "outputAllBubble";
        int chartDimension = 3; // X, Y, or Z (bubble size)

        _buildRandomProductCharts(chartTargetFolder, chartDimension);

    }

    /**
     * Generate a produch chart for each PCM in the folder (hard-coded below).
     * The output folder is chartTargetFolder
     * user can specify if she wants two or three dimensions
     * @param chartTargetFolder
     * @param chartDimension
     * @throws IOException
     */
    private void _buildRandomProductCharts(String chartTargetFolder, int chartDimension) throws IOException {
        assertTrue(chartDimension == 2 || chartDimension == 3);

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        File dir = new File("/Users/macher1/Downloads/model/");
        assertTrue(dir.isDirectory());

        File[] pcms = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".pcm");
            }
        });

        assertTrue(pcms.length > 0);

        int i = 0;
        for (File pcmFile : pcms) {

            PCMLoader loader = new KMFJSONLoader();
            List<PCMContainer> pcmContainers = loader.load(pcmFile);
            for (PCMContainer pcmContainer : pcmContainers) {
                PCM pcm = pcmContainer.getPcm();
                assertNotNull(pcm);
                i++;

                if (!_buildRandomProductChart(pcm, pcmFile.getName(), chartTargetFolder, chartDimension))
                    break;
            }
        }


    }

    /**
     *
     * build a product chart for a specific PCM with a random strategy for picking random, numerical features
     * @param pcm
     * @param pcmName
     * @param chartTargetFolder
     * @param chartDimension
     * @return
     * @throws IOException
     */
    private boolean _buildRandomProductChart(PCM pcm, String pcmName, String chartTargetFolder, int chartDimension) throws IOException {


        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(new FileSystemTemplateLocator(1, "template/", "html"))
                .build();

        Collection<String> candidateFts = _collectUniformAndNumericalFeatures(pcm);



        if (candidateFts.size() < chartDimension) {
            _log.warning("Impossible to produce a product chart for PCM: " + pcmName);
            return false;
        }

        List<String> lCandidateFts = new ArrayList<String>(candidateFts);
        Collections.shuffle(lCandidateFts);
        List<String> lfts = lCandidateFts.subList(0, chartDimension);

        assertTrue(lfts.size() <= chartDimension);
        String xFeature = lfts.get(0);
        String yFeature = lfts.get(1);
        String zFeature = null;

        ProductChartBuilder pchart = null ;
        if (chartDimension == 2)
            pchart = new ProductChartBuilder(pcm, xFeature, yFeature);
        else {
            assertEquals(3, chartDimension);
            zFeature = lfts.get(2);
            pchart = new ProductChartBuilder(pcm, xFeature, yFeature, zFeature);
        }



        String data = pchart.buildData();

        Mustache mustache = engine.getMustache("index");


        Map<String, Object> valueTemplates = ImmutableMap.<String, Object>of("pcmData", data,
                "pcmTitle", pcmName,
                "xFeature", xFeature,
                "yFeature", yFeature
        );
        if (chartDimension == 3) {
            // TODO: weird because yep this is an immutable (unmodifiable) which is OK btw
            // and we can't use put or methods to basically add new key, values
            // problem is that method "of" only supports 4 pairs
            valueTemplates = new HashMap<String, Object>(valueTemplates);
            valueTemplates.put("zFeature", zFeature);
        }

        String output = mustache.render(valueTemplates);

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index-" + pcmName + ".html"));
        fw.write(output);
        fw.close();

        return true;

    }


    private Collection<String> _collectUniformAndNumericalFeatures(PCM pcmPokemon) {
        Collection<String> rFts = new ArrayList<String>() ;
        List<Feature> fts = pcmPokemon.getConcreteFeatures();
        for (Feature ft : fts) {
            if(_checkFeatureUniformity(ft))
                 rFts.add(ft.getName());
        }

        return rFts;

    }

    private boolean _checkFeatureUniformity(Feature ft) {

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
