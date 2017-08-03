package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.RDFTerm.FunctionTermMap;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.Source;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.FnVocabulary;
import be.ugent.mmlab.rml.vocabularies.PROVVocabulary;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RML Processor
 *
 * @author bjdmeest
 */
public class FnOMetadataGenerator {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(FnOMetadataGenerator.class.getSimpleName());

    private Map<TriplesMap, Resource> triplesMapNodes = new HashMap<>();

    public void generateFunctionTermMetaData(MetadataRMLDataset dataset, FunctionTermMap functionTermMap, String function, Map<String, String> parameters, List<Value> objects) {

        SimpleValueFactory vf = SimpleValueFactory.getInstance();

        Resource execActivity = vf.createBNode(
                RandomStringUtils.randomAlphanumeric(10));
        dataset.add(execActivity, RDF.TYPE, vf.createIRI(FnVocabulary.FNO_NAMESPACE, "Execution"));
        dataset.add(execActivity, vf.createIRI(FnVocabulary.FNO_NAMESPACE, "executes"), vf.createIRI(function));
        dataset.add(execActivity, RDF.TYPE, vf.createIRI(PROVVocabulary.PROV_NAMESPACE, "Execution"));
        dataset.add(execActivity, RDF.TYPE, vf.createIRI(PROVVocabulary.PROV_NAMESPACE, "Execution"));

        for (Object o : parameters.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            Resource input = vf.createBNode(
                    RandomStringUtils.randomAlphanumeric(10));
            dataset.add(input, RDF.VALUE, vf.createLiteral((String) pair.getValue()));
            dataset.add(execActivity, vf.createIRI((String) pair.getKey()), input);
        }

        for (Value o : objects) {
            Resource output = vf.createBNode(
                    RandomStringUtils.randomAlphanumeric(10));
            dataset.add(output, RDF.VALUE, o);
            dataset.add(execActivity, vf.createIRI(FnVocabulary.FNO_NAMESPACE, "output"), output);
        }

        // TODO started/ended

        log.info("EXEC PROV METADATA: " + execActivity.toString() + " - " + function);
    }

}
