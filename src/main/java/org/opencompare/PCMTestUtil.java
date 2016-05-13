package org.opencompare;

import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;
import org.opencompare.api.java.extractor.CellContentInterpreter;
import org.opencompare.api.java.impl.PCMFactoryImpl;
import org.opencompare.api.java.io.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by macher1 on 13/05/2016.
 */
public class PCMTestUtil {


    public static PCM mkPokemonPCM() throws IOException {
        CSVLoader csvL = new CSVLoader(
                new PCMFactoryImpl(),
                new CellContentInterpreter(new PCMFactoryImpl()));

        List<PCMContainer> pcms = csvL.load(new File("pcms/pokemon.csv"));
        PCM _pcmPokemon = pcms.get(0).getPcm();

        return _pcmPokemon;
    }


}
