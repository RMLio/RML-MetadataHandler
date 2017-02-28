package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.DCATVocabulary;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;

/**
 * RML - Metadata : DCAT Metadata Generator
 *
 * @author andimou
 */
public class DCATMetadataGenerator {
    
    public void generateDatasetMetaData(
            IRI datasetURI, RMLDataset metadataDataset){
        //Add DCAT Distribution type
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        Resource obj = vf.createIRI(
                DCATVocabulary.DCAT_NAMESPACE
                + DCATVocabulary.DcatTerm.DISTRIBUTIION_CLASS);

        metadataDataset.add(datasetURI, RDF.TYPE, obj);
    }
    
    public void generateTriplesMapMetaData(
            IRI datasetURI, RMLDataset metadataDataset, TriplesMap triplesMap){
        triplesMap.getLogicalSource().getSource().getTemplate();
        
        //Add DCAT Distribution type
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        Resource obj = vf.createIRI(
                DCATVocabulary.DCAT_NAMESPACE
                + DCATVocabulary.DcatTerm.DISTRIBUTIION_CLASS);

        metadataDataset.add(datasetURI, RDF.TYPE, obj);
    }

}
