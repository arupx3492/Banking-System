package com.greatLearning.GreatLearningAssignment.security;

import com.greatLearning.GreatLearningAssignment.entity.Token;
import com.greatLearning.GreatLearningAssignment.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TokenRepository tokenRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");


        String username=null;
        String jwt=null;

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){

            jwt=authorizationHeader.substring(7);
            username=jwtUtil.extractUsername(jwt);

            Token token=tokenRepository.findByToken(jwt);
            if(token!=null && token.isBlacklisted()){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("You have logout from the current session . Please Try to login ");
                return;
            }
        }

        if(username!= null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails=customUserDetailsService.loadUserByUsername(username);


         if(jwtUtil.validateToken(jwt,userDetails.getUsername())){
             UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
             authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
             SecurityContextHolder.getContext().setAuthentication(authToken);
         }
        }

        filterChain.doFilter(request,response);


    }
}