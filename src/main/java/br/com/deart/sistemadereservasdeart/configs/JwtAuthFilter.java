package br.com.deart.sistemadereservasdeart.configs;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.deart.sistemadereservasdeart.services.JwtService;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            corsLiberation(response);
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        username = jwtService.extractUsername(jwtToken);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null ){

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if(jwtService.isTokenValid(jwtToken, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        corsLiberation(response);
        filterChain.doFilter(request, response);
    }

    private void corsLiberation(HttpServletResponse response) {
        if(response.getHeader("Access-Control-Allow-Origin") == null){
            response.addHeader("Access-Control-Allow-Origin", "*");
        }
        if(response.getHeader("Access-Control-Allow-Headers") == null){
            response.addHeader("Access-Control-Allow-Headers", "*");
        }
        if(response.getHeader("Access-Control-Request-Headers") == null){
            response.addHeader("Access-Control-Request-Headers", "*");
        }
        if(response.getHeader("Access-Control-Allow-Methods") == null){
            response.addHeader("Access-Control-Allow-Methods", "*");
        }
    }
}
