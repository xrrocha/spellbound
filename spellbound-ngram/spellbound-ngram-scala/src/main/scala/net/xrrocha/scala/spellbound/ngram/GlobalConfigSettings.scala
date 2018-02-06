package net.xrrocha.scala.spellbound.ngram

import com.typesafe.config.{Config, ConfigFactory}

trait GlobalConfigSettings {
  def globalConfig: Config = ConfigFactory.load().resolve()
}

trait ConfigSettings extends GlobalConfigSettings {
  def configName: String

  final def config: Config = globalConfig.getConfig(configName)
}
