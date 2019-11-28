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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Service
public class UserService extends BaseService<UserDao, User> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private ConfirmationTokenDao confirmationTokenDao;

    @Autowired
    private Environment environment;

    protected UserService(UserDao dao) {
        super(dao);
    }

    @Transactional
    public int createUser(UserDto userDto) throws UnknownHostException {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

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

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Potvrzení registrace");
        mailMessage.setFrom("noreply@tk21.cz");
        mailMessage.setText("Pro potvrzení vaší emailové adresy klikněte prosím zde:\n\n"
                + "http://195.181.209.16:14023"
                + "/api/confirm?token="+ token.getConfirmationToken());

        mailService.sendEmail(mailMessage);

        super.dao.persist(user);
        return user.getId();
    }

    @Transactional
    public boolean isEmailTokenValid(String token){
        Optional<ConfirmationToken> confirmationToken = confirmationTokenDao.findByConfirmationToken(token);
        if(confirmationToken.isPresent() && confirmationToken.get().isValid()){
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

}
