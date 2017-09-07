package com.mc855.spark.core.championship.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc855.spark.core.championship.model.ChampionshipChart;
import com.mc855.spark.core.championship.model.MatchResult;
import com.mc855.spark.core.hadoop.HadoopService;
import com.mc855.spark.core.hadoop.mapper.ChampionshipResultsMapper;
import com.mc855.spark.core.hadoop.reducer.ChampionshipResultsReducer;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class ChampionshipChartService {

    private final ObjectMapper objectMapper;
    private final ChampionshipMatchResultsRetriever championshipMatchResultsRetriever;
    private final HadoopService hadoopService;

    public ChampionshipChartService(final ChampionshipMatchResultsRetriever championshipMatchResultsRetriever,
                                    final HadoopService hadoopService) {

        this.championshipMatchResultsRetriever = championshipMatchResultsRetriever;
        this.hadoopService = hadoopService;

        this.objectMapper = new ObjectMapper();
    }

    public ChampionshipChart retrieveChartForYear(final Integer year) throws IOException, InterruptedException,
                                                                             ClassNotFoundException,
                                                                             URISyntaxException {

        final Collection<MatchResult> results = championshipMatchResultsRetriever.retrieveResultsForYear(year);
        final Collection<String> resultJsonList = toJson(results);

        final Path remoteFile = hadoopService.exportContentToHdfs(resultJsonList, "match-results-" + year);

        final HadoopService.MapReduceJobConfiguration mapReduceJobConfig = new HadoopService.MapReduceJobConfiguration(
            remoteFile,
            ChampionshipResultsMapper.class,
            ChampionshipResultsReducer.class,
            ChampionshipResultsReducer.outputKeyClasz(),
            ChampionshipResultsReducer.outputValueClasz());

        final HadoopService.MapReduceJobResult mapReduceJobResult = hadoopService.executeJob(mapReduceJobConfig);
        if (!mapReduceJobResult.success()) {
            return new ChampionshipChart(new LinkedList<ChampionshipChart.ChartPosition>());
        }

        return buildOrderedChartFromJobOutput(mapReduceJobResult.outputPairs());
    }

    private ChampionshipChart buildOrderedChartFromJobOutput(final Map<String, String> jobOutput) {

        LinkedList<ChampionshipChart.ChartPosition> chartPositions = new LinkedList<>();
        for (Map.Entry<String, String> entry : jobOutput.entrySet()) {
            chartPositions.add(new ChampionshipChart.ChartPosition(entry.getKey(), Integer.valueOf(entry.getValue())));
        }

        // sort decreasingly to represent the championship result
        Collections.sort(chartPositions, new Comparator<ChampionshipChart.ChartPosition>() {
            @Override
            public int compare(ChampionshipChart.ChartPosition o1, ChampionshipChart.ChartPosition o2) {
                return o2.getScore().compareTo(o1.getScore());
            }
        });

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
