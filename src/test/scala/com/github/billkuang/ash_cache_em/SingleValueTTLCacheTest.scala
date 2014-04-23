package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._

class SingleValueTTLCacheTest extends Matchers {

  val cache = new SingleValueTTLCache[Long](1 second)(System.currentTimeMillis)

  @Test
  def testExpiration {
    val initial = cache.get
    val rightAfter = cache.get
    Thread.sleep(1500L)
    val twoSecondsLater = cache.get

    initial should be (rightAfter)
    initial should not be (twoSecondsLater)
  }
}
