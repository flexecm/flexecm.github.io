package com.ever365.ecm.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.ever365.ecm.authority.AuthenticationUtil;
import com.ever365.ecm.authority.PersonService;
import com.ever365.ecm.entity.Entity;
import com.ever365.ecm.entity.EntityDAO;
import com.ever365.mongo.MongoDataSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Repository Mongodb persistence impl
 * @author LiuHan 
 */

public class RepositoryDAOImpl implements RepositoryDAO {

	private MongoDataSource dataSource;
	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	private Map<String, Repository> repositoryCache = new HashMap<String, Repository>();
	
	public static final String REPOSITORIES = "repositories";
	
	private EntityDAO entityDAO;
	
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Override
	public List<Repository> getRepositories() {

		DBCollection repoColl = dataSource.getCollection(  
				 REPOSITORIES);
		BasicDBObject query = new BasicDBObject("tenant", AuthenticationUtil.getTenant());
		DBCursor cursor = repoColl.find(query);
		
		ArrayList<Repository> result = new ArrayList<Repository>();
		while (cursor.hasNext()) {
			DBObject dbo = cursor.next();
			result.add(extractRepository(dbo));
		}
		return result;
	}

	@Override
	public List<Repository> getRepositories(String protocol) {

		DBCollection repoColl = dataSource.getCollection(  
				 REPOSITORIES);
		BasicDBObject query = new BasicDBObject("tenant", AuthenticationUtil.getTenant());
		query.put("p", protocol);
		DBCursor cursor = repoColl.find(query);
		
		ArrayList<Repository> result = new ArrayList<Repository>();
		while (cursor.hasNext()) {
			DBObject dbo = cursor.next();
			
			result.add(extractRepository(dbo));
		}
		return result;
	}

	@Override
	public List<Repository> getRepositoriesByOwner(String owner) {
		DBCollection repoColl = dataSource.getCollection(REPOSITORIES);
		DBObject dbo = new BasicDBObject();
		dbo.put("owner", owner);
		
		DBCursor cursor = repoColl.find(dbo);
		List<Repository> result = new ArrayList<Repository>();
		while(cursor.hasNext()) {
			DBObject repodbo = cursor.next();
			Repository repo = extractRepository(repodbo);
			result.add(repo);
		}
		return result;
	}

	public Repository extractRepository(DBObject repodbo) {
		
		if (repositoryCache.get(repodbo.get("_id").toString())==null) {
			Repository repo = new Repository((String)repodbo.get("p"), (String)repodbo.get("name"));
			repo.setId(repodbo.get("_id").toString());
			repo.setOwner(repodbo.get("owner").toString());
			repo.setRootEntity(entityDAO.getEntityById(repodbo.get("_root").toString()));
			repo.setTrashEntity(entityDAO.getEntityById(repodbo.get("_trash").toString()));
			repositoryCache.put(repodbo.get("_id").toString(), repo);
		}
		return repositoryCache.get(repodbo.get("_id").toString());
	}

	@Override
	public Repository getRepository(String name, boolean autoCreate) {
		Repository repo = new Repository(name);
		
		DBCollection repoColl = dataSource.getCollection(REPOSITORIES);
		DBObject dbo = new BasicDBObject();
		dbo.put("tenant", AuthenticationUtil.getTenant());
		dbo.put("p", repo.getProtocol());
		dbo.put("name", repo.getIdentifier());

		DBObject found = repoColl.findOne(dbo);
		
		if (found==null) {
			if (autoCreate) {
				return addRepository(name, PersonService.ADMIN, null);
			} else {
				return null;
			}
		} else {
			return extractRepository(found);
		}
	}

	@Override
	public Repository addRepository(String repository, String owner,
			String contentStore) {
		DBCollection repoColl = dataSource.getCollection(REPOSITORIES);
		Repository r = new Repository(repository);
		
		DBObject dbo = new BasicDBObject();
		dbo.put("tenant", AuthenticationUtil.getTenant());
		dbo.put("p", r.getProtocol());
		dbo.put("name", r.getIdentifier());
		
		if (repoColl.findOne(dbo)==null) {
			Map<QName, Serializable> specialPropties = new HashMap<QName, Serializable>(1);
			specialPropties.put(Model.PROP_IS_ROOT, true);
			Entity rootEntity  = entityDAO.addEntity(r, null, Model.FS_CONTAINS, null, Model.TYPE_FOLDER, "root", null, specialPropties);
			Entity trashEntity = entityDAO.addEntity(r, null, Model.FS_CONTAINS, null, Model.TYPE_FOLDER, "trash", null, specialPropties);
			
			dbo.put("_root", new ObjectId(rootEntity.getId()));
			dbo.put("_trash", new ObjectId(trashEntity.getId()));
			dbo.put("owner", owner);
			dbo.put("store", contentStore);
			
			repoColl.insert(dbo);
			r.setRootEntity(rootEntity);
			r.setTrashEntity(trashEntity);
			r.setId(dbo.get("_id").toString());
			return r;
		} else {
			return null;
		}
	}

	@Override
	public void dropRepository(String repository) {
		DBCollection repoColl = dataSource.getCollection(REPOSITORIES);
		DBObject dbo = new BasicDBObject();
		Repository r = new Repository(repository);
		dbo.put("tenant", AuthenticationUtil.getTenant());
		dbo.put("p", r.getProtocol());
		dbo.put("name", r.getIdentifier());
		repoColl.remove(dbo);
	}

}
