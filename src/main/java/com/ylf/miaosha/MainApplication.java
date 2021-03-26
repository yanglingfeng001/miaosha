package com.ylf.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class MainApplication  {
    //extends SpringBootServletInitializer 可能和打war包有关
    public static void main(String[] args) throws Exception{
        SpringApplication.run(MainApplication.class,args);
    }
}
