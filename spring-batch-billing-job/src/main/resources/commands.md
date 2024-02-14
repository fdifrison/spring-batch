# Command to execute

* clean tables

```shell
docker exec -it -u postgres psqlContainer bash -c "cd /mnt/sql && psql -f schema-drop-postgresql.sql -U postgres"
```

* create tables

```shell
docker exec -it -u postgres psqlContainer bash -c "cd /mnt/sql && psql -f schema-postgresql.sql -U postgres"
```

* execute a query inside the docker container to retrieve the job that have been run

```shell
docker exec psqlContainer psql -U postgres -c 'select * from BATCH_JOB_EXECUTION;'
```

```shell
docker exec psqlContainer psql -U postgres -c 'select * from BATCH_JOB_INSTANCE;'
```

```shell
docker exec psqlContainer psql -U postgres -c 'select * from BATCH_JOB_EXECUTION_PARAMS;'
```

```shell
docker exec psqlContainer psql -U postgres -c 'select * from BATCH_STEP_EXECUTION;'
```

```shell
docker exec psqlContainer psql -U postgres -c 'select count(*) from BILLING_DATA;'
```

* launch the jar with an input file

```shell
cd ../../..
~/.jdks/corretto-17.0.9/bin/java -jar target/billing-job-0.0.1-SNAPSHOT.jar input.file=src/main/resources/inputfiles/billing-2023-01.csv
``` 

* launch the jar with all the jobParameters dynamically

```shell
cd ../../..
~/.jdks/corretto-17.0.9/bin/java -jar target/billing-job-0.0.1-SNAPSHOT.jar input.file=src/main/resources/inputfiles/billing-2023-01.csv output.file=src/staging/billing-report-2023-01.csv data.year=2023 data.month=1
``` 

* launch the jar with skipping data and listener

```shell
cd ../../..
~/.jdks/corretto-17.0.9/bin/java -jar target/billing-job-0.0.1-SNAPSHOT.jar input.file=src/main/resources/inputfiles/billing-2023-03.csv output.file=src/staging/billing-report-2023-03.csv skip-file=src/staging/billing-data-skip-2023-3.csv data.year=2023 data.month=3
```

* launch the jar with simulated faulty service generating a PricingException

```shell
cd ../../..
~/.jdks/corretto-17.0.9/bin/java -jar target/billing-job-0.0.1-SNAPSHOT.jar input.file=src/main/resources/inputfiles/billing-2023-04.csv output.file=src/staging/billing-report-2023-04.csv skip.file=src/staging/billing-data-skip-2023-04.csv data.year=2023 data.month=4
``` 

