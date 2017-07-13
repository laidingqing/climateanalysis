package com.gmos.lab.ml.regression

import com.gmos.lab.hbase.Utils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.evaluation._
import org.apache.spark.ml.feature.{VectorAssembler, VectorIndexer}
import org.apache.spark.ml.regression.{RandomForestRegressionModel, RandomForestRegressor}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.types.StructType
import org.dmg.pmml.PMML
import org.jpmml.model.MetroJAXBUtil
import org.jpmml.sparkml.ConverterUtil

class RandomForestPL (val sqlContext: SQLContext, val trainRdd: RDD[(Double, Double, Double, Double, Double)], val testRdd: RDD[(Double, Double, Double, Double, Double)]){

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

      val rf = new RandomForestRegressor()
        .setLabelCol("label")
        .setFeaturesCol("indexedFeatures")
        .setPredictionCol("prediction")
        .setImpurity("variance")
        .setMaxBins(32)
        .setMaxDepth(5)
        .setNumTrees(5)

      val pipeline = new Pipeline().setStages(Array(va, vi, rf))

      val eva = new RegressionEvaluator()
        .setLabelCol(rf.getLabelCol)
        .setPredictionCol(rf.getPredictionCol)
        .setMetricName("rmse")

      val paramGrid = new ParamGridBuilder()
       .addGrid(rf.maxBins, Array(32))
       .addGrid(rf.maxDepth, Array(5, 10))
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
      val bestRegressionModel: RandomForestRegressionModel = bestPipelineModel.stages(2).asInstanceOf[RandomForestRegressionModel]
      val res = bestRegressionModel.transform(vi.fit(va.transform(testing)).transform(va.transform(testing))).select(rf.getLabelCol, rf.getFeaturesCol, rf.getPredictionCol)

      println("Average Metrics Param Grid: " + combined.foreach(println))
      println("Training Root Mean Squared Error = " + eva.evaluate(res))
      //println("Learned regression tree model:\n" + bestRegressionModel.toDebugString)

      val fs = FileSystem.newInstance(new Configuration())

      val schema:StructType = training.schema
      val pmml:PMML = ConverterUtil.toPMML(schema, bestPipelineModel);
      Utils.delete(new Path("/root/gmos/model/pmml/rfpl"), fs)
      val fsos = fs.create(new Path("/root/gmos/model/pmml/rfpl"));
      MetroJAXBUtil.marshalPMML(pmml, fsos);
      fsos.close;

   }
}
