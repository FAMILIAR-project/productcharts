package org.opencompare;

import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;
import org.opencompare.api.java.extractor.CellContentInterpreter;
import org.opencompare.api.java.impl.PCMFactoryImpl;
import org.opencompare.api.java.io.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by macher1 on 13/05/2016.
 */
public class PCMTestUtil {

    private final static Logger _log = Logger.getLogger("PCMTestUtil");

    public static PCM mkPokemonPCM() throws IOException {
        CSVLoader csvL = new CSVLoader(
                new PCMFactoryImpl(),
                new CellContentInterpreter(new PCMFactoryImpl()));

        List<PCMContainer> pcms = csvL.load(new File("pcms/pokemon.csv"));
        PCM _pcmPokemon = pcms.get(0).getPcm();

        return _pcmPokemon;
    }


    public static Optional<String> mkBasicStats(PCM pcm) {
        StringBuilder sb = new StringBuilder();
        PCMHelper pcmHelper = new PCMHelper();
        Collection<String> fts = pcmHelper.collectUniformAndNumericalFeatures(pcm);
        for (String ft : fts) {
            Optional<Double> max = pcmHelper.max(pcm, ft);
            Optional<Double> min = pcmHelper.min(pcm, ft);
            if (!max.isPresent() || !min.isPresent()) {
                _log.warning("Impossible to comptute max/min values for feature: " + ft);
                return Optional.empty();
            }
            sb.append("Max (min) of feature " + ft + " = " + max.get() + " (" + min.get() + ")" + "\n");
        }

        return Optional.of(sb.toString());

    }
}
