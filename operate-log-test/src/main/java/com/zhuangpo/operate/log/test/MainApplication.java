package com.zhuangpo.operate.log.test;

import com.zhuangpo.operate.log.core.config.LogAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 *  启动类
 *
 * @author xub
 * @since 2023/8/28 下午3:48
 */
@Import(LogAutoConfiguration.class)
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
