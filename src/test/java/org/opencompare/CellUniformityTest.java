package org.opencompare;

import org.junit.Ignore;
import org.junit.Test;
import org.opencompare.api.java.*;
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


        assertTrue(new PCMHelper().checkFeatureNumericalUniformity(ft));

    }



    @Test
    public void testFindUniformCells() throws IOException {

        Collection<String> fts = new PCMHelper().collectUniformAndNumericalFeatures(mkPokemonPCM());
        System.err.println(fts);
        assertEquals(7, fts.size());
    }

    @Test
    public void testFindUniformCells2() throws IOException {

        Collection<String> fts = new PCMHelper().collectUniformAndNumericalFeatures(PCMUtils.loadPCM("pcms/Comparison_of_Nikon_DSLR_cameras_0.pcm"));
        System.err.println(fts);
        assertEquals(8, fts.size());
    }


    @Ignore
    @Test
    public void testFindUniformCells3() throws IOException {

        Collection<String> fts = new PCMHelper().collectUniformAndNumericalFeatures(PCMUtils.loadPCM("pcms/List_of_AMD_graphics_processing_units_37.pcm"));
        System.err.println(fts);
        assertEquals(7, fts.size());
    }





}
