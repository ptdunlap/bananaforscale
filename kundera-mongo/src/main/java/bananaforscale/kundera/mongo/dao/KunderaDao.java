package bananaforscale.kundera.mongo.dao;

import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ptdunlap
 */
public interface KunderaDao {

    String getHostName();

    String getHostPort();

    String getDatabaseName();

    String getRestUrl();

    EntityManager getEntityManager();

    void closeEntityManager();

    void shutDown();

    void save(Object entity);

    void update(Object entity);

    void remove(Object entity);

    <T> void saveOrUpdate(Object entity);

    List<?> findAll(Class<?> entityClazz);

    <T> T findById(Class<T> entityClazz, Object id);

    List<?> findByQuery(String Query);

    List<?> findByQuery(String queryString, String paramater, Object parameterValue);

    <T> void removeById(Class<T> entityClazz, Object id);

    <T> void removeAll(Class<T> entityClazz);
}
