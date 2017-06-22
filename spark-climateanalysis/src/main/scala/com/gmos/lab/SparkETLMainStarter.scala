package com.gmos.lab

import org.apache.spark.{SparkConf, SparkContext}
import com.gmos.lab.action._

object SparkETLMainStarter {
  def main(args: Array[String]){
    val conf = new SparkConf().setAppName("Spark GMOS ETL Main Starter")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    // Dataset comes from this link: https://www.ncei.noaa.gov/data/gsom/archive/gsom_latest.tar.gz
    // There are a couple of data ingestion solutions here:
    // 1) With Oozie job run once a day to grab the dataset from the link above, then upload into HDFS
    //    Then trigger the next spark ETL action to transform, enrich and persist the dataset into HDFS as parquet
    //    Hive, HBase or Cassandra
    // 2) Use Nifi to grab the dataset from the link above, then upload into HDFS
    //    Spark Streaming will pick up the dataset from HDFS
    //    Then trigger the next spark ETL action to transform, enrich and persist the dataset into HDFS as parquet
    //    Hive, HBase or Cassandra

    // spark submit command line at my local sandbox
    // spark-submit --class com.gmos.lab.SparkETLMainStarter --driver-memory 2G --executor-memory 2G --master local[1] /root/GmosOnSpark.jar

    // Since raw file is gz format file and unsplittable, so this transformer will make the raw file splittable into HDFS
    // otherwise the further data processing and computation is not in processed in parallel
    // input path: /root/gsom-latest.tar.gz
    // output path: /root/gmos/income
    // It will take about 2 minutes running at my local sandbox
    // The file size will reduce from to 9G to 7G

    // Will uncomment out after test
    GmosGzSplittable.transform(sc)

    // To do
    // Another way to split the gz file is to create custom input format
    // and tag the dataset as schema category

    // Read the raw gmos file, which will be splittable into about 50 partitions
    // input path: /root/gmos/income

    // Will uncomment out after test
    val rawGmosRdd = ReadGmosRaw.action(sc)

    //Should be uncommnent out in order to cahce RDD
    //But since my local sandbox has limited memory, it doesn't make sense to cache because the linage is always recomputed
    //Also bring up some lock issue since the broadcast is waiting there forever
    //rawGmosRdd.cache()

    // To do
    // Create custom partitioner based on the schema category

    // Generate full schema based on the header of all csv files and merge them together

    // Will uncomment out after test
    val tFullSchema = FullSchemaGenerator.createSchema(rawGmosRdd)

    // Generate station schema based on the header of each station csv file
    // This process will take 2 or 3 minutes at my local sandbox

    // Will uncomment out after test
    val schemaPerStation:scala.collection.Map[String, Map[Int,Int]] = StationSchemaCollector.createSchema(sc, rawGmosRdd, tFullSchema)


    // To do
    // Another solution is to group the station dataset as schema category, about 100 schema categories
    // Save each schema category as parquet which has the same schema
    // Merge the schema category and parquet with date frame using Spark
    // This process is very expensive

    // Broadcast full schema and schema for station collections to the executors

    // Will uncomment out after test
    val broadcastVar = sc.broadcast(schemaPerStation)
    val fullschemaVar = sc.broadcast(tFullSchema._2)

    // Save raw dataset as parquet file with full schema
    // Destination folder: /root/gmos/etl/gmos_raw.parquet
    // This transformation will take one hour or so at my local sandbox

    // Will uncomment out after test
    RawDatasetToParquetTransformer.transform(sqlContext, rawGmosRdd, broadcastVar, fullschemaVar, tFullSchema._1)

    // Save raw dataset as avro file with full schema
    // The performance is much worse than parquet
    // Comment for now because this is just for comparison with parquet
    // RawDatasetToAvroTransformer.transform(sqlContext);

    // Enrichment of Raw Parquet File
    // This will take about one hour or so at my local sandbox
    // Will uncomment out after test
    RawParquetEnrichment.transform(sqlContext)

    // Enrichment of Raw Parquet File With Partition
    // Comment out because this process is pretty slow at my local sandbox
    // RawParquetEnrichmentWithPartition.transform(sqlContext)

    // Create External Hive Table
    // Location /root/gmos/etl/gmos_enrich.parquet
    // Will uncomment out after test
    HiveTableAsParquetCreator.transform(sqlContext)

    // Generate the report
    // Will uncomment out after test
    ReportGenerator.action(sqlContext)

    // Persist the report to Cassandra
    // Will uncomment out after test
    ReportToCassandra.action(sqlContext)

    // Output station and geo mapping dataset into my sql as reference table
    // Will uncomment out after test
    StationGeoMapper.action(sqlContext)

    }

}
