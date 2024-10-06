# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

## UML Diagram as created in Phase 2:

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYtQAr5kvDrco4Lh+Sx9AC5zAgUCYoOUICpCgIANgatIdKi6KxNiMEFK6LJkhSKGvig776K8SwmkS4YFJaMAgIkABmSBQMMMB+nIMBETAoHvFxWywDEMBUMAyArrh6hJvkMHlAAasJSArth+Rie68C6pksb+tOIbKdRHIwNG+lBg6OnQXKsEwJCYA+LcGmoSOb4wNOs7oFhZkSf+aYItCyJqLmsLYh5Ek-qUgEVhOXxOY2zbhd+V6-gU2Q9v2g69AM5bAeOrYzH0kVzgubbLqu3h+IEXgoOge4Hr4zDHukmSYIlF5FNQ17SAAorubX1G1zQtA+qhPt0uXoO2v75B55TDWgmCBZJZnlAgB41L6KFTa5aooDhYZuqUcAIUhtkac5aAUUybq6R6DFMcMrJscwR1RUkAlCSJoaUedpkbaUL3yY6ohbe9eEcCg3DqUGsIPXOp1mqoF0yCDFKGP1T7GdtLJBfNsmvdhf6QZcXmZtmfkBXj8VzS1aY9LFFMSY1YB9gOQ5LpwRXroEkK2ru0IwAA4qOrK1aeDXnswsoU+UFQ811vX2KOQ1Bsdo240CJbkmAfOjKoAYK49JMq2NUkwGrGtqNrda639KgA2deG0ftDa2Wtb02+J+Q0fBiENqonLQFj8mQyNinKfh6v8-5zsw3DjGcKywkIIJckcCbGjGBJwcWRS1loKoftJ-zEdUZ9TrysnVOW5tyupqUkKIjCRMIGA4eBUXAF9LLmvjJUbejgAktILZPCemQGgAchl4VPDoCCgMhHRj6M+VfO3KDz2OMUuBvjTUyUtMiwzKX9MvqidxU3ejH3A9TEP+pz+PWWT9PSGj3f1ZTMvq+L3sG8uFvZgs54xUNzYGstgbgqkvSZF5qOFIdUzw5FFpecWlRagNBlnLYIOs5xDnfqObelBK7AjgmpFAJszYzgtrNQ2no9SkKdkHNGpI7ae0dpglyBdzpuz0h7JC3tfaJwDtNEy5Ni7mR+gpNyBIGHumoZkWhrC0CzBwaMaGVFOFEIgWoE2MBkgZFSI5eRjleRKLEEIw2Yjy7WxhiHE2q8yHHRURwmilks6smMew9GRdwRG1HKvCxBCSw128lmXyDdYTGL1qmDGSDD6jgrJlYxeCxp033kOM+6BgJVnAn-FcAC2YBEsCDRayQYAACkIA8igaMQIU8Z7C3gWyYKyDKR3haMveW5ssG9FAcAApUA4AQEWlARRvdpCJP8WUAAVuUtAciOnoFmD0bpvT+mDOGefUZESoLCK8VMnkszyFzgWUsygKzoBrJQH3PY60RGWPDHBe2LC5knXca7d29teFQFzgIl5sNPHyhgLnCxSkpEentqQ8JPyLrcK9j7KAJs2oAA9vJoBUJCv5sFnG3BNqYehgNtT21OVABQQlti6G4LCY5fSBlnOXn3Bx6M1FMJ4T3ei9RUhyBNmwVQbK5CEuJb8MlYhoWEopdPZZ1KoD0vEuimSicgXBz8FoWRo4wmjnfGKk5ErZhESlbDRliq9SuKQDQGZ0CdF+lYghWiEqYCqEWswc4HF0rilTrcnaGcrK3Fula2l0g0XbP+b6vx41SYQgzHXEJjdNn4OEa3X1MUlb5GSTAZKqTfW7CyYVXJJUAheB6V2L0sBgDYFAYQeIiQYFCzpg0uKEt2qdW6r1YwiaJrw1BigNqJRLB+h5PYJupMZXSARpkTtlBu3pAcOHXFLt3RDvbaOqwPbJ26rhsYFwIBuB4HDqY+aCdsZuXGWG2uPkcyhOjQbWtpYxlJr3qm3ozMVxAA

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
