package com.tf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by jason_moo on 2018/11/16.
 */

@SpringBootApplication
@MapperScan("com.tf.mapper")
public class TfApplication {

    public static void main(String[] args) {
        SpringApplication.run(TfApplication.class);
    }

}
