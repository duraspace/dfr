/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
/**
 * The root application configuration class.
 * @author Daniel Bernstein
 *
 */
@Configuration
@ComponentScan( basePackages = {"org.duraspace.dfr.sync"} )
@EnableWebMvc
public class AppConfig {
   
   @Bean
   public static PropertyPlaceholderConfigurer properties(){
      PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
      final Resource[] resources = new ClassPathResource[ ] { 
         //new ClassPathResource( "properties1.properties" ),  
         //new ClassPathResource( "properties2.properties" ) 
      };  
      ppc.setLocations( resources );
      ppc.setIgnoreUnresolvablePlaceholders( true );
      return ppc;
   }   
 
   @Bean
   public MessageSource messageSource(){
       ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
       ms.setBasename("classpath:messages");
       ms.setDefaultEncoding("UTF-8");
       return ms;
   }
   
   @Bean
   public MessageSource localeChangeInterceptor(){
       ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
       ms.setBasename("classpath:messages");
       ms.setDefaultEncoding("UTF-8");
       return ms;
   }
}
