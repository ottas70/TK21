package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.util.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Repository
public class PostDao extends BaseDao<Post>{

    protected PostDao() {
        super(Post.class);
    }

    public List<Post> findPostsByClub(Club club, int page, int size){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);

        query.select(root);
        ParameterExpression<Club> param = cb.parameter(Club.class);
        query.where(cb.equal(root.get("club"), param));

        TypedQuery<Post> typedQuery = em.createQuery(query);
        typedQuery.setParameter(param, club);
        typedQuery.setFirstResult((page-1) * size);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    public List<Post> findPostsByUser(User user, int page, int size){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);

        query.select(root);
        ParameterExpression<User> param = cb.parameter(User.class);
        query.where(cb.equal(root.get("user"), param));

        TypedQuery<Post> typedQuery = em.createQuery(query);
        typedQuery.setParameter(param, user);
        typedQuery.setFirstResult((page-1) * size);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    public long countPostsByClub(Club club){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Post> root = query.from(Post.class);

        query.select(cb.count(root));
        ParameterExpression<Club> param = cb.parameter(Club.class);
        query.where(cb.equal(root.get("club"), param));

        TypedQuery<Long> typedQuery = em.createQuery(query);
        typedQuery.setParameter(param, club);

        return typedQuery.getSingleResult();
    }

    public long countPostsByUser(User user){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Post> root = query.from(Post.class);

        query.select(cb.count(root));
        ParameterExpression<User> param = cb.parameter(User.class);
        query.where(cb.equal(root.get("user"), param));

        TypedQuery<Long> typedQuery = em.createQuery(query);
        typedQuery.setParameter(param, user);

        return typedQuery.getSingleResult();
    }

}
