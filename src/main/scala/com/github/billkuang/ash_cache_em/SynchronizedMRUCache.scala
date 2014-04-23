package com.github.billkuang.ash_cache_em

import scala.collection.mutable.ArrayBuffer
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

/**
 * Cache that replaces the most recently used cached item.
 * @param maxSize Maximum size of the key value store.
 * @param f Function that will generate a value from a key if the item isn't in the store
 * @tparam K Type of key
 * @tparam V Type of value
 */
class SynchronizedMRUCache[K, V](override val maxSize: Int)(f: K => V)
  extends IterableCache[K, V] {

  override val keyValueStores = Seq(new ConcurrentHashMap[K, Option[V]]())

  private def keyValueStore = keyValueStores.head
  private val expirationOrder = new ArrayBuffer[K]()

  override protected def expire(store: ConcurrentHashMap[K, Option[V]]): Option[K] = {
    if (!isMaxedOut()) { None }
    else {
      val key = expirationOrder.remove(expirationOrder.size - 1)
      store.remove(key)
      Some(key)
    }
  }

  override def get(key: K): V = {
    keyValueStore.synchronized {
      val value = Option(keyValueStore.putIfAbsent(key, STUB)) getOrElse None
      if (value.isDefined) {
        hits += 1
        expirationOrder.remove(expirationOrder.indexOf(key))
        expirationOrder += key
        return value.get
      } else {
        misses += 1
        if (isMaxedOut()) {
          val expiredKey = expire(keyValueStore)
          expiredKey foreach { locks.remove _ }
        }
        expirationOrder += key
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
    if (!expirationOrder.contains(pair._1)) {
      expirationOrder += pair._1
    } else {
      expirationOrder.remove(expirationOrder.indexOf(pair._1))
      expirationOrder += pair._1
    }
  }
}

object SynchronizedMRUCache {
  def getInstance[K, V](maxSize: Int)(f: K => V) =
    new SynchronizedMRUCache[K, V](maxSize)(f)
}