package be.ugent.mmlab.rml.metadata;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.vocabularies.DCATVocabulary;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * RML - Metadata : DCAT Metadata Generator
 *
 * @author andimou
 */
public class DCATMetadataGenerator {
    
    public void generateDatasetMetaData(
            URI datasetURI, RMLDataset metadataDataset){
        //Add DCAT Distribution type
        Resource obj = new URIImpl(
                DCATVocabulary.DCAT_NAMESPACE
                + DCATVocabulary.DcatTerm.DISTRIBUTIION_CLASS);

        metadataDataset.add(datasetURI, RDF.TYPE, obj);
    }

}
