package cz.cvut.fel.tk21.util;

import cz.cvut.fel.tk21.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Service
public class RequestBodyValidator {

    @Autowired
    private Validator validator;

    public <E> void validate(E object){
        Set<ConstraintViolation<E>> violations = validator.validate(object);
        for (ConstraintViolation<E> violation : violations) {
            if(violation.getMessage() != null){
                throw new ValidationException(violation.getMessage());
            }else{
                throw new ValidationException("Invalid data");
            }
        }
    }

}
