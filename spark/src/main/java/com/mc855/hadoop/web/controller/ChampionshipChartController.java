package com.mc855.hadoop.web.controller;

import com.mc855.hadoop.core.championship.model.ChampionshipChart;
import com.mc855.hadoop.core.championship.service.ChampionshipChartService;
import com.mc855.hadoop.web.model.RetrieveChartResult;
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
    ChampionshipChartService chartService;

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

}
