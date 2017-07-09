package com.gmos.lab.ml.regression

import com.gmos.lab.hbase.Utils
import com.gmos.lab.ml.evaluation.RegressionMetrics
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.evaluation._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.LinearRegressionModel
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.types.StructType
import org.dmg.pmml.PMML
import org.jpmml.model.{ImportFilter, MetroJAXBUtil}
import org.jpmml.sparkml.ConverterUtil
import org.xml.sax.InputSource
import org.dmg.pmml.RegressionModel

class LinearRegressionPL (val sqlContext: SQLContext, val trainRdd: RDD[(Double, Double, Double, Double, Double)], val testRdd: RDD[(Double, Double, Double, Double, Double)]){

   def execute() = {
      import sqlContext.implicits._

      val training = trainRdd.toDF("label","latitude", "longitude", "year", "month")
      val testing = testRdd.toDF("label","latitude", "longitude", "year", "month")

      val va:VectorAssembler = new VectorAssembler()
        .setInputCols(Array("latitude", "longitude", "year", "month"))
        .setOutputCol("features");

      val lir = new org.apache.spark.ml.regression.LinearRegression()
        .setFeaturesCol("features")
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setRegParam(0.0)
        .setElasticNetParam(1.0)
        .setMaxIter(100)
        .setStandardization(true)
        .setTol(1E-9)
        .setFitIntercept(true)
        .setSolver("l-bfgs")

      val pipeline = new Pipeline().setStages(Array(va, lir))

      val eva = new RegressionEvaluator()
        .setLabelCol(lir.getLabelCol)
        .setPredictionCol(lir.getPredictionCol)
        .setMetricName("mse")

      val paramGrid = new ParamGridBuilder()
       .addGrid(lir.regParam, Array(0.0))
       .addGrid(lir.elasticNetParam, Array(1.0, 0.5, 0.0))
       .addGrid(lir.maxIter, Array(100))
       .addGrid(lir.tol, Array(1E-9))
       .addGrid(lir.solver, Array("auto"))
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
      val bestRegressionModel = bestPipelineModel.stages(1).asInstanceOf[LinearRegressionModel]
      val res = bestRegressionModel.transform(va.transform(testing)).select(lir.getLabelCol, lir.getFeaturesCol, lir.getPredictionCol)

      println("Average Metrics Param Grid: " + combined.foreach(println))
      println("Training Mean Squared Error = " + eva.evaluate(res))
      println(s"Weights: ${bestRegressionModel.coefficients} Intercept: ${bestRegressionModel.intercept}")

      val trainingSummary = bestRegressionModel.summary
      println(s"numIterations: ${trainingSummary.totalIterations}")
      println(s"objectiveHistory: ${trainingSummary.objectiveHistory.toList}")
      trainingSummary.residuals.show()
      println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
      println(s"r2: ${trainingSummary.r2}")

      val fs = FileSystem.newInstance(new Configuration())
      Utils.delete(new Path("/root/gmos/model/lrpl"), fs)
      bestRegressionModel.save("/root/gmos/model/lrpl")

      val savedRegressionModel = LinearRegressionModel.load("/root/gmos/model/lrpl")
      val predictions = savedRegressionModel.transform(va.transform(testing)).select(lir.getLabelCol, lir.getFeaturesCol, lir.getPredictionCol)
      println("Prediction Mean Squared Error = " + eva.evaluate(predictions))

      val schema:StructType = training.schema
      val pmml:PMML = ConverterUtil.toPMML(schema, bestPipelineModel);
      Utils.delete(new Path("/root/gmos/model/pmml/lrpl"), fs)
      val fsos = fs.create(new Path("/root/gmos/model/pmml/lrpl"));
      MetroJAXBUtil.marshalPMML(pmml, fsos);
      fsos.close;

      val fsis = fs.open(new Path("/root/gmos/model/pmml/lrpl"));
      val transformedSource = ImportFilter.apply(new InputSource(fsis));
      val pmmlloaded = org.jpmml.model.JAXBUtil.unmarshalPMML(transformedSource);
      val model = pmmlloaded.getModels.get(0).asInstanceOf[RegressionModel]
      val table = model.getRegressionTables.get(0)
      val intercept = table.getIntercept
      val latitude = table.getNumericPredictors.get(0).getCoefficient
      val longitude = table.getNumericPredictors.get(1).getCoefficient
      val year = table.getNumericPredictors.get(2).getCoefficient
      val month = table.getNumericPredictors.get(3).getCoefficient
      val lrModelfromPMML = new org.apache.spark.mllib.regression.LinearRegressionModel(Vectors.dense(Array(latitude,longitude,year,month)), intercept)
      val valuesAndPreds = testRdd.map { point =>
         val prediction = lrModelfromPMML.predict(Vectors.dense(point._2, point._3, point._4, point._5))
         (point._1, BigDecimal(prediction).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)
      }
      val rm = new RegressionMetrics(valuesAndPreds)
      println("Prediction PMML Mean Squared Error = " + rm.meanSquaredError)

   }
}
