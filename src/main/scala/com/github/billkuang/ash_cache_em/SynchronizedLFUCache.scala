package com.github.billkuang.ash_cache_em

import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable.HashMap

/**
 * Cache that will replace the least frequently used cached item.
 * @param maxSize Maximum size of the key value store.
 * @param f Function that will generate a value from a key if the item isn't in the store
 * @tparam K Type f key
 * @tparam V Type of value
 */
class SynchronizedLFUCache[K, V](override val maxSize: Int)(f: K => V)
  extends IterableCache[K, V] {

  override protected val keyValueStores = Seq(new ConcurrentHashMap[K, Option[V]]())

  private def keyValueStore = keyValueStores.head
  private val usageCounter = new HashMap[K, Long]()

  override protected def expire(store: ConcurrentHashMap[K, Option[V]]): Option[K] = {
    if (!isMaxedOut()) { None }
    else {
      val (lfuKey, minCounter) = usageCounter minBy { case (key, value) => value }
      store.remove(lfuKey)
      Some(lfuKey)
    }
  }

  override def get(key: K): V = {
    keyValueStore.synchronized {
      val value = Option(keyValueStore.putIfAbsent(key, STUB)) getOrElse None
      if (value.isDefined) {
        hits += 1
        return value.get
      } else {
        misses += 1
        if (isMaxedOut()) {
          val expiredKey = expire(keyValueStore)
          expiredKey foreach { locks.remove _ }
        }
        usageCounter += key -> 1L
        locks += key -> new Object()
      }
    }

    val lock = locks(key)

    lock.synchronized {
      val value = keyValueStore.get(key)
      if (value.isEmpty) {
        val newValue = f(key)
        keyValueStore.put(key, Some(newValue))
        newValue
      } else {
        value.get
      }
    }
  }

  override def +=(pair: (K, V)) = keyValueStore.synchronized {
    if (isMaxedOut()) expire(keyValueStore)
    keyValueStore.put(pair._1, Some(pair._2))
    usageCounter += pair._1 -> 0L
  }
}

object SynchronizedLFUCache {
  def getInstance[K, V](maxSize: Int)(f: K => V) =
    new SynchronizedLFUCache[K, V](maxSize)(f)
}