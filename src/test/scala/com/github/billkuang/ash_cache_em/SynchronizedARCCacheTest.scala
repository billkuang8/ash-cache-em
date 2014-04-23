package com.github.billkuang.ash_cache_em

import org.scalatest.Matchers
import org.junit.Test
import scala.concurrent.duration._
import com.github.billkuang.ash_cache_em.SlowQuery.numToString

class SynchronizedARCCacheTest extends Matchers {

//  val arcCache = SynchronizedARCCache.getInstance(4)(numToString)

  @Test
  def testReplacement {
//    arcCache.get(1)
//    arcCache.get(2)
//    arcCache.get(3)   // recent ghost gets 1
//    arcCache.get(2)   // frequency gets 2
//    arcCache.get(3)   // frequency gets 3, recent store now empty
//
//    arcCache.getHits should be (2)
//    arcCache.getMisses should be (3)
//    arcCache.frequencyGhosts should be (empty)
//    arcCache.recentGhosts should contain (1)
//
//    arcCache.get(4)
//    arcCache.get(5)
//    arcCache.getKeyValueStore() should have size (4)
//
//    arcCache.get(1)   // recent store size + 1 = 3, recent ghost size - 1 = 1
//    arcCache.frequencyGhosts should contain (2)   // frequency store now only has size 1. 2 gets pushed to ghost list
//
//    arcCache.get(2)
//    arcCache.recentGhosts should contain (4)
//
//    arcCache.getKeyValueStore() should have size (4)
  }
}
