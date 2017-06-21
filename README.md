# Problem - Climate Analysis

1.  Normalize and Ingest monthly historical weather station data from [NCDC archive](https://www.ncdc.noaa.gov/cdo-web/datasets) into a data store of your choice:

    > Global monthly summary data: [https://www.ncei.noaa.gov/data/gsom/archive/gsom-latest.tar.gz](https://www.ncei.noaa.gov/data/gsom/archive/gsom_latest.tar.gz)  
    > Documentation: [https://www1.ncdc.noaa.gov/pub/data/cdo/documentation/gsom-gsoy_documentation.pdf](https://www1.ncdc.noaa.gov/pub/data/cdo/documentation/gsom-gsoy_documentation.pdf)  
    > Station Data (also includes geolocation data):[https://www1.ncdc.noaa.gov/pub/data/ghcn/daily/ghcnd-stations.txt](https://www1.ncdc.noaa.gov/pub/data/ghcn/daily/ghcnd-stations.txt)  
    > Unzcompressed dataset is around 5.8GB

2.  Calculate and store average **seasonal temperature per year** for all years after 1900 for each 1°x1° grid on [Geographic Coordinate System](https://en.wikipedia.org/wiki/Geographic_coordinate_system). For the sake of this problem, assume that spring covers _March, April and May_; summer covers _June, July and August_; fall covers _September, October and November_; and winter covers _December, January and February._ Details of the implementation are left up to you. Sparsity of the data should be taken into account in your solution (i.e. store the number of available datapoints for each grid cell).

3.  **Bonus 1:** Develop a simple REST endpoint that accepts a coordinate (lat, lon) and will serve the following information:

    *   Average seasonal temperature for each season and year where data is available
    *   List of weather stations and number of available datapoints (i.e. non-null temperature entries) for each season and year where data is available
    
4.  **Bonus 2:** Develop an endpoint for your API that accepts 2 sets of coordinates _(lat1, lon1, lat2, lon2)_ and 2 integers _(startYear and endYear)_ as parameters to serve average temperature and available datapoints similar to above. The data should be averaged over the ‘rectangular’ area defined by supplied coordinates _(lat1, lon1)_ and _(lat2, lon2)_. Data should also be averaged over _startYear-endYear_ range .

5.  **Bonus 3:** Identify the country of each weather station, where applicable. You may use some publicly available API or come up with your own heuristic.

6.  **Bonus 4:** Define a metric that represents change in global temperature and/or precipitation over time, and visualize it on a heatmap.


# Solution - End to End Batch/Real-time ETL Data Pipeline

## Powered by the BIG DATA STACK

![Framework and Skills Used In The Solution](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/1.png)


## Pipeline Architecture Overview

![Pipeline Architecture Overview](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/2.png)


## Spark Batch ETL Architecture Overview

![Spark Batch ETL Architecture Overview](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/3.png)


## Spark Real-time ETL Architecture Overview

![Spark Real-time ETL Architecture Overview](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/4.png)


## Oozie Workflow Overview

![Oozie Workflow Overview](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/5.png)


## REST APIs Architecture

![REST APIs Architecture](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/6.png)

  
##  Screenshots
### Apache Zeppelin Notebooks
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/7.png)

### Apache Zeppelin Notebooks
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/8.png)

### Apache Zeppelin Notebooks
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/9.png)

### Apache Zeppelin Notebooks
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/10.png)

### Apache Nifi Workflow
![Apache Nifi Workflow](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/11.png)

### TensorFlow Notebooks
![TensorFlow Notebooks](http://advancedspark.com/img/tensorflow.png)

### Deploy Spark ML and TensorFlow Models into Production with Netflix OSS
![Hystrix Dashboard](http://advancedspark.com/img/hystrix-example-600x306.png)
![Hystrix Dashboard](http://advancedspark.com/img/hystrix-dashboard-annotated-640x411.png)

### Apache NiFi Data Flows
![Apache NiFi Data Flows](http://advancedspark.com/img/nifi-flow.png)

### AirFlow Workflows
![AirFlow Workflows](http://advancedspark.com/img/airflow.png)

### Presto Queries
![Presto Queries](http://advancedspark.com/img/presto.png)

### Tableau Integration
![Tableau Integration](http://advancedspark.com/img/flux-tableau.png)

### Beeline Command-line Hive Client
![Beeline Command-line Hive Client](http://advancedspark.com/img/flux-beeline.png)

### Log Visualization with Kibana & Logstash
![Log Visualization with Kibana & Logstash](http://advancedspark.com/img/flux-kibana.png)

### Spark, Spark Streaming, and Spark SQL Admin UIs
![Spark Admin UI](http://advancedspark.com/img/flux-spark-1.png)
![Spark Admin UI](http://advancedspark.com/img/flux-spark-2.png)
![Spark Admin UI](http://advancedspark.com/img/flux-spark-3.png)
![Spark Admin UI](http://advancedspark.com/img/flux-spark-4.png)
![Spark Admin UI](http://advancedspark.com/img/flux-spark-5.png)
![Spark Admin UI](http://advancedspark.com/img/flux-spark-6.png)

### Vector Host and Guest (Docker) System Metric UIs
![Vector Metrics UI](http://advancedspark.com/img/vector-01.png)
![Vector Metrics UI](http://advancedspark.com/img/vector-02.png)
![Vector Metrics UI](http://advancedspark.com/img/vector-03.png)

### Ganglia System and JVM Metrics Monitoring UIs
![Ganglia Metrics UI](http://advancedspark.com/img/flux-ganglia-1.png)
![Ganglia Metrics UI](http://advancedspark.com/img/flux-ganglia-2.png)
![Ganglia Metrics UI](http://advancedspark.com/img/flux-ganglia-3.png)

## Tools Overview
![Apache Spark](http://spark.apache.org/images/spark-logo.png) ![Redis](https://upload.wikimedia.org/wikipedia/en/thumb/6/6b/Redis_Logo.svg/200px-Redis_Logo.svg.png)
![Apache Cassandra](https://upload.wikimedia.org/wikipedia/commons/a/a0/Cassandra_logo.png)
![Apache Kafka](http://www.bogotobogo.com/Hadoop/images/Ecosystem/Kafka.png)
![NiFi](http://advancedspark.com/img/nifi-logo.png)
![ElasticSearch Logstash Kibana](https://www.enalean.com/sites/default/files/field/image/elk-logos.png) ![Apache Zeppelin](http://4.bp.blogspot.com/-rsc3t_dZmBg/VbPDwhb_IBI/AAAAAAAABeY/9zKUjK4VFbQ/s1600/zeppelin-bl.png) ![Ganglia](https://developer.nvidia.com/sites/default/files/akamai/cuda/images/Ganglia-logo-small-rs.jpg) ![Hadoop HDFS](http://datatechblog.com/wp-content/uploads/2014/04/HadoopHive.png) ![iPython Notebook](http://ipython.org/ipython-doc/dev/_images/ipynb_icon_128x128.png)
![Docker](http://blog.docker.com/wp-content/uploads/2013/08/KuDr42X_ITXghJhSInDZekNEF0jLt3NeVxtRye3tqco.png)
