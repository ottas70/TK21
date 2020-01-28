package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Post;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import java.util.Objects;

@Repository
public class PostDao extends BaseDao<Post>{

    protected PostDao() {
        super(Post.class);
    }

}
