package com.morrle.service;

import com.morrle.domain.Authority;
import com.morrle.domain.User;
import com.morrle.domain.util.AuthoritiesConstants;
import com.morrle.repository.AuthorityRepository;
import com.morrle.repository.UserRepository;
import com.morrle.security.SecurityUtils;
import com.morrle.service.util.RandomUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    private AuthorityRepository authorityRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    //激活用户
    public User activateRegistration(String key){
        log.debug("Activating user for activation key {}", key);

        User user = userRepository.findOneByActivationKey(key);
        // 根据 activation key 激活用户
        if(user != null){
            user.setActivated(true);
            user.setActivationKey(null);
            userRepository.save(user);
            log.debug("Activated user: {}", user);
        }
        return user;
    }


    public User requestPasswordReset(String mail) {
        User user = userRepository.findOneByEmail(mail);
        if (user != null && user.isActivated()) {
            user.setResetKey(RandomUtil.generateResetKey());
            user.setResetDate(DateTime.now());
            userRepository.save(user);
            return user;
        }
        return null;
    }

    public User completePasswordReset(String newPassword,String key){
        log.debug("Reset user password for reset key {}", key);
        User user = userRepository.findOneByResetKey(key);

        DateTime oneDayAgo = DateTime.now().minusHours(24);
        if(user != null && user.isActivated()){
            if(user.getResetDate().isAfter(oneDayAgo.toInstant().getMillis())){
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetDate(null);
                user.setResetKey(null);
                userRepository.save(user);
                return user;
            }
        }
        return null;
    }

    public User createUserInformation(String login,String password,String email,String langKey){
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        newUser.setLogin(login);
        newUser.setPassword(encryptedPassword);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }


    public void updateUserInformation(String login,String email,String langKey){
        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin());
        currentUser.setLogin(login);
        currentUser.setEmail(email);
        currentUser.setLangKey(langKey);
        userRepository.save(currentUser);
        log.debug("Changed Information for User: {}", currentUser);
    }

    public void changePassword(String password){
        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin());
        String encryptedPassword = passwordEncoder.encode(password);
        currentUser.setPassword(encryptedPassword);
        userRepository.save(currentUser);
        log.debug("Changed password for User: {}", currentUser);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin());
        currentUser.getAuthorities().size(); // eagerly load the association
        return currentUser;
    }

    /**
     * 未激活的帐号只会保存3天
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * 这个任务会在每天凌晨1点开始执行
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        DateTime now = new DateTime();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }


}
