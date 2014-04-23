package com.github.billkuang.ash_cache_em

import scala.concurrent.duration._

/**
 * Basic class that will do a stale check on the cache after a given interval
 * @param duration Interval between stale checks
 * @param isStale Function that will tell us whether the current cache is stale or not
 * @param f Function that will produce the cache item if cache is stale or empty
 * @tparam T Type of cache
 */
class SingleValueTimedStaleCheckCache[T](duration: Duration, isStale: => Boolean)(f: => T) {
  private[this] var item: Option[T] = None
  private[this] var expiration = 0L

  def get: T = synchronized {
    val now = System.currentTimeMillis()
    if (item.isEmpty) {
      expiration = now + duration.toMillis
      item = Some(f)
    } else {
      if (now > expiration) {
        expiration = now + duration.toMillis
        if (isStale) item = Some(f)
      }
    }
    item.get
  }
}
