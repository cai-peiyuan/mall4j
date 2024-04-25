

package com.yami.shop.admin;


import com.yami.shop.admin.listener.WebApplicationStartedListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author lgh
 */
@SpringBootApplication
@ComponentScan({"com.yami.shop", "org.jeecg.modules.jmreport"})
@EntityScan({"com.yami.shop", "org.jeecg.modules.jmreport"})
@EnableAsync
public class WebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebApplication.class);
        app.addListeners(new WebApplicationStartedListener());
        app.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WebApplication.class);
    }

}
