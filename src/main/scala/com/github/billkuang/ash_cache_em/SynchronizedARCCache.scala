package com.github.billkuang.ash_cache_em

import scala.collection.mutable
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache that balances between single hit caches and multiple hit caches.
 * @param maxSize Maximum size of the key value store.
 * @param f Function that will generate a value from a key if the item isn't in the store
 * @tparam K Type of key
 * @tparam V Type of value
 */
//class SynchronizedARCCache[K, V](override val maxSize: Int)(f: K => V)
//  extends IterableCache[K, V] {
//
//  @volatile private[this] var maxRecentStoreSize = maxSize / 2
//  @volatile private[this] var maxFrequencyStoreSize = maxSize - maxRecentStoreSize
//  private[this] def maxRecentGhostSize = maxFrequencyStoreSize
//  private[this] def maxFrequencyGhostSize = maxRecentGhostSize
//
//  private[this] val keyValueStoreLock = new Object()
//
//  override protected val keyValueStores = Seq(new ConcurrentHashMap[K, Option[V]](), new ConcurrentHashMap[K, Option[V]]())
//
//  private def recentStore = keyValueStores.head
//
//  private def frequencyStore = keyValueStores.last
//
//  val recentGhostList = new mutable.ArrayBuffer[K]()
//
//  val frequencyGhostList = new mutable.ArrayBuffer[K]()
//
//  override protected def expire(store: ConcurrentHashMap[K, Option[V]]): Option[K] = None
//
//  override def get(key: K): V = {
//    keyValueStoreLock.synchronized {
//      if (Option(recentStore.get(key)).getOrElse(None).isDefined) {
//        hits += 1
//        val value = recentStore.remove(key)
//        enqueueFrequencyStore(key, value.get)
//      } else if (Option(frequencyStore.get(key)).getOrElse(None).isDefined) {
//        hits += 1
//        val value = frequencyStore.remove(key)
//        enqueueFrequencyStore(key, value.get)
//      } else {
//        misses += 1
//        if (recentGhosts.contains(key)) {
//          maxRecentStoreSize += 1
//          maxFrequencyStoreSize -= 1
//          recentGhostList.remove(recentGhostList.indexOf(key))
//          enqueueRecentStore(key, STUB)
//          expireFrequencyStore()
//        } else if (frequencyGhostList.contains(key)) {
//          maxFrequencyStoreSize += 1
//          maxRecentStoreSize -= 1
//          frequencyGhostList.remove(frequencyGhostList.indexOf(key))
//          enqueueFrequencyStore(key, STUB)
//          expireRecentStore()
//        } else {
//          enqueueRecentStore(key, STUB)
//        }
//      }
//    }
//    if (recentStore.get(key)) {
//      hits += 1
//      val value = recentStore.remove(key).get
//      enqueueFrequencyStore(key, value)
//      value
//    } else if (frequencyStore.get(key)) {
//      hits += 1
//      val value = frequencyStore.remove(key).get
//      enqueueFrequencyStore(key, value)
//      value
//    } else {
//      misses += 1
//      val value = f(key)
//      if (recentGhostList.contains(key)) {
//        maxRecentStoreSize += 1
//        maxFrequencyStoreSize -= 1
//        recentGhostList.remove(recentGhostList.indexOf(key))
//        enqueueRecentStore(key, value)
//        expireFrequencyStore()
//      } else if (frequencyStore.contains(key)) {
//        maxFrequencyStoreSize += 1
//        maxRecentStoreSize -= 1
//        frequencyGhostList.remove(frequencyGhostList.indexOf(key))
//        enqueueFrequencyStore(key, f(key))
//        expireRecentStore()
//      } else {
//        enqueueRecentStore(key, f(key))
//      }
//      value
//    }
//  }
//
//  override def +=(pair: (K, V)) = synchronized {
//    val (key, value) = pair
//    if (recentStore.get(key)) {
//      recentStore.remove(key)
//      enqueueFrequencyStore(key, value)
//    } else if (frequencyStore.get(key)) {
//      frequencyStore.remove(key)
//      enqueueFrequencyStore(key, value)
//    } else {
//      if (recentGhostList.contains(key)) {
//        recentGhostList.remove(recentGhostList.indexOf(key))
//        enqueueRecentStore(key, value)
//      } else if (frequencyGhostList.contains(key)) {
//        frequencyGhostList.remove(frequencyGhostList.indexOf(key))
//        enqueueFrequencyStore(key, value)
//      } else {
//        enqueueRecentStore(key, value)
//      }
//    }
//  }
//
//  private[this] def enqueueFrequencyStore(key: K, value: V) {
//    if (frequencyStore.size < maxFrequencyStoreSize) {
//      frequencyStore += key -> value
//      expireFrequencyGhosts()
//    } else {
//      frequencyGhostList += frequencyStore.head._1
//      frequencyStore.remove(frequencyStore.head._1)
//      frequencyStore += key -> value
//      expireFrequencyGhosts()
//    }
//  }
//
//  private[this] def enqueueRecentStore(key: K, value: V) {
//    if (recentStore.size < maxRecentStoreSize) {
//      recentStore += key -> value
//      expireRecentGhosts()
//    } else {
//      recentGhostList += recentStore.head._1
//      recentStore.remove(recentStore.head._1)
//      recentStore += key -> value
//      expireRecentGhosts()
//    }
//  }
//
//  private[this] def expireFrequencyGhosts() =
//    while (frequencyGhostList.size > maxFrequencyGhostSize) frequencyGhostList.remove(0)
//  private[this] def expireRecentGhosts() =
//    while (recentGhostList.size > maxRecentGhostSize) recentGhostList.remove(0)
//  private[this] def expireFrequencyStore() = while (frequencyStore.size > maxFrequencyStoreSize) {
//    val (key, value) = frequencyStore.head
//    frequencyStore.remove(key)
//    frequencyGhostList += key
//    expireFrequencyGhosts()
//  }
//  private[this] def expireRecentStore() = while (recentStore.size > maxRecentStoreSize) {
//    val (key, value) = recentStore.head
//    recentStore.remove(key)
//    recentGhostList += key
//    expireRecentGhosts()
//  }
//
//  private[utils] def frequencyGhosts = frequencyGhostList
//  private[utils] def recentGhosts = recentGhostList
//}

object SynchronizedARCCache {
//  def getInstance[K, V](maxSize: Int)(f: K => V) =
//    new SynchronizedARCCache[K, V](maxSize)(f)
}
