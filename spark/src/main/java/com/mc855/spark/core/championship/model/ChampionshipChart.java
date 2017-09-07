package com.mc855.spark.core.championship.model;

import java.util.LinkedList;

public class ChampionshipChart {

    private LinkedList<ChartPosition> entries;

    public ChampionshipChart() {
    }

    public ChampionshipChart(final LinkedList<ChartPosition> entries) {
        this.entries = entries;
    }

    public LinkedList<ChartPosition> getEntries() {
        return entries;
    }

    public void setEntries(LinkedList<ChartPosition> entries) {
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChampionshipChart that = (ChampionshipChart) o;

        return entries != null ? entries.equals(that.entries) : that.entries == null;
    }

    @Override
    public int hashCode() {
        return entries != null ? entries.hashCode() : 0;
    }

    public static class ChartPosition {

        private String teamName;
        private Integer score;

        public ChartPosition() {
        }

        public ChartPosition(String teamName, Integer score) {
            this.teamName = teamName;
            this.score = score;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ChartPosition that = (ChartPosition) o;

            if (teamName != null ? !teamName.equals(that.teamName) : that.teamName != null) {
                return false;
            }
            return score != null ? score.equals(that.score) : that.score == null;
        }

        @Override
        public int hashCode() {
            int result = teamName != null ? teamName.hashCode() : 0;
            result = 31 * result + (score != null ? score.hashCode() : 0);
            return result;
        }
    }
}
