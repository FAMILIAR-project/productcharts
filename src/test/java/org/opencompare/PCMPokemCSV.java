package org.opencompare;

import org.junit.Test;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;
import org.opencompare.api.java.Product;
import org.opencompare.api.java.extractor.CellContentInterpreter;
import org.opencompare.api.java.impl.PCMFactoryImpl;
import org.opencompare.api.java.io.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by macher1 on 14/05/2016.
 */
public class PCMPokemCSV {


    @Test
    public void test1() throws IOException {


        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "ability_changelog.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        System.err.println("pcm:" + pcm);
        assertEquals(53, pcm.getProducts().size());
        assertEquals(3, pcm.getConcreteFeatures().size());

    }

    @Test
    public void test2() throws IOException {



        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "machines.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        System.err.println("pcm:" + pcm);
        assertEquals(1229, pcm.getProducts().size());
        assertEquals(2, pcm.getConcreteFeatures().size());

    }

    @Test
    public void test3() throws IOException {


        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "pokemon.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        System.err.println("pcm:" + pcm);
        assertEquals(811, pcm.getProducts().size());
        assertEquals(8, pcm.getConcreteFeatures().size());

    }

    @Test
    public void test4() throws IOException {



        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "moves.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        System.err.println("pcm:" + pcm);
        assertEquals(639, pcm.getProducts().size());
        assertEquals(15, pcm.getConcreteFeatures().size());

    }

    @Test
    public void test5() throws IOException {



        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "experience.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        System.err.println("pcm:" + pcm);
        assertEquals(601, pcm.getProducts().size());
        assertEquals(15, pcm.getConcreteFeatures().size());

    }

    @Test
    public void test6() throws IOException {



        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "abilities.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        System.err.println("pcm:" + pcm);
        assertEquals(251, pcm.getProducts().size());
        assertEquals(4, pcm.getConcreteFeatures().size());

    }

    @Test
    public void test7() throws IOException {

        List<PCMContainer> pcms = PCMUtils.loadCSV(PCMTestUtil.CSV_POKEMON_DIR + "berries.csv");
        assertEquals(pcms.size(), 1);
        PCM pcm = pcms.get(0).getPcm();

        System.err.println("pcm:" + pcm);

        pcm.getConcreteFeatures().stream().forEach(ft -> System.out.println("" + ft.getName()));
        assertEquals(65, pcm.getProducts().size());
        assertEquals(9, pcm.getConcreteFeatures().size());

    }
}
