### Overview

A sandbox playgroud with Jedis.  Testing different aproach about how to write/read data to Redis.


### HOW-TO

````
mvn clean compile assembly:single
java -jar ./target/redis-playground-1.0-SNAPSHOT-jar-with-dependencies.jar

````

### Test results

test machine : ubuntu 16.04, Intel Core i7-4510U, 6Gb
Redis: v. 3.2.8, default configuraion from sources

#### case 1: write 10000 recods SET

