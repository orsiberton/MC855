package com.mc855.hadoop.web.model;

import com.mc855.hadoop.core.championship.model.ChampionshipChart;

public class RetrieveChartResult {

    private boolean success;
    private ChampionshipChart chart;

    public RetrieveChartResult() {
    }

    public RetrieveChartResult(boolean success, ChampionshipChart chart) {
        this.success = success;
        this.chart = chart;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ChampionshipChart getChart() {
        return chart;
    }

    public void setChart(ChampionshipChart chart) {
        this.chart = chart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RetrieveChartResult that = (RetrieveChartResult) o;

        if (success != that.success) {
            return false;
        }
        return chart != null ? chart.equals(that.chart) : that.chart == null;
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (chart != null ? chart.hashCode() : 0);
        return result;
    }
}
