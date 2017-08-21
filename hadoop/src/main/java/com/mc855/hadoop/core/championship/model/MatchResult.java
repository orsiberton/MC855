package com.mc855.hadoop.core.championship.model;

public class MatchResult {

    private String homeTeam;
    private String awayTeam;

    private Integer homeTeamScore;
    private Integer awayTeamScore;

    public MatchResult(String homeTeam, String awayTeam, Integer homeTeamScore, Integer awayTeamScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public Integer getHomeTeamScore() {
        return homeTeamScore;
    }

    public Integer getAwayTeamScore() {
        return awayTeamScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MatchResult that = (MatchResult) o;

        if (homeTeam != null ? !homeTeam.equals(that.homeTeam) : that.homeTeam != null) {
            return false;
        }
        if (awayTeam != null ? !awayTeam.equals(that.awayTeam) : that.awayTeam != null) {
            return false;
        }
        if (homeTeamScore != null ? !homeTeamScore.equals(that.homeTeamScore) : that.homeTeamScore != null) {
            return false;
        }
        return awayTeamScore != null ? awayTeamScore.equals(that.awayTeamScore) : that.awayTeamScore == null;
    }

    @Override
    public int hashCode() {
        int result = homeTeam != null ? homeTeam.hashCode() : 0;
        result = 31 * result + (awayTeam != null ? awayTeam.hashCode() : 0);
        result = 31 * result + (homeTeamScore != null ? homeTeamScore.hashCode() : 0);
        result = 31 * result + (awayTeamScore != null ? awayTeamScore.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
               "homeTeam='" +
               homeTeam +
               '\'' +
               ", awayTeam='" +
               awayTeam +
               '\'' +
               ", homeTeamScore=" +
               homeTeamScore +
               ", awayTeamScore=" +
               awayTeamScore +
               '}';
    }
}
