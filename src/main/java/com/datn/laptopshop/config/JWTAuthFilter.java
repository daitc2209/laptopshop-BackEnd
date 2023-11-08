package com.datn.laptopshop.config;

import com.datn.laptopshop.repos.TokenRepository;
import com.datn.laptopshop.service.impl.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserService userDetailsService;
    @Autowired
    private TokenRepository tokenRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String userEmail;
        System.out.println("doFilterInternal ****");
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            try {
                String token = authHeader.substring("Bearer ".length());
                System.out.println("1****");
                userEmail=jwtService.extractUsername(token);
                String role = jwtService.extractUserRole(token);
                System.out.println("2**** userEmail: "+userEmail);
                System.out.println("2**** role: "+role);
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication()==null){
                    System.out.println("3****");
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    System.out.println("UserDetails: "+userDetails);
                    var isTokenValid = tokenRepository.findByToken(token)
                            .map(t->!t.isExpired() && !t.isRevoked())
                            .orElse(false);
                    System.out.println("isTokenValid: "+isTokenValid);
                    if(jwtService.isTokenValid(token, userDetails) && isTokenValid){
                        System.out.println("4****");
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        auth.setDetails((new WebAuthenticationDetailsSource().buildDetails(request)));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                filterChain.doFilter(request,response);
            }catch (Exception e){
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "403");
                errorResponse.put("error", "Forbidden");
                errorResponse.put("message", e.getMessage());

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            }
        }
        else {
            filterChain.doFilter(request,response);
        }
    }
}
