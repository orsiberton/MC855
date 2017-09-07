package com.mc855.spark.core.hadoop.reducer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class ChampionshipResultsReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    public static Class<? extends WritableComparable> outputKeyClasz() {
        return Text.class;
    }

    public static Class<? extends WritableComparable> outputValueClasz() {
        return IntWritable.class;
    }

    @Override
    public void reduce(Text text, Iterable<IntWritable> values, Context context) throws IOException,
                                                                                        InterruptedException {

        int scoreSumForTeam = 0;
        for (IntWritable value : values) {
            scoreSumForTeam += value.get();
        }
        // text contains the team name
        context.write(text, new IntWritable(scoreSumForTeam));

    }

}
