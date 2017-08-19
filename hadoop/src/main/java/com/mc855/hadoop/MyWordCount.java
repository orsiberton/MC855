package com.mc855.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MyWordCount {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        conf.set("fs.defaultFS", "hdfs://hdfs-namenode:9000");

        Path inputFile = new Path("/README.txt");
        Path outputPath = new Path("/README.result000");

        Job job = new Job(conf, "My Word Count WITH SPRING");
        job.setJarByClass(MyWordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, inputFile);
        FileOutputFormat.setOutputPath(job, outputPath);

        try (FileSystem hdfs = FileSystem.get(conf)) {
            if (hdfs.exists(outputPath)) {
                hdfs.delete(outputPath, true);
            }
        }

        Boolean completed = job.waitForCompletion(true);

        try (FileSystem hdfs = FileSystem.get(conf)) {
            FileStatus[] filesStatus = hdfs.listStatus(outputPath);

            System.out.println("MapReduce Result");

            for (FileStatus status : filesStatus) {
                BufferedReader br = new BufferedReader(new InputStreamReader(hdfs.open(status.getPath())));
                String line;
                line = br.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = br.readLine();
                }
            }
        }

        System.exit(completed ? 0 : 1);
    }

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
}