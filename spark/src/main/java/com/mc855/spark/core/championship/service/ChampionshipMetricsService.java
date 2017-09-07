package com.mc855.spark.core.championship.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc855.spark.Utils;
import com.mc855.spark.core.championship.model.ChampionshipChart;
import com.mc855.spark.core.championship.model.MatchResult;
import com.mc855.spark.core.hadoop.HadoopService;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class ChampionshipMetricsService {

    private final ObjectMapper objectMapper;
    private final ChampionshipMatchResultsRetriever championshipMatchResultsRetriever;
    private final HadoopService hadoopService;
    private final JavaSparkContext javaSparkContext;

    public ChampionshipMetricsService(final ChampionshipMatchResultsRetriever championshipMatchResultsRetriever,
                                      final HadoopService hadoopService,
                                      final JavaSparkContext javaSparkContext) {
        this.championshipMatchResultsRetriever = championshipMatchResultsRetriever;
        this.hadoopService = hadoopService;
        this.javaSparkContext = javaSparkContext;

        this.objectMapper = new ObjectMapper();
    }

    public ChampionshipChart retrieveChartForYear(final Integer year) throws IOException, URISyntaxException {

        Collection<MatchResult> matchResults = championshipMatchResultsRetriever.retrieveResultsForYear(year);
        Collection<String> resultJsonList = toJson(matchResults);
        Path remoteFile = hadoopService.exportContentToHdfs(resultJsonList, "match-results-" + year);

        JavaRDD<String> javaRDD = javaSparkContext.textFile("hdfs://hdfs-namenode:9000/user/root/" + remoteFile.getName()).cache();
        JavaRDD<MatchResult> matchResultsRDD = javaRDD.map(Utils::createMatchResult);

        return buildOrderedChartFromJobOutput(makeChart(matchResultsRDD).collectAsMap());
    }

    private JavaPairRDD<String, Integer> makeChart(JavaRDD<MatchResult> matchResultRDD) {
        JavaPairRDD<String, Integer> homeTeamPoints = matchResultRDD
                .filter(matchResult -> matchResult.getHomeTeamScore() > matchResult.getAwayTeamScore())
                .mapToPair(matchResult -> new Tuple2<>(matchResult.getHomeTeam(), 3));

        JavaPairRDD<String, Integer> awayTeamPoints = matchResultRDD
                .filter(matchResult -> matchResult.getHomeTeamScore() < matchResult.getAwayTeamScore())
                .mapToPair(matchResult -> new Tuple2<>(matchResult.getAwayTeam(), 3));

        JavaPairRDD<String, Integer> homeTeamDrawPoints = matchResultRDD
                .filter(matchResult -> matchResult.getHomeTeamScore().equals(matchResult.getAwayTeamScore()))
                .mapToPair(matchResult -> new Tuple2<>(matchResult.getHomeTeam(), 1));

        JavaPairRDD<String, Integer> awayTeamDrawPoints = matchResultRDD
                .filter(matchResult -> matchResult.getHomeTeamScore().equals(matchResult.getAwayTeamScore()))
                .mapToPair(matchResult -> new Tuple2<>(matchResult.getAwayTeam(), 1));

        return homeTeamPoints.union(awayTeamPoints).union(homeTeamDrawPoints).union(awayTeamDrawPoints).reduceByKey((x, y) -> x + y);
    }

    private ChampionshipChart buildOrderedChartFromJobOutput(final Map<String, Integer> jobOutput) {

        LinkedList<ChampionshipChart.ChartPosition> chartPositions = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : jobOutput.entrySet()) {
            chartPositions.add(new ChampionshipChart.ChartPosition(entry.getKey(), entry.getValue()));
        }

        // sort decreasingly to represent the championship result
        chartPositions.sort((o1, o2) -> o2.getScore().compareTo(o1.getScore()));

        return new ChampionshipChart(chartPositions);
    }

    private Collection<String> toJson(Collection<MatchResult> results) throws JsonProcessingException {

        final Collection<String> result = new HashSet<>();
        for (MatchResult matchResult : results) {
            result.add(objectMapper.writeValueAsString(matchResult));
        }

        return result;
    }

}
