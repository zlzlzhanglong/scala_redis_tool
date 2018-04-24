# scala redis tool

You need to include the jedis.jar file in the classpath.

```
libraryDependencies += "redis.clients" % "jedis" % "2.8.2"
```

or

```
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.8.2</version>
</dependency>

```

## Example
```
val value = JedisPool(jedis => jedis.get("key"))
```

```
val list = List("1", "3")
JedisPool.pipeline[String, java.lang.Long]((pipe, item) => {
  pipe.lpush("list_key", item)
}, list)
```

```
val keys = List("a", "b")
val values = JedisPool.pipeline[String, String]((pipe, key) => {
  pipe.get(key)
}, list)
println(values("a"))
println(values("b"))
```