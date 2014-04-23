package com.github.billkuang.ash_cache_em

import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable.LinkedHashMap
import scala.concurrent.duration._

/**
 * Cache that will replace all caches after a given interval (expiration timestamp)
 * @param duration Expiration time
 * @param maxSize Maximum size of the key value store.
 * @param f Function that will generate a value from a key if the item isn't in the store
 * @tparam K Type of key
 * @tparam V Type of value
 */
class SynchronizedTTLCache[K, V](override val maxSize: Int, duration: Duration)(f: K => V)
  extends IterableCache[K, V] {

  override protected val keyValueStores = Seq(new ConcurrentHashMap[K, Option[V]]())

  private def keyValueStore = keyValueStores.head
  private val timestamps = new LinkedHashMap[K, Long]()

  override protected def expire(store: ConcurrentHashMap[K, Option[V]]): Option[K] = {
    val key = timestamps.head._1
    timestamps.remove(key)
    store.remove(key)
    Some(key)
  }

  override def get(key: K): V = {
    keyValueStore.synchronized {
      while (timestamps.headOption.isDefined && timestamps.head._2 < System.currentTimeMillis() - duration.toMillis) {
        val expiredKey = expire(keyValueStore)
        expiredKey foreach { locks.remove _ }
      }
      val value = Option(keyValueStore.putIfAbsent(key, STUB)) getOrElse None
      if (value.isDefined) {
        hits += 1
        return value.get
      } else {
        misses += 1
        timestamps += key -> System.currentTimeMillis()
        if (isMaxedOut()) {
          val expiredKey = expire(keyValueStore)
          expiredKey foreach { locks.remove _ }
        }
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
    timestamps += pair._1 -> System.currentTimeMillis()
  }
}

object SynchronizedTTLCache {
  def getInstance[K, V](duration: Duration, maxSize: Int)(f: K => V) =
    new SynchronizedTTLCache[K, V](maxSize, duration)(f)
}