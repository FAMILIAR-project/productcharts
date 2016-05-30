package org.opencompare;

import org.junit.Ignore;
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
import org.trimou.engine.resolver.DummyTransformResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.Transformer;
import org.trimou.handlebars.BasicValueHelper;
import org.trimou.handlebars.Helper;
import org.trimou.handlebars.Options;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by macher1 on 13/05/2016.
 */
public class RandomProductChartTest {

    private final static Logger _log = Logger.getLogger("RandomProductChartTest");

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

        _buildRandomProductChart(PCMTestUtil.mkPokemonPCM(), "Pokemon2", chartTargetFolder, 2);
        _buildRandomProductChart(PCMTestUtil.mkPokemonPCM(), "Pokemon3", chartTargetFolder, 3);

    }

    @Test
    public void testProductChartCSVPok1() throws IOException {

        String chartTargetFolder = "outputPokemon";

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "moves.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        _buildRandomProductChart(pcm, "PokemonMoves2", chartTargetFolder, 2);
        _buildRandomProductChart(pcm, "PokemonMoves3", chartTargetFolder, 3);

    }

    @Test
    public void testProductChartCSVPok2() throws IOException {

        String chartTargetFolder = "outputPokemon";

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "abilities.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        _buildRandomProductChart(pcm, "PokemonAbilities2", chartTargetFolder, 2);
        _buildRandomProductChart(pcm, "PokemonAbilities3", chartTargetFolder, 3);

    }

    @Test
    public void testProductChartCSVPok3() throws IOException {

        String chartTargetFolder = "outputPokemon";

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "berries.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        _buildRandomProductChart(pcm, "PokemonBerries2", chartTargetFolder, 2);
        _buildRandomProductChart(pcm, "PokemonBerries3", chartTargetFolder, 3);

    }

    @Test
    public void testProductChartCSVPok4() throws IOException {

        String chartTargetFolder = "outputPokemon";

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "natures.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        _buildRandomProductChart(pcm, "PokemonNatures2", chartTargetFolder, 2);
        _buildRandomProductChart(pcm, "PokemonNatures3", chartTargetFolder, 3);

    }




    @Test
    public void testProductCharts1() throws Exception {


        String chartTargetFolder = "outputAll";
        int chartDimension = 2; // X, Y, or Z (bubble size)

        Collection<PCMContainer> pcms = collectPCMContainersInAFolder(new File(PCMTestUtil.PCM_WIKIPEDIASAMPLE_DIR));
        assertEquals(1411, pcms.size());

        _buildRandomProductCharts(pcms, chartTargetFolder, chartDimension);

    }

    @Test
    public void testProductCharts2() throws Exception {


        String chartTargetFolder = "outputAllBubble";
        int chartDimension = 3; // X, Y, or Z (bubble size)

        Collection<PCMContainer> pcms = collectPCMContainersInAFolder(new File(PCMTestUtil.PCM_WIKIPEDIASAMPLE_DIR));
        assertEquals(1411, pcms.size());

        _buildRandomProductCharts(pcms, chartTargetFolder, chartDimension);

    }

    @Test
    public void testProductChartsCSVPokemon() throws Exception {


        String chartTargetFolder = "outputAllBubbleCSV";
        int chartDimension = 3; // X, Y, or Z (bubble size)

        _buildRandomProductCharts(collectPCMContainersInACSVFolder(new File(PCMTestUtil.CSV_POKEMON_DIR)), chartTargetFolder, chartDimension);

    }

    // CSV folder of Pokemon: "



    /**
     * Generate a produch chart for each PCM in the collection
     * The output folder is chartTargetFolder
     * user can specify if she wants two or three dimensions
     * @param allPcmContainers
     * @param chartTargetFolder
     * @param chartDimension
     * @throws IOException
     */
    private void _buildRandomProductCharts(Collection<PCMContainer> allPcmContainers, String chartTargetFolder, int chartDimension) throws IOException {
        assertTrue(chartDimension == 2 || chartDimension == 3);

        // precondition: output folder exist
        File f = new File(chartTargetFolder);
        if (!f.exists() || !f.isDirectory())
            assertTrue(f.mkdir());

        for (PCMContainer pcmContainer : allPcmContainers) {
                PCM pcm = pcmContainer.getPcm();
                assertNotNull(pcm);
                _buildRandomProductChart(pcm, pcm.getName(), chartTargetFolder, chartDimension);

        }

    }

    /**
     *  collect PCMs of a folder (and set the PCM name with the name file)
     *  each .pcm is loaded
     *  note that a .pcm can contain several PCMContainers
     * @param dir
     * @return
     * @throws IOException
     */
    private Collection<PCMContainer> collectPCMContainersInAFolder(File dir) throws IOException {

        assertTrue(dir.isDirectory());
        Collection<PCMContainer> containers = new ArrayList<PCMContainer>();

        File[] pcms = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".pcm");
            }
        });

        assertTrue(pcms.length > 0);

        for (File pcmFile : pcms) {

            PCMLoader loader = new KMFJSONLoader();
            List<PCMContainer> pcmContainers = loader.load(pcmFile);
            for (PCMContainer pc : pcmContainers) {
                containers.add(pc);
                pc.getPcm().setName(pcmFile.getName());
            }



        }

        return containers;

    }

    /**
     *  collect PCMs of a folder (and set the PCM name with the name file)
     *  each .pcm is loaded
     *  note that a .pcm can contain several PCMContainers
     * @param dir
     * @return
     * @throws IOException
     */
    private Collection<PCMContainer> collectPCMContainersInACSVFolder(File dir) throws IOException {

        assertTrue(dir.isDirectory());
        Collection<PCMContainer> containers = new ArrayList<PCMContainer>();

        File[] pcms = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv")
                        // very specific to CSV folder (CSVs are too BIG)
                        && !name.contains("pokemon_moves")
                        && !name.contains("pokemon_species_flavor_text")
                        && !name.contains("move_flavor_text")
                        && !name.contains("encounters")
                        && !name.contains("item_flavor_text")
                        ;
            }
        });

        assertTrue(pcms.length > 0);

        for (File pcmFile : pcms) {

            PCMLoader loader = new CSVLoader(
                    new PCMFactoryImpl(),
                    new CellContentInterpreter(new PCMFactoryImpl()));

            List<PCMContainer> pcmContainers = loader.load(pcmFile);
            for (PCMContainer pc : pcmContainers) {
                containers.add(pc);
                pc.getPcm().setName(pcmFile.getName());
            }



        }

        return containers;

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



        String data = pchart.buildJSON(); // pchart.buildData();
        Optional<String> basicStats = _mkBasicStats(pcm);

        Mustache mustache = engine.getMustache("index");

        Map<String, String> valueTemplates = new HashMap<String, String>();
        valueTemplates.put("pcmData", data);
        valueTemplates.put("pcmTitle", pcmName);
        valueTemplates.put("xFeature", xFeature);
        valueTemplates.put("yFeature", yFeature);
        valueTemplates.put("zFeature", zFeature);
        valueTemplates.put("basicSummary", basicStats.orElse(""));
        valueTemplates.put("candidateFts", '[' + candidateFts.stream()
                .map(ft -> "\'" + ft + "\'")
                .collect(Collectors.joining(", ")) + ']');


        String output = mustache.render(valueTemplates);

        FileWriter fw = new FileWriter(new File(chartTargetFolder + "/" + "index-" + pcmName + ".html"));
        fw.write(output);
        fw.close();

        return true;

    }

    private Optional<String> _mkBasicStats(PCM pcm) {
        return PCMTestUtil.mkBasicStats(pcm);
    }


}
