package og.prj.adminservice.jwt;


import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class TokenController {

    public static Map<Long,String> tokenMap = new HashMap<>();

    //@ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "/gettoken/{id}")
    public String sendToken(@PathVariable Long id) {
       // Cookie[] cookies = request.getCookies();

      /*  if (cookies != null) {
            // myCookie.setValue(Arrays.stream(cookies).filter(c -> c.getName().equals("STOREJWT")).findFirst().get().getValue()) ;
            response.addHeader("STOREJWT", Arrays.stream(cookies).filter(c -> c.getName().equals("STOREJWT")).findFirst().get().getValue());
        }*/

        //  response.addCookie(myCookie);

/*
        Cookie myCookie = new Cookie("REACTJWT", tokenMap.get(id));
        myCookie.setSecure(true);
        myCookie.setPath("/");
        myCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(myCookie);
        response.addHeader("REACTJWT", tokenMap.get(id));
*/
       return  tokenMap.get(id);

    /*  ResponseCookie cookie = ResponseCookie.from("REACTJWT", tokenMap.get(id)).build();
        System.out.println("Cookie for    " +id + "  :"  +cookie.toString());
      return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, cookie.toString())
              .build();*/
    }

}
