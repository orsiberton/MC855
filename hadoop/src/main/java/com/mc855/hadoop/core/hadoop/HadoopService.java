package com.mc855.hadoop.core.hadoop;

import com.mc855.hadoop.HadoopApplication;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class HadoopService {

    private final FileSystem hdfs;

    public HadoopService(final String nameNodeAddress) throws IOException {

        final Configuration conf = new Configuration();
        conf.set("fs.defaultFS", nameNodeAddress);

        this.hdfs = FileSystem.get(conf);
    }

    @PostConstruct
    public void init() throws Exception {
        new FsShell(hdfs.getConf()).run(new String[] {"-chmod", "-R", "777", "/"});
    }

    @PreDestroy
    public void destroy() {
        try {
            this.hdfs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path exportContentToHdfs(Collection<String> inputContentLines, String remoteFileName) throws IOException {

        final Path remoteInputFile = new Path(remoteFileName);

        try (final FSDataOutputStream outputStream = hdfs.create(remoteInputFile, true)) {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            for (String line : inputContentLines) {
                writer.write(line);
                writer.newLine();
            }
        }

        return remoteInputFile;
    }

    public MapReduceJobResult executeJob(final MapReduceJobConfiguration jobConfiguration) throws IOException,
                                                                                                  ClassNotFoundException,
                                                                                                  InterruptedException {

        Job job = new Job(this.hdfs.getConf(), UUID.randomUUID().toString());
        job.setJarByClass(HadoopApplication.class);
        job.setMapperClass(jobConfiguration.mapperClasz);
        job.setCombinerClass(jobConfiguration.reducerClasz);
        job.setReducerClass(jobConfiguration.reducerClasz);
        job.setOutputKeyClass(jobConfiguration.outputKeyClasz);
        job.setOutputValueClass(jobConfiguration.outputValueClasz);

        final Path remoteInputFilePath = jobConfiguration.remoteInputFilePath;
        final Path remoteOutputFolderPath = new Path(remoteInputFilePath.getName() + "-output");

        FileInputFormat.addInputPath(job, remoteInputFilePath);
        FileOutputFormat.setOutputPath(job, remoteOutputFolderPath);

        if (hdfs.exists(remoteOutputFolderPath)) {
            hdfs.delete(remoteOutputFolderPath, true);
        }

        boolean result = job.waitForCompletion(true);
        if (!result) {
            return new MapReduceJobResult(false, Collections.<String, String>emptyMap());
        }

        return new MapReduceJobResult(true, buildJobOutput(remoteOutputFolderPath));
    }

    private Map<String, String> buildJobOutput(final Path outputPath) throws IOException {

        final FileStatus[] filesStatus = hdfs.listStatus(outputPath);

        final Map<String, String> outputMap = new HashMap<>();
        for (FileStatus status : filesStatus) {
            final BufferedReader br = new BufferedReader(new InputStreamReader(hdfs.open(status.getPath())));

            String line = br.readLine();
            while (isNotBlank(line)) {
                line = br.readLine();

                String[] words = line.split(" ");
                outputMap.put(words[0], words[1]);
            }
        }

        return outputMap;
    }

    public static class MapReduceJobConfiguration {

        private Path remoteInputFilePath;
        private final Class<? extends Mapper> mapperClasz;
        private final Class<? extends Reducer> reducerClasz;
        private final Class outputKeyClasz;
        private final Class outputValueClasz;

        public MapReduceJobConfiguration(final Path remoteInputFilePath,
                                         final Class<? extends Mapper> mapperClasz,
                                         final Class<? extends Reducer> reducerClasz,
                                         final Class outputKeyClasz,
                                         final Class outputValueClasz) {

            this.remoteInputFilePath = remoteInputFilePath;
            this.mapperClasz = mapperClasz;
            this.reducerClasz = reducerClasz;
            this.outputKeyClasz = outputKeyClasz;
            this.outputValueClasz = outputValueClasz;
        }

    }


    public class MapReduceJobResult {

        private final Boolean success;
        private final Map<String, String> outputPairs;

        public MapReduceJobResult(Boolean success, Map<String, String> outputPairs) {
            this.success = success;
            this.outputPairs = outputPairs;
        }

        public Boolean success() {
            return success;
        }

        public Map<String, String> outputPairs() {
            return outputPairs;
        }

    }

}
