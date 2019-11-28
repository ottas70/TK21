package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.UserDao;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.rest.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends BaseService<UserDao, User> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    protected UserService(UserDao dao) {
        super(dao);
    }

    @Transactional
    public int createUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (!dao.isEmailUnique(userDto.getEmail())) {
            throw new ValidationException("Účet s tímto emailem již existuje");
        }
        //TODO email verification
        User user = new User(userDto.getName(), userDto.getSurname(), userDto.getEmail(), userDto.getPassword(), true);
        super.dao.persist(user);
        return user.getId();
    }

}
