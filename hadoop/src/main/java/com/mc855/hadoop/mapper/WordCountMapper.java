package com.mc855.hadoop.mapper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {

    private Text word = new Text();

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer lineTokenz = new StringTokenizer(line);

        while (lineTokenz.hasMoreTokens()) {
            String cleanedData = removeNonLettersNonNumbers(lineTokenz.nextToken());
            word.set(cleanedData);
            context.write(word, new IntWritable(1));
        }
    }

    /**
     * Replace all Unicode characters that are neither numbers nor letters with an empty string.
     *
     * @param original, It is the original string
     * @return a string object that contains only letters and numbers
     */
    private String removeNonLettersNonNumbers(String original) {
        return original.replaceAll("[^\\p{L}\\p{N}]", "");
    }
}