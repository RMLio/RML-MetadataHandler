package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import java.io.File;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
    
    public MetadataGenerator() {
        voidMetadataGenerator = new VoIDMetadataGenerator();
        provMetadataGenerator = new PROVMetadataGenerator();
    }
    
    public MetadataGenerator(String pathToNativeStore) {

        voidMetadataGenerator = new VoIDMetadataGenerator();
        provMetadataGenerator = new PROVMetadataGenerator();

        //generate the datasetURI
        File file = new File(pathToNativeStore);
        datasetURI = new URIImpl("file://" + file.getAbsolutePath().toString());
    }
    
    public MetadataGenerator(
            MetadataRMLDataset metadataDataset, String pathToNativeStore) {
        voidMetadataGenerator = new VoIDMetadataGenerator();
        provMetadataGenerator = new PROVMetadataGenerator();

        //generate the datasetURI
        File file = new File(pathToNativeStore);
        datasetURI = new URIImpl("file://" + file.getAbsolutePath().toString());
    }

    //TODO:Perhaps completely skip this method
    public void generateMetaData(RMLMapping rmlMapping, MetadataRMLDataset dataset, 
            String outputFile, String startTime, String endTime) {
        generateDatasetMetaData(
                rmlMapping, dataset, outputFile, startTime, endTime);
        if(dataset.getMetadataDataset() == null)
            log.debug("No metasata were generated.");
        log.debug("metadata dataset size " + dataset.getMetadataDataset().getSize());
    }

    private void generateDatasetMetaData(RMLMapping rmlMapping,
            MetadataRMLDataset dataset, String outputFile,
            String startTime, String endTime) {
        log.debug("Generating metadata on dataset level...");
        List vocabs = dataset.getMetadataVocab();
        
        if (vocabs == null || vocabs.isEmpty()) {
            log.info("No metadata vocabularies specified, "
                    + "generate metadata for all.");
            provMetadataGenerator.generateDatasetMetaData(
                    datasetURI, rmlMapping, dataset, dataset.getMetadataDataset(),
                    outputFile, startTime, endTime);
            voidMetadataGenerator.generateDatasetMetaData(datasetURI, 
                            dataset, dataset.getMetadataDataset(), outputFile);
            return;
        }
        
        for ( Object vocab : vocabs) {
            switch (vocab.toString()){
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
            MetadataRMLDataset dataset, TriplesMap triplesMap, String outputFile,
            String startDateTime, String endDateTime) {
        log.debug("Generating metadata on Triples Map level...");
        RMLDataset metadataDataset = dataset.getMetadataDataset();
        List vocabs = dataset.getMetadataVocab();
        
        if(vocabs.isEmpty()){
            provMetadataGenerator.generateTriplesMapMetaData(datasetURI, triplesMap,
                    dataset, metadataDataset, outputFile, startDateTime, endDateTime);
            voidMetadataGenerator.generateTriplesMapMetaData(datasetURI, dataset, 
                    metadataDataset, triplesMap, outputFile);
        }
        
        for ( Object vocab : vocabs) {
            switch (vocab.toString()){
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
    
    public void generateTripleMetaData(MetadataRMLDataset dataset, 
            Resource subject, URI predicate, Value object)
    {
        RMLDataset metadataDataset = dataset.getMetadataDataset();
        
        provMetadataGenerator.generateTripleMetaData(dataset, metadataDataset, 
                subject, predicate, object);
    }
}
