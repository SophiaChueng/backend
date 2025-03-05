package com.yizhi.training.application;

import com.yizhi.application.orm.config.WmyDataSourceConfig;
import com.yizhi.util.application.constant.QueueConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/23 22:01
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.yizhi"})
@ComponentScan(
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WmyDataSourceConfig.class})},
        basePackages = {"com.yizhi"})
//@EnableMPP
public class TrainingProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingProjectApplication.class, args);
    }

    @Bean
    public Queue createTpEventQueue() {
        return new Queue(QueueConstant.TRAINING_PROJECT_EVENT_QUEUE, true, false, false);
    }
}
