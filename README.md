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

#### case 1: write 10000 records SET, json value

````
Run 1: avg 236ms.
Run 2: avg 232ms.
Run 3: avg 233ms.
````

#### case 2: write 10000 records SET (pipeline), json value

````
Run 1: avg 59ms.
Run 2: avg 51ms.
Run 3: avg 51ms.
````

#### case 3: write 10000 records as one key, json value

````
Run 1: avg 56ms.
Run 2: avg 48ms.
Run 3: avg 49ms.
````

#### case 4: write 10000 recods as HASH (pipeline)

````
Run 1: avg 64ms.
Run 2: avg 52ms.
Run 3: avg 56ms.
````

note: a resource monitor shows more extensive CPU usage


