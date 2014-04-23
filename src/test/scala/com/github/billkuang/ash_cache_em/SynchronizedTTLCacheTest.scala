package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._
import com.github.billkuang.ash_cache_em.SlowQuery.numToString

class SynchronizedTTLCacheTest extends Matchers {

  val ttlCache = SynchronizedTTLCache.getInstance((1 second), 3)(numToString)

  @Test
  def testReplacement {

    ttlCache.get(5)
    Thread.sleep(1500L)

    ttlCache.get(4)
    ttlCache.get(3)
    ttlCache.get(2)

    val values = new scala.collection.mutable.ArrayBuffer[String]()

    ttlCache foreach { case (key, value) => values += value }

    values should contain ("4")
    values should contain ("3")
    values should contain ("2")
  }

  @Test
  def testReplacementIfNoneExpires {
    ttlCache.get(5)
    ttlCache.get(4)
    ttlCache.get(3)
    ttlCache.get(2)

    val values = new scala.collection.mutable.ArrayBuffer[String]()

    ttlCache foreach { case (key, value) => values += value }

    values should contain ("4")
    values should contain ("3")
    values should contain ("2")
  }
}
