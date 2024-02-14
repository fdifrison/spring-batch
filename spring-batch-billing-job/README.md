# Spring Batch

# Spring Batch Overview

In this lesson, we describe what batch processing is, and the challenges that come with it. We also introduce the Spring Batch framework, explain its domain model, and its internal architecture.

## Introduction to Batch Processing

Batch processing is a method of processing large volumes of data simultaneously, instead of processing them individually, in real time (in that case, we could talk about stream processing). This approach is widely used in many industries, including finance, manufacturing, and telecommunications. Batch processing is often used for tasks that require the processing of large amounts of data, such as payroll processing or billing, as well as tasks that require time-consuming calculations or analysis. Batch applications are ephemeral, which means that once they've completed, they end.

This type of processing comes with a number of challenges, including, but not limited to:

Handling large amounts of data efficientlyTolerance to human errors and hardware deficienciesScalability

When it's time to provide a batch-based application for processing large amounts of data in a structured way, Spring Batch provides a robust and efficient solution. So, what is Spring Batch exactly? How does it help address batch processing challenges? Let's find out!

## Spring Batch Framework

Spring Batch is a lightweight, comprehensive framework, designed to enable the development of robust batch applications that are vital for the daily operations of enterprise systems.

It provides all the necessary features that are essential for processing large volumes of data, including transaction management, job processing status, statistics, and fault-tolerance features. It also provides advanced scalability features that enable high-performance batch jobs through multi-threaded processing and data-partitioning techniques. You can use Spring Batch in both simple use cases (such as loading a file into a database), and complex, high-volume use cases (like moving data between databases, transforming it, and so on).

Spring Batch integrates seamlessly with other Spring technologies, making it an excellent choice for writing batch applications with Spring.

## Batch Domain Language

The key concepts of the Spring Batch domain model are represented in the following diagram:

![https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/overview-lesson-domain-model.svg](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/overview-lesson-domain-model.svg)

A `Job` is an entity that encapsulates an entire batch process, that runs from start to finish without interruption. A `Job` has one or more steps. A `Step` is a unit of work that can be a simple task (such as copying a file or creating an archive), or an item-oriented task (such as exporting records from a relational database table to a file), in which case, it would have an `ItemReader`, an `ItemProcessor` (which is optional), and an `ItemWriter`.

A `Job` needs to be launched with a `JobLauncher`, and can be launched with a set of `JobParameter`s. Execution metadata about the currently running `Job` is stored in a `JobRepository`.

We will cover each of these key concepts in detail throughout the course.

## Batch Domain Model

Spring Batch uses a robust and well-designed model for the batch processing domain. It provides a rich set of Java APIs with interfaces and classes that represent all of the key concepts of batch processing like `Job`, `Step`, `JobLauncher`, `JobRepository`, and more. We will use these APIs in this course.

While the batch domain model can be implemented with any persistence technology (like a relational database, a non-relational database, a graph database, etc), Spring Batch provides a relational model of the batch domain concepts with metadata tables that closely match the classes and interfaces in the Java API.

The following entity-relationship diagram presents the main metadata tables:

![https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/overview-lesson-relational-model.svg](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/overview-lesson-relational-model.svg)

`Job_Instance`: This table contains all information relevant to a job definition, such as the job name and its identification key.`Job_Execution`: This table holds all information relevant to the execution of a job, like the start time, end time, and status. Every time a job is run, a new row is inserted in this table.`Job_Execution_Context`: This table holds the execution context of a job. An execution context is a set of key/value pairs of runtime information that typically represents the state that must be retrieved after a failure.`Step_Execution`: This table holds all information relevant to the execution of a step, such as the start time, end time, item read count, and item write count. Every time a step is run, a new row is inserted in this table.`Step_Execution_Context`: This table holds the execution context of a step. This is similar to the table that holds the execution context of a job, but instead it stores the execution context of a step.`Job_Execution_Params`: This table contains the runtime parameters of a job execution.

## Spring Batch Architecture

Spring Batch is designed in a modular, extensible way. The following diagram shows the layered architecture that supports the ease of use of the framework for end users:

![https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/overview-lesson-architecture.svg](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/overview-lesson-architecture.svg)

This layered architecture highlights three major high-level components:

The `Application` layer: contains the batch job and custom code written by the developers of the batch application.The `Batch Core` layer: contains the core runtime classes provided by Spring Batch that are necessary to create and control batch jobs. It includes implementations for `Job` and `Step`, as well as common services like `JobLauncher` and `JobRepository`.The `Batch Infrastructure` layer: contains common item readers and writers provided by Spring Batch, plus base services such as the repeat and retry mechanisms, which are used both by application developers and the core framework itself.

As a Spring Batch developer, you typically use APIs provided by Spring Batch in the `Batch Infrastructure` and `Batch Core` modules to define your jobs and steps in the `Application` layer. Spring Batch provides a rich library of batch components that you can use out of the box (such as item readers, item writers, data partitioners, and more).

# Understanding jobs and how to run them

In the previous lesson, you learned that a `Job` is an entity that encapsulates an entire batch process that runs from start to finish without interaction or interruption. In this lesson, you'll learn how `Job`s are represented internally in Spring Batch, understand how they're launched, and understand how their execution metadata is persisted.

# What Is a Job?

A `Job` is an entity that encapsulates an entire batch process that runs from start to finish. It consists of a set of steps that run in a specific order. We'll cover steps in a future lesson. Here, we focus on what a `Job` is and how it is represented in Spring Batch.

A batch job in Spring Batch is represented by the `Job` interface provided by the `spring-batch-core` dependency:

COPY

`public interface Job {
    String getName();
    void execute(JobExecution execution);
}`

At a fundamental level, the `Job` interface requires that implementations specify the `Job` name (the `getName()` method) and what the `Job` is supposed to do (the `execute` method).

The `execute` method gives a reference to a `JobExecution` object. The`JobExecution` represents the actual execution of the `Job` at runtime. It contains a number of runtime details, such as the start time, the end time, the execution status, and so on. This runtime information is stored by Spring Batch in a metadata repository, which we'll cover in the next section.

Note how the `execute` method isn't expected to throw any exception. Runtime exceptions should be handled by implementations, and added in the `JobExecution` object. Clients should inspect the `JobExecution` status to determine success or failure.

# Understanding Job Metadata

One of the key concepts in Spring Batch is the `JobRepository`. The `JobRepository` is where all metadata about jobs and steps is stored. A `JobRepository` could be a persistent store, or an in-memory store. A persistent store has the advantage of providing metadata even after a `Job` is finished, which could be used for post analysis or to restart a `Job` in the case of a failure. We'll cover `Job` restartability in a later lesson.

Spring Batch provides a JDBC implementation of the `JobRepository`, which stores batch metadata in a relational database. In a production-grade system, you need to create a few tables that Spring Batch uses to store its execution metadata. We've covered metadata tables in the previous Lab.

The `JobRepository` is what creates a `JobExecution` object when a `Job` is first launched. But, how are `Job`s launched? Let's look at that in the next section.

# Launching Jobs

Launching jobs in Spring Batch is done through the `JobLauncher` concept, which is represented by the following interface:

COPY

`public interface JobLauncher {

   JobExecution run(Job job, JobParameters jobParameters)
          throws
             JobExecutionAlreadyRunningException,
             JobRestartException,
             JobInstanceAlreadyCompleteException,
             JobParametersInvalidException;
}`

The `run` method is designed to launch a given `Job` with a set of `JobParameters`. We'll cover job parameters in detail in a later lesson. For now, you can think of them as a collection of key/value pairs that are passed to the `Job` at runtime. There are two important aspects to understand here:

It is expected that implementations of the `JobLauncher` interface obtain a valid `JobExecution` from the `JobRepository` and execute the `Job`.The run method throws different types of exceptions. We'll cover all of these exceptions in detail during the course.

You'll almost never have to implement the `JobLauncher` interface yourself, because Spring Batch provides an implementation that's ready to use. The following diagram shows how the `JobLauncher`, the `JobRepository` and the `Job` interact with each other.

![https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/job-launcher-repository.svg](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/job-launcher-repository.svg)

Batch jobs are typically launched in one of two ways:

From the command line interfaceFrom within a web container

In this course, we'll only cover launching jobs from the command line. Please refer to the further resources links for more details about how to launch jobs from within a web container.

# Understanding Job Instances

In the previous lesson, you learned about `Job`s and `JobExecution`s. In this lesson, we'll explore another key concept from the Batch domain model, which is `JobInstance`. We'll explain what `JobInstance`s are, and how they relate to `Job`s and `JobExecution`s.

## What Are Job Instances?

A `Job` might be defined once, but it'll likely run many times, commonly on a set schedule. In Spring Batch, a `Job` is the generic definition of a batch process specified by a developer. This generic definition must be parametrized to create actual instances of a `Job`, which are called `JobInstance`s.

A `JobInstance` is a unique parametrization of a `Job` definition. For example, imagine a batch process that needs to be executed once at the end of each day, or when a certain file is present. In the once-per-day scenario, we can use Spring Batch to create an `EndOfDay` `Job` for that. There would be a single `EndOfDay` `Job` definition, but multiple instances of that same `Job`, one per day. Each instance would process the data of a particular day, and might have a different outcome (success or failure) from other instances. Therefore, each individual instance of the `Job` must be tracked separately.

A `JobInstance` is distinct from other `JobInstance`s by a specific parameter, or a set of parameters. For example, a parameter named `schedule.date` would specify a specific day. Such a parameter is called a `JobParameter`. `JobParameter`s are what distinguish one `JobInstance` from another. The following diagram shows how `JobParameter`s define `JobInstance`s:

![https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/job-parameters.svg](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/job-parameters.svg)

## What Do Job Instances and Job Parameters Represent?

`JobInstance`s are distinct from each other by distinct `JobParameter`s. Those parameters usually represent the data intended to be processed by a given `JobInstance`. For example, in the case of the `EndOfDay` `Job`, the `schedule.date` `JobParameter` for January 1st defines the `JobInstance` that will process the data of January 1st. The `schedule.date` `JobParameter` for January 2nd defines the `JobInstance` that will process the data of January 2nd, and so forth.

While it is not required for `Job` parameters to represent the data to be processed, this is a good hint - and a good practice - to correctly design `JobInstance`s. Designing `JobInstance`s to represent the data to be processed is easier to configure, to test, and to think about, in case of failure.

The definition of a `JobInstance` itself has absolutely no bearing on the data to be loaded. It is entirely up to the `Job` implementation to determine how data is loaded, based on `JobParameter`s. Here are a few examples of `JobParameter`s, and how they represent the data to be processed by the corresponding `JobInstance`:

A specific date: In this case, we would have a `JobInstance` per date.A specific file: In this case, we would have a `JobInstance` per file.A specific range of records in a relational database table: In this case, we would have a `JobInstance` per range.And more.

For our course, the `BillingJob` for Spring Cellular consumes a flat file as input, which is a good candidate to be passed as a `JobParameter` to our `Job`. This is what we'll see in the upcoming Lab of this lesson.

## How Do Job Instances Relate to Job Executions?

A `JobExecution` refers to the technical concept of a single *attempt* to run a `JobInstance`. As seen in the previous lesson, a `JobExecution` may end in a success or failure. In the case of the `EndOfDay` `Job`, if the January 1st run fails the first time and is run again the next day, it is still the January 1st run. Therefore, each `JobInstance` can have multiple `JobExecution`s.

The relation between the concepts of `Job`, `JobInstance`, `JobParameters`, and `JobExecution` is summarized in the following diagram:

![https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/job-class-relations.svg](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/job-class-relations.svg)

Here's a concrete example of the lifecycle of a `JobInstance` in the case of the `EndOfDay` `Job`:

![https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/lifecycle-example.svg](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-batch-essentials/lifecycle-example.svg)

In this example, the first execution attempt of `Job Instance 1` fails, so another execution is run and succeeds. This leads to two `JobExecution`s for the same `JobInstance`. For `Job Instance 2` however, the first execution attempt succeeds, therefore there is no need to launch a second execution.

In Spring Batch, a `JobInstance` is not considered to be complete unless a `JobExecution` completes successfully. A `JobInstance` that is complete can't be restarted again. This is a design choice to prevent accidental re-processing of the same data for batch `Job`s that are not idempotent.

## The Different Types of Job Parameters

`JobParameter`s are typically used to distinguish one `JobInstance` from another. In other words, they are used to *identify* a specific `JobInstance`.

Not all parameters can be used to identify `Job` instances. For example, if the `EndOfDay` `Job` takes another parameter - say, `file.format`) - that represents the format of the output file (CSV, XML, and others), this parameter does not really represent the data to process, so, it could be excluded from the process of identifying the `Job` instances.

This is where non-identifying `JobParameter`s come into play. In Spring Batch, `JobParameter`s can be either identifying or non-identifying. An identifying `JobParameter` contributes to the identification of `JobInstance`, while a non-identifying one doesn't. By default, `JobParameter`s are identifying, and Spring Batch provides APIs to specify whether a `JobParameter` is identifying or not.

In the example of the `EndOfDay` `Job`, the parameters can be defined in the following table:

| Job parameter | Identifying? | Example |
| --- | --- | --- |
| schedule.date | Yes | 2023-01-01 |
| file.format | No | csv |

Now the question is: Why is this important, and how is it used in Spring Batch? Identifying `JobParameter`s play a crucial role in the case of failure. In a production environment, where hundreds of `Job` instances are running, and one of them fails, we need a way to identify which instance has failed. This is where identifying `Job` parameters are key. When a `JobExecution` for a given `JobInstance` fails, launching the same job with the *same* set of identifying `JobParameter`s will create a new `JobExecution` (ie a new attempt) for the *same* `JobInstance`.

# Testing Your Job

We've already seen how to test Spring Batch jobs using JUnit 5 and Spring Boot test utilities in previous Labs. In this lesson, we'll focus on the test utilities provided by Spring Batch in the `spring-batch-test` module, which is designed to simplify testing batch artifacts.

## The Different Types of Tests for Batch Jobs

When it comes to testing batch jobs, there are several levels of testing:

Testing the job from end to end: In this scenario, a test should provide the input data, execute the job, and verify the end result. We can qualify this kind of testing as "black-box" testing, where we consider the job as a black box that we test based on inputs and outputs. End to end testing is what we have been doing thus far in this course.Testing each step of the job individually: In this scenario, a complex batch job is defined in a workflow of steps, and we test each step in isolation without launching the entire job.

In both cases, there's a need to set up test data and launch a job, or a specific step. For this requirement, Spring Batch provides the `JobLauncherTestUtils` API that is designed to launch entire jobs or individual steps in tests. The `JobLauncherTestUtils` provides several utilities and methods. Here are the most important ones:

Random job parameters generation: this feature allows you to generate a unique set of job parameters in order to have distinct job instances during tests. This is particularly useful to make your tests repeatable, and your builds idempotent. In fact, this prevents restarting the same job instances again across different tests, which would make some tests fail. These methods include:
    ◦ `JobLauncherTestUtils.getUniqueJobParameters`
    ◦ `JobLauncherTestUtils.getUniqueJobParametersBuilder`

Launching an entire job from end to end: `JobLauncherTestUtils.launchJob` allows you to launch a job in the same way you would launch it in production. You have the choice to launch it with a set of randomly generated job parameters, or with a specific set of parameters.Launching an individual step: `JobLauncherTestUtils.launchStep` allows you to test a step in isolation from other steps without having to launch the enclosing job.

We'll use these utilities in the upcoming Lab for this lesson.

## Database Decisions

When testing Spring Batch jobs, it is important to decide how to manage metadata across tests to avoid restartability issues between job instances. Nobody wants to see their tests failing with that familiar "A job instance already exists and is complete" error!

The most typical choice for keeping test runs idempotent is using a disposable, in-memory database for each test like H2, HSQL, or Derby. Although using an in-memory database for tests is a good option to avoid metadata sharing, it has the disadvantage of often being different from the database used in production. This might be an unacceptable risk for teams that need to minimize the number of differences between the different parts of their system, including differences between test and live infrastructure.

For this reason, we'll demonstrate running tests against an instance of a production-grade database: the very popular PostgreSQL database.

## Database Sharing Trade-Offs

So, we've decided to use a "real" database when running our tests. Now, we face another decision: how will we install and manage this database?

A database instance can be installed as a separate process on the test machine, or run in a containerized environment like Docker. When using a containerized environment, libraries like TestContainers can be helpful to create disposable database containers for tests. You can learn more about TestContainers using the resource links provided in this lesson.

Now, the question is: should the same database instance (be it run as a separate process, or in a container) be shared between all tests or should each test have its own database instance?

While making each test have its own database instance is technically possible, it has the drawback of being expensive, both in terms of execution time, and in resources. In fact, we'll need to create and destroy real database instances or containers for each test, and execute the Spring Batch metadata initialization scripts, every time. Alternatively, we could try to set up fancy (and error-prone) configuration with database transactions or rollbacks. This might work with a few tests, but in our experience these techniques tend to be unbearably slow if scaled to hundreds (or even thousands) of tests, which is typically the case in real projects.

For the above reasons we have chosen the following option for this course: use a shared instance of the same database product between tests. But, in this scenario, we'll need to make sure that metadata is cleared between tests, in order to avoid the dreaded "A job instance already exists and is complete" error.

## Spring Batch Testing Utilities

To help with database cleanup during tests, Spring Batch provides the `JobRepositoryTestUtils` that can be used to create or delete `JobExecution`s as needed during tests.

A typical usage of `JobRepositoryTestUtils` is to clear the batch metadata from the database before or after each test run. This allows you to have a clean environment for each test case without compromising the test suite execution time, or performance. For example:

```java
@BeforeEach
void setUp() {
    this.jobRepositoryTestUtils.removeJobExecutions();
}

// OR

**@AfterEach
void tearDown() {
    this.jobRepositoryTestUtils.removeJobExecutions();
}**
```

Which option you choose might depend on how you set up or tear down your tests.

## Additional Test Utilities

You might need to do more in your tests than clean up the database. Luckily, `JobLauncherTestUtils` and `JobRepositoryTestUtils` are not the only test utilities that Spring Batch provides in the `spring-batch-test` module.

Other utilities include, but not limited to, the following items:

The `ExecutionContextTestUtils` class: this class provides static methods to access attributes from the execution context of `JobExecution`s and `StepExecution`s.The `MetaDataInstanceFactory` class: this class is helpful to create metadata entities, such as `JobInstance` and `JobExecution`, with the constraints defined by the batch domain model, such as the parent-child link between `JobInstance` and `JobExecution`, or the parent-child link between `JobExecution` and `StepExecution`.The `@SpringBatchTest` annotation: This annotation registers test utilities (like `JobLauncherTestUtils`, `JobRepositoryTestUtils`, etc) as beans in the test context in order to be able to use them in tests.

### Preview: Spring Batch Scopes Test Utilities

In addition, you might need fine-grained management of your Batch artifacts during tests. Spring Batch provides additional utilities, such as test execution listeners like `JobScopeTestExecutionListener` and `StepScopeTestExecutionListener`. These listeners are used to test components (like item readers, item writers, etc) that are scoped with the custom Spring scopes provided by Spring Batch which are `JobScope` and `StepScope`.

We haven't addressed scoped beans yet in the course, but we'll address them in a future module. For now, you just need to know that those test listeners are part of the utilities provided by Spring Batch in the `spring-batch-test` module, and we'll use them in future Labs. Stay tuned!

# Understanding Steps

In the previous lessons, we discussed how to create a job, test it, and run it. We learned how to implement the `Job` interface and, specifically, implement the `execute` method to define what the job should do. As we said, you'll rarely have to implement the `Job` interface as such, since Spring Batch provides ready-to-use classes that let you define your job as a flow of steps. In this lesson, you'll learn about the different types of steps, what comprises them, and how to create a flow of steps to define the logic of your job.

## What Is a Step?

In everyday life we often talk about taking steps. A step down the path. A step in the right direction. Stepping up to the task.

Spring Batch has steps, too. A `Step` is a domain object that encapsulates an independent, sequential phase of a batch `Job`. It contains all of the information necessary to define a unit of work in a batch `Job`.

A `Step` in Spring Batch is represented by the `Step` interface provided by the `spring-batch-core` dependency:

```java
public interface Step {

  String getName();

  void execute(StepExecution stepExecution) throws JobInterruptedException;
}
```

Similar to the `Job` interface, the `Step` interface requires, at a fundamental level, an implementation to specify the step name (the `getName()` method) and what the step is supposed to do (the `execute` method).

The `execute` method provides a reference to a `StepExecution` object. The `StepExecution` represents the actual execution of the step at runtime. It contains a number of runtime details, such as the start time, the end time, the execution status, and so on. This runtime information is stored by Spring Batch in the metadata repository, similar to the `JobExecution`, as we have seen previously.

The `execute` method is designed to throw a `JobInterruptedException` if the job should be interrupted at that particular step.

## What Are the Different Types of Steps?

While it's possible to implement the `Step` interface manually to define the logic of a step, Spring Batch provides different implementations for common use cases. All these implementations derive from the base `AbstractStep` class that provides the common requirements such as setting the start time, end time of a step, updating the exit status of the step, persisting the step's metadata in the job repository, etc.

The most commonly used `Step` types are the following:

`TaskletStep`: Designed for simple tasks (like copying a file or creating an archive), or item-oriented tasks (like reading a file or a database table).`PartitionedStep`: Designed to process the input data set in partitions.`FlowStep`: Useful for logically grouping steps into flows.`JobStep`: Similar to a `FlowStep` but actually creates and launches a separate job execution for the steps in the specified flow. This is useful for creating a complex flow of jobs and sub-jobs.

The following diagram explains the hierarchy and relation between these different types of `Steps`.

![https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/bcbed634eed342346d30d61c0cfd8a30a55036a3/courses/course-spring-batch-essentials/step-and-tasklet.svg](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/bcbed634eed342346d30d61c0cfd8a30a55036a3/courses/course-spring-batch-essentials/step-and-tasklet.svg)

In this course, we'll focus on the `TaskletStep` type.

## The `TaskletStep`

The `[TaskletStep](https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#taskletStep)` is an implementation of the `Step` interface based on the concept of a `Tasklet`. A `Tasklet` represents a unit of work that the Step should do when invoked. The `Tasklet` interface is defined as follows:

```java
@FunctionalInterface
public interface Tasklet {

  @Nullable
  RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception;
}
```

The `execute` method of this functional interface is designed to contain one iteration of the business logic of a `TaskletStep`. There are a few key elements to understand:

The return type of the `execute` method is of type `RepeatStatus`. This is an enumeration that's used to signal to the framework that work has been completed (`RepeatStatus.FINISHED`), or not completed yet (`RepeatStatus.CONTINUABLE`). In that latter case, the `TaskletStep` re-invokes that `Tasklet` again.Each iteration of the `Tasklet` is executed in the scope of a database transaction. This way, Spring Batch saves the work that has been done during the iteration in the persistent job repository. That way, the step can resume where it left off, in case of failure. For this reason, the `TaskletStep` requires a `PlatformTransactionManager` to manage the transaction of the `Tasklet`. We'll address this in the Lab for this lesson.The `execute` method provides a reference to a `StepContribution` object, which represents the contribution of this `Tasklet` to the step (for example how many items were read, written, or otherwise processed) and a reference to a `ChunkContext` object, which is a bag of key/value pairs that provide detail about the execution context of the `Tasklet`.The `execute` method is designed to throw an exception if any error occurs during the processing, in which case, the step will be marked as failed.

Spring Batch provides several implementations of the `Tasklet` interface for common use cases:

`ChunkOrientedTasklet`: Designed for item-oriented data sets, like a flat file or database table. We'll explain this implementation in more detail, and use it in the Labs of this course.`SystemCommandTasklet`: Lets you invoke an Operating System command within the `Tasklet`. We won't use this implementation in this course, but you can refer to the "References" section for more information about how to use it.Others. See the [Spring Batch Reference documentation](https://docs.spring.io/spring-batch/docs/current/reference/html/index.html) for details.

# Using Steps

In the previous lesson we learned all about *what* Spring Batch Steps are. Now let's learn how to use them in our applications.

## Using Steps To Define the Job Execution Flow

As we said, you'll rarely have to implement the `Job` interface manually, like we did in the previous lessons (which we did deliberately, for learning purposes). In fact, Spring Batch provides the `AbstractJob` class that lets you define your job as a flow of steps. This class has two variations:

`SimpleJob`: For sequential execution of steps.`FlowJob`: For complex step flows, including conditional branching and parallel execution.

In this course, you'll learn how to use the `SimpleJob` variation. For more information about `FlowJob`, see the "Condition Flows" and "Parallel Flows" references in the "Links" section of this lesson.

## The `SimpleJob`

The `SimpleJob` class is designed to compose a job as a sequence of steps. A step should be completed successfully in order for the next step in the sequence to start. If a step fails, the job is immediately terminated and subsequent steps are not executed. You can create a sequential flow of steps by using the `JobBuilder` API. Here's an example with two steps:

```java
@Bean
public Job myJob(JobRepository jobRepository, Step step1, Step step2) {
  return new JobBuilder("job", jobRepository)
    .start(step1)
    .next(step2)
    .build();
}
```

In this example, the job (`myJob`) is defined as a sequence of two steps, `step1` and `step2`. The job starts with `step1` and moves to `step2` only if `step1` completes successfully. If `step1` fails, the job is terminated, and `step2` won't be executed. Our `BillingJob` for Spring Cellular is defined as a sequence of three steps, and we'll define it in a similar manner in the Lab for this lesson.

In the previous example, we pass steps as parameters to the `myJob` bean definition method. But, how are these steps defined, and how are they created? This is what we'll see in the next section.

## How To Create Steps?

Similar to the `JobBuilder` API, Spring Batch provides the `StepBuilder` API to let you create different types of steps. All step types share some common properties (like the step name, the job repository to report metadata to, and others), but each step type has its own specific properties. For this reason, Spring Batch provides a specific builder for each step type (`TaskletStepBuilder`, `PartitionedStepBuilder`, and others).

You shouldn't worry about how to create those specific builders, as Spring Batch guides you to using the corresponding one depending on the type of step you are creating. The main entry point to create steps is the `StepBuilder` API. Here's an example that creates a `TaskletStep`:

```java
@Bean
public Step taskletStep(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager transactionManager) {
  return new StepBuilder("step1", jobRepository)
    .tasklet(tasklet, transactionManager)
    .build();
}
```

In this example, we define the step as a Spring bean. The `StepBuilder` accepts the step name and the job repository to report metadata to at construction time, as those are common to all step types.

After that, we call the `StepBuilder.tasklet` method, which will use a `TaskletStepBuilder` to further define specific properties of the `TaskletStep`, mainly the `Tasklet` to execute as part of the step and the transaction manager to use for transactions. Note how we did not use the `TaskletStepBuilder` directly.

The pattern is similar if, for instance, you want to create a partitioned step. Here's an example to create a `PartitionedStep`:

```java
@Bean
public Step partitionedtStep(JobRepository jobRepository, Partitioner partitioner) {
  return new StepBuilder("step1", jobRepository)
    .partitioner("worker", partitioner)
    .build();
}
```

In the same way that we've created a `TaskletStep` in our lab, we'll use the main entry point (the `StepBuilder`) by passing the common properties to it in the constructor (that is, the step name and job repository), then call `StepBulider.partitioner` to further configure the partitioned step with its specific attributes (the `partitioner` in this case). The `Partitioner` is beyond the scope of this lesson and course. We'll use it here only as an example, to show the difference between step type specific attributes. Note that we didn't use the `PartitionedStepBuilder`, instead we used the `StepBuilder` directly.

## Understanding Step Metadata

Similar to job-level metadata, which is stored in the `BATCH_JOB_EXECUTION` table, Spring Batch stores step-level metadata in the `BATCH_STEP_EXECUTION` table. This includes the start time of the step, its end time, its execution status, and other details. We'll see an example of this step-level metadata in the lab for this course.

Another similarity with the job level is the execution context. Each step has an execution context, which is nothing more than a set of key/value pairs to store runtime information about the execution of the step. This includes the step type, the tasklet type if the step is a `TaskletStep`, and other details. The context might also record the progress of a step, like item read count, item write count, and other metrics. These key/value pairs can be used to restart a step where it left off, in case of failure.

By default, a successful step is not re-executed when restarting a failed job instance. However, in some situations, even a successful step should be re-executed when re-attempting a job instance. Spring Batch makes that possible through the `StepBuilder.allowStartIfComplete` parameter. You can also limit the number of times a step is restarted by using the `StepBuilder.startLimit` parameter

# Reading and Writing Data

In the previous Lab, we implemented our first step to copy a file from one directory to another. We did that by creating a custom `Tasklet` and using it within a `TaskletStep`. Now, the second step of our job consists of reading the billing data from that file and writing it into a database table. While we could also do that by implementing a custom `Tasklet`, we'll have to write a lot of boilerplate code to read the file content, parse it, and write it to the database.

Fortunately, Spring Batch provides APIs to take care of all of the above mentioned toil for us in a configurable and reusable manner!

In this lesson, we'll cover those APIs and see how to use them to read and write data to a variety of item-oriented datasources, like files and databases.

## Understanding the Chunk-Oriented Processing Model

Ingesting a file into a database table seems like a simple and easy task at first glance, but this simple task is actually quite challenging! What if the input file is big enough that it doesn't fit in memory? What if the process that ingests the file is terminated abruptly half way through? How do we deal with these situations in an efficient and fault-tolerant way?

Spring Batch comes with a processing model that is designed and implemented to address those challenges. It is called the *chunk-oriented processing model*. The idea of this model is to process the datasource in *chunks* of a configurable size.

A chunk is a collection of "items" from the datasource. An item can be a line in a flat file, a record in a database table, etc.

A chunk of items is represented by the `Chunk<T>` API, which is a thin wrapper around a list of objects of type `T`. The generic type `T` represents the type of items. This means items can be of any type.

```java
public class Chunk<T> implements Iterable<T> {

   private List<T> items;

   // methods to add and get items
}
```

### Transactions

Each chunk of items is read and written within the scope of a transaction. This way, chunks are either committed together or rolled back together, meaning they either all succeed or fail together. You can think of this as an "all-or-nothing" approach, but only for the specific chunk that's being processed.

If the transaction is committed, Spring Batch records, as part of the transaction, the execution progress (read count, write count, etc) in its metadata repository and uses that information to restart where it left off in case of a failure.

If an error occurs while processing a chunk of items, then the transaction will be rolled back by the framework. Therefore, the execution state won't be updated and Spring Batch would restart from the last successful save-point in case of failure – kind of like a video game!

The number of items to include in a chunk is called the *commit-interval*, which is the configurable size of a chunk that should be processed within the scope of a single transaction.

In the next section, we'll explore the APIs to read and write items in chunks that are provided by Spring Batch.

## Reading Data

Reading data from a flat file is different from reading data from a database table or from a messaging broker queue. For this reason, Spring Batch provides a strategy interface to read items in a consistent and implementation-agnostic way.

### The `ItemReader` Interface

Reading data in Spring Batch is done through the `ItemReader` interface, which is defined as follows:

```java
@FunctionalInterface
public interface ItemReader<T> {
   @Nullable
   T read() throws Exception;
}
```

This functional interface provides a single method named `read` to read one piece of data, or an item. Each call to this method is expected to return a single item, one at a time.

Spring Batch will call `read` as needed to create chunks of items. This way, Spring Batch never loads the entire datasource in memory, only chunks of data.

The type of items `T` is generic, so it's up to the implementation to decide which type of items is returned.

### Error Handling

If an error occurs while reading an item, implementations are expected to throw an exception to signal the error to the framework.

Note how the method `read` is annotated with `@Nullable`, which means it might return `null`.

COPY

`@Nullable
T read() throws Exception;`

Returning `null` from that method is the way to signal to the framework that the datasource is exhausted. Remember, batch processing is about processing finite data sets, not infinite streams of data.

### The `ItemReader` Library

Spring Batch comes with a large library of `ItemReader` implementations to read data from a variety of datasources, like files, databases, message brokers, etc. As a Spring Batch developer, you'll typically just have to configure one of these readers and use it in a step.

In this lesson and its Lab, we'll read data from a flat file, so we'll use the `FlatFileItemReader` which is designed for just that. Here is an example of how to configure such a reader:

```java
@Bean
public FlatFileItemReader<BillingData> billingDataFileReader() {
    return new FlatFileItemReaderBuilder<BillingData>()
            .name("billingDataFileReader")
            .resource(new FileSystemResource("staging/billing-data.csv"))
            .delimited()
            .names("dataYear", "dataMonth", "accountId", "phoneNumber", "dataUsage", "callDuration", "smsCount")
            .targetType(BillingData.class)
            .build();
}
```

In this example, we create a bean of type `FlatFileItemReader` which returns items of type `BillingData`. This type represents one item from the billing data file, which we'll explain and create in the Lab of this lesson. To build a `FlatFileItemReader`, we'll use the `FlatFileItemReaderBuilder` API to specify the path of the input file, the expected fields in that file, and the target type to map data to. We'll explain all these details in the Lab for this lesson. All you have to understand for now is that leveraging one of the built-in readers really boils down to configuring an instance of it like we did in this snippet.

Unless you have a very specific requirement to implement a custom item reader, you should be able to leverage one of the item readers provided by Spring Batch out-of-the-box, as shown above. In a future lesson, we'll use the `JdbcCursorItemReader` to read data from a database table using the JDBC API. You can find the list of all available readers in the "Links" section of the lesson.

## Writing Data

Similar to the `ItemReader` interface, writing data with Spring Batch is done through the `ItemWriter` interface, which is defined as follows:

```java
@FunctionalInterface
public interface ItemWriter<T> {

   void write(Chunk<? extends T> chunk) throws Exception;
}
```

### Writing Chunks

The `write` method expects a chunk of items. *Unlike* reading items which is done one item at a time, writing items is done in chunks. The reason for this is that bulk writes are typically more efficient than single writes.

For example, the `JdbcBatchItemWriter`, which is designed to write items in a relational database table, leverages the JDBC Batch API to insert items in batch mode. This would not have been possible if the `ItemWriter` interface was designed to write one item at a time.

Spring Batch provides several implementations of the `ItemWriter` interface to write data to a variety of targets like files, databases and message brokers. You'll find the list of available writers in the "Links" section of this lesson.

Note: if an error occurs while writing items, implementations of this interface are expected to throw an exception to signal the problem to the framework.

### `ItemWriter` Example

For the first step of our Billing job, we need to write data to a relational database table, so we'll use a `JdbcBatchItemWriter` for that. Here's an example of how to configure such a writer:

```java
@Bean
public JdbcBatchItemWriter<BillingData> billingDataTableWriter(DataSource dataSource) {
    String sql = "insert into BILLING_DATA values (:dataYear, :dataMonth, :accountId, :phoneNumber, :dataUsage, :callDuration, :smsCount)";
    return new JdbcBatchItemWriterBuilder<BillingData>()
            .dataSource(dataSource)
            .sql(sql)
            .beanMapped()
            .build();
}
```

We'll go over this snippet in detail in the Lab. For now, make note of how the `JdbcBatchItemWriter` is built using database-specific constructs such as a SQL statement and a `DataSource`. Consider that other writers, such as `FlatFileItemWriter`, would be built using constructs that are specific to those implementations.

## Configuring Chunk-Oriented Tasklet Steps

Now that we've explained how to read and write data in Spring Batch using item readers and writers, it's time to use those readers and writers to define a chunk-oriented step.

A chunk-oriented step in Spring Batch is a `TaskletStep` configured with a specific `Tasklet` type, the `ChunkOrientedTasklet`. This `Tasklet` is what implements the chunk-oriented processing model (explained above) using an item reader and an item writer.

Similar to configuring a `TaskletStep` with a custom tasklet, configuring a chunk-oriented tasklet is also done through the `StepBuilder` API, except we'll need to call the `chunk` method instead of the `tasklet` method.

So let's see how to use this API to create the second step of our job, the file ingestion step.

### File Ingestion

The file ingestion step is intended to read billing data from a flat file and write it into a relational database, so assuming the `FlatFileItemReader<BillingData>` and `JdbcBatchItemWriter<BillingData>` that we showed in previous sections are in place, here's how to configure the step:

```java
@Bean
public Step ingestFile(
              JobRepository jobRepository,
              PlatformTransactionManager transactionManager,
              FlatFileItemReader<BillingData> billingDataFileReader,
              JdbcBatchItemWriter<BillingData> billingDataTableWriter) {

    return new StepBuilder("fileIngestion", jobRepository)
            .<BillingData, BillingData>chunk(100, transactionManager)
            .reader(billingDataFileReader)
            .writer(billingDataTableWriter)
            .build();
}
```

As we've seen before, we'll need to specify the step name and the job repository. This is common for all step types. Since we're creating a chunk-oriented `TaskletStep`, we'll need to provide two parameters:

The first parameter is the chunk-size, or the commit-interval. In this case, it is set to 100, which means Spring Batch will read and write 100 items as a unit in each transaction.The second parameter is a reference to a `PlatformTransactionManager` to manage the transactions of the tasklet. Remember, each call the `Tasklet.execute` is done within the scope of a transaction.

Finally, the `StepBuilder.chunk` API guides the user to further configure the chunk-oriented tasklet, by specifying the item reader and writer to use through the `reader` and `writer` methods respectively.

# Processing Data

In the previous lesson, you learned how to read and write data and how to configure a chunk-oriented step. You implemented the second step of the `BillingJob` in the previous Lab, and ingested data from the input file into the relational database table without any modification.

While it's common to move data around, it's rarely done without modifying it, adapting it, or processing it in some way. In fact, input/output systems aren't always a perfect match. As a batch developer, you'll have to find a way to process data within a chunk-oriented step.

In this lesson, you'll learn how to process data in Spring Batch. You'll learn the main API for data processing, and how to use it within a chunk-oriented step.

## The `ItemProcessor` API

Processing items in a chunk-oriented step happens between reading and writing data. It's an optional phase of the chunk-oriented processing model, where items returned by the reader are processed, then handed over to the writer. You can think of it as an intermediate stage in a processing pipeline.

Processing items in Spring Batch is done by implementing the `ItemProcessor` interface, which is defined as follows:

```java
@FunctionalInterface
public interface ItemProcessor<I, O> {

   @Nullable
   O process(@NonNull I item) throws Exception;
}
```

This is a functional interface with a single method `process`. This method takes an item of type `I` as input and returns an item of type `O` as output. The method name `process` is generic on purpose as processing data is a broad term and covers different use cases like transforming data, enriching it, validating it, or filtering the data.

Each outcome from this method corresponds to a particular use case which we'll cover in the next sections.

## Transforming Data

The `process` method takes an item of type `I` as input and returns an item of type `O` as output. This clear distinction between input and output types is designed to allow developers to change the item type during the processing phase. This is useful when adapting the input data to the format expected by the target system.

Here's an example of an item processor that transforms items from a hypothetical type `BillingData` to another type `ReportingData`:

```java
public class BillingDataProcessor implements ItemProcessor<BillingData, ReportingData> {

   public ReportingData process(BillingData item) {
       return new ReportingData(item);
   }
}
```

While it's possible to change the item type during processing, what if we don't need that? Meaning, what if the input type `I` is the same as the output type `O`? This is what we'll discuss in the next section.

## Enriching Data

Transforming data is not the only use case of an `ItemProcessor`. In fact, items aren't necessarily transformed from one type to another.

In many batch processing jobs, the requirement is to enrich input data with additional details before saving it to the target system. In this case, items aren't transformed from one type to another, but enriched with additional information. For instance, an item processor can request an external system to enrich the current item, then return the enriched item:

```java
public class EnrichingItemProcessor implements ItemProcessor<Person, Person> {

   private AddressService addressService;

   public EnrichingItemProcessor(AddressService addressService) {
      this.addressService = addressService;
   }

   public Person process(Person person) {
      Address address = this.addressService.getAddress(person);
      person.setAddress(address);
      return person;
   }
}
```

In this `EnrichingItemProcessor`, items of type `Person` are enriched with the person's address using a hypothetical `AddressService`. This is a typical example of an item processor that enriches data without transforming it.

## Validating Data

The `process` method is designed to throw an exception in case of a processing error. Processing errors could be technical errors (like a failure to call an external service) or functional errors (like invalid items).

One of the most common use cases of an item processor is data validation. Once we read items from an input source, we might need to validate if the input data is valid or not before saving it to the target system. In typical enterprise systems, data is often consumed from a trusted source, but this isn't always the case. In fact, in many enterprise batch systems, data is consumed from external sources which might not be trustworthy, in which case, validating data is crucial to the security and integrity of the target system.

An `ItemProcessor` is the ideal place to implement data validation rules. Here's an example:

```java
public class ValidatingItemProcessor implements ItemProcessor<Person, Person> {

   private EmailService emailService;

   public ValidatingItemProcessor(EmailService emailService) {
      this.emailService = emailService;
   }

   public Person process(Person person) {
      if (!this.emailService.isValid(person.getEmail()) {
         throw new InvalidEmailException("Invalid email for " + person);
      }
      return person;
   }
}
```

This `ValidatingItemProcessor` is designed to validate the person's email using a hypothetical `EmailService`, and reject invalid items by throwing an `InvalidEmailException`. On the other hand, if the person's email is valid, the item is returned as-is.

## Filtering Data

The last outcome from the `process` method that we haven't addressed yet, is when the method returns `null`. How does Spring Batch interpret such a result from the item processor?

Returning `null` from the `process` method tells Spring Batch to *filter out the current item*. Filtering the current item simply means to not let it continue in the processing pipeline. Therefore, it'll be excluded from being written as part of the current chunk. You can think of this like Unix filters or Java's `Stream#filter` operation.

Data filtering is another typical use case for an item processor and which we'll use in the Lab of this lesson. In fact, the last step of our `BillingJob` is to generate a billing report for customers who spent more than $150 per month. This means we should filter out all customers who spent less than that amount. We'll implement this business rule in the Lab of this lesson, but here's a typical item processor for that particular case:

```java
public class FilteringItemProcessor implements ItemProcessor<BillingData, BillingData> {

   public BillingData process(BillingData item) }
      if (item.getMonthlySpending() < 150) {
         return null; // filter customers spending less than $150
      }
      return item;
   }
}
```

In this example, items with a `monthlySpending` amount less than $150 are filtered and won't be included in the final report.

# Batch Scoped Components

In the previous Lab, you implemented the third and final step of the `BillingJob`. Billing data is now read from the input file, saved in the database and the billing report is generated in the staging directory.

There's still an issue though: The path to the input file is hard-coded in the bean definition method of `billingDataFileReader`:

```java
@Bean
public FlatFileItemReader<BillingData> billingDataFileReader() {
   return new FlatFileItemReaderBuilder<BillingData>()
	.name("billingDataFileReader")
	.resource(new FileSystemResource("staging/billing-2023-01.csv")) //hardcoded value
	.delimited()
	.names("dataYear", "dataMonth", "accountId", "phoneNumber", "dataUsage", "callDuration", "smsCount")
	.targetType(BillingData.class)
	.build();
}
```

What if we decide to ingest another file – say, `billing-2023-02.csv`? As currently defined, the same file (`billing-2023-01.csv`) will always be used, no matter which input file we pass as a job parameter. Since the input file is passed to the job as a parameter *at runtime*, there's no way to know its value at *configuration time*.

This is a common situation faced by many developers of batch applications: how to configure the item reader *lazily at runtime* until the value of the job parameter is resolved?

This is where Spring Batch custom scopes come into play, and this is what you'll learn about in this lesson: job-scoped and step-scoped components.

## Understanding Batch Scopes

Spring Batch provides two custom Spring bean scopes: job scope and step scope. Batch-scoped beans are not created at application startup like singleton beans. Rather, they are created at runtime when the job or step is executed. A job-scoped bean will only be instantiated when the job is started. Similarly, a step-scoped bean won't be instantiated until the step is started. Since this type of bean is lazily instantiated at runtime, any runtime job parameter or execution context attribute can be resolved.

Configuring batch-scoped components requires a powerful Spring technology that we haven't introduced yet: the Spring Expression Language, or SpEL. Let's learn about it now.

## Spring Expression Language (SpEL)

Job parameters and execution context values can be resolved by using the Spring Expression Language, or SpEL. This powerful notation lets you specify which job parameter or context value should be bound in the bean definition method "late" at runtime. We talk about "Late binding" of job or step attributes. Let's see a simple example.

## Example of a Step-Scoped Component

The following example shows a step-scoped item reader bean:

```java
@Bean
@StepScope
public FlatFileItemReader<BillingData> reader(@Value("#{jobParameters['input.file']}") String inputFile) {
   return new FlatFileItemReaderBuilder<BillingData>()
   .resource(new FileSystemResource(inputFile))
	   // other properties

	   .build();
}
```

This snippet creates a step-scoped bean of type `FlatFileItemReader`, thanks to the `@StepScope` annotation. This annotation is provided by Spring Batch and marks a bean as step-scoped. A similar annotation (`@JobScope`) exists for job-scoped beans as well.

This item reader is configured with an input file from a job parameter called `input.file`. The job parameter is specified by using the `@Value("#{jobParameters['input.file']}")` notation, which instructs Spring Framework to resolve the value `lazily` from the `jobParameters` object. With that in place, this item reader will be configured dynamically with the value of the input file resolved at runtime from job parameters.

# State management and restartability

Batch jobs are never executed in isolation. They consume data, produce data and often interact with external components and services. This interaction with the external world makes them exposed to different kinds of human errors and system faults like receiving bad input data or interacting with unreliable services.

As a batch developer, you're faced with several challenges: How should you address incorrect input? How should you handle processing errors? These and other questions are all real-world problems that almost all batch systems encounter.

Therefore, you should design and implement your applications in a robust and fault-tolerant way, and Spring Batch can help you tremendously in that task.

In this lesson, we'll introduce the fault-tolerance features in Spring Batch and explain when to use them.

## State Management

We've already seen how the domain model of Spring Batch is designed to allow the restart of failed job instances. In fact, thanks to saving the state of each job execution in a persistent job repository, Spring Batch is able to resume job instances where they left off.

Restartability in Spring Batch is implemented at two distinct levels: *inter-step* restartability and *inner-step* restartability.

### Inter-Step Restartability

Inter-step restartability refers to *resuming a job from the last failed step*, without re-executing the steps that were successfully executed in the previous run.

For instance, if a job is composed of 2 sequential steps and fails at the second step, then the first step won't be re-executed in case of a restart.

### Inner-Step Restartability

Inner-step restartability refers to *resuming a failed step where it left off*, i.e. from within the step itself.

This feature isn't particular to a specific type of steps, but is typically related to chunk-oriented steps. In fact, the chunk-oriented processing model is designed to be tolerant to faults and to restart from the last save point, meaning that successfully processed chunks aren't re-processed again, in the case of a restart.

We'll explore this with an example in the next Lab.

## Restarting Failed Jobs

Restartability is a feature in Spring Batch that you don't activate with a flag or by using a specific API. Rather, it's a feature that's provided automatically by the framework, thanks to its powerful domain model design. In fact, restarting a failed job instance is just a matter of relaunching it with the same identifying job parameters. There are no APIs to call or features to activate.

## Error Handling

By default, if an exception occurs in a given step, the step and its enclosing job will fail. At this point, you'll have several choices about how to proceed, depending on the nature of the error:

If the error is *transient* (like a failed call to a flaky web service), you can decide to restart the job. The job will restart where it left off and might succeed the second time. However, this is not guaranteed as the transient error might happen again. In this case, you'd want to find a way to implement a retry policy around the operation that might fail.If the error is *not transient* (like an incorrect input data), then restarting the job won't solve the problem. In this case, you'll need to decide if you want to either fix the problem and restart the job, or to tolerate the bad input and skip it for later analysis or reprocessing.

# Handling non-transient errors with skips

While transient errors can be addressed by retrying operations, some errors are "permanent". For example, if a line in a flat file is not correctly formatted and the item reader can't parse it, no matter how many times you retry the read operation, it'll always fail.

So, how do we address these kinds of errors? Should we let the job fail until we fix the input file and restart the job afterwards? Or, can we just skip that line and continue processing the rest of the file?

In the previous lesson and Lab, you learned how to fix the data and restart a failed job instance. In this lesson, you'll learn how to skip incorrect items.

## Skipping Incorrect Items

In the previous lesson and Lab, you've seen an example of how you can fix the input data of a batch job and restart it until completion. In fact, we had to fix a couple of incorrect lines and restart the job twice.

While this was feasible because only a couple of lines were incorrect, what if dozens of input lines are incorrect? Are we going to fix lines one by one and restart the job dozens of times? This is obviously inefficient, and for this reason, Spring Batch provides a feature to skip bad items for later analysis.

The skip feature in Spring Batch is specific to chunk-oriented steps. This feature covers all of the phases of the chunk-oriented processing model: when errors occur while reading, processing, or writing items.

To activate this feature, you'll need to define a "fault-tolerant" step.

### Fault-Tolerant Step

As mentioned in previous lessons, the main entry point to create steps is the `StepBuilder` API. Creating a fault-tolerant step is done by calling the `faultTolerant()` method on the `StepBuilder`.

Here's an example:

```java
**@Bean
public Step step(
   JobRepository jobRepository, JdbcTransactionManager transactionManager,
   ItemReader<String> itemReader, ItemWriter<String> itemWriter) {
   return new StepBuilder("myStep", jobRepository)
		.<String, String>chunk(100, transactionManager)
		.reader(itemReader)
		.writer(itemWriter)
		.faultTolerant()
		.skip(FlatFileParseException.class)
		.skipLimit(5)
		.build();
}**
```

This method returns a `FaultTolerantStepBuilder` that allows you to define fault-tolerance features (skip and retry). In this case, we're defining a skip policy.

The `skip()` method defines which exception should cause the current item to be skipped. In this example, if a `FlatFileParseException` occurs (most likely during the read operation because the current line cannot be parsed), the current line in the flat-file will be skipped.

But, what does it mean for an item to be skipped?

### Skipped Items

An item being skipped simply means that it'll be excluded from the current chunk. Spring Batch won't fail the step immediately, rather, it'll continue processing the next item.

### Skip Limit

Finally, the `skipLimit()` method is used to define the maximum number of items to skip. We might tolerate a reasonable number of items to skip, but if this number is high, then something is fundamentally wrong with the input data, and it would be better to let the step fail, then analyze the problem in detail.

## Handling Skipped Items

Skipped items can be audited by using a `SkipListener`.

The `SkipListener` is an extension point that Spring Batch provides to give the developer a way to handle skipped items, like logging them or saving them somewhere for later analysis.

The `SkipListener` is defined as follows:

COPY

`public interface SkipListener<T, S> extends StepListener {

   default void onSkipInRead(Throwable t) { }

   default void onSkipInWrite(S item, Throwable t) { }

   default void onSkipInProcess(T item, Throwable t) { }

}`

This interface provides 3 methods to implement the logic of what to do if an item is skipped during the `read`, `process` or `write` operation. Once implemented, the `SkipListener` can be registered in the step using the `FaultTolerantStepBuilder.listener(SkipListener)` API.

We'll see an example of how to implement this interface and use it in the Lab for this Lesson.

## Custom Skip Policies

The skip policy we've seen earlier is based on an Exception. You declare which exception should happen to skip the current item. But what if the decision to skip an item isn't (only) based on an exception? This is where the `SkipPolicy` interface comes into play.

Spring Batch provides a strategy interface called `SkipPolicy` that allows you to provide a custom skip policy that's based on a custom business rule. The `SkipPolicy` interface is defined as follows:

```java
**@FunctionalInterface
public interface SkipPolicy {

	boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException;

}**
```

This is a functional interface with a single method `shouldSkip` that's designed to specify whether the current item should be skipped or not. This method provides a handle to a `Throwable` object that contains all the details and context about the current item and the exception that happened.

A typical example of a skippable exception is the `FlaFileParseException` that we saw earlier, which provides the current item along with the line number in the input file.

Once the custom `SkipPolicy` is implemented, it can be registered in the step using the `FaultTolerantStepBuilder.skipPolicy(SkipPolicy)` API.

# Handling transient errors with retries

Some errors are transient by nature, such as calling a flaky web service or hitting a database lock. Retrying the same operation when such errors occur might succeed in a subsequent attempt. It would be unfortunate and inefficient to fail an entire job and have to restart it later if one could just retry an operation.

For this reason, Spring Batch provides a retry feature that lets you retry operations that might encounter a transient error. This lesson is about the retry feature.

## Retrying Transient Errors

The retry feature in Spring Batch is based on the [Spring Retry](https://github.com/spring-projects/spring-retry) library. Spring Retry was historically part of Spring Batch itself but was extracted as a separate library, which is now used by many other projects in the Spring portfolio.

Similar to the skip feature, the retry feature is designed for chunk-oriented steps, specifically for the processing and writing phases. The reading phase is *not* retryable.

To activate the retry feature, you'll need to define a "fault-tolerant" step. As mentioned in previous lessons, the main entry point to create steps is the `StepBuilder` API, and creating a fault tolerant step is done by calling the `faultTolerant()` method.

Here's an example:

```java
**@Bean
public Step step(
   JobRepository jobRepository, JdbcTransactionManager transactionManager,
   ItemReader<String> itemReader, ItemWriter<String> itemWriter) {
   return new StepBuilder("myStep", jobRepository)
		.<String, String>chunk(100, transactionManager)
		.reader(itemReader)
		.writer(itemWriter)
		.faultTolerant()
		.retry(TransientException.class)
		.retryLimit(5)
		.build();
}**
```

In this snippet, the chunk-oriented step, `myStep`, is declared as a fault-tolerant step, thanks to the call to the `.faultTolerant()` method. This method returns a `FaultTolerantStepBuilder` that allows you to define the fault-tolerance features (skip and retry).

In this case, we're defining a retry policy as follows:

> Any TransientException (or one of its subclasses) should be retried at most 5 times, after which the step should be marked as failed.
> 

The exception to retry is defined with the `.retry()` method, while the retry limit is defined with the `retryLimit()` method.

We'll implement a retry feature in the Lab of this lesson.

## Handling Retry Attempts

For auditing purposes, Spring Batch provides a way to register a `RetryListener` in the step in order to plug in custom code during retry attempts: `onError`, `onSuccess`, and so on.

The `RetryListener` API is part of Spring Retry and is defined as follows:

```java
**public interface RetryListener {

   default <T, E extends Throwable> void onSuccess(
          RetryContext context,
          RetryCallback<T, E> callback,
          T result) {
   }

   default <T, E extends Throwable> void onError(
          RetryContext context,
          RetryCallback<T, E> callback,
	   Throwable throwable) {
   }
}**
```

This interface is an extension point that gives the developer a way to execute custom code during retry attempts (that is, during failed attempts), by implementing the `onError` method. You can execute custom code upon successful attempts by implementing the `onSuccess` method.

Typical examples of using this API are logging and reporting retry operations. See the "Links" section for more details about this API.

Once you've implemented the `RetryListener`, you can register it in the step by using the `FaultTolerantStepBuilder.listener(RetryListener)` method.

## Custom Retry Policies

Similar to the `SkipPolicy` API for custom skip policies, Spring Batch provides a way to use custom retry policies by implementing the `RetryPolicy` interface. The `RetryPolicy` interface is part of Spring Retry and is defined as follows:

```java
**public interface RetryPolicy extends Serializable {
   boolean canRetry(RetryContext context);
   void registerThrowable(RetryContext context, Throwable throwable);
}**
```

This interface is an extension point that lets the user utilize custom rules for retrying items. Spring Retry already provides several ready-to-use implementations of this interface, including `MaxAttemptsRetryPolicy`, `CircuitBreakerRetryPolicy`, and others.