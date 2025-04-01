package com.lotus.booking.Config;

import com.lotus.booking.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(fl->
                fl.usernameParameter("username").passwordParameter("password"));
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(c->c.configurationSource(corsConfigurationSource()));
        http.authorizeHttpRequests(auth->{
            auth.requestMatchers("/auth/**").permitAll();
            auth.requestMatchers("/api/save").permitAll();
            auth.requestMatchers("/api/checkin").permitAll();
            auth.requestMatchers("/api/subscriber").permitAll();
            auth.requestMatchers("/api/notification/**").permitAll();
            auth.requestMatchers("/api/list/**").permitAll();
            auth.requestMatchers("/api/list/date").permitAll();
            auth.requestMatchers("/ws").permitAll();
            auth.requestMatchers("/user/**").hasAnyRole("USER","ADMIN");
            auth.requestMatchers("/admin/**").hasRole("ADMIN");
            auth.anyRequest().authenticated();
        });
        http.exceptionHandling(exception-> exception.authenticationEntryPoint((request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, authException.getMessage());
        }));
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//        http.requiresChannel(re->re.requestMatchers(r->r.getHeader("X-Forwarded-Proto") !=null).requiresSecure());
        return http.build();

    }



    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(username -> userRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User" + username + " not found.")));
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("https://lotus-ui.web.app");
        configuration.addAllowedOrigin("https://lotuscheckin.web.app");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:3001");
        configuration.addAllowedOrigin("https://lotusnails-67281.web.app");
        configuration.addAllowedOrigin("https://lotus-nailsspa.web.app");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new
                UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public static String getSiteURL(HttpServletRequest request){
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
