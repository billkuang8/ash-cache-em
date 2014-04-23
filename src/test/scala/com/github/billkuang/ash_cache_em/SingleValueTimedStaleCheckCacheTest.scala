package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._

class SingleValueTimedStaleCheckCacheTest extends Matchers {

  val alwaysStaleCache = new SingleValueTimedStaleCheckCache[Long]((1 second), alwaysStale)(System.currentTimeMillis)
  val neverStaleCache = new SingleValueTimedStaleCheckCache[Long]((1 second), neverStale)(System.currentTimeMillis)

  @Test
  def testAlwaysStale {
    val initial = alwaysStaleCache.get
    val rightAfter = alwaysStaleCache.get
    Thread.sleep(1500L)
    val twoSecondsLater = alwaysStaleCache.get

    initial should be (rightAfter)
    initial should not be (twoSecondsLater)
  }

  @Test
  def testNeverStale {
    val initial = neverStaleCache.get
    val rightAfter = neverStaleCache.get
    Thread.sleep(1500L)
    val twoSecondsLater = neverStaleCache.get

    initial should be (rightAfter)
    initial should be (twoSecondsLater)
  }

  def alwaysStale(): Boolean = true
  def neverStale(): Boolean = false
}
