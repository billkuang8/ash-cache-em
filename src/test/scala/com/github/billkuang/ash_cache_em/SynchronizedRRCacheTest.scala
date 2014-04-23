package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._
import com.github.billkuang.ash_cache_em.SlowQuery.numToString

class SynchronizedRRCacheTest extends Matchers {

  val rrCache = SynchronizedRRCache.getInstance(3)(numToString)

  @Test
  def testReplacement {
    rrCache.get(1)
    rrCache.get(2)
    rrCache.get(3)
    rrCache.get(2)
    rrCache.get(4)

    val values = new scala.collection.mutable.ArrayBuffer[String]()

    rrCache foreach { case (key, value) => values += value }

    values should contain ("4")
  }
}
