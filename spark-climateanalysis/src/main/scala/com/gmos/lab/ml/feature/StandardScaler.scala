package com.gmos.lab.ml.feature

import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

object StandardScaler {

  def transform(rdd: RDD[LabeledPoint]) = {
    val scaler = new StandardScaler(withMean = true, withStd = true).fit(rdd.map(x => x.features))
    rdd.map(x => LabeledPoint(x.label, scaler.transform(x.features)))
  }

}
