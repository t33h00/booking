package com.lotus.booking.Config;

import com.lotus.booking.Entity.Role;
import com.lotus.booking.Entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!hasAuthorizationHeader(request)) {
            filterChain.doFilter(request,response);
            return;
        }
        String accessToken = getAccesstoken(request);

        if(!jwtUtil.validateAccessToken(accessToken)){
            filterChain.doFilter(request,response);
            return;
        }
        setAuthenticationContext(accessToken,request);
        filterChain.doFilter(request,response);
    }

    private void setAuthenticationContext(String accessToken, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(accessToken);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public UserDetails getUserDetails(String accessToken) {
        User userDetails = new User();
        Claims claims = jwtUtil.parseClaims(accessToken);

        String claimRoles = (String) claims.get("roles");
        claimRoles = claimRoles.replace("[","").replace("]","");
        System.out.println("claimRoles: " + claimRoles);
        String[] roleNames = claimRoles.split(",");
        for (String roleName : roleNames){
            userDetails.addRole(new Role(roleName));
        }

        String subject = (String) claims.get(Claims.SUBJECT);
        String[] subjectArray = subject.split(",");
        userDetails.setId(Long.parseLong(subjectArray[0]));
        userDetails.setEmail(subjectArray[1]);
        return userDetails;
    }

    private boolean hasAuthorizationHeader(HttpServletRequest request){
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(ObjectUtils.isEmpty(header) || !header.startsWith("Bearer")){
            return false;
        }
        return true;
    }

    private String getAccesstoken(HttpServletRequest request){
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = header.split(" ")[1].trim();
        return token;
    }
}
