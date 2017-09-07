package com.mc855.hadoop.conf;

import com.mc855.hadoop.core.championship.service.ChampionshipChartService;
import com.mc855.hadoop.core.championship.service.ChampionshipMatchResultsRetriever;
import com.mc855.hadoop.core.hadoop.HadoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ServicesConfiguration {

    @Bean
    public HadoopService hadoopService() throws IOException {

        final String nameNomeAddress = "hdfs://hdfs-namenode:9000";

        return new HadoopService(nameNomeAddress);
    }

    @Bean
    public ChampionshipMatchResultsRetriever championshipMatchResultsRetriever() {

        final String premierLeagueResultsFolder = "championship-results";

        return new ChampionshipMatchResultsRetriever(premierLeagueResultsFolder);
    }

    @Bean
    @Autowired
    public ChampionshipChartService championshipChartService(ChampionshipMatchResultsRetriever championshipMatchResultsRetriever,
                                                             HadoopService hadoopService) {

        return new ChampionshipChartService(championshipMatchResultsRetriever, hadoopService);
    }

}
