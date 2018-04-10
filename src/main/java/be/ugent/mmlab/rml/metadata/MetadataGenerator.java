package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.RDFTerm.FunctionTermMap;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
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
            LoggerFactory.getLogger(MetadataGenerator.class.getSimpleName());
    private IRI datasetURI;
    private VoIDMetadataGenerator voidMetadataGenerator;
    private PROVMetadataGenerator provMetadataGenerator;
    private CoMetadataGenerator coMetadataGenerator;
    private DCATMetadataGenerator dcatMetadataGenerator;
    private FnOMetadataGenerator fnoMetadataGenerator;
    protected LocalRepositoryManager manager;
    
    public MetadataGenerator() {
        buildMetadataGenerators();
    }
    
    public MetadataGenerator(String pathToNativeStore) {
        SimpleValueFactory vf = SimpleValueFactory.getInstance();

        buildMetadataGenerators();

        //generate the datasetURI
        File file = new File(pathToNativeStore);
        datasetURI = vf.createIRI("file://" + file.getAbsolutePath().toString());
        this.manager = new LocalRepositoryManager(new File(pathToNativeStore));
        try {
            this.manager.initialize();
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
    }
    
    public MetadataGenerator(
            String pathToNativeStore, LocalRepositoryManager manager) {
        SimpleValueFactory vf = SimpleValueFactory.getInstance();

        buildMetadataGenerators();

        this.manager = manager;

        //generate the datasetURI
        File file = new File(pathToNativeStore);
        datasetURI = vf.createIRI("file://" + file.getAbsolutePath().toString());
    }
    
    public MetadataGenerator(
            MetadataRMLDataset metadataDataset, String pathToNativeStore) {
        SimpleValueFactory vf = SimpleValueFactory.getInstance();

        buildMetadataGenerators();

        //generate the datasetURI
        File file = new File(pathToNativeStore);
        datasetURI = vf.createIRI("file://" + file.getAbsolutePath().toString());
    }

    private void buildMetadataGenerators() {
        voidMetadataGenerator = new VoIDMetadataGenerator();
        provMetadataGenerator = new PROVMetadataGenerator();
        coMetadataGenerator = new CoMetadataGenerator();
        dcatMetadataGenerator = new DCATMetadataGenerator();
        fnoMetadataGenerator = new FnOMetadataGenerator();
    }

    //TODO:Perhaps completely skip this method
    public MetadataRMLDataset generateMetaData(
            RMLMapping rmlMapping, MetadataRMLDataset dataset, 
            String outputFile, String startTime, String endTime) {
        try {
            dataset.setRepository(manager.getRepository("metadata"));
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
        generateDatasetMetaData(
                rmlMapping, dataset, outputFile, startTime, endTime);
        if(dataset == null)
            log.debug("No metadata were generated.");
        
        return dataset;
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
                    datasetURI, rmlMapping, dataset,
                    outputFile, startTime, endTime);
            voidMetadataGenerator.generateDatasetMetaData(
                    datasetURI, dataset, outputFile, manager);
            return;
        }
        
        for ( Object vocab : vocabs) {
            switch (vocab.toString()){
                case "prov":
                    log.debug("Generating PROV metadata...");
                    provMetadataGenerator.generateDatasetMetaData(datasetURI, 
                        rmlMapping, dataset, outputFile, startTime, endTime);
                    break;
                    
                case "void":
                    log.debug("Generating VoID metadata...");
                    voidMetadataGenerator.generateDatasetMetaData(
                            datasetURI, dataset, outputFile, manager);
                    break;
                    
                case "dcat":
                    log.debug("Generating DCAT metadata...");
                    dcatMetadataGenerator.generateDatasetMetaData(
                            datasetURI, dataset);
                    break;
                case "co":
                    log.debug("Generating validation metadata...");
            }
        }
        log.info("RML mapping done! Generated "
                + dataset.getSize()
                + " metadata triples.");

    }

    public void generateTriplesMapMetaData(
            MetadataRMLDataset dataset, TriplesMap triplesMap, String outputFile,
            String startDateTime, String endDateTime, LocalRepositoryManager manager) {
        log.debug("Generating metadata on Triples Map level...");
        List vocabs = dataset.getMetadataVocab();
        
        if(vocabs.isEmpty()){
            provMetadataGenerator.generateTriplesMapMetaData(datasetURI, triplesMap,
                    dataset, outputFile, startDateTime, endDateTime);
            voidMetadataGenerator.generateTriplesMapMetaData(datasetURI, dataset, 
                    triplesMap, outputFile, manager);
        }
        
        for ( Object vocab : vocabs) {
            switch (vocab.toString()){
                case "prov":
                    provMetadataGenerator.generateTriplesMapMetaData(datasetURI, 
                    triplesMap, dataset, outputFile, startDateTime, endDateTime);
                    break;
                case "void":
                    voidMetadataGenerator.generateTriplesMapMetaData(datasetURI, 
                    dataset, triplesMap, outputFile, manager);
                    break;
                case "co":
                    provMetadataGenerator.generateTriplesMapMetaData(datasetURI, triplesMap,
                            dataset, outputFile, startDateTime, endDateTime);
                    voidMetadataGenerator.generateTriplesMapMetaData(datasetURI, dataset, 
                            triplesMap, outputFile, manager);
                case "dcat":
                    dcatMetadataGenerator.generateTriplesMapMetaData(
                            datasetURI, dataset, triplesMap);
            }
        }
    }
    
    public void generateTripleMetaData(MetadataRMLDataset dataset, TriplesMap map, 
            Resource subject, IRI predicate, Value object, String validation) {
        Repository tmp = dataset.getRepository();
        List vocabs = dataset.getMetadataVocab();
        
        log.debug("Generating triple metadata...");
        
        try {
            if(!manager.isInitialized())
                manager.initialize();
            Repository repo = manager.getRepository("metadata");
            repo.initialize();
            dataset.setRepository(repo);
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
        
        if(vocabs.isEmpty()){
            provMetadataGenerator.generateTripleMetaData(
                (RMLDataset) dataset, map, subject, predicate, object);
        }
        
        for (Object vocab : vocabs) {
            switch (vocab.toString()) {
                case "prov":
                    provMetadataGenerator.generateTripleMetaData(
                            (RMLDataset) dataset, map, subject, predicate, object);
                    break;
                case "co":
                    log.debug("Generating validation metadata");
                    coMetadataGenerator.generateTripleMetaData((RMLDataset) dataset, 
                            map, subject, predicate, object, validation);
                    break;
                default:
                    log.debug("No option found");
            }
        }

        dataset.setRepository(tmp);
    }

    public void generateFunctionTermMetadata(MetadataRMLDataset dataset, FunctionTermMap functionTermMap, String function, Map<String, Object> parameters, List<Value> objects) {
        Repository tmp = dataset.getRepository();
        List vocabs = dataset.getMetadataVocab();

        log.debug("Generating function term metadata...");

        try {
            if(!manager.isInitialized())
                manager.initialize();
            Repository repo = manager.getRepository("metadata");
            repo.initialize();
            dataset.setRepository(repo);
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }

        if(vocabs.isEmpty()){
            provMetadataGenerator.generateFunctionTermMetaData(dataset, functionTermMap, function, parameters, objects);
            fnoMetadataGenerator.generateFunctionTermMetaData(dataset, functionTermMap, function, parameters, objects);
        }

        for (Object vocab : vocabs) {
            switch (vocab.toString()) {
                case "prov":
                    provMetadataGenerator.generateFunctionTermMetaData(dataset, functionTermMap, function, parameters, objects);
                    break;
                case "fno":
                    fnoMetadataGenerator.generateFunctionTermMetaData(dataset, functionTermMap, function, parameters, objects);
                    break;
                case "co":
                    log.debug("TODO, generateFunctionTermMetadata for `co` vocab");
                    break;
                default:
                    log.debug("No option found");
            }
        }

        dataset.setRepository(tmp);
    }
}
