package co.edu.usta.telco.iot.service.impl;

import co.edu.usta.telco.iot.data.model.User;
import co.edu.usta.telco.iot.data.repository.UserRepository;
import co.edu.usta.telco.iot.service.UserService;
import co.edu.usta.telco.iot.util.IdentifierUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User validateToken(String token) {
        if (StringUtils.isEmpty(token)) {
            LOG.debug("Error: userToken not found on request");
            return null;
        }
        User user = userRepository.findByToken(token);
        if (Objects.isNull(user)) {
            LOG.debug("Error: User not found in database. userToken " + token);
            return null;
        }
        return user;
    }

    @Override
    public User activateToken(User user) {
        User foundUser = userRepository.findOne(user.getId());
        if (StringUtils.isBlank(foundUser.getToken())) {
            return null; // TODO: throw new exception
        }
        return saveNewToken(user);
    }

    @Override
    public User saveNewToken(User user) {
        String token = IdentifierUtil.nextSessionId();
        User foundUser = userRepository.findOne(user.getId());
        foundUser.setToken(token);
        return userRepository.save(foundUser);
    }

    @Override
    public String getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return "";
    }
}

