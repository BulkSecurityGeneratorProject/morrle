package com.morrle.web.rest;

import com.codahale.metrics.annotation.Timed;

import com.morrle.domain.Authority;
import com.morrle.domain.User;
import com.morrle.repository.UserRepository;
import com.morrle.security.SecurityUtils;
import com.morrle.service.MailService;
import com.morrle.service.UserService;
import com.morrle.web.rest.dto.UserDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    /**
     * POST  /signup -> signup the user.
     */
    @PostMapping(value = "/signup",
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<?> signupAccount(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
        User user = userRepository.findOneByLogin(userDTO.getLogin());
        if (user != null) {
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("用户名已被使用!");
        } else {
            if (userRepository.findOneByEmail(userDTO.getEmail()) != null) {
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("e-mail已被使用!");
            }
            user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
                    userDTO.getEmail().toLowerCase(), userDTO.getLangKey());
            String baseUrl = request.getScheme() + // "http"
                    "://" +                            // "://"
                    request.getServerName() +          // "myhost"
                    ":" +                              // ":"
                    request.getServerPort();           // "80"

            mailService.sendActivationEmail(user, baseUrl);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @PostMapping(value = "/signin", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity signin(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
        SecurityUtils.isAuthenticated();
        return null;
    }


    /**
     * GET  /activate -> activate the registered user.
     */
    @RequestMapping(value = "/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        User user = userService.activateRegistration(key);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * GET  /authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account -> get the current user.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getAccount() {
        User user = userService.getUserWithAuthorities();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<String> roles = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList());
        return new ResponseEntity<>(
                new UserDTO(
                        user.getLogin(),
                        null,
                        user.getEmail(),
                        user.getLangKey(),
                        roles),
                HttpStatus.OK);
    }

    /**
     * POST  /account -> update the current user information.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> saveAccount(@RequestBody UserDTO userDTO) {
        User userHavingThisLogin = userRepository.findOneByLogin(userDTO.getLogin());
        if (userHavingThisLogin != null && !userHavingThisLogin.getLogin().equals(SecurityUtils.getCurrentLogin())) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        userService.updateUserInformation(userDTO.getLogin(), userDTO.getEmail(),
                userDTO.getLangKey());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * POST  /change_password -> changes the current user's password
     */
    @RequestMapping(value = "/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> changePassword(@RequestBody String password) {
        if (!checkPasswordLength(password)) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/account/reset_password/init",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {

        User user = userService.requestPasswordReset(mail);

        if (user != null) {
            String baseUrl = request.getScheme() +
                    "://" +
                    request.getServerName() +
                    ":" +
                    request.getServerPort();
            mailService.sendPasswordResetMail(user, baseUrl);
            return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/account/reset_password/finish",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> finishPasswordReset(@RequestParam(value = "key") String key, @RequestParam(value = "newPassword") String newPassword) {
        if (!checkPasswordLength(newPassword)) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        }
        User user = userService.completePasswordReset(newPassword, key);
        if (user != null) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean checkPasswordLength(String password) {
        return (!StringUtils.isEmpty(password) && password.length() >= UserDTO.PASSWORD_MIN_LENGTH && password.length() <= UserDTO.PASSWORD_MAX_LENGTH);
    }
}
