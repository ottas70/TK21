package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.BaseDao;
import cz.cvut.fel.tk21.model.AbstractEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Abstract implementation of GenericService
 * @param <T> dao type
 * @param <R> type of objects that are managed by this service
 */
public abstract class BaseService<T extends BaseDao<R>, R extends AbstractEntity> implements GenericService<R> {

    protected T dao;

    protected BaseService(T dao) {
        this.dao = dao;
    }

    @Override
    @Transactional
    public long getCount() {
        return this.dao.getCount();
    }

    @Override
    @Transactional
    public List<R> getPage(int size, int page) {
        return this.dao.getPage(size, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<R> find(int id) {
            return dao.find(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAll() {
        return dao.findAll();
    }

    @Override
    @Transactional
    public void persist(R entity) {
        dao.persist(entity);
    }

    @Override
    @Transactional
    public void persist(Collection<R> entities) {
        dao.persist(entities);
    }

    @Override
    public void refresh(R entity) {
        dao.refresh(entity);
    }

    @Override
    @Transactional
    public void update(R entity) {
        dao.update(entity);
    }

    @Override
    @Transactional
    public void remove(R entity) {
        dao.remove(entity);
    }

    @Override
    @Transactional
    public void removeById(int id) {
        final var entity = dao.find(id);
        entity.ifPresent(r -> dao.remove(r));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(int id) {
        return dao.exists(id);
    }
}
