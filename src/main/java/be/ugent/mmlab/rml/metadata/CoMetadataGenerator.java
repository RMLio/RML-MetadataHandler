
package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.CoVocabulary;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.BNodeImpl;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class CoMetadataGenerator {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(CoMetadataGenerator.class.getSimpleName());
    
    public void generateTripleMetaData(RMLDataset originalDataset, TriplesMap map,
            Resource subject, URI predicate, Value object, String validation){
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset ;
        
        Resource tripleBN = new BNodeImpl(
                                  RandomStringUtils.randomAlphanumeric(10));
        //Add subject
        URI pre = new URIImpl(
                CoVocabulary.CO_NAMESPACE + CoVocabulary.COTerm.REQUIRES);
        log.debug("pre " + pre);
        if (validation != null) {
            switch (validation) {
                case "validation":
                    log.debug("Adding validation metadata");
                    URIImpl valid = new URIImpl(
                            CoVocabulary.CO_NAMESPACE + CoVocabulary.COTerm.VERIFICATION_CLASS);
                    dataset.add(tripleBN, pre, valid);
                    addTripleDetails(dataset, tripleBN, subject, predicate, object);
                    break;
                case "completion":
                    log.debug("Adding completion metadata");
                    URIImpl complete = new URIImpl(
                            CoVocabulary.CO_NAMESPACE + CoVocabulary.COTerm.COMPLETION_CLASS);
                    dataset.add(tripleBN, pre, complete);
                    addTripleDetails(dataset, tripleBN, subject, predicate, object);
                    break;
                default:
                    log.debug("no option");
            }
        }
        
        /*dataset.add(tripleBN, 
                        new URIImpl(PROVVocabulary.PROV_NAMESPACE + 
                        PROVVocabulary.PROVTerm.WASGENERATEDBY.toString()),
                        new URIImpl(map.getName()));*/
    }
    
    private void addTripleDetails(MetadataRMLDataset dataset, Resource tripleBN,
            Resource subject, URI predicate, Value object) {
        //Add subject
        URI pre = new URIImpl(RDF.NAMESPACE + "subject");

        dataset.add(tripleBN, pre, subject);

        //Add predicate
        pre = new URIImpl(RDF.NAMESPACE + "predicate");

        dataset.add(tripleBN, pre, predicate);

        //Add object
        if (object != null) {
            pre = new URIImpl(RDF.NAMESPACE + "object");

            dataset.add(tripleBN, pre, object);
        }
    }

}
