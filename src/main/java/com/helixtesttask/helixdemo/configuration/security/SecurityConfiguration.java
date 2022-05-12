package com.helixtesttask.helixdemo.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helixtesttask.helixdemo.dto.Credentials;
import com.helixtesttask.helixdemo.service.AuthService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String DETAILS = "details";
    private final AuthService authService;
    private final ObjectMapper mapper;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .antMatchers("/authenticate").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/batch/operations/**").hasAnyAuthority(Credentials.Role.ADMIN.toString(), Credentials.Role.USER.toString())
                .antMatchers("/users/**").hasAnyAuthority(Credentials.Role.ADMIN.toString(), Credentials.Role.USER.toString())
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomHttp403ForbiddenEntryPoint())
                .and()
                .addFilterBefore(new JwtValidatingFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    public class JwtValidatingFilter extends OncePerRequestFilter {

        public static final String BEARER = "Bearer ";

        @Override
        protected void doFilterInternal(
                HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            try {
                String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith(BEARER)) {
                    authService.validateJwt(authHeader.substring(7));
                } else {
                    request.setAttribute(DETAILS, "No Bearer token found in the request");
                }
            } catch (Exception e) {
                log.error("JWT error occurred: {}", e.getMessage());
                request.setAttribute(DETAILS, e.getMessage());
            }
            filterChain.doFilter(request, response);
        }
    }

    public class CustomHttp403ForbiddenEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException, ServletException {
            val err = new ErrorDto();
            err.setErrorStatus(HttpStatus.UNAUTHORIZED);
            err.setTimestamp(Instant.now().toString());
            err.setHttpCode(HttpStatus.UNAUTHORIZED.value());
            err.setDetails(request.getAttribute(DETAILS));
            err.setInfo(authException.getMessage());
            response.getWriter().print(mapper.writeValueAsString(err));
        }
    }

    public class CustomAccessDeniedHandler implements AccessDeniedHandler {

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException e)
                throws IOException, ServletException {
            val err = new ErrorDto();
            err.setErrorStatus(HttpStatus.FORBIDDEN);
            err.setTimestamp(Instant.now().toString());
            err.setHttpCode(HttpStatus.FORBIDDEN.value());
            err.setDetails(request.getAttribute(DETAILS));
            err.setInfo(e.getMessage());
            response.getWriter().print(mapper.writeValueAsString(err));
        }
    }

    @Data
    private static class ErrorDto {
        String info;
        Object details;
        int httpCode;
        String timestamp;
        HttpStatus errorStatus;
    }
}
