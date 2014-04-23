package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._
import com.github.billkuang.ash_cache_em.SlowQuery.numToString

class SynchronizedLRUCacheTest extends Matchers {

  val lruCache = SynchronizedLRUCache.getInstance(3)(numToString)

  @Test
  def testReplacement {
    lruCache.get(1)
    lruCache.get(2)
    lruCache.get(3)

    lruCache.get(2)

    lruCache.get(4)
    lruCache.get(5)

    val values = new scala.collection.mutable.ArrayBuffer[String]()

    lruCache foreach { case (key, value) => values += value }

    values should contain ("2")
    values should contain ("4")
    values should contain ("5")
  }
}
