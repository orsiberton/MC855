package com.mc855.spark.core.hadoop.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc855.spark.core.championship.model.MatchResult;
import org.apache.commons.math3.util.Pair;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

public class ChampionshipResultsMapper extends Mapper<Object, Text, Text, IntWritable> {

    private static final int WIN_SCORE = 3;
    private static final int DRAW_SCORE = 1;

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

        final MatchResult matchResult = objectMapper.readValue(value.toString(), MatchResult.class);
        final Collection<Pair<String, Integer>> teamsAndScores = retrieveScoreForTeams(matchResult);
        for (Pair<String, Integer> teamAndScore : teamsAndScores) {
            context.write(new Text(teamAndScore.getKey()), new IntWritable(teamAndScore.getValue()));
        }
        
    }

    private Collection<Pair<String, Integer>> retrieveScoreForTeams(MatchResult matchResult) {

        Integer homeTeamGoalCount = matchResult.getHomeTeamScore();
        Integer awayTeamGoalCount = matchResult.getAwayTeamScore();

        if (homeTeamGoalCount > awayTeamGoalCount) {

            // home team won
            return singletonList(new Pair<>(matchResult.getHomeTeam(), WIN_SCORE));
        } else if (homeTeamGoalCount < awayTeamGoalCount) {

            // away team won
            return singletonList(new Pair<>(matchResult.getAwayTeam(), WIN_SCORE));

        } else {

            // draw
            final List<Pair<String, Integer>> drawResults = new ArrayList<>();
            drawResults.add(new Pair<>(matchResult.getAwayTeam(), DRAW_SCORE));
            drawResults.add(new Pair<>(matchResult.getHomeTeam(), DRAW_SCORE));
            return drawResults;
        }
    }

}