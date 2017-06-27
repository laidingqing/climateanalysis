package com.gmos.lab.hbase

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import com.redislabs.provider.redis._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.Partitioner

object SparkETLToHBase {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark GMOS ETL To ELK")
    conf.set("redis.host", "localhost")
    conf.set("redis.port", "6379")
    val esConfig = Map("pushdown" -> "true", "es.nodes" -> "sandbox.hortonworks.com", "es.port" -> "9200")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._
    val stationRDD:RDD[(String,String)] = sc.fromRedisKV("*")
    val dfredis = stationRDD.toDF("station","country")
    dfredis.registerTempTable("gmos_lookup")
    val dfparquet = sqlContext.read.parquet("/root/gmos/etl/gmos_enrich.parquet")
    dfparquet.filter(dfparquet("gtavg").isNotNull).registerTempTable("gmos_enrich")
    sqlContext.udf.register("toString", (d: Double) => String.valueOf(d))
    sqlContext.udf.register("toDouble", (s: String) => s.toDouble)
    val df = sqlContext.sql("select a.gstation as station, b.country, a.gdate as date, a.year, a.season, toString(a.glatitude) as latitude, toString(a.glongitude) as longitude, toDouble(a.gtavg) as tavg  from gmos_enrich a left join gmos_lookup b on a.gstation = b.station where length(a.gtavg) > 0 and a.glatitude is not null and a.glongitude is not null")
    val saltedRDD = df.rdd.map(row => (salt((row.getString(row.fieldIndex("station")),row.getString(row.fieldIndex("date"))),5), row))
    val partitionedRDD = saltedRDD.repartitionAndSortWithinPartitions(new SaltPrefixPartitioner(5))
    val cells = partitionedRDD.flatMap(r => {
      val row = r._2
      val saltedRowKey = Bytes.toBytes(r._1)
      val station = new KeyValue(
        saltedRowKey,
        Bytes.toBytes("climate"),
        Bytes.toBytes("c1"),
        Bytes.toBytes(row.getString(row.fieldIndex("station"))))
      val country = new KeyValue(
        saltedRowKey,
        Bytes.toBytes("climate"),
        Bytes.toBytes("c2"),
        Bytes.toBytes(row.getString(row.fieldIndex("country"))))
      val date = new KeyValue(
        saltedRowKey,
        Bytes.toBytes("climate"),
        Bytes.toBytes("c3"),
        Bytes.toBytes(row.getString(row.fieldIndex("date"))))
      val year = new KeyValue(
        saltedRowKey,
        Bytes.toBytes("climate"),
        Bytes.toBytes("c4"),
        Bytes.toBytes(row.getString(row.fieldIndex("year"))))
      val season = new KeyValue(
        saltedRowKey,
        Bytes.toBytes("climate"),
        Bytes.toBytes("c5"),
        Bytes.toBytes(row.getString(row.fieldIndex("season"))))
      val latitude = new KeyValue(
        saltedRowKey,
        Bytes.toBytes("climate"),
        Bytes.toBytes("c6"),
        Bytes.toBytes(row.getString(row.fieldIndex("latitude"))))
      val longitude = new KeyValue(
        saltedRowKey,
        Bytes.toBytes("climate"),
        Bytes.toBytes("c7"),
        Bytes.toBytes(row.getString(row.fieldIndex("longitude"))))
      val tavg = new KeyValue(
        saltedRowKey,
        Bytes.toBytes("climate"),
        Bytes.toBytes("c8"),
        Bytes.toBytes(row.getDouble(row.fieldIndex("tavg"))))

      List((new ImmutableBytesWritable(Bytes.toBytes(r._1 + ":0")), station),
        (new ImmutableBytesWritable(Bytes.toBytes(r._1 + ":1")), country),
        (new ImmutableBytesWritable(Bytes.toBytes(r._1 + ":2")), date),
        (new ImmutableBytesWritable(Bytes.toBytes(r._1 + ":3")), year),
        (new ImmutableBytesWritable(Bytes.toBytes(r._1 + ":4")), season),
        (new ImmutableBytesWritable(Bytes.toBytes(r._1 + ":5")), latitude),
        (new ImmutableBytesWritable(Bytes.toBytes(r._1 + ":6")), longitude),
        (new ImmutableBytesWritable(Bytes.toBytes(r._1 + ":7")), tavg)
      )})
    val baseConf = HBaseConfiguration.create()
    baseConf.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
    baseConf.set("zookeeper.znode.parent", "/hbase-unsecure");
    val job = Job.getInstance(baseConf, "climate data")
    val hconf = job.getConfiguration
    val table = new HTable(hconf, "climate_data")
    CustomHFileOutputFormat2.configureIncrementalLoad(job, table)
    val fs = FileSystem.newInstance(hconf)
    Utils.delete(new Path("/tmp/climate_data/hfiles"), fs)
    cells.saveAsNewAPIHadoopFile(
      "/tmp/climate_data/hfiles",
      classOf[ImmutableBytesWritable],
      classOf[KeyValue],
      classOf[CustomHFileOutputFormat2],
      hconf)
    Utils.chmod(new Path("/tmp/climate_data/hfiles"), fs)
    Utils.doBulkLoad(hconf,"/tmp/climate_data/hfiles","climate_data")
  }


  def salt(key: (String, String), modulus: Int) : String = {
    val saltAsInt = Math.abs(key._1.hashCode) % modulus
    val charsInSalt = digitsRequired(modulus)
    ("%0" + charsInSalt + "d").format(saltAsInt) + ":" + key._1 + ":" + key._2
  }

  def digitsRequired(modulus: Int) : Int = {
    (Math.log10(modulus-1)+1).asInstanceOf[Int]
  }

  class SaltPrefixPartitioner[K,V](modulus: Int) extends Partitioner {
    val charsInSalt = digitsRequired(modulus)
    def getPartition(key: Any): Int = {
      key.toString.substring(0,charsInSalt).toInt
    }

    override def numPartitions: Int = modulus
  }

}
