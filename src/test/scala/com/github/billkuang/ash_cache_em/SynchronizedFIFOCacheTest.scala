package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._
import com.github.billkuang.ash_cache_em.SlowQuery.numToString

class SynchronizedFIFOCacheTest extends Serializable with Matchers {

  val fifoCache = SynchronizedFIFOCache.getInstance(maxSize = 3)(numToString)

  @Test
  def testReplacement {
    fifoCache.get(1)
    fifoCache.get(2)
    fifoCache.get(3)
    fifoCache.getMisses should be (3)

    fifoCache.get(1)
    fifoCache.get(2)
    fifoCache.get(3)
    fifoCache.getHits should be (3)
    fifoCache.getHitRatio should be (0.5)

    val values = new scala.collection.mutable.ArrayBuffer[String]()
    fifoCache.get(4)
    fifoCache.get(5)

    fifoCache foreach { case (key, value) => values += value }

    values should contain ("3")
    values should contain ("4")
    values should contain ("5")
  }
}