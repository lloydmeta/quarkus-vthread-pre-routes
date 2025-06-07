# quarkus-vthread-pre-routes

This is a small project that demonstrates what I described in https://github.com/quarkusio/quarkus/discussions/48263

## How to use

Assuming you've cloned this repo and have Java 24 installed

1. Start Quarkus (`./gradlew quarkusDev`)
2. In another terminal, run `./curl_loop.sh`

Logs in (1) should be from `quarkus-virtual-thread`s, with no Quarkus errors, like so

```java
 sleeping for 5 seconds
2025-06-07 15:13:35,210 INFO  [com.bea.aut.MyAuthIdentityProvider] (quarkus-virtual-thread-9867) Done sleeping, creating SecurityIdentity for user: joe-9867
2025-06-07 15:13:35,211 INFO  [com.bea.fil.ReqIdRequestFilter] (quarkus-virtual-thread-9867) Processing request in ReqIdRequestFilter
2025-06-07 15:13:35,211 INFO  [com.bea.rou.hel.GreetingResource] (quarkus-virtual-thread-9867) Handling say-my-name within GreetingResource
2025-06-07 15:13:35,211 INFO  [com.bea.rou.hel.GreetingResource] (quarkus-virtual-thread-9867) Authenticated user: joe-9867
2025-06-07 15:13:35,211 INFO  [com.bea.fil.ReqIdResponseFilter] (quarkus-virtual-thread-9867) Processing response in ReqIdResponseFilter
2025-06-07 15:13:35,211 INFO  [com.bea.fil.ReqIdResponseFilter] (quarkus-virtual-thread-9867) Using existing request ID: 87c5869e-87ff-47c6-acd5-41e36f47bedb
2025-06-07 15:13:35,226 INFO  [com.bea.aut.MyAuthMechanism] (quarkus-virtual-thread-10140) Handling authentication in MyAuthMechanism
2025-06-07 15:13:35,226 INFO  [com.bea.aut.MyAuthIdentityProvider] (quarkus-virtual-thread-10140) Handling authentication in MyAuthIdentityProvider for user: joe-10140
2025-06-07 15:13:35,226 INFO  [com.bea.aut.MyAuthIdentityProvider] (quarkus-virtual-thread-10140) Simulating IO by sleeping for 5 seconds
2025-06-07 15:13:35,228 INFO  [com.bea.aut.MyAuthIdentityProvider] (quarkus-virtual-thread-9868) Done sleeping, creating SecurityIdentity for user: joe-9868
2025-06-07 15:13:35,228 INFO  [com.bea.fil.ReqIdRequestFilter] (quarkus-virtual-thread-9868) Processing request in ReqIdRequestFilter
```

--

# Standard Quarkus generated stuff

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/quarkus-vthread-pre-routes-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
