package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.RMLMapping;
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
    private URI datasetURI;
    private VoIDMetadataGenerator voidMetadataGenerator;
    private PROVMetadataGenerator provMetadataGenerator;
    
    public MetadataGenerator(String pathToNativeStore) {

        voidMetadataGenerator = new VoIDMetadataGenerator();
        provMetadataGenerator = new PROVMetadataGenerator();

        //generate the datasetURI
        File file = new File(pathToNativeStore);
        datasetURI = new URIImpl("file://" + file.getAbsolutePath().toString());
    }
    
    public MetadataGenerator(
            RMLDataset metadataDataset, String pathToNativeStore) {
        voidMetadataGenerator = new VoIDMetadataGenerator();
        provMetadataGenerator = new PROVMetadataGenerator();

        //generate the datasetURI
        File file = new File(pathToNativeStore);
        datasetURI = new URIImpl("file://" + file.getAbsolutePath().toString());
    }

    //TODO:Perhaps completely skip this method
    public void generateMetaData(RMLMapping rmlMapping, RMLDataset dataset, 
            String outputFile, String startTime, String endTime) {

        generateDatasetMetaData(rmlMapping, dataset, outputFile, startTime, endTime);
    }

    private void generateDatasetMetaData(RMLMapping rmlMapping,
            RMLDataset dataset, String outputFile,
            String startTime, String endTime) {
        log.debug("Generating metadata on dataset level...");
        String[] vocabs = dataset.getMetadataVocab();
        
        if (vocabs.length == 0) {
            provMetadataGenerator.generateDatasetMetaData(datasetURI,
                    rmlMapping, dataset, dataset.getMetadataDataset(),
                    outputFile, startTime, endTime);
            voidMetadataGenerator.generateDatasetMetaData(datasetURI, 
                            dataset, dataset.getMetadataDataset(), outputFile);
        }
        
        for (String vocab : vocabs) {
            switch (vocab){
                case "prov":
                    log.debug("Generating PROV metadata...");
                    provMetadataGenerator.generateDatasetMetaData(datasetURI, 
                        rmlMapping, dataset, dataset.getMetadataDataset(), 
                        outputFile, startTime, endTime);
                    break;
                    
                case "void":
                    log.debug("Generating VoID metadata...");
                    voidMetadataGenerator.generateDatasetMetaData(datasetURI, 
                            dataset, dataset.getMetadataDataset(), outputFile);
                    break;
                    
                case "dcat":
                    DCATMetadataGenerator dcatMetadataGenerator =
                            new DCATMetadataGenerator();
                    dcatMetadataGenerator.generateDatasetMetaData(
                            datasetURI, dataset.getMetadataDataset());
                    break;
            }
        }
        log.info("RML mapping done! Generated "
                + dataset.getMetadataDataset().getSize()
                + " metadata triples.");

    }

    public void generateTriplesMapMetaData(
            RMLDataset dataset, TriplesMap triplesMap, String outputFile,
            String startDateTime, String endDateTime) {
        log.debug("Generating metadata on Triples Map level...");
        RMLDataset metadataDataset = dataset.getMetadataDataset();
        String[] vocabs = dataset.getMetadataVocab();
        
        if(vocabs.length == 0){
            provMetadataGenerator.generateTriplesMapMetaData(datasetURI, triplesMap,
                    dataset, metadataDataset, outputFile, startDateTime, endDateTime);
            voidMetadataGenerator.generateTriplesMapMetaData(datasetURI, dataset, 
                    metadataDataset, triplesMap, outputFile);
        }
        
        for (String vocab : vocabs) {
            switch (vocab){
                case "prov":
                    provMetadataGenerator.generateTriplesMapMetaData(datasetURI, triplesMap, 
                    dataset, metadataDataset, outputFile, startDateTime, endDateTime);
                    break;
                case "void":
                    voidMetadataGenerator.generateTriplesMapMetaData(datasetURI, dataset, 
                    metadataDataset, triplesMap, outputFile);
                    break;
            }
        }
        
        
        
        
        
        

    }
}
