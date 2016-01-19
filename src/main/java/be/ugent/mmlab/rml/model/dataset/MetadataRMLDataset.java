package be.ugent.mmlab.rml.model.dataset;

import be.ugent.mmlab.rml.model.TriplesMap;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author andimou
 */
public interface MetadataRMLDataset extends RMLDataset{
    
    public void addReification(
            Resource s, URI p, Value o, TriplesMap map, Resource... contexts);
    
    public int getNumberOfDistinctSubjects();
    
    public int getNumberOfDistinctObjects();
    
    public int getNumberOfDistinctEntities();
    
    public int getNumberOfTriples();
    
    public int getNumberOfClasses();
    
    public int getNumberOfProperties();
    
    public boolean checkDistinctSubject(Resource s);
    
    public boolean checkDistinctObject(Value o);
    
    public boolean checkDistinctClass(Value o);
    
    public boolean checkDistinctProperty(URI p);
    
    public void checkDistinctEntities(Resource s, URI p, Value o);
    
    public String getMetadataLevel();
    
    public String getMetadataFormat();
    
    public RMLDataset getMetadataDataset();
    
    public List getMetadataVocab();
}
