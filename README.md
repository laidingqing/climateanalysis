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
### Apache Zeppelin Notebooks via Spark SQL, Datamap and Java Script
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/7.png)

### Apache Zeppelin Notebooks via Spark SQL and Hive Query
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/8.png)

### Apache Zeppelin Notebooks via Spark SQL and Hive Query
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/9.png)

### Apache Zeppelin Notebooks via Spark SQL and Hive Query
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/10.png)

### Apache Zeppelin Notebooks - Report via Cassandra Query
![Apache Zeppelin Notebooks](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/31.png)

### Apache Nifi Data Flow
![Apache Nifi Data Flow](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/11.png)

### Spark, Spark Streaming, and Spark SQL Admin UIs
![Spark Admin UI](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/12.png)

### Spark Streaming Driver Console
![Spark Driver Console](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/24.png)

### Falcon Data Management UIs - Cluster Configuration
![Falcon Data Management UI](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/13.png)

### Falcon Data Management UIs - Feed Configuration
![Falcon Data Management UI](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/14.png)

### Falcon Data Management UIs - Process Configuration
![Falcon Data Management UI](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/15.png)

### Falcon Data Management UIs - Process Running Status
![Falcon Data Management UI](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/16.png)

### Oozie Server Monitoring UIs - ETL Actions
![Oozie Server Monitoring UI](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/17.png)

### Oozie Server Monitoring UIs - Workflow
![Oozie Server Monitoring UI](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/18.png)

### Spring Boot Console
![Spring Boot Console](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/19.png)

### Intellij IDE for Spring Boot, Spring MVC and REST Template
![Spring Boot Console](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/26.png)

### Presto Queries via CLI
![Presto Queries](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/20.png)

### Spark SQL Queries via Spark Shell
![spark SQL Queries](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/21.png)

### Hive Queries via Hive CLI
![Hive Queries](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/22.png)

### Redis Queries via Redis Client CLI
![Redis Queries](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/23.png)

### Cassandra Queries via CQLSH
![Cassandra Queries via CQLSH](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/30.png)

### Kafka Message Consumer via Kafka Console
![Kafka Message Consumer](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/25.png)

### REST API via Spark Thrift Server
![REST API via Spark Thrift Server](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/27.png)

### Dataset Schema Merged by Spark ETL
![Dataset Schema Merged by Spark ETL](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/28.png)

### Report Schema Generated by Spark ETL
![Report Schema Generated by Spark ETL](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/29.png)

### ElasticSearch and Kibana - Start Service and Create Index
![ElasticSearch and Kibana](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/32.png)

### Kibana Visualization Dashboard UIs
![Kibana Visualization Dashboard UIs](https://github.com/binjiangca/climateanalysis/blob/master/spark-climateanalysis/doc/img/33.png)
