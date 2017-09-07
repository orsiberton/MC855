package com.mc855.spark.web.controller;

import com.mc855.spark.core.championship.model.ChampionshipChart;
import com.mc855.spark.core.championship.service.ChampionshipChartService;
import com.mc855.spark.core.championship.service.ChampionshipMetricsService;
import com.mc855.spark.web.model.RetrieveChartResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

@RestController
@RequestMapping("")
public class ChampionshipChartController {

    @Autowired
    private ChampionshipChartService chartService;

    @Autowired
    private ChampionshipMetricsService metricsService;

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
    public RetrieveChartResult metricsChart(@PathVariable("year") Integer year) throws URISyntaxException, IOException {
        final ChampionshipChart championshipChart = metricsService.retrieveChartForYear(year);

        return new RetrieveChartResult(true, championshipChart);
    }
}
