package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.RDFTerm.FunctionTermMap;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.Source;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.PROVVocabulary;

import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
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

    private Map<TriplesMap, Resource> triplesMapNodes = new HashMap<>();
    
    public void generateDatasetMetaData(IRI datasetURI, RMLMapping rmlMapping,
            RMLDataset dataset, String outputFile, String startTime, String endTime){
        log.debug("PROV Metadata generation...");

        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        
        //Add Entity type
        Value obj = null;
        try{
            obj = vf.createIRI(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.ENTITY_CLASS);
        }
        catch(Exception ex){
            log.error("Could not generate URI" + ex);
        }

        dataset.add(datasetURI, RDF.TYPE, obj);
   
        //Add prov:wasGeneratedBy
        IRI pre = vf.createIRI(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.WASGENERATEDBY.toString());
        
        Resource mappingActivity = vf.createBNode(
                                  RandomStringUtils.randomAlphanumeric(10));
        
        dataset.add(datasetURI, pre, mappingActivity);
        
        //Add a prov:Activity
        obj = vf.createIRI(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.ACTIVITY_CLASS);
        
        dataset.add(mappingActivity, RDF.TYPE, obj);
        
        addStartEndDateTime(
                dataset, mappingActivity, startTime, endTime);
        
        //Add prov:wasDerivedFrom
        pre = vf.createIRI(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.WASDERIVEDFROM.toString());
        
        Collection<TriplesMap> triplesMaps = rmlMapping.getTriplesMaps();
        for(TriplesMap triplesMap : triplesMaps){
            Source source = triplesMap.getLogicalSource().getSource();;
            obj = vf.createLiteral(source.getTemplate());
            dataset.add(datasetURI, pre, obj);
        }
        
    }
    
    public void generateTriplesMapMetaData(IRI datasetURI, TriplesMap map,
            RMLDataset dataset, String outputFile, String startTime, String endTime){
        Value obj;
        log.debug("PROV Triples Map Metadata generation...");

        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        
        Resource mappingActivity = vf.createBNode(
                                  RandomStringUtils.randomAlphanumeric(10));
        this.triplesMapNodes.put(map, mappingActivity);
        
        log.debug("mapping activity " + mappingActivity);
        //Add prov:used
        IRI pre = vf.createIRI(PROVVocabulary.PROV_NAMESPACE +
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.USED.toString());
        
        //Add prov:used
        addUsed(dataset, map, mappingActivity, pre);
        
        //Add prov:wasGeneratedBy
        pre = vf.createIRI(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.WASGENERATEDBY.toString());      
        
        dataset.add(vf.createIRI(map.getName()), pre, mappingActivity);
        
        addStartEndDateTime(dataset, mappingActivity, startTime, endTime);
        
    }
    
    public void generateTripleMetaData(RMLDataset originalDataset, TriplesMap map,
            Resource subject, IRI predicate, Value object){
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset ;

        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        
        Resource tripleBN = vf.createBNode(
                                  RandomStringUtils.randomAlphanumeric(10));
        
        //Add subject
        IRI pre = vf.createIRI(RDF.NAMESPACE + "subject");
        
        dataset.add(tripleBN, pre, subject);
        
        //Add predicate
        pre = vf.createIRI(RDF.NAMESPACE + "predicate");
        
        dataset.add(tripleBN, pre, predicate);
        
        //Add object
        pre = vf.createIRI(RDF.NAMESPACE + "object");
        
        dataset.add(tripleBN, pre, object);
        
        dataset.add(tripleBN, 
                        vf.createIRI(PROVVocabulary.PROV_NAMESPACE +
                        PROVVocabulary.PROVTerm.WASGENERATEDBY.toString()),
                        vf.createIRI(map.getName()));
    }

    public void generateFunctionTermMetaData(MetadataRMLDataset dataset, FunctionTermMap functionTermMap, String function, Map<String, String> parameters, List<Value> objects) {

        SimpleValueFactory vf = SimpleValueFactory.getInstance();

        TriplesMap parentMap = functionTermMap.getOwnTriplesMap();
        Resource mappingActivity;
        if (!this.triplesMapNodes.containsKey(parentMap)) {
            mappingActivity = vf.createBNode(
                    RandomStringUtils.randomAlphanumeric(10));
            this.triplesMapNodes.put(parentMap, mappingActivity);
        } else {
            mappingActivity = this.triplesMapNodes.get(parentMap);
        }

        dataset.add(mappingActivity, RDF.TYPE, vf.createIRI(PROVVocabulary.PROV_NAMESPACE, "Activity"));

        Resource execActivity = vf.createBNode(
                RandomStringUtils.randomAlphanumeric(10));
        dataset.add(execActivity, vf.createIRI(PROVVocabulary.PROV_NAMESPACE, "wasInformedBy"), mappingActivity);
        dataset.add(execActivity, vf.createIRI(PROVVocabulary.PROV_NAMESPACE, "used"), vf.createIRI(function));

        for (Object o : parameters.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            Resource input = vf.createBNode(
                    RandomStringUtils.randomAlphanumeric(10));
            dataset.add(input, RDF.VALUE, vf.createLiteral((String) pair.getValue()));
            dataset.add(execActivity, vf.createIRI(PROVVocabulary.PROV_NAMESPACE, "used"), input);
        }

        for (Value o : objects) {
            Resource output = vf.createBNode(
                    RandomStringUtils.randomAlphanumeric(10));
            dataset.add(output, RDF.VALUE, o);
            dataset.add(output, vf.createIRI(PROVVocabulary.PROV_NAMESPACE, "wasGeneratedBy"), execActivity);
        }

        // TODO started/ended

        log.info("EXEC PROV METADATA: " + execActivity.toString() + " - " + function);
    }
    
    private void addStartEndDateTime(RMLDataset metadataDataset, 
            Resource mappingActivity, String startTime, String endTime){
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        
        //Add prov:startedAtTime
        IRI pre = vf.createIRI(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.STARTEDATTIME.toString());
        
        Value obj = vf.createLiteral(startTime,
                vf.createIRI("http://www.w3.org/2001/XMLSchema#date"));
        
        metadataDataset.add(mappingActivity, pre, obj);
        
        //Add prov:startedAtTime
        pre = vf.createIRI(
                PROVVocabulary.PROV_NAMESPACE
                + PROVVocabulary.PROVTerm.ENDDATETIME.toString());
        
        obj = vf.createLiteral(endTime, vf.createIRI("http://www.w3.org/2001/XMLSchema#date"));
        
        metadataDataset.add(mappingActivity, pre, obj);
    }
    
    public void addUsed (RMLDataset metadataDataset, TriplesMap map,
            Resource mappingActivity, IRI pre){
        Value obj;
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        if(map.getLogicalSource().getSource().getClass().getSimpleName().equals("StdLocalFileSource"))
        obj = vf.createIRI("file://" +
                map.getLogicalSource().getSource().getTemplate());
        else
            //TODO: Check if that's sustainable
            obj = vf.createIRI(map.getLogicalSource().getSource().getTemplate());
        
        metadataDataset.add(mappingActivity, pre, obj);
    }

}
