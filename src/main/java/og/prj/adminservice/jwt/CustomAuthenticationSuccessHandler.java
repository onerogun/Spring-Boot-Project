package og.prj.adminservice.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import og.prj.adminservice.jpafiles.CustomUserDetails;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.sql.Date;
import java.time.LocalDate;

@Configuration
public class CustomAuthenticationSuccessHandler implements
        AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String key = "somesecurekey";
        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authentication.getAuthorities())
                .setIssuedAt(new java.util.Date())
                .setExpiration(Date.valueOf(LocalDate.now().plusWeeks(2)))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        response.addHeader("Authorization", "Bearer " + token);


        CustomUserDetails principal = (CustomUserDetails)  authentication.getPrincipal();
        System.out.println("token created for user id = " + principal.getId());
        /* //////////////// */

        TokenController.tokenMap.put(principal.getId(), token);

        /* ////////////////// */
        Cookie myCookie = new Cookie("STOREJWT", token);
        response.addCookie(myCookie);

        String userRole = authentication.getAuthorities().stream().map(a -> a.getAuthority()).findFirst().get();
       if( userRole.equals("ROLE_ADMIN")) {
           response.sendRedirect("/admin");
       } else if(userRole.equals("ROLE_MANAGER")){
           response.sendRedirect("/manager");
       } else {
           response.sendRedirect("/orderlist");
       }

    }

}
