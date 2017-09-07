package com.mc855.spark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc855.spark.core.championship.model.MatchResult;

import java.io.IOException;

public class Utils {

    public static MatchResult createMatchResult(String line) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        MatchResult matchResult = objectMapper.readValue(line, MatchResult.class);

        return matchResult;
    }
}
