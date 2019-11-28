package cz.cvut.fel.tk21.service.security;

import cz.cvut.fel.tk21.dao.UserDao;
import cz.cvut.fel.tk21.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userDao.getUserByEmail(email);
        user.orElseThrow(() -> new UsernameNotFoundException("Nesprávné přihlašovací údaje"));
        return new cz.cvut.fel.tk21.model.security.UserDetails(user.get());
    }

}
