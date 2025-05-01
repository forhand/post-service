package org.example;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PostServiceApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
