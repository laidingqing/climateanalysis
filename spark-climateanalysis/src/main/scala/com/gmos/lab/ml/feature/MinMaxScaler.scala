package com.gmos.lab.ml.feature

import org.apache.spark.ml.feature.MinMaxScaler
import org.apache.spark.sql.DataFrame

object MinMaxScaler {

  def transform(df:DataFrame) = {
    val scaler = new MinMaxScaler().setInputCol("features").setOutputCol("scaledFeatures")
    val scalerModel = scaler.fit(df)
    scalerModel.transform(df)
  }
}
