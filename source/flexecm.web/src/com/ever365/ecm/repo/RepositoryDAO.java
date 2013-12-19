package com.ever365.ecm.repo;

import java.util.List;

/**
 * DAO services for <b>repository</b> and related 
 * @author Liu Han
 */
public interface RepositoryDAO {
	/**
	 * get all repositories
	 * @return
	 */
    public List<Repository> getRepositories();

    public List<Repository> getRepositories(String protocol);
    /**
     * get the repository list by owner
     * @param owner
     * @return
     */
    public List<Repository> getRepositoriesByOwner(String owner);
    
    /**
     * get repository by full repo name
     * @param name
     * @param autoCreate   if true, the repository owner is admin(System)
     * @return
     */
    public Repository getRepository(String name, boolean autoCreate);
    
    /**
     * Creates a unique repository for the given protocol and identifier combination.
     * The root entity is created with a "root" aspect.
     * @return Returns the root entity, which is added automatically.
     */
    public Repository addRepository(String repository, String owner, String contentStore);
    
    public void dropRepository(String repository);
    
}
