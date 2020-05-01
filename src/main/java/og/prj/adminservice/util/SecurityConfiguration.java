package og.prj.adminservice.util;

import og.prj.adminservice.jpafiles.CustomUserDetailsService;
import og.prj.adminservice.jwt.CustomAuthenticationSuccessHandler;
import og.prj.adminservice.jwt.JwtTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@EnableWebSecurity
class MultiHttpSecurityConfig {


    private PasswordConfig passwordConfig;

    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private CustomUserDetailsService userDetailsService;

    private JwtTokenVerifier tokenVerifier;

    @Autowired
    MultiHttpSecurityConfig(PasswordConfig passwordConfig, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, CustomUserDetailsService userDetailsService, JwtTokenVerifier tokenVerifier) {
        this.passwordConfig = passwordConfig;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.userDetailsService = userDetailsService;
        this.tokenVerifier = tokenVerifier;
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Configuration
    @Order(1)
    public class JWTSecurityConfiguration extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(userDetailsService);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/getproducts/**").authorizeRequests().and()
                    .cors().and()
                   // .csrf().and()


                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilterBefore(tokenVerifier, UsernamePasswordAuthenticationFilter.class)
                  /*  .addFilter(new JwtUserNamePasswordAuthFilter(authenticationManager()))
                    .addFilterAfter(new JwtTokenVerifier(), JwtUserNamePasswordAuthFilter.class)*/
                    .authorizeRequests()
                    .antMatchers("/", "/css/**", "/css/*", "/css", "css/**","/send-pin","/check-pin" , "/getproducts/getimage/*","/gettoken/**",
                            "/gettoken/*","/getjson","/getimageforjson/**","/signup","/signupsuccess").permitAll()
                    .antMatchers("/getproducts/saveimage/*").hasAnyRole("ADMIN", "MANAGER")
                    .antMatchers("/getorders/*","/getorders/**","/getorders").hasRole( "CUSTOMER")
                    .anyRequest().authenticated();
             http.csrf().disable();


        }
    }

    @Configuration
    @Order(2)
    public class JWTSecurityConfigurationCustomerInfo extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(userDetailsService);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/getcustomerinfo/*").authorizeRequests().and()
                    .cors().and()
                    // .csrf().and()


                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilterBefore(tokenVerifier, UsernamePasswordAuthenticationFilter.class)
                    /*  .addFilter(new JwtUserNamePasswordAuthFilter(authenticationManager()))
                      .addFilterAfter(new JwtTokenVerifier(), JwtUserNamePasswordAuthFilter.class)*/
                    .authorizeRequests()
                    .antMatchers("/", "/css/**", "/css/*", "/css", "css/**","/send-pin","/check-pin" , "/getproducts/getimage/*","/gettoken/**",
                            "/gettoken/*","/getjson","/getimageforjson/**","/signup","/signupsuccess").permitAll()
                    .antMatchers("/getorders/*","/getorders/**","/getorders", "/getcustomerinfo/*").hasRole( "CUSTOMER")
                    .anyRequest().authenticated();
            http.csrf().disable();


        }
    }


    @Configuration
    @Order(3)
    public class JWTSecurityConfigurationCustomer extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(userDetailsService);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/getorders/*")//.authorizeRequests().and()
                    .cors().and()
                    // .csrf().and()


                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilterBefore(tokenVerifier, UsernamePasswordAuthenticationFilter.class)
                    /*  .addFilter(new JwtUserNamePasswordAuthFilter(authenticationManager()))
                      .addFilterAfter(new JwtTokenVerifier(), JwtUserNamePasswordAuthFilter.class)*/
                    .authorizeRequests()
                    .antMatchers("/", "/css/**", "/css/*", "/css", "css/**","/send-pin","/check-pin" ,"/gettoken/**","/gettoken/*","/getjson","/getimageforjson/**","/signup","/signupsuccess").permitAll()
                    .antMatchers("/getorders/*").hasRole( "CUSTOMER")
                    .anyRequest().authenticated();
            http.csrf().disable();


        }
    }


    @Configuration
    @Order(4)
    public class JWTSecurityConfigurationPlaceOrder extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(userDetailsService);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/placeorderreact/*")//.authorizeRequests().and()
                    .cors().and()
                    // .csrf().and()


                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilterBefore(tokenVerifier, UsernamePasswordAuthenticationFilter.class)
                    /*  .addFilter(new JwtUserNamePasswordAuthFilter(authenticationManager()))
                      .addFilterAfter(new JwtTokenVerifier(), JwtUserNamePasswordAuthFilter.class)*/
                    .authorizeRequests()
                    .antMatchers("/", "/css/**", "/css/*", "/css", "css/**","/send-pin","/check-pin" ,"/gettoken/**","/gettoken/*","/getjson","/getimageforjson/**","/signup","/signupsuccess").permitAll()
                    .antMatchers("/getorders/*","/getorders/**","/getorders", "/placeorderreact/*").hasRole( "CUSTOMER")
                    .anyRequest().authenticated();
            http.csrf().disable();


        }
    }


    @Configuration
    @Order(5)
    public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(userDetailsService);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/**")
                    .authorizeRequests().and()
                    .cors().and()
                   //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                              .csrf().disable()
                      /*          .antMatcher("/getproducts")
                                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .and()
                                .addFilter(new JwtUserNamePasswordAuthFilter(authenticationManager()))
                                .addFilterAfter(new JwtTokenVerifier(), JwtUserNamePasswordAuthFilter.class)
                        */
                   .authorizeRequests()
                    .antMatchers("/", "/error","/home", "/css/**", "/css/*", "/css", "css/**","/signin", "/logoutsuccessful"
                            ,"/gettoken/**","/gettoken/*","/getjson","/getimageforjson/**","/signup","/signupsuccess").permitAll()
                    .antMatchers("/admin", "/admin/*", "/adduser/**", "/edituser/**", "/edituser").hasRole("ADMIN")
                    .antMatchers("/manager", "/products", "/addproduct/**", "/editproduct/*", "/editproductsreact").hasAnyRole("ADMIN", "MANAGER")
                    .antMatchers("/order", "/orderlist", "/orders").hasRole("CUSTOMER")
                    .anyRequest().authenticated()
                   // .and().csrf().disable()
                    .and()
                    .exceptionHandling().accessDeniedPage("/accessdenied")
                    .and().formLogin().loginProcessingUrl("/signin").loginPage("/login").permitAll()
                    .successHandler(customAuthenticationSuccessHandler).and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/logoutsuccessful")
                    .and()
                    .rememberMe();


        }

 /*   @Bean
    public PasswordEncoder getPassWordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }*/
    }


}