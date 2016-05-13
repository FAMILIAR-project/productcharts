package org.opencompare;

import org.opencompare.api.java.Feature;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;
import org.opencompare.api.java.impl.io.KMFJSONLoader;
import org.opencompare.api.java.io.PCMLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by macher1 on 12/05/2016.
 */
public class PCMUtils {

    /** of course this is weird to put such methods as an helper/utility
     * alternatives are to consider putting such facility as part of PCM class
     * @param pcm
     * @param ftName
     * @return
     */
    public static Feature getFeature(PCM pcm, String ftName) {


        List<Feature> fts = pcm.getConcreteFeatures();
        for (Feature ft : fts) {
            if (ft.getName().equals(ftName)) {
                return ft;
            }
        }
        return null;

    }

    /**
     * A helper method to get a PCM (share the limitation of _loadPCMContainter)
     * @param filename
     * @return
     * @throws IOException
     */
    public static PCM loadPCM(String filename) throws IOException {

        PCMContainer pcmContainer = loadPCMContainer(filename);
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
    public static PCMContainer loadPCMContainer(String filename) throws IOException {

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
