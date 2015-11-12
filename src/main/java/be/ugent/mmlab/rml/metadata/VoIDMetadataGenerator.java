package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.VoIDVocabulary;
import java.io.File;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
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
            LoggerFactory.getLogger(VoIDMetadataGenerator.class);
    
    public void generateDatasetMetaData(URI datasetURI, RMLDataset dataset, 
            RMLDataset metadataDataset, String outputFile){
        
        //Add VoID Dataset type
        Value obj = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DATASET_CLASS);

        metadataDataset.add(datasetURI, RDF.TYPE, obj);
        
        //Add void:triples
        URI pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.TRIPLES.toString());

        numberOfTriples = dataset.getNumberOfTriples();
        obj = new LiteralImpl(String.valueOf(numberOfTriples));
         
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:feature
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.FEATURE.toString());
        RDFFormat format = metadataDataset.getFormat();
        obj = new URIImpl(format.getStandardURI().stringValue());
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:distinctSubjects
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DISTINCTSUBJECTS.toString());

        numberOfDistinctSubjects = dataset.getNumberOfDistinctSubjects();
        obj = new LiteralImpl(String.valueOf(numberOfDistinctSubjects));
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:distinctObjects
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DISTINCTOBJECTS.toString());

        numberOfDistinctObjects = dataset.getNumberOfDistinctObjects();
        obj = new LiteralImpl(String.valueOf(numberOfDistinctObjects));
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:entities
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.ENTITIES.toString());

        numberOfDistinctEntities = dataset.getNumberOfDistinctEntities();
        obj = new LiteralImpl(String.valueOf(numberOfDistinctEntities));
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:classes
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.CLASSES.toString());

        numberOfdistinctClasses = dataset.getNumberOfClasses();
        obj = new LiteralImpl(String.valueOf(numberOfdistinctClasses));
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:properties
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.PROPERTIES.toString());

        numberOfdistinctProperties = dataset.getNumberOfProperties();
        obj = new LiteralImpl(String.valueOf(numberOfdistinctProperties));
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:documents
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DOCUMENTS.toString());
        String numberOfDocuments = "1";
        obj = new LiteralImpl(String.valueOf(numberOfDocuments));
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:dataDump
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DATADUMP.toString());
        
        File file = new File(outputFile);
        obj = new URIImpl("file://" + file.getAbsolutePath());
        
        metadataDataset.add(datasetURI, pre, obj);
    }
    
    public Resource generateDatasetFeature(String format) {
        
        URI obj = null;
        switch (format) {
            case "ntriples":
                obj =  new URIImpl("http://www.w3.org/ns/formats/N-Triples");
                break;
            case "n3":
                obj =  new URIImpl("http://www.w3.org/ns/formats/N3");
                break;
            case "turtle":
                obj = new URIImpl("http://www.w3.org/ns/formats/Turtle");
                break;
            case "nquads":
                obj =  new URIImpl("http://www.w3.org/ns/formats/N-Quads");
                break;
            case "rdfxml":
                obj =  new URIImpl("http://www.w3.org/ns/formats/RDF_XML");
                break;
            case "rdfjson":
                obj =  new URIImpl("http://www.w3.org/ns/formats/RDF_JSON");;
                break;
            case "jsonld":
                obj =  new URIImpl("http://www.w3.org/ns/formats/JSON_LD");
                break;
        }
        
        return obj;

    }
    
    public void generateTriplesMapMetaData(
            URI datasetURI, RMLDataset dataset, RMLDataset metadataDataset, 
            TriplesMap triplesMap, String outputFile) {
        Value obj ;
        Resource sub = new URIImpl(triplesMap.getName());
        
        //Add void:triples
        URI pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.TRIPLES.toString());

        obj = new LiteralImpl(String.valueOf(
                dataset.getNumberOfTriples() - numberOfTriples));
        numberOfTriples = dataset.getNumberOfTriples();
        
        metadataDataset.add(sub, pre, obj);
        
        //Add void:dataDump
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DATADUMP);
        
        File file = new File(outputFile);
        obj = new URIImpl("file://" + file.getAbsolutePath());
        
        metadataDataset.add(sub, pre, obj);
        
        //Add void:distinctSubjects
        VoIDVocabulary.VoIDTerm property;
        property = VoIDVocabulary.VoIDTerm.DISTINCTSUBJECTS;
        
        obj = new LiteralImpl(String.valueOf(
                dataset.getNumberOfDistinctSubjects() - numberOfDistinctSubjects));
        numberOfDistinctSubjects = dataset.getNumberOfDistinctSubjects();
        
        generateTriplesMapEntitiesMetadata(sub, metadataDataset, property, obj);
        
        //Add void:distinctObjects
        property = VoIDVocabulary.VoIDTerm.DISTINCTOBJECTS;
        
        obj = new LiteralImpl(String.valueOf(
                dataset.getNumberOfDistinctObjects() - numberOfDistinctObjects));
        numberOfDistinctObjects = dataset.getNumberOfDistinctObjects();
        
        generateTriplesMapEntitiesMetadata(sub, metadataDataset, property, obj);
        
        //Add void:triples
        property = VoIDVocabulary.VoIDTerm.ENTITIES;
        
        obj = new LiteralImpl(String.valueOf(
                dataset.getNumberOfDistinctEntities() - numberOfDistinctEntities));
        numberOfDistinctEntities = dataset.getNumberOfDistinctEntities();
        
        generateTriplesMapEntitiesMetadata(sub, metadataDataset, property, obj);
        
        //Add void:classes
        property = VoIDVocabulary.VoIDTerm.CLASSES;
        
        obj = new LiteralImpl(String.valueOf(
                dataset.getNumberOfClasses() - numberOfdistinctClasses));
        numberOfdistinctClasses = dataset.getNumberOfClasses();
        
        generateTriplesMapEntitiesMetadata(sub, metadataDataset, property, obj);
        
        //Add void:properties
        property = VoIDVocabulary.VoIDTerm.PROPERTIES;
        
        obj = new LiteralImpl(String.valueOf(
                dataset.getNumberOfProperties() - numberOfdistinctProperties));
        numberOfdistinctProperties = dataset.getNumberOfProperties();
        
        generateTriplesMapEntitiesMetadata(sub, metadataDataset, property, obj);
    }
    
    private void generateTriplesMapEntitiesMetadata(
            Resource subject, RMLDataset metadataDataset, 
            VoIDVocabulary.VoIDTerm property, Value obj){
        log.debug("Generating Entities metadata...");
        
        //Add void:entities
        URI pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + property.toString());
        log.debug("pre: " + pre);

        metadataDataset.add(subject, pre, obj);
        
    }

}
