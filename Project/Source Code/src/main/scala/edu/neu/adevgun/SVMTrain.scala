package edu.neu.adevgun

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

/**
  * Data mining based on SVM
  */
object SVMTrain {
  def main(args: Array[String]) {

    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("Model Trainer"))

    //Input training data
    val input = sc.textFile(args(0)).map(row => row.split(","))

    //Converted the input file to libsvm format
    val trainingData = input.map(row => new LabeledPoint(row.last.toByte,
      Vectors.dense(row.take(row.length-1).map(str => str.toDouble))))

    //Configurable parameters
    val numOfIterations = 1
    val regParam = 0.1

    //Train Model
    val svmAlg = new SVMWithSGD()
    svmAlg.optimizer.setNumIterations(numOfIterations).setRegParam(regParam)
    val model = svmAlg.run(trainingData)

    //Input test data
    val testInput = sc.textFile(args(1)).map(row => row.split(","))

    //Converted the input file to libsvm format
    val testData = testInput.map(row => new LabeledPoint(row.last.toByte,
      Vectors.dense(row.take(row.length - 1).map(str => str.toDouble))))

    //This is the default threshold
    model.setThreshold(0)

    // Compute raw scores on the test set.
    val scoreAndLabels = testData.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }

    val accuracy = scoreAndLabels.filter(r => r._1 == r._2).count.toDouble / testData.count()
    println("SVM Accuracy = " + accuracy)

    // Instantiate metrics object
    val auroc = new BinaryClassificationMetrics(scoreAndLabels).areaUnderROC()
    println("SVM Area under ROC = " +auroc)

    sc.parallelize(Seq("SVM Accuracy = "+accuracy,"SVM Area under ROC = "+auroc)).coalesce(1).saveAsTextFile(args(2)+"/metrics")

    // Save and load model
    model.save(sc, args(2)+"/model")
  }
}
