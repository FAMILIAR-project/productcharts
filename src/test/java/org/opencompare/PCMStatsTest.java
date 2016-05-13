package org.opencompare;

import org.junit.Test;
import org.opencompare.api.java.PCM;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by macher1 on 13/05/2016.
 */
public class PCMStatsTest {


    @Test
    public void testMax() throws IOException {

        PCM pcm = PCMTestUtil.mkPokemonPCM();
        PCMHelper h = new PCMHelper();
        Optional<Double> max = h.max(pcm, "height");
        assertTrue(max.isPresent());
        assertEquals(1080.0, max.get(), 0.0);

        Optional<Double> max2 = h.max(pcm, "weight");
        assertTrue(max2.isPresent());
        assertEquals(9997.0, max2.get(), 0.0);

        Optional<Double> max3 = h.max(pcm, "identifier");
        assertEquals(Optional.empty(), max3);
        assertFalse(max3.isPresent());


    }

    @Test
    public void testMin() throws IOException {

        PCM pcm = PCMTestUtil.mkPokemonPCM();
        PCMHelper h = new PCMHelper();
        Optional<Double> min = h.min(pcm, "height");
        assertTrue(min.isPresent());
        assertEquals(1.0, min.get(), 0.0);

        Optional<Double> min2 = h.min(pcm, "weight");
        assertTrue(min2.isPresent());
        assertEquals(1.0, min2.get(), 0.0);

        Optional<Double> min3 = h.min(pcm, "identifier");
        assertEquals(Optional.empty(), min3);
        assertFalse(min3.isPresent());

    }

    @Test
    public void testMinFail() throws IOException {

        PCM pcm = PCMTestUtil.mkPokemonPCM();
        PCMHelper h = new PCMHelper();
        Optional<Double> min = h.min(pcm, "heighht");
        assertFalse(min.isPresent());


        Optional<Double> min2 = h.min(pcm, "wweight");
        assertFalse(min2.isPresent());


        Optional<Double> min3 = h.min(pcm, "identifierrrr");
        assertFalse(min3.isPresent());

    }

    @Test
    public void testMaxFail() throws IOException {

        PCM pcm = PCMTestUtil.mkPokemonPCM();
        PCMHelper h = new PCMHelper();
        Optional<Double> max = h.max(pcm, "heighht");
        assertFalse(max.isPresent());

        // Optional<Integer> min = h.min(pcm, "height");
        Optional<Double> max2 = h.max(pcm, "wweight");
        assertFalse(max2.isPresent());

        Optional<Double> max3 = h.max(pcm, "identifierrrr");
        assertFalse(max3.isPresent());

    }

    @Test
    public void testMaxPokemon() throws IOException {

        PCM pcm = PCMTestUtil.mkPokemonPCM();
        PCMHelper pcmmHelper = new PCMHelper();

        Collection<String> fts = pcmmHelper.collectUniformAndNumericalFeatures(pcm);
        for (String ft : fts) {
            Optional<Double> max = pcmmHelper.max(pcm, ft);
            Optional<Double> min = pcmmHelper.min(pcm, ft);
            assertTrue(max.isPresent());
            assertTrue(min.isPresent());
            System.err.println("Max of feature " + ft + " = " + max.get());
        }

    }

    @Test
    public void testMaxPokemonFail() throws IOException {

        PCM pcm = PCMTestUtil.mkPokemonPCM();
        PCMHelper pcmmHelper = new PCMHelper();

        Collection<String> fts = pcm.getConcreteFeatures().
                stream().
                map(f -> f.getName()).
                collect(Collectors.toList()); // all feature names

        boolean hasFailed = false;
        for (String ft : fts) {
            Optional<Double> max = pcmmHelper.max(pcm, ft);
            Optional<Double> min = pcmmHelper.min(pcm, ft);
            if (!max.isPresent() || !min.isPresent()) {
                assertEquals("identifier", ft);
                hasFailed = true;
            }
        }
        assertTrue(hasFailed);

    }

    @Test
    public void testMaxNokia() throws IOException {

        PCM pcm = PCMUtils.loadPCM("pcms/Comparison_of_Nikon_DSLR_cameras_0.pcm");
        PCMHelper pcmmHelper = new PCMHelper();

        Collection<String> fts = pcmmHelper.collectUniformAndNumericalFeatures(pcm);
        for (String ft : fts) {
            Optional<Double> max = pcmmHelper.max(pcm, ft);
            Optional<Double> min = pcmmHelper.min(pcm, ft);
            assertTrue(max.isPresent());
            assertTrue(min.isPresent());
            System.err.println("Max (min) of feature " + ft + " = " + max.get() + " (" + min.get() + ")");

        }

    }
}
