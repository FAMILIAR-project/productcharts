package org.opencompare;

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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by macher1 on 13/05/2016.
 */
public class RandomProductChartTest {

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

        Collection<String> candidateFts = new PCMHelper().collectUniformAndNumericalFeatures(pcm);



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
}
