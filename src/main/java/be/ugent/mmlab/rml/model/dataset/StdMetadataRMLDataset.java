package be.ugent.mmlab.rml.model.dataset;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.vocabularies.PROVVocabulary;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class StdMetadataRMLDataset extends StdRMLDataset implements MetadataRMLDataset {
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(StdMetadataRMLDataset.class);
    
    protected Integer 
            distinctClasses = 0, distinctProperties = 0,
            distinctSubjects = 0, distinctObjects = 0, 
            distinctEntities = 0, triples = 0;
    protected String metadataLevel = "None";
    protected String metadataFormat = null;
    protected RMLDataset metadataDataset ;
    protected List metadataVocab;
    
    public StdMetadataRMLDataset() {
        this(false);
    }

    public StdMetadataRMLDataset(boolean inferencing) {
        super(inferencing);
    }
    
    //TODO: Spring it
    @Override
    public void add(Resource s, URI p, Value o, Resource... contexts) {
        if (log.isDebugEnabled()) {
            log.debug("Add triple (" + s.stringValue()
                    + ", " + p.stringValue() + ", " + o.stringValue() + ").");
        }
        try {
            RepositoryConnection con = repository.getConnection();
            try {
                ValueFactory myFactory = con.getValueFactory();
                Statement st = myFactory.createStatement((Resource) s, p,
                        (Value) o);
                con.add(st, contexts);
                con.commit();
            } catch (Exception ex) {
                log.error("Exception " + ex);
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            log.error("Exception " + ex);
        }
    }
    
    @Override
    public void addReification(
            Resource s, URI p, Value o, TriplesMap map, Resource... contexts) {
        
        log.debug("Add triple (" + s.stringValue()
                + ", " + p.stringValue() + ", " + o.stringValue() + ").");
        
        try {
            RepositoryConnection con = repository.getConnection();
            try {
                ValueFactory myFactory = con.getValueFactory();
                Resource triple = new BNodeImpl(
                        RandomStringUtils.randomAlphanumeric(10));
                Statement st = myFactory.createStatement(triple, RDF.TYPE,
                        RDF.STATEMENT);
                con.add(st, contexts);
                
                //Add subject
                st = myFactory.createStatement(triple, RDF.SUBJECT, (Value) s);
                con.add(st, contexts);
                
                //Add predicate
                st = myFactory.createStatement(triple, RDF.PREDICATE, p);
                con.add(st, contexts);
                
                //Add object
                st = myFactory.createStatement(triple, RDF.OBJECT, o);
                con.add(st, contexts);
                
                //Add prov:wasGeneratedBy
                //TODO:Make it point to the SM/POM after skolemization
                st = myFactory.createStatement(triple, 
                        new URIImpl(PROVVocabulary.PROV_NAMESPACE + 
                        PROVVocabulary.PROVTerm.WASGENERATEDBY.toString()),
                        new URIImpl(map.getName()));
                con.add(st, contexts);
                
                con.commit();                
            } catch (Exception ex) {
                log.error("Exception " + ex);
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            log.error("Exception " + ex);
        }
    }
   
    /**
     *
     * @return
     */
    @Override
    public int getNumberOfDistinctSubjects() {
        return distinctSubjects;
    }

    /**
     *
     * @return
     */
    @Override
    public int getNumberOfDistinctObjects() {
        return distinctObjects;
    }
    
    /**
     *
     * @return
     */
    @Override
    public int getNumberOfDistinctEntities() {
        return distinctEntities;
    }
    
    /**
     *
     * @return
     */
    @Override
    public int getNumberOfTriples() {
        return triples;
    }
    
    /**
     *
     * @return
     */
    @Override
    public int getNumberOfClasses() {
        return distinctClasses;
    }
    
    /**
     *
     * @return
     */
    @Override
    public int getNumberOfProperties() {
        return distinctProperties;
    }
    
    @Override
    public boolean checkDistinctSubject(Resource s) {
        RepositoryConnection con = null;
        try {
            con = repository.getConnection();
            RepositoryResult<Statement> results = 
                    con.getStatements(s, null, null, true);
            if(!results.hasNext())
                return true;
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }
        }
        return false;
    }

    @Override
    public boolean checkDistinctObject(Value o) {
        RepositoryConnection con = null;
        try {
            con = repository.getConnection();
            //TODO: Change the following to con.hasStatements
            RepositoryResult<Statement> results = 
                    con.getStatements(null, null, o, true);
            if (!results.hasNext()) {
                return true;
            }
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }
        }
        return false;
    }
    
    @Override
    public boolean checkDistinctClass(Value o) {
        RepositoryConnection con = null;
        try {
            con = repository.getConnection();
            RepositoryResult<Statement> results = 
                    con.getStatements(null, RDF.TYPE, o, true);
            if (!results.hasNext()) {
                return true;
            }
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }
        }
        return false;
    }
    
    @Override
    public boolean checkDistinctProperty(URI p) {
        RepositoryConnection con = null;
        try {
            con = repository.getConnection();
            RepositoryResult<Statement> results = 
                    con.getStatements(null, p, null, true);
            if (!results.hasNext()) {
                return true;
            }
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }
        }
        return false;
    }
    
    @Override
    public void checkDistinctEntities(Resource s, URI p, Value o){
        if(checkDistinctSubject(s)){
            ++distinctSubjects;
            if(checkDistinctObject(s))
                ++distinctEntities;
        }
        
        if(checkDistinctObject(o)){
            ++distinctObjects;
            if(!o.getClass().getSimpleName().equals("LiteralImpl") 
                    && checkDistinctSubject((Resource) o))
                ++distinctEntities;
        }
        
        if(p.equals(RDF.TYPE) && checkDistinctClass(o))
            ++distinctClasses;
        if(checkDistinctProperty(p))
            ++distinctProperties;
    }
    
    public RDFFormat getFormat(){
        return format;
    }
    
    public void setDatasetMetadata(RMLDataset metadataDataset, 
            String metadataLevel, String metadataFormat, String metadataVocab){
        log.info("Setting up medata dataset configuration...");
        setMetadataLevel(metadataLevel);
        setMetadataFormat(metadataFormat);
        setMetadataDataset(metadataDataset);
        setMetadataVocab(metadataVocab);
    }
    
    private void setMetadataLevel(String metadataLevel){
        this.metadataLevel = metadataLevel;
    }
    
    private void setMetadataFormat(String metadataFormat){
        this.metadataFormat = metadataFormat;
    }
    
    private void setMetadataDataset(RMLDataset metadataDataset){
        this.metadataDataset = metadataDataset;
    }
    
    private void setMetadataVocab(String metadataVocab){
        String[] vocabs = null;
        if(metadataVocab != null){
            vocabs = metadataVocab.split(",");
        }
        this.metadataVocab = new ArrayList();
        if(vocabs != null)
            for(String vocab:vocabs)
                this.metadataVocab.add(vocab);
    }
    
    @Override
    public String getMetadataLevel(){
        return metadataLevel;
    }
    
    @Override
    public String getMetadataFormat(){
        return metadataFormat;
    }
    
    @Override
    public RMLDataset getMetadataDataset(){
        return metadataDataset;
    }
    
    @Override
    public List getMetadataVocab(){
        return metadataVocab;
    }
}

