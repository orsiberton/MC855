docker run -d --name yarn \
		-h yarn \
		-p 8088:8088 \
     	-p 8042:8042 \
		--link=260_namenode_1:260_namenode_1 \
		--link=260_datanode_1:260_datanode_1 \
		-v $HOME/data/hadoop/hdfs:/data \
		gelog/hadoop start-yarn.sh && \
docker logs -f yarn
