package com.tsemkalo.senlaExperienceExchangeApp.service;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends AbstractService<User>, UserDetailsService {
    void setAuthenticationManager(AuthenticationManager authenticationManager);

    User saveUser(User user);

    void changePassword(String currentUsername, String oldPassword, String newPassword);

    String changeUsername(String currentUsername, String newUsername, String password);

    Long editInfo(String currentUsername, User editedUser);

    void sendForgotPasswordEmail(String username);

    void resetPassword(String resetPasswordToken, String newPassword);

    void login(String username, String password);
}
