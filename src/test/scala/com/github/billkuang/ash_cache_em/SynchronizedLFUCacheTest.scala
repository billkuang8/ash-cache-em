package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._
import com.github.billkuang.ash_cache_em.SlowQuery.numToString

class SynchronizedLFUCacheTest extends Matchers {

  val lfuCache = SynchronizedLFUCache.getInstance(3)(numToString)

  @Test
  def testReplacement {
    lfuCache.get(1)
    lfuCache.get(2)
    lfuCache.get(3)

    lfuCache.get(1)
    lfuCache.get(1)

    lfuCache.get(4)
    lfuCache.get(5)

    lfuCache.getMisses should be (5)
    lfuCache.getHits should be (2)

    val values = new scala.collection.mutable.ArrayBuffer[String]()
    lfuCache foreach { case (key, value) => values += value }

    values should contain ("1")
    values should contain ("4")
    values should contain ("5")
  }
}
