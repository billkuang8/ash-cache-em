package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._
import com.github.billkuang.ash_cache_em.SlowQuery.numToString

class SynchronizedMRUCacheTest extends Matchers {

  val mruCache = SynchronizedMRUCache.getInstance(3)(numToString)

  @Test
  def testReplacement {
    mruCache.get(1)
    mruCache.get(2)
    mruCache.get(3)

    mruCache.get(2)

    mruCache.get(4)

    val values = new scala.collection.mutable.ArrayBuffer[String]()

    mruCache foreach { case (key, value) => values += value }

    values should contain ("4")
    values should contain ("1")
    values should contain ("3")
  }
}
