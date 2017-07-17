package com.gmos.lab.ml.regression

import com.gmos.lab.hbase.Utils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.evaluation._
import org.apache.spark.ml.feature.{VectorAssembler, VectorIndexer}
import org.apache.spark.ml.regression.{GBTRegressionModel, GBTRegressor}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.types.StructType
import org.dmg.pmml.PMML
import org.jpmml.model.MetroJAXBUtil
import org.jpmml.sparkml.ConverterUtil

class GradientBoostedTreesPL (val sqlContext: SQLContext, val trainRdd: RDD[(Double, Double, Double, Double, Double)], val testRdd: RDD[(Double, Double, Double, Double, Double)]){

   def execute() = {
      import sqlContext.implicits._

      val training = trainRdd.toDF("label","latitude", "longitude", "year", "month")
      val testing = testRdd.toDF("label","latitude", "longitude", "year", "month")

      val va = new VectorAssembler()
        .setInputCols(Array("latitude", "longitude", "year", "month"))
        .setOutputCol("features");

      val vi = new VectorIndexer()
        .setInputCol("features")
        .setOutputCol("indexedFeatures")
        .setMaxCategories(5)

      val gbt = new GBTRegressor()
        .setLabelCol("label")
        .setFeaturesCol("indexedFeatures")
        .setPredictionCol("prediction")
        //.setMaxIter(10)
        .setMaxBins(32)
        .setMaxDepth(5)

      val pipeline = new Pipeline().setStages(Array(va, vi, gbt))

      val eva = new RegressionEvaluator()
        .setLabelCol(gbt.getLabelCol)
        .setPredictionCol(gbt.getPredictionCol)
        .setMetricName("rmse")

      val paramGrid = new ParamGridBuilder()
       //.addGrid(gbt.maxIter, Array(5, 10))
       .addGrid(gbt.maxBins, Array(32))
       .addGrid(gbt.maxDepth, Array(5))
       .build()

      val numFolds = 3
      val modelValidator = new CrossValidator()
       .setEstimator(pipeline)
       .setEvaluator(eva)
       .setNumFolds(numFolds)
       .setEstimatorParamMaps(paramGrid)

      val crossValidatorModel = modelValidator.fit(training)
      val avgMetricsParamGrid = crossValidatorModel.avgMetrics
      val combined = paramGrid.zip(avgMetricsParamGrid).sortBy(paramGridMetricTuple => (1 - paramGridMetricTuple._2))
      val bestPipelineModel = crossValidatorModel.bestModel.asInstanceOf[PipelineModel]
      val bestRegressionModel: GBTRegressionModel = bestPipelineModel.stages(2).asInstanceOf[GBTRegressionModel]
      val res = bestRegressionModel.transform(vi.fit(va.transform(testing)).transform(va.transform(testing))).select(gbt.getLabelCol, gbt.getFeaturesCol, gbt.getPredictionCol)

      println("Average Metrics Param Grid: " + combined.foreach(println))
      println("Training Root Mean Squared Error = " + eva.evaluate(res))
      //println("Learned regression tree model:\n" + bestRegressionModel.toDebugString)

      val fs = FileSystem.newInstance(new Configuration())

      val schema:StructType = training.schema
      val pmml:PMML = ConverterUtil.toPMML(schema, bestPipelineModel);
      Utils.delete(new Path("/root/gmos/model/pmml/gbtpl"), fs)
      val fsos = fs.create(new Path("/root/gmos/model/pmml/gbtpl"));
      MetroJAXBUtil.marshalPMML(pmml, fsos);
      fsos.close;

   }
}
