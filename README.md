# MC855

![hadoop logo](https://hadoop.apache.org/images/hadoop-logo.jpg)
![spring logo](https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png)
![docker logo](https://www.docker.com/sites/default/files/horizontal_large.png)

Esta aplicação tem como objetivo mostrar uma das formas de se usar o hadoop em um mundo corporativo. Integrando o hadoop com outros dois frameworks muito comuns nas empresas, que são o Spring Framework e o Docker.

Para iniciar a aplicação, primeiramente inicialize o docker, indo na pasta docker-hadoop-master/2.6.0 e inicie o hadoop namenode e datanode com:
```sh
$ docker-compose up -d && docker-compose logs
```
Este comando irá executar um namenode, um segundo namenode e um datanode.

Após o hadoop executando dentro do docker, vá para a pasta hadoop e inicie a aplicação com:
```sh
$ gradle bootRun
```
Pronto, sua aplicação está executando agora.
Agora é só acessar a url http://localhost:17071/championship_chart/{year} escolhando algum ano e você terá a tabela do campeonato do ano escolhido.

Dados foram baixados aqui:
- http://www.football-data.co.uk/englandm.php

Referências:
- https://hadoop.apache.org/
- https://spring.io/
- https://www.docker.com/
- https://gradle.org/
- https://github.com/bigdatafoundation/docker-hadoop
- http://www.devx.com/opensource/create-an-apache-hadoop-mapreduce-job-using-spring.html
- https://www.slideshare.net/SpringCentral/spring-one2gx-2013springforapachehadoop
