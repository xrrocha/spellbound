package net.xrrocha.scala.spellbound

import scala.util.Try

package object norvig {

  type Rank = Int
  type Word = String

  def time[A](action: => A): (Try[A], Long) = {
    val startTime = System.currentTimeMillis()
    val result = Try(action)
    val endTime = System.currentTimeMillis()
    (result, endTime - startTime)
  }

  def runUntilCompletion(action: => Unit): Unit = {
    // Spark imposes Scala 2.11 which doesn't allow for Java-style lambdas :-(
    // val thread = new Thread(() => action)
    val thread = new Thread(new Runnable() {
      override def run(): Unit = {
        action
      }
    })
    // new Thread(() => thread.join())
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
      override def run(): Unit = {
        thread.join()
      }
    }))
    thread.start()
  }
}
