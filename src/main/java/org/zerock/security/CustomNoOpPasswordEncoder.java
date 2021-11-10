package org.zerock.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
public class CustomNoOpPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        log.debug("encode(rawPassword) invoked.");

        log.warn("before encode : " + rawPassword);

        return rawPassword.toString();
    } //encode

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        log.debug("matches(rawPassword, encodedPassword) invoked.");

        log.warn("matches: {} -> {}", rawPassword, encodedPassword);

        return rawPassword.toString().equals(encodedPassword);
    } //matches
} //end class
