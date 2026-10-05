package com.catrescue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/login", "/register",
                        "/user/login", "/user/register", "/user/sendCode",
                        "/user/check", "/user/update", "/user/apply/adopter",
                        "/user/profile", "/user/applications", "/user/logout",
                        "/cat/list", "/cat/detail/**", "/cat/favorites", "/cat/list/json",
                        "/cat/favorite", "/cat/unfavorite", "/cat/search",
                        "/news/list", "/news/detail/**", "/news/hot",
                        "/news/like/**", "/news/comment",
                        "/uploads/**", "/css/**", "/js/**", "/images/**",
                        "/clue/submit", "/clue/my-clues", "/clue/add", "/clue/submit/**",
                        "/donation/donate", "/donation/create", "/donation/records","/donation/report",
                        "/adoption/apply/**", "/adoption/my-applications", "/adoption/progress/**",
                        "/admin/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")  // 登录页面路径
                .loginProcessingUrl("/doLogin")  // 处理登录请求的路径
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }
}