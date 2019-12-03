package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ConfirmationTokenDao;
import cz.cvut.fel.tk21.dao.UserDao;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.mail.ConfirmationToken;
import cz.cvut.fel.tk21.rest.dto.UserDto;
import cz.cvut.fel.tk21.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService extends BaseService<UserDao, User> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private ConfirmationTokenDao confirmationTokenDao;

    protected UserService(UserDao dao) {
        super(dao);
    }

    @Transactional
    public int createUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        System.out.println(userDto.getPassword());

        if (!dao.isEmailUnique(userDto.getEmail())) {
            throw new ValidationException("Účet s tímto emailem již existuje");
        }
        User user = new User();
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setVerifiedAccount(false);

        ConfirmationToken token = new ConfirmationToken(user);
        user.setConfirmationToken(token);

        super.dao.persist(user);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Potvrzení registrace");
        mailMessage.setFrom("noreply@tk21.cz");
        mailMessage.setText("Pro potvrzení vaší emailové adresy klikněte prosím zde:\n\n"
                + "http://195.181.209.16:14023"
                + "/#/overeni/"+ token.getConfirmationToken());

        mailService.sendEmail(mailMessage);
        return user.getId();
    }

    @Transactional
    public boolean isEmailTokenValid(String token){
        Optional<ConfirmationToken> confirmationToken = confirmationTokenDao.findByConfirmationToken(token);
        if(confirmationToken.isPresent()){
            if (!confirmationToken.get().isValid()) throw new ValidationException("Platnost tokenu již vypršela");
            Optional<User> user = dao.getUserByEmail(confirmationToken.get().getUser().getEmail());
            if(user.isPresent()){
                user.get().setVerifiedAccount(true);
                dao.update(user.get());
            }else{
                return false;
            }
            return true;
        }

        return false;
    }

    @Transactional
    public Optional<User> findUserByEmail(String email){
        return dao.getUserByEmail(email);
    }

}
