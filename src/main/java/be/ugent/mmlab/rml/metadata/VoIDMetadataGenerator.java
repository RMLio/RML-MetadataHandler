package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.VoIDVocabulary;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML - Metadata : VoID Metadata Generator
 *
 * @author andimou
 */
public class VoIDMetadataGenerator {
    Integer numberOfdistinctClasses = 0, numberOfdistinctProperties = 0,
            numberOfTriples = 0, numberOfDistinctEntities = 0, 
            numberOfDistinctSubjects = 0, numberOfDistinctObjects = 0;
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(
            VoIDMetadataGenerator.class.getSimpleName());
    
    public void generateDatasetMetaData(IRI datasetURI, MetadataRMLDataset dataset,
            String outputFile, LocalRepositoryManager manager){
        Set<String> repos;
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        log.debug("VoID Metadata generation...");
        
        //Add VoID Dataset type
        Value obj = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DATASET_CLASS);

        dataset.add(datasetURI, RDF.TYPE, obj);
        
        //Add void:triples
        IRI pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.TRIPLES.toString());

        if(dataset.getMetadataLevel().equals("dataset"))
            numberOfTriples = getDatasetSize( dataset, manager);
        obj = vf.createLiteral(String.valueOf(numberOfTriples));
         
        dataset.add(datasetURI, pre, obj);
        
        //Add void:feature
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.FEATURE.toString());
        RDFFormat format = dataset.selectFormat(dataset.getMetadataFormat());

        obj = vf.createIRI(format.getStandardURI().stringValue());
        
        dataset.add(datasetURI, pre, obj);
        
        //Add void:distinctSubjects
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DISTINCTSUBJECTS.toString());

        if(dataset.getMetadataLevel().equals("dataset"))
            numberOfDistinctSubjects = dataset.getNumberOfDistinctSubjects();
        obj = vf.createLiteral(String.valueOf(numberOfDistinctSubjects));
        
        dataset.add(datasetURI, pre, obj);
        
        //Add void:distinctObjects
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DISTINCTOBJECTS.toString());

        if(dataset.getMetadataLevel().equals("dataset"))
            numberOfDistinctObjects = dataset.getNumberOfDistinctObjects();
        obj = vf.createLiteral(String.valueOf(numberOfDistinctObjects));
        
        dataset.add(datasetURI, pre, obj);
        
        //Add void:entities
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.ENTITIES.toString());

        if(dataset.getMetadataLevel().equals("dataset"))
            numberOfDistinctEntities = dataset.getNumberOfDistinctEntities();
        obj = vf.createLiteral(String.valueOf(numberOfDistinctEntities));
        
        dataset.add(datasetURI, pre, obj);
        
        //Add void:classes
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.CLASSES.toString());

        if(dataset.getMetadataLevel().equals("dataset"))
            numberOfdistinctClasses = dataset.getNumberOfClasses();
        obj = vf.createLiteral(String.valueOf(numberOfdistinctClasses));
        
        dataset.add(datasetURI, pre, obj);
        
        //Add void:properties
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.PROPERTIES.toString());

        if(dataset.getMetadataLevel().equals("dataset"))
            numberOfdistinctProperties = dataset.getNumberOfProperties();
        obj = vf.createLiteral(String.valueOf(numberOfdistinctProperties));
        
        dataset.add(datasetURI, pre, obj);
        
        //Add void:documents
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DOCUMENTS.toString());
        String numberOfDocuments = "1";
        if(dataset.getMetadataLevel().equals("triplesmap")){
            try {
                repos = manager.getRepositoryIDs();
                numberOfDocuments = String.valueOf(repos.size() - 3);
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }
        }
        obj = vf.createLiteral(String.valueOf(numberOfDocuments));
        
        dataset.add(datasetURI, pre, obj);
        
        //Add void:dataDump
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DATADUMP.toString());

        File file = new File(outputFile);
        obj = vf.createIRI("file://" + file.getAbsolutePath());
        
        dataset.add(datasetURI, pre, obj);
    }
    
    public Resource generateDatasetFeature(String format) {
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        IRI obj = null;
        switch (format) {
            case "ntriples":
                obj =  vf.createIRI("http://www.w3.org/ns/formats/N-Triples");
                break;
            case "n3":
                obj =  vf.createIRI("http://www.w3.org/ns/formats/N3");
                break;
            case "turtle":
                obj = vf.createIRI("http://www.w3.org/ns/formats/Turtle");
                break;
            case "nquads":
                obj = vf.createIRI("http://www.w3.org/ns/formats/N-Quads");
                break;
            case "rdfxml":
                obj = vf.createIRI("http://www.w3.org/ns/formats/RDF_XML");
                break;
            case "rdfjson":
                obj = vf.createIRI("http://www.w3.org/ns/formats/RDF_JSON");;
                break;
            case "jsonld":
                obj =  vf.createIRI("http://www.w3.org/ns/formats/JSON_LD");
                break;
        }
        
        return obj;

    }
    
    public void generateTriplesMapMetaData(
            IRI datasetURI, MetadataRMLDataset dataset, TriplesMap triplesMap,
            String outputFile, LocalRepositoryManager manager) {
        Value obj;
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        Resource sub = vf.createIRI(triplesMap.getName());

        //Add void:triples
        IRI pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.TRIPLES.toString());
        int size = getDatasetSize(dataset, triplesMap, manager);
        numberOfTriples += size;
        obj = vf.createLiteral(String.valueOf(size));
        dataset.add(sub, pre, obj);
        
        //Add void:dataDump
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DATADUMP);
        
        File file = new File(outputFile);
        String[] name = triplesMap.getName().split("#");
        try {
        obj = vf.createIRI("file://"
                + file.getCanonicalPath().replaceAll("(\\.[a-zA-Z0-9]*)", "1" + "$1"));
        } catch (IOException ex) {
            log.error("IO Exception " + ex);
        }
        
        dataset.add(sub, pre, obj);
        
        //Add void:subset
        pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.SUBSET);
        obj = vf.createIRI(triplesMap.getName());
        dataset.add(vf.createIRI("file://" + file.getAbsolutePath()), pre, obj);
        
        //Add void:distinctSubjects
        VoIDVocabulary.VoIDTerm property;
        property = VoIDVocabulary.VoIDTerm.DISTINCTSUBJECTS;
        
        obj = vf.createLiteral(String.valueOf(dataset.getNumberOfDistinctSubjects()));
        numberOfDistinctSubjects += dataset.getNumberOfDistinctSubjects();
        
        generateTriplesMapEntitiesMetadata(sub, dataset, property, obj);
        
        //Add void:distinctObjects
        property = VoIDVocabulary.VoIDTerm.DISTINCTOBJECTS;
        
        obj = vf.createLiteral(String.valueOf(dataset.getNumberOfDistinctObjects()));
        numberOfDistinctObjects += dataset.getNumberOfDistinctObjects();
        
        generateTriplesMapEntitiesMetadata(sub, dataset, property, obj);
        
        //Add void:entities
        property = VoIDVocabulary.VoIDTerm.ENTITIES;
        
        obj = vf.createLiteral(String.valueOf(dataset.getNumberOfDistinctEntities()));
        numberOfDistinctEntities += dataset.getNumberOfDistinctEntities();
        
        generateTriplesMapEntitiesMetadata(sub, dataset, property, obj);
        
        //Add void:classes
        property = VoIDVocabulary.VoIDTerm.CLASSES;
        
        obj = vf.createLiteral(String.valueOf(dataset.getNumberOfClasses()));
        numberOfdistinctClasses += dataset.getNumberOfClasses();
        
        generateTriplesMapEntitiesMetadata(sub, dataset, property, obj);
        
        //Add void:properties
        property = VoIDVocabulary.VoIDTerm.PROPERTIES;
        
        obj = vf.createLiteral(String.valueOf(dataset.getNumberOfProperties()));
        numberOfdistinctProperties += dataset.getNumberOfProperties();
        
        generateTriplesMapEntitiesMetadata(sub, dataset, property, obj);
    }
    
    private void generateTriplesMapEntitiesMetadata(
            Resource subject, RMLDataset metadataDataset, 
            VoIDVocabulary.VoIDTerm property, Value obj){
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        //Add void:entities
        IRI pre = vf.createIRI(
                VoIDVocabulary.VOID_NAMESPACE
                + property.toString());

        metadataDataset.add(subject, pre, obj);
        
    }
    
    private int getDatasetSize(MetadataRMLDataset dataset, TriplesMap map,
            LocalRepositoryManager manager){
        int size = 0;
        String[] name = map.getName().split("#");
        try {
            dataset.setRepository(manager.getRepository("1"));
            size = dataset.getSize();
            dataset.setRepository(manager.getRepository("metadata"));
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
        return size;
    }
    
    private int getDatasetSize(
            MetadataRMLDataset dataset, LocalRepositoryManager manager){
        int size = 0;
        try {
            dataset.setRepository(manager.getRepository(dataset.getID()));
            size = dataset.getSize();
            dataset.setRepository(manager.getRepository("metadata"));
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
        return size;
    }
}
