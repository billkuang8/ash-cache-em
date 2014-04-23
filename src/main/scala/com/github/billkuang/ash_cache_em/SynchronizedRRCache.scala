package com.github.billkuang.ash_cache_em

import java.util.concurrent.ConcurrentHashMap
import scala.util.Random
import scala.collection.JavaConverters._

/**
 * Cache that will randomly replace a cached item.
 * @param maxSize Maximum size of the key value store.
 * @param f Function that will generate a value from a key if the item isn't in the store
 * @tparam K Type of key
 * @tparam V Type of value
 */
class SynchronizedRRCache[K, V](override val maxSize: Int)(f: K => V)
  extends IterableCache[K, V] {

  override protected val keyValueStores = Seq(new ConcurrentHashMap[K, Option[V]]())

  private def keyValueStore = keyValueStores.head

  override protected def expire(store: ConcurrentHashMap[K, Option[V]]): Option[K] = {
    if (!isMaxedOut()) { None }
    else {
      val keys = store.asScala.filter(_._2.isDefined).keySet.to[Vector]
      val key = keys(new Random().nextInt(keys.size))
      store.remove(key)
      Some(key)
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

  def +=(pair: (K, V)) = keyValueStore.synchronized {
    if (isMaxedOut()) expire(keyValueStore)
    keyValueStore.put(pair._1, Some(pair._2))
  }
}

object SynchronizedRRCache {
  def getInstance[K, V](maxSize: Int)(f: K => V) =
    new SynchronizedRRCache[K, V](maxSize)(f)
}