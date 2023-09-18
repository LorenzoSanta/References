package com.security.SecurityProj.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
//filtro per l'autenticazione (prima esecuzione per il check del token)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    //processo di validazione del token
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        //estraggo il valore di authorization nell'header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        //controllo se il token inizia con bearer o è null per vedere se è valido
        if (authHeader==null||!authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        //estraggo il jwt dalla parola "Bearer " che equivale a 7
        jwt = authHeader.substring(7);
        //estraggo l'email dello user dal jwt utilizzando il JwtService
        userEmail = jwtService.extractUsername(jwt);
        //se il risultato è null lo user non è ancora autenticato se lo è non si riprete questo processo
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if(jwtService.isTokenValid(jwt,userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                //implementiamo nel token i dettagli della request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                //updato il security contex holder e autentico il token
                SecurityContextHolder.getContext().setAuthentication(authToken);
                //passo al prossimo filtro della filterchain
                filterChain.doFilter(request,response);
            }
        }
    }
}
