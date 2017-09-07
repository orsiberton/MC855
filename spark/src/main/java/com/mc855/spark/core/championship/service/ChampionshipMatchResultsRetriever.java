package com.mc855.spark.core.championship.service;

import com.mc855.spark.core.championship.model.MatchResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.lang.ClassLoader.getSystemResource;

public class ChampionshipMatchResultsRetriever {

    private static final Character DELIMITER = ',';

    private static final String AWAY_TEAM = "AwayTeam";
    private static final String HOME_TEAM = "HomeTeam";
    private static final String FTAG = "FTAG";
    private static final String FTHG = "FTHG";

    private final String premierLeagueResultsFolder;

    public ChampionshipMatchResultsRetriever(String premierLeagueResultsFolder) {
        this.premierLeagueResultsFolder = premierLeagueResultsFolder;
    }

    public Collection<MatchResult> retrieveResultsForYear(final Integer year) throws IOException, URISyntaxException {

        final Set<MatchResult> matchResults = new HashSet<>();

        final String fileName = year + ".csv";

        final URL resultsCsvFileUrl = getSystemResource(premierLeagueResultsFolder + "/" + fileName);
        final File inputFile = new File(resultsCsvFileUrl.toURI());

        if (!inputFile.exists()) {
            return matchResults;
        }

        try (final InputStream csvStream = new FileInputStream(inputFile)) {

            final BufferedReader fileReader = new BufferedReader(new InputStreamReader(csvStream));

            final Iterable<CSVRecord> records =
                CSVFormat.EXCEL.withFirstRecordAsHeader().withDelimiter(DELIMITER).parse(fileReader);
            for (final CSVRecord record : records) {

                final String homeTeam = record.get(HOME_TEAM);
                final String awayTeam = record.get(AWAY_TEAM);
                final String htGoals = record.get(FTHG);
                final String atGoals = record.get(FTAG);

                matchResults.add(new MatchResult(homeTeam,
                    awayTeam,
                    Integer.valueOf(htGoals),
                    Integer.valueOf(atGoals)));
            }
        }

        return matchResults;
    }

}
