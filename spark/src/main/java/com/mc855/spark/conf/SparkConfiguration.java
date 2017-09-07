package com.mc855.spark.conf;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfiguration {

    @Bean
    public JavaSparkContext javaSparkContext() {

        SparkConf conf = new SparkConf().setAppName("Spark with Spring example")
                .setMaster("local")
                .set("spark.executor.memory", "512m")
                .set("spark.cores.max", "1")
                .set("spark.default.parallelism", "3");

        return new JavaSparkContext(conf);
    }

}
