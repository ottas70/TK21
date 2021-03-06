package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.AbstractEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class BaseDao<T extends AbstractEntity> implements GenericDao<T>{

    @PersistenceContext
    protected EntityManager em;

    protected final Class<T> type;

    protected BaseDao(Class<T> type) {
        this.type = type;
    }


    public List<T> getPage(int size, int page) {
        return this.em.createQuery("select l from " + type.getSimpleName() + " l", type)
                .setMaxResults(size)
                .setFirstResult((page - 1) * size)
                .getResultList();
    }

    public long getCount() {
        return this.em.createQuery("select count(l) from " + type.getSimpleName() + " l", Long.class)
                .getSingleResult();
    }

    public Optional<T> find(Integer id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(em.find(type, id));
    }

    public List<T> findAll() {
        try {
            return em.createQuery("SELECT e FROM " + type.getSimpleName() + " e", type).getResultList();
        } finally {
            em.close();
        }
    }

    public T persist(T entity) {
        Objects.requireNonNull(entity);
        try {
            em.persist(entity);
            return entity;
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(Collection<T> entities) {
        Objects.requireNonNull(entities);
        if (entities.isEmpty()) {
            return;
        }
        try {
            entities.forEach(this::persist);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void refresh(T entity){
        em.refresh(entity);
    }

    public T update(T entity) {
        Objects.requireNonNull(entity);
        try {
            return em.merge(entity);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void remove(T entity) {
        Objects.requireNonNull(entity);
        try {
            final T toRemove = em.merge(entity);
            if (toRemove != null) {
                em.remove(toRemove);
            }
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public boolean exists(Integer id) {
        return id != null && em.find(type, id) != null;
    }
}
