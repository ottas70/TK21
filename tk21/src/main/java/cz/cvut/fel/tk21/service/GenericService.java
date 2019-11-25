package cz.cvut.fel.tk21.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface that specifies basic operations for services such as find, persist, update, db pagination and so on.
 * @param <T> type of objects that are managed by this service
 */
public interface GenericService<T> {

    /**
     * Returns entity with given id.
     * @param id id of entity
     * @return Optional of entity object
     */
    Optional<T> find(int id);

    /**
     * Returns all entities of type T.
     * @return list of entities
     */
    List<T> findAll();

    /**
     * Returns count of all stored entities.
     * @return number of entities stored
     */
    long getCount();

    /**
     * Returns list of items for given page with given size.
     * @param size size of page
     * @param page requested page
     * @return list of elements for requested page
     */
    List<T> getPage(int size, int page);

    /**
     * Persists entity
     * @param entity entity to be persisted
     */
    void persist(T entity);

    /**
     * Persists all entities in given collection.
     * @param entities collection of entities to be persisted
     */
    void persist(Collection<T> entities);

    /**
     * Refreshes entity state from the storage.
     * @param entity entity to be refreshed
     */
    void refresh(T entity);

    /**
     * Updates entity in the storage.
     * @param entity entity to be updated
     */
    void update(T entity);

    /**
     * Removes entity from the storage.
     * @param entity entity to be removed
     */
    void remove(T entity);

    /**
     * Removes entity with given id from the storage.
     * @param id entity id
     */
    void removeById(int id);

    /**
     * Checks if entity with given id exists in the storage.
     * @param id entity id
     * @return true if entity does exist, false otherwise
     */
    boolean exists(int id);

}
