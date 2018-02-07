package net.xrrocha.spellbound.kotlin

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object Settings {
  val config: Config = ConfigFactory.load().resolve()

  val ngramFilename: String by lazy { config.getString("files.ngramFilename") }
  val dictionaryFilename: String by lazy { config.getString("files.dictionaryFilename") }

  val ngramLength: Int by lazy { config.getInt("parameters.ngramLength") }
  val maxDistance: Double by lazy { config.getDouble("parameters.maxDistance") }

  val stringMetric: StringMetric by lazy {
    if (!config.hasPath("parameters.stringMetric")) {
      LevensteinStringMetric
    } else {
      val stringMetricName: String = config.getString("parameters.stringMetric")
      when (stringMetricName.toLowerCase()) {
        "levenstein" -> LevensteinStringMetric
        "jarowinkler" -> JaroWinklerStringMetric
        else -> Class.forName(stringMetricName) as StringMetric
      }
    }
  }
}