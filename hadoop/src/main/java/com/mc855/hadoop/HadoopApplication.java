package com.mc855.hadoop;

import com.mc855.hadoop.mapper.WordCountMapper;
import com.mc855.hadoop.reducer.WordCountReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.io.IOException;


/*import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@Configuration
@ImportResource("/META-INF/spring/application-context.xml")*/
public class HadoopApplication {

   /* public static void main(String[] args) {
        SpringApplication.run(HadoopApplication.class, args);
    }*/


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        // paths to input and output files
        /*Path inputPath = new Path("/README.txt");
        Path outputPath = new Path("/user/bberton/output/");*/
        Path inputPath = new Path("build.gradle");
        Path outputPath = new Path("/home/bberton/Documents/mc855/MC855/hadoop/results.result");

        // loads the default configuration
        Configuration configuration = new YarnConfiguration();

        // create the job
        Job job = Job.getInstance(configuration, "WordCount");
        job.setJarByClass(HadoopApplication.class);

        // Configure the mapper
        job.setMapperClass(WordCountMapper.class);
        // Configure the reducer
        job.setReducerClass(WordCountReducer.class);

        job.setNumReduceTasks(1);

        // specify key / value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Input
        FileInputFormat.addInputPath(job, inputPath);
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, outputPath);
        job.setOutputFormatClass(TextOutputFormat.class);

        // Delete output if exists
        int code;
        try (FileSystem hdfs = FileSystem.get(configuration)) {
            if (hdfs.exists(outputPath))
                hdfs.delete(outputPath, true);
        }

        // Execute job
        code = job.waitForCompletion(true) ? 0 : 1;
        System.exit(code);
    }

}
