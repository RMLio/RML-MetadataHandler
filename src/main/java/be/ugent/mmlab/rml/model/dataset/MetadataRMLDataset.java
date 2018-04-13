package be.ugent.mmlab.rml.model.dataset;

import be.ugent.mmlab.rml.model.TriplesMap;
import java.io.File;
import java.util.List;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;

/**
 *
 * @author andimou
 */
public interface MetadataRMLDataset extends RMLDataset{
    
    /**
     *
     * @param s
     * @param p
     * @param o
     * @param map
     * @param contexts
     */
    public void addReification(TriplesMap map,
            Resource s, IRI p, Value o, Resource... contexts);
    
    /**
     *
     * @return
     */
    public int getNumberOfDistinctSubjects();
    
    /**
     *
     * @return
     */
    public int getNumberOfDistinctObjects();
    
    /**
     *
     * @return
     */
    public int getNumberOfDistinctEntities();
    
    /**
     *
     * @return
     */
    public int getNumberOfTriples();
    
    /**
     *
     * @return
     */
    public int getNumberOfClasses();
    
    /**
     *
     * @return
     */
    public int getNumberOfProperties();
    
    /**
     *
     */
    public void setNumbers();
    
    /**
     *
     * @param s
     * @return
     */
    public boolean checkDistinctSubject(Resource s);
    
    /**
     *
     * @param o
     * @return
     */
    public boolean checkDistinctObject(Value o);
    
    /**
     *
     * @param o
     * @return
     */
    public boolean checkDistinctClass(Value o);
    
    /**
     *
     * @param p
     * @return
     */
    public boolean checkDistinctProperty(IRI p);
    
    /**
     *
     * @param s
     * @param p
     * @param o
     */
    public void checkDistinctEntities(Resource s, IRI p, Value o);
    
    /**
     *
     * @return
     */
    @Override
    public String getMetadataLevel();
    
    /**
     *
     * @return
     */
    public String getMetadataFormat();
   
    
    /**
     *
     * @return
     */
    public File getTarget();
    
    /**
     *
     * @return
     */
    public String getID();
    
    /**
     *
     */
    public void closeSubRepository();
    
    /**
     *
     * @param repository
     */
    public void setRepository(Repository repository);
    
    /**
     *
     * @param metadataLevel
     * @param metadataFormat
     * @param metadataVocab
     */
    public void setDatasetMetadata(
            String metadataLevel, String metadataFormat, String metadataVocab);
}
