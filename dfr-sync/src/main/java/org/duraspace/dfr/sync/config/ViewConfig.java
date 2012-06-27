/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.SpringBeanPreparerFactory;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;
/**
 * A bean configuration class supporting the view layer.
 * @author Daniel Bernstein
 *
 */
@Configuration
public class ViewConfig{
   
    @Bean
    public ViewResolver viewResolver(){
        UrlBasedViewResolver r = new UrlBasedViewResolver();
        r.setViewClass(TilesView.class);
        r.setOrder(1);
        return r;
    }

    @Bean
    public TilesConfigurer tilesConfigurer(){
        TilesConfigurer t = new TilesConfigurer();
        t.setDefinitions(new String[]{
            "/WEB-INF/**/views.xml"
        });
        t.setPreparerFactoryClass(SpringBeanPreparerFactory.class);
        return t;
    }
    
    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver r = new InternalResourceViewResolver();
        r.setViewClass(JstlView.class);
        r.setPrefix("/WEB-INF/jsp");
        r.setSuffix(".jsp");
        return r;
    }
}
