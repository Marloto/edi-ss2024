# Siddhi Example

Start services with `docker-compose up -d`

Open http://localhost:9390/editor

Create new file with:

```
@Source(type = 'http',
        receiver.url='http://0.0.0.0:8006/productionStream',
        basic.auth.enabled='false',
        @map(type='json'))
define stream SweetProductionStream (name string, amount double);

@sink(type='log')
define stream TotalCountStream (totalCount long);

-- Count the incoming events
@info(name='query1')
from SweetProductionStream
select count() as totalCount
insert into TotalCountStream;
```

Save and run.