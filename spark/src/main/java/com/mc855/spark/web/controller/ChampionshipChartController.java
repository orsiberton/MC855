package com.mc855.spark.web.controller;

import com.mc855.spark.Utils;
import com.mc855.spark.core.championship.model.ChampionshipChart;
import com.mc855.spark.core.championship.model.MatchResult;
import com.mc855.spark.core.championship.service.ChampionshipChartService;
import com.mc855.spark.web.model.RetrieveChartResult;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("")
public class ChampionshipChartController {

    @Autowired
    private ChampionshipChartService chartService;

    @Autowired
    private JavaSparkContext javaSparkContext;

    @RequestMapping(value = "/championship_chart/{year}", method = RequestMethod.GET)
    public RetrieveChartResult processChart(@PathVariable("year") Integer year) throws IOException,
            ClassNotFoundException,
            InterruptedException,
            URISyntaxException {

        final ChampionshipChart chart = chartService.retrieveChartForYear(year);
        if (chart.getEntries().isEmpty()) {
            return new RetrieveChartResult(false,
                    new ChampionshipChart(new LinkedList<ChampionshipChart.ChartPosition>()));
        }

        return new RetrieveChartResult(true, chart);
    }

    @RequestMapping(value = "/championship_chart/metrics/{year}", method = RequestMethod.GET)
    public void metricsChart(@PathVariable("year") Integer year) {
        HashMap<String, Integer> results = new HashMap<>();
        JavaRDD<String> javaRDD = javaSparkContext.textFile("hdfs://hdfs-namenode:9000/user/root/match-results-2000").cache();

        JavaRDD<MatchResult> matchResults = javaRDD.map(Utils::createMatchResult);

        // Simple count function
        JavaPairRDD<String, Integer> counts = matchResults.mapToPair(
                match -> new Tuple2<>(match.getHomeTeam(), 1))
                .reduceByKey((x, y) -> x + y);

        List<Tuple2<String, Integer>> output = counts.collect();
        for (Tuple2<String, Integer> tuple : output) {
            results.put(tuple._1(), tuple._2());
        }

        System.out.printf("" + results.get(1));
    }
}
