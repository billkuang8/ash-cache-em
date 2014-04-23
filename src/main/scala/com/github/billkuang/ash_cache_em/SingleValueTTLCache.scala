package com.github.billkuang.ash_cache_em

import scala.concurrent.duration._

/**
 * Cache that will be renewed after a given interval
 * @param staleDuration Interval at which caches will be renewed
 * @param f Function that will produce the cached item
 * @tparam T Type of cache
 */
class SingleValueTTLCache[T](staleDuration: Duration)(f: => T) {
  private[this] var item: Option[T] = None
  private[this] var expiration: Long = 0L

  def get(): T = synchronized {
    val now = System.currentTimeMillis()
    if (item.isEmpty || now > expiration) {
      item = Some(f)
      expiration = staleDuration.toMillis + now
    }
    item.get
  }
}
