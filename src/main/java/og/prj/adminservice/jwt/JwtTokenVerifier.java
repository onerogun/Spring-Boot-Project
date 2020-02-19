package og.prj.adminservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Strings;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class JwtTokenVerifier extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String xrf = request.getHeader("X-XSRF-TOKEN");


        if( authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token =  authHeader.replace("Bearer ", "");

        try {
            String key = "somesecurekey";
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

            String userName = claims.getSubject();

            List<Map<String,String>> authorities = (List<Map<String,String>>) claims.get("authorities");

            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream().map(m ->
                    new SimpleGrantedAuthority(m.get("authority"))).collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userName,
                    null,
                    simpleGrantedAuthorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            throw new IllegalStateException(String.format("Token %s cannot be validated", token));
        }
        filterChain.doFilter(request,response);
    }
}
