package bananaforscale.kundera.mongo.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author ptdunlap
 */
public class SimpleKunderaDao implements KunderaDao {

    private final EntityManager em;
    private final String hostName;
    private final String hostPort;
    private final String databaseName;
    private final String restUrl;

    public SimpleKunderaDao(String persistenceUnit) throws IOException {
        // load the properties for data source.
        Properties props = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/META-INF/datasource.properties");
        props.load(in);

        // Set properties
        hostName = props.getProperty("hostName");
        hostPort = props.getProperty("hostPort");
        databaseName = props.getProperty("databaseName");
        restUrl = props.getProperty("restUrl");
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put("kundera.nodes", hostName);
        propertyMap.put("kundera.port", hostPort);
        propertyMap.put("kundera.keyspace", databaseName);

        // Create the entity manager and set the options to get the datasource information
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit, propertyMap);
        em = entityManagerFactory.createEntityManager();
    }

    /**
     * Save an entity to the database. If the entity already exists within the
     * database it will not be overwritten.
     */
    @Override
    public void save(Object entity) {
        em.persist(entity);
        em.clear();
    }

    /**
     * Update an entity in the database.
     *
     * @param entity
     */
    @Override
    public void update(Object entity) {
        em.merge(entity);
        em.clear();
    }

    /**
     * Remove an entity from the database.
     *
     * @param entity
     */
    @Override
    public void remove(Object entity) {
        em.remove(entity);
        em.clear();
    }

    /**
     * Save or update an entity in the database. This method checks if the
     * entity already exists in the database. If it does not, it is persisted
     * using the "save" method. If it does, it is persisted using the "update"
     * method.
     *
     * @param <T>
     * @param entity
     */
    @Override
    public <T> void saveOrUpdate(Object entity) {
        Object primaryKey = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        // if the entity does not have a primary key it could be assumed it does not 
        // exist within the database
        if (primaryKey == null) {
            save(entity);
            return;
        }
        T persistedEntity = (T) findById(entity.getClass(), primaryKey);
        if (persistedEntity == null) {
            save(entity);
        } else {
            update(entity);
        }
    }

    /**
     * Find an entity by identifier.
     *
     * @param <T>
     * @param entityClazz
     * @param id
     * @return
     */
    @Override
    public <T> T findById(Class<T> entityClazz, Object id) {
        T results = em.find(entityClazz, id);
        return results;
    }

    /**
     * Find all entities of a certain class.
     *
     * @param entityClazz
     * @return
     */
    @Override
    public List<?> findAll(Class<?> entityClazz) {
        String query = "SELECT e FROM " + entityClazz.getSimpleName() + " e";
        List<?> results = findByQuery(query);
        return results;
    }

    /**
     * Find entities by a custom JQL query.
     *
     * @param queryString
     * @return
     */
    @Override
    public List<?> findByQuery(String queryString) {
        Query query = em.createQuery(queryString);
        List<?> resultList = query.getResultList();
        return resultList;
    }

    /**
     * Find entities by a custom JQL query making use of a single parameter
     *
     * @param queryString
     * @return
     */
    @Override
    public List<?> findByQuery(String queryString, String paramater, Object parameterValue) {
        Query query = em.createQuery(queryString);
        query.setParameter(paramater, parameterValue);
        List<?> resultList = query.getResultList();
        return resultList;
    }

    /**
     * Remove an entity by its identifier.
     *
     * @param <T>
     * @param entityClazz
     * @param id
     */
    @Override
    public <T> void removeById(Class<T> entityClazz, Object id) {
        T entity = findById(entityClazz, id);
        remove(entity);
    }

    /**
     * Remove all entities of a certain type.
     *
     * @param <T>
     * @param entityClazz
     */
    @Override
    public <T> void removeAll(Class<T> entityClazz) {
        String deleteQuery = "DELETE FROM " + entityClazz.getSimpleName();
        em.createQuery(deleteQuery).executeUpdate();
    }

    /**
     * Provides access to the Kundera JPA Entity Mananger.
     *
     * @return
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Closes the Kundera JPA Entity Mananger.
     */
    @Override
    public void closeEntityManager() {
        if (em != null) {
            em.close();
        }
    }

    @Override
    public void shutDown() {
        if (em != null) {
            em.close();
        }
    }

    /**
     * Returns the host name defined in the data source properties file.
     *
     * @return
     */
    @Override
    public String getHostName() {
        return hostName;
    }

    /**
     * Returns the host port defined in the data source properties file.
     *
     * @return
     */
    @Override
    public String getHostPort() {
        return hostPort;
    }

    /**
     * Returns the database name defined in the data source properties file.
     *
     * @return
     */
    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Returns the REST URL defined in the data source properties file.
     *
     * @return
     */
    @Override
    public String getRestUrl() {
        return restUrl;
    }
}
