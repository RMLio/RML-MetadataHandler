package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import java.io.File;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class MetadataGenerator {

    // Log
    private static final Logger log =
            LoggerFactory.getLogger(MetadataGenerator.class);
    private RMLDataset metadataDataset;
    private URI datasetURI;
    
    public MetadataGenerator(
            RMLDataset metadataDataset, String pathToNativeStore) {
        //generate dataset for the metadata graph
        this.metadataDataset = metadataDataset;

        //generate the datasetURI
        File file = new File(pathToNativeStore);
        datasetURI = new URIImpl("file://" + file.getAbsolutePath().toString());
    }

    //TODO:Perhaps completely skip this method
    public void generateMetaData(String outputFormat,
            RMLDataset sesameDataSet, long startTime, 
            Integer totaldistinctSubjects) {

        generateDatasetMetaData(outputFormat, sesameDataSet, 
                metadataDataset, totaldistinctSubjects);

        //TODO:add metadata this Triples Map started then, finished then and lasted that much
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.info("RML mapping done! Generated "
                + sesameDataSet.getSize() + " in "
                + ((double) duration) / 1000000000 + "s . ");
    }

    private void generateDatasetMetaData(String format,
            RMLDataset dataset, RMLDataset metadataDataset, 
            Integer totaldistinctSubjects) {
        log.debug("Generating metadata on dataset level...");
        
        int numberOfTriples = dataset.getSize();
        
        VoIDMetadataGenerator voidMetadataGenerator = 
                new VoIDMetadataGenerator();
        
        voidMetadataGenerator.generateDatasetMetaData(datasetURI, dataset,
                metadataDataset, numberOfTriples, format);
        
        DCATMetadataGenerator dcatMetadataGenerator = 
                new DCATMetadataGenerator();
        
        dcatMetadataGenerator.generateDatasetMetaData(
                datasetURI, metadataDataset);
        
        log.info("RML mapping done! Generated "
                + metadataDataset.getSize()
                + " metadata triples.");
        
        //TODO: Add void:vocabulary metadata
    }

    public void generateTriplesMapMetaData(
            RMLDataset dataset, RMLDataset metadataDataset,
            TriplesMap triplesMap, Integer numberOfTriples, Integer entities) {
        log.debug("Generating metadata on Triples Map level...");

        VoIDMetadataGenerator voidMetadataGenerator = 
                new VoIDMetadataGenerator();
        
        voidMetadataGenerator.generateTriplesMapMetaData(datasetURI, dataset, 
                metadataDataset, triplesMap, numberOfTriples);

    }
}
