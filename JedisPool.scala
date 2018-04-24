package com.yhcs.crawler.tools

import java.util.Properties

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig, Pipeline, Response}

import scala.io.Source

/**
  * Created by zhanglong on 2017/10/9.
  */
object JedisPool {

  val props: Properties = new Properties()
  props.load(Source.fromResource("redis.properties").bufferedReader())
  //创建jedis池配置实例
  val config: JedisPoolConfig = new JedisPoolConfig()
  //设置池配置项值
  config.setMaxTotal(props.getProperty("jedis.pool.maxTotal").toInt)
  config.setMaxIdle(props.getProperty("jedis.pool.maxIdle").toInt)
  config.setMaxWaitMillis(props.getProperty("jedis.pool.maxWaitMillis").toLong)
  config.setTestOnBorrow(props.getProperty("jedis.pool.testOnBorrow").toBoolean)
  config.setTestOnReturn(props.getProperty("jedis.pool.testOnReturn").toBoolean)
  //根据配置实例化jedis池
  val pool = new JedisPool(config, props.getProperty("redis.ip"), props.getProperty("redis.port").toInt)

  def getJedis: Jedis = {
    pool.getResource
  }

  def close(jedis: Jedis): Unit = {
    jedis.close()
  }

  def apply[R](fn: (Jedis) => R, jedis: Jedis = getJedis): R = {
    val r = fn(jedis)
    close(jedis)
    r
  }

  def pipeline[A, B](fn: (Pipeline, A) => Response[B], data: Iterable[A]): Map[A, B] = {
    if (data.isEmpty) {
      return Map[A, B]()
    }
    val jedis = getJedis
    val p = jedis.pipelined()
    var result = Map[A, Response[B]]()
    data.foreach(x => result += (x -> fn(p, x)))
    p.sync()
    close(jedis)
    result.map(x => (x._1, x._2.get()))
  }

}

