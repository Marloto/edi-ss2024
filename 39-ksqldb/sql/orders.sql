CREATE STREAM orders (
  order_id VARCHAR,
  customer_id VARCHAR,
  items ARRAY<STRUCT<product_id VARCHAR, quantity INT, price DOUBLE>>
) WITH (
  KAFKA_TOPIC='orders_topic',
  VALUE_FORMAT='JSON',
  PARTITIONS=4
);
