package cz.cvut.fel.tk21.aspect;

import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.service.ClubService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ClubManagementAspect {

    private final ClubService clubService;

    @Autowired
    public ClubManagementAspect(ClubService clubService) {
        this.clubService = clubService;
    }

    @Before("execution(@cz.cvut.fel.tk21.annotation.ClubManagementOnly * *(.., cz.cvut.fel.tk21.model.Club, ..)))")
    public void checkPermission(JoinPoint thisJoinPoint){
        for(Object arg : thisJoinPoint.getArgs()) {
            if (arg instanceof Club){
                if(!clubService.isCurrentUserAllowedToManageThisClub((Club) arg)) {
                    throw new UnauthorizedException("Přístup odepřen");
                }
                break;
            }
        }
    }

}
