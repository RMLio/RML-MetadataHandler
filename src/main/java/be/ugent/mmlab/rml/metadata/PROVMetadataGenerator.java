package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.Source;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.PROVVocabulary;
import java.util.Collection;
import org.apache.commons.lang3.RandomStringUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class PROVMetadataGenerator {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(PROVMetadataGenerator.class.getSimpleName());
    
    public void generateDatasetMetaData(URI datasetURI, RMLMapping rmlMapping, 
            RMLDataset dataset, String outputFile, String startTime, String endTime){
        log.debug("PROV Metadata generation...");
        
        //Add Entity type
        Value obj = null;
        try{
            obj = new URIImpl(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.ENTITY_CLASS);
        }
        catch(Exception ex){
            log.error("Could not generate URI" + ex);
        }

        dataset.add(datasetURI, RDF.TYPE, obj);
   
        //Add prov:wasGeneratedBy
        URI pre = new URIImpl(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.WASGENERATEDBY.toString());
        
        Resource mappingActivity = new BNodeImpl(
                                  RandomStringUtils.randomAlphanumeric(10));
        
        dataset.add(datasetURI, pre, mappingActivity);
        
        //Add a prov:Activity
        obj = new URIImpl(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.ACTIVITY_CLASS);
        
        dataset.add(mappingActivity, RDF.TYPE, obj);
        
        addStartEndDateTime(
                dataset, mappingActivity, startTime, endTime);
        
        //Add prov:wasDerivedFrom
        pre = new URIImpl(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.WASDERIVEDFROM.toString());
        
        Collection<TriplesMap> triplesMaps = rmlMapping.getTriplesMaps();
        for(TriplesMap triplesMap : triplesMaps){
            Source source = triplesMap.getLogicalSource().getSource();;
            obj = new LiteralImpl(source.getTemplate());
            dataset.add(datasetURI, pre, obj);
        }
        
    }
    
    public void generateTriplesMapMetaData(URI datasetURI, TriplesMap map, 
            RMLDataset dataset, String outputFile, String startTime, String endTime){
        Value obj;
        log.debug("PROV Triples Map Metadata generation...");
        
        Resource mappingActivity = new BNodeImpl(
                                  RandomStringUtils.randomAlphanumeric(10));
        
        log.debug("mapping activity " + mappingActivity);
        //Add prov:used
        URI pre = new URIImpl(PROVVocabulary.PROV_NAMESPACE + 
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.USED.toString());
        
        //Add prov:used
        addUsed(dataset, map, mappingActivity, pre);
        
        //Add prov:wasGeneratedBy
        pre = new URIImpl(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.WASGENERATEDBY.toString());      
        
        dataset.add(new URIImpl(map.getName()), pre, mappingActivity);
        
        addStartEndDateTime(dataset, mappingActivity, startTime, endTime);
        
    }
    
    public void generateTripleMetaData(RMLDataset originalDataset, TriplesMap map,
            Resource subject, URI predicate, Value object){
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset ;
        
        Resource tripleBN = new BNodeImpl(
                                  RandomStringUtils.randomAlphanumeric(10));
        
        //Add subject
        URI pre = new URIImpl(RDF.NAMESPACE + "subject");
        
        dataset.add(tripleBN, pre, subject);
        
        //Add predicate
        pre = new URIImpl(RDF.NAMESPACE + "predicate");
        
        dataset.add(tripleBN, pre, predicate);
        
        //Add object
        pre = new URIImpl(RDF.NAMESPACE + "object");
        
        dataset.add(tripleBN, pre, object);
        
        dataset.add(tripleBN, 
                        new URIImpl(PROVVocabulary.PROV_NAMESPACE + 
                        PROVVocabulary.PROVTerm.WASGENERATEDBY.toString()),
                        new URIImpl(map.getName()));
    }
    
    private void addStartEndDateTime(RMLDataset metadataDataset, 
            Resource mappingActivity, String startTime, String endTime){ 
        
        //Add prov:startedAtTime
        URI pre = new URIImpl(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.STARTEDATTIME.toString());
        
        Value obj = new LiteralImpl(startTime, 
                new URIImpl("http://www.w3.org/2001/XMLSchema#date"));
        
        metadataDataset.add(mappingActivity, pre, obj);
        
        //Add prov:startedAtTime
        pre = new URIImpl(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.ENDDATETIME.toString());
        
        obj = new LiteralImpl(endTime, new URIImpl("http://www.w3.org/2001/XMLSchema#date"));
        
        metadataDataset.add(mappingActivity, pre, obj);
    }
    
    public void addUsed (RMLDataset metadataDataset, TriplesMap map,
            Resource mappingActivity, URI pre){
        Value obj;
        if(map.getLogicalSource().getSource().getClass().getSimpleName().equals("StdLocalFileSource"))
        obj = new URIImpl("file://" +
                map.getLogicalSource().getSource().getTemplate());
        else
            //TODO: Check if that's sustainable
            obj = new URIImpl(map.getLogicalSource().getSource().getTemplate());
        
        metadataDataset.add(mappingActivity, pre, obj);
    }

}
