
package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.CoVocabulary;
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
public class CoMetadataGenerator {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(CoMetadataGenerator.class.getSimpleName());
    
    public void generateTripleMetaData(RMLDataset originalDataset, TriplesMap map,
            Resource subject, IRI predicate, Value object, String validation){
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset ;
        SimpleValueFactory vf = SimpleValueFactory.getInstance();

        Resource tripleBN = vf.createIRI(
                                  RandomStringUtils.randomAlphanumeric(10));
        //Add subject
        IRI pre = vf.createIRI(
                CoVocabulary.CO_NAMESPACE + CoVocabulary.COTerm.REQUIRES);
        log.debug("pre " + pre);
        if (validation != null) {
            switch (validation) {
                case "validation":
                    log.debug("Adding validation metadata");
                    IRI valid = vf.createIRI(
                            CoVocabulary.CO_NAMESPACE + CoVocabulary.COTerm.VERIFICATION_CLASS);
                    dataset.add(tripleBN, pre, valid);
                    addTripleDetails(dataset, tripleBN, subject, predicate, object);
                    break;
                case "completion":
                    log.debug("Adding completion metadata");
                    IRI complete = vf.createIRI(
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
            Resource subject, IRI predicate, Value object) {
        //Add subject
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        IRI pre = vf.createIRI(RDF.NAMESPACE + "subject");

        dataset.add(tripleBN, pre, subject);

        //Add predicate
        pre = vf.createIRI(RDF.NAMESPACE + "predicate");

        dataset.add(tripleBN, pre, predicate);

        //Add object
        if (object != null) {
            pre = vf.createIRI(RDF.NAMESPACE + "object");

            dataset.add(tripleBN, pre, object);
        }
    }

}
