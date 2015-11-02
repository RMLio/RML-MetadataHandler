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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML - Metadata : VoID Metadata Generator
 *
 * @author andimou
 */
public class VoIDMetadataGenerator {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(VoIDMetadataGenerator.class);
    
    public void generateDatasetMetaData(
            URI datasetURI, RMLDataset dataset, RMLDataset metadataDataset, 
            Integer numberOfTriples, String format){
        
        //Add VoID Dataset type
        Value obj = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DATASET_CLASS);

        metadataDataset.add(datasetURI, RDF.TYPE, obj);
        
        //Add void:triples
        URI pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.TRIPLES.toString());

        obj = new LiteralImpl(String.valueOf(numberOfTriples));
        
        metadataDataset.add(datasetURI, pre, obj);
        
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.FEATURE.toString());
        
        obj = generateDatasetFeature(format);
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:distinctSubjects
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DISTINCTSUBJECTS.toString());

        obj = new LiteralImpl(String.valueOf(dataset.getDistinctSubjects()));
        
        metadataDataset.add(datasetURI, pre, obj);
        
        //Add void:distinctObjects
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DISTINCTOBJECTS.toString());

        obj = new LiteralImpl(String.valueOf(dataset.getDistinctObjects()));
        
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
                obj = new URIImpl("http://www.w3.org/ns/formats/turtle");
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
            TriplesMap triplesMap, Integer numberOfTriples) {
        Resource sub = new URIImpl(triplesMap.getName());
        
        //Add void:triples
        URI pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.TRIPLES.toString());

        Value obj = new LiteralImpl(String.valueOf(numberOfTriples));
        
        metadataDataset.add(sub, pre, obj);
        
        pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + VoIDVocabulary.VoIDTerm.DATADUMP);
        
        File file = new File(
                triplesMap.getLogicalSource().getSource().getTemplate());
        
        obj = new URIImpl("file://" + file.getAbsolutePath());
        
        metadataDataset.add(datasetURI, pre, obj);
        
        VoIDVocabulary.VoIDTerm property = 
                VoIDVocabulary.VoIDTerm.DISTINCTSUBJECTS;
        generateTriplesMapEntitiesMetadata(datasetURI, metadataDataset, 
                property, dataset.getDistinctSubjects());
        
        property = VoIDVocabulary.VoIDTerm.DISTINCTOBJECTS;
        generateTriplesMapEntitiesMetadata(datasetURI, metadataDataset, 
                property, dataset.getDistinctObjects());
    }
    
    private void generateTriplesMapEntitiesMetadata(
            URI datasetURI, RMLDataset metadataDataset, 
            VoIDVocabulary.VoIDTerm property, Integer entities){
        log.debug("Generating Entities metadata...");
        
        //Add void:entities
        URI pre = new URIImpl(
                VoIDVocabulary.VOID_NAMESPACE
                + property.toString());
        log.debug("pre: " + pre);

        Value obj = new LiteralImpl(String.valueOf(entities));
        
        metadataDataset.add(datasetURI, pre, obj);
        
    }

}
