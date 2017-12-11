package edu.neu.adevgun

import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest

/**
  * Data mining based on Random Forest
  */
object RandomForestTrain {
  def main(args: Array[String]) {

    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("RF Model Trainer"))

    //Input training data
    val input = sc.textFile(args(0)).map(row => row.split(","))

    //Converted the input file to libsvm format
    val trainingData = input.map(row => new LabeledPoint(row.last.toByte,
      Vectors.dense(row.take(row.length - 1).map(str => str.toDouble))))


    // Train a RandomForest model.
    // Empty categoricalFeaturesInfo indicates all features are continuous.
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 10 // Use more in practice.
    val featureSubsetStrategy = "auto" // Let the algorithm choose.
    val impurity = "gini"
    val maxDepth = 10
    val maxBins = 64

    //Train Model
    val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

    //Input test data
    val testInput = sc.textFile(args(1)).map(row => row.split(","))

    //Converted the input file to libsvm format
    val testData = testInput.map(row => new LabeledPoint(row.last.toByte,
      Vectors.dense(row.take(row.length - 1).map(str => str.toDouble))))

    // Evaluate model on test instances and compute test error
    val scoreAndLabels = testData.map { point =>
      val prediction = model.predict(point.features)
      (prediction, point.label)
    }
    val accuracy = scoreAndLabels.filter(r => r._1 == r._2).count.toDouble / testData.count()
    println("RF Accuracy = " + accuracy)

    // Instantiate metrics object
    val auroc = new BinaryClassificationMetrics(scoreAndLabels).areaUnderROC()
    println("RF Area under ROC = " +auroc)

    sc.parallelize(Seq("RF Accuracy = "+accuracy,"RF Area under ROC = "+auroc)).coalesce(1).saveAsTextFile(args(2)+"/metrics")

    // Save and load model
    model.save(sc, args(2)+"/model")
    //val sameModel = RandomForestModel.load(sc, "target/tmp/myRandomForestClassificationModel")
  }
}