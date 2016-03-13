package be.ugent.mmlab.rml.model.dataset;

import be.ugent.mmlab.rml.model.TriplesMap;
import java.io.File;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 * 
 * @author andimou
 */
public class MetadataFileDataset extends StdMetadataRMLDataset implements MetadataRMLDataset {

    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(
            MetadataFileDataset.class.getSimpleName());
    
    private File target;
    private String ID = null;
    
    /**
     *
     * @param target
     * @param outputFormat
     * @param manager
     * @param repositoryID
     */
    public MetadataFileDataset(LocalRepositoryManager manager,
            String target, String outputFormat, String repositoryID) {
        this.ID = repositoryID;
        this.target = new File(target);
        this.manager = manager;
    }
    
    @Override
    public File getTarget(){
        return this.target;
    }
    
    @Override
    public String getID(){
        return this.ID;
    }
    
    @Override
    public void setRepository(Repository repository){
        this.repository = repository;
    }
    
    @Override
    public void addRepository(String repositoryID, LocalRepositoryManager manager) {
        String indexes = "spoc";
            SailRepositoryConfig repositoryTypeSpec = 
                    new SailRepositoryConfig(new NativeStoreConfig(indexes));
            RepositoryConfig repConfig = 
                    new RepositoryConfig(repositoryID, repositoryTypeSpec);
        try {
            manager.addRepositoryConfig(repConfig);
            repository.initialize();
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } catch (RepositoryConfigException ex) {
            log.error("Repository config Exception " + ex);
        }
    }
    
    @Override
    public void addToRepository(TriplesMap map, 
        Resource s, URI p, Value o, Resource... contexts) {
        try {
            //TODO: Spring it!
            String[] name = map.getName().split("#");
            Repository currentRepo = manager.getRepository(name[1]);
            RepositoryConnection con = currentRepo.getConnection();
            addTriple(con, s, p, o, contexts);
            con.close();
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
    }
    
    @Override
    public void add(Resource s, URI p, Value o, Resource... contexts) {
        //log.debug("Add triple (" + s.stringValue()
        //        + ", " + p.stringValue() + ", " + o.stringValue() + ").");
        
        try {
            RepositoryConnection con = repository.getConnection();
            
            try {
                addTriple(con, s, p, o, contexts);
                if (metadataLevel.equals("triplesmap")) {
                    ValueFactory myFactory = con.getValueFactory();
                    Statement st = myFactory.createStatement((Resource) s, p,
                            (Value) o);
                    checkDistinctEntities(s, p, o);
                    con.add(st, contexts);
                }
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            log.error("Exception " + ex);
        }
    }
    
    private void addTriple(RepositoryConnection con,
            Resource s, URI p, Value o, Resource... contexts) {
        boolean flag = true;
        try {
            ValueFactory myFactory = con.getValueFactory();
            Statement st = myFactory.createStatement((Resource) s, p,
                    (Value) o);
            Repository metadataRepo;
            try {
                metadataRepo = manager.getRepository("metadata");
                if (this.getRepository().equals(metadataRepo))
                    flag = false;
                
            } catch (RepositoryConfigException ex) {
                log.error("Repository Config Exception " + ex);
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }

            switch (this.metadataLevel) {
                case "dataset":
                    //TODO: Configure enabling if it writes unique triples only
                    if ((metadataVocab.isEmpty()
                            || metadataVocab.contains("void")) 
                            && flag) {
                        checkDistinctEntities(s, p, o);
                    }
                    con.add(st, contexts);
                    break;
                case "triplesmap":
                    if(flag)
                        checkDistinctEntities(s, p, o);
                    con.add(st, contexts);
                    break;
                case "triple":
                    checkDistinctEntities(s, p, o);
                    con.add(st, contexts);
                    break;
                case "None":
                    con.add(st, contexts);
                    break;
            }

            con.commit();
        } catch (Exception ex) {
            log.error("Exception " + ex);
        } 
    }
}