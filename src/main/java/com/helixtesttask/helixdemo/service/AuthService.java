package com.helixtesttask.helixdemo.service;

import com.helixtesttask.helixdemo.configuration.CredentialsProperties;
import com.helixtesttask.helixdemo.dto.Credentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    @Value("${authentication.jwt.expiration-period-seconds}")
    private int expiration;
    @Value("${authentication.jwt.secret}")
    private String secret;

    private final CredentialsProperties properties;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credentials user = properties.getCredentials().get(username);
        if (user != null) {
            val roles = user.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.toString()))
                    .collect(Collectors.toSet());
            return new User(user.getUsername(), user.getPassword(), roles);
        }
        throw new UsernameNotFoundException("User not found by username: " + username);
    }

    public String getJwt(Credentials credentials) {
        if (credentials == null) {
            return null;
        }
        if (properties.getCredentials().containsKey(credentials.getUsername())) {
            val configuredCredentials = properties.getCredentials().get(credentials.getUsername());
            if (configuredCredentials.getPassword().equals(credentials.getPassword())) {
                String id = UUID.randomUUID().toString().replace("-", "");
                val now = Instant.now();
                val jwt = Jwts.builder()
                        .setId(id)
                        .setExpiration(Date.from(now.plusSeconds(expiration)))
                        .setIssuedAt(Date.from(now))
                        .setIssuer(credentials.getUsername())
                        .signWith(HS256, Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8)))
                        .compact();
                log.info("Generated JWT: {} for issuer {} having roles {}",
                        jwt, credentials.getUsername(), credentials.getRoles());
                return jwt;
            }
        }
        throw new IllegalArgumentException("Incorrect credentials provided");
    }

    public Jws<Claims> validateJwt(String jwt) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8)), HS256.getJcaName());
        DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(HS256, secretKeySpec);
        boolean isTokenSignatureValid = validator.isValid(jwt.substring(0, jwt.lastIndexOf('.')), jwt.substring(jwt.lastIndexOf('.') + 1));

        if (!isTokenSignatureValid) {
            throw new IllegalArgumentException("Incorrect token signature");
        }

        val result = Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8)))
                .parseClaimsJws(jwt);

        val roles = properties.getCredentials().get(result.getBody().getIssuer()).getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toSet());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(result.getBody().getIssuer(), null, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return result;
    }
}
