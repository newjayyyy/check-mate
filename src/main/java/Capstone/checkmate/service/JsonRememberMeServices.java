package Capstone.checkmate.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

public class JsonRememberMeServices extends PersistentTokenBasedRememberMeServices {

    public JsonRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    // Json 기반이기에 Parameter 사용 안하므로 직접 상속 구현
    @Override
    public void loginSuccess(HttpServletRequest requset,
                             HttpServletResponse response,
                             Authentication successfulAuthentication) {
        onLoginSuccess(requset, response, successfulAuthentication);
    }
}
