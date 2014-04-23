package com.github.billkuang.ash_cache_em

import scala.collection.mutable
import scala.collection.JavaConverters._
import java.util.concurrent.ConcurrentHashMap
import java.io.{ObjectOutputStream, ByteArrayOutputStream}

/**
 * Basic trait that all Synchronized caches in this package will extend.
 * Will contain information such as hit/miss ratios. Offers foreach method.
 * @tparam K Type of key
 * @tparam V Type of value
 */
trait IterableCache[K, V] {

  @volatile protected var hits = 0
  @volatile protected var misses = 0

  protected val STUB: Option[V] = None

  protected val locks = new mutable.HashMap[K, Object]()

  protected val maxSize: Int

  protected val keyValueStores: Seq[ConcurrentHashMap[K, Option[V]]]

  protected def expire(store: ConcurrentHashMap[K, Option[V]]): Option[K]

  def get(key: K): V

  def +=(pair: (K, V)): Unit

  def getHits: Int = hits

  def getMisses: Int = misses

  def getHitRatio: Double = try { hits.toDouble / (hits + misses).toDouble } catch { case e: NumberFormatException => 0.0 }

  def getMissRatio: Double = try { misses.toDouble / (hits + misses).toDouble } catch { case e: NumberFormatException => 0.0 }

  def foreach[U](op: ((K, V)) => U) = getKeyValueStore() filter { _._2.isDefined } foreach { case (key, Some(value)) => op(key, value) }

  def isMaxedOut(): Boolean = getKeyValueStore().size > maxSize

  def getKeyValueStore() = keyValueStores map { _.asScala.toMap } reduce { _ ++ _ }
}
