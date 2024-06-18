```
docker compose exec kafka kafka-console-producer.sh --topic orders_topic --bootstrap-server localhost:9092
```

```
> {"order_id": "123","customer_id": "456","items": [{"product_id": "A1", "quantity": 2, "price": 10.0},{"product_id": "B2", "quantity": 1, "price": 20.0}]}
> {"order_id": "456","customer_id": "789","items": [{"product_id": "B1", "quantity": 2, "price": 5.0},{"product_id": "C2", "quantity": 1, "price": 40.0}]}
```

```
docker compose exec ksqldb ksql http://localhost:8088
```

```
RUN SCRIPT '/etc/sql/orders.sql'
```

```
SET 'auto.offset.reset' = 'earliest';
```

```
CREATE STREAM exploded_orders AS
SELECT 
  order_id,
  customer_id,
  EXPLODE(items) AS item
FROM orders;
```

```
SELECT * FROM exploded_orders EMIT CHANGES;
```

```
CREATE STREAM processed_orders AS
SELECT
  order_id,
  customer_id,
  item->product_id AS product_id,
  item->quantity AS quantity,
  item->price AS price,
  item->quantity * item->price AS total_price
FROM exploded_orders;
```