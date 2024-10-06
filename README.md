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

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYtQAr5kvDrco4Lh+Sx9AC5zAgUCYoOUICpCgIANgatIdKi6KxNiMEFK6LJkhSKGvig776K8SwmkS4YFJaMAgIkABmSBQMMMB+nIMBETAoHvFxWywDEMBUMAyArth+S4aS8C6pksb+tOIYSao1EcjA0aqUGDqKUm+QweUQkiY6ojaf+aYItCyJqLmsLYiZ2k-qUgEVhOXzTrOzbOd+V6-gU2Q9v2g69AM5bAeOrYzH0rmNs2oWLmYnCrt4fiBF4KDoHuB6+Mwx7pJkmC+ReRTUNe0gAKK7iV9Qlc0LQPqoT7dJFc7tr++QmeUjXoJgtk6XKsEwAgB41L6KEdWgWG9ThYZuqUcAIUhsmwrJbljaGlFuspHoMUxwysmxzBLVFSQCfpSArlp0G9aUJ2iRN4lTXhHAoNwMlBotQbLRRTLrfkNHSE9FKGLVT6afd6h2ZdABqwmnYZKjGZBlxmZm2ZWTZCPeT1RVpj0nlY9p+VgH2A5Dku8WeIlG6Qrau7QjAADio6stlp55eezCylj5QVHTFXVfYo4Ne9h3NX+6P4WADOjKoAZC3OaNApQF1qn15IS4zMt1od43K5Na14bRc0Ngto2fWaSk-Sp8GIQ2qictAUMiQdTViYp4uS2o1mrV9LIbTAjGcKywkIIJ0McO7GjGNprswJCYA+LcqgO6d4de2b4PK6U4c47DKDwwrZSQoiMIowgYCe7ZSslFcfT81L4yVDXo4AJLSC2TwnpkBoAHIhc5Tw6AgoDIR0PejAuk6N6Mo9jh5Lhz40uMlPjbNEwF-S12o9cVJPKAt23Uwd-qI+92F-eD0h3cn9WUwb9P4-gXPLgL3FK7k+ugTYPH2DcFJXqZPTo4Ug5TPDkdml5OaVFqA0PmAtgiy3QEOW+o5F6K1amLWi0kUDuw1jOLWXV0aV3BBgv+WDRw4I+jnXW3tJJW3mq9E2qcqIWzgobW29tQ5O06udTGTp5QhwMmJGQoN3Sej1Ng0aswkGjFNkwmiojMiqHdjAZIGRUgwFGuo3kUixDcN0vwmGLthFu1HNPcheCtLMJjhSeOaBWTaMYetQhfD7HYVFvnCEGZi6WVLrCbR8tUzpyrqWHeFZQraJQS1Amq8hwhOAlWcCL8ErvwCJYJ6A1kgwAAFIQB5AA0YgQB5D1ZqAtk9lIGUjvC0DegtNZziHN-YAqSoBwAgANKAkjm7SAiW41MpQABWOS0DiPgWgDpowW7+KgjwohAyeTDNqegMZu9pDa14bnO6esaGG2NiMmR305GsLtlAJOHBOFoAcT7JxfUTmUI2dQkRhtsF0TaUs6eeyfbMNoTbI57sSoAA9zJoBUBcsGVzSixxse7UwhjNkPOti0tpCghLbF0NwWEzzoDTA3u8sGlivmqCbvReoqQ5DuzYKoYlcgEXQCRb8VFYgvnUrhBiqAOKlJgpgDcmF9zSh+C0JkbBG9ZgstmERNlvs+V6jsUgGgQzAEqL9KxBCtFWnQBgKoAazBzgcWCuKSOdyzYeLjgnJVhgN4txBebPR5rpCUJ6cCDxRcLI5h8ZM1BZT16dI8iLfIUSYD+RiTa3YCTlxJKSgELwjSuxelgMAbA39CDxESEAlmBNSleS5qVcqlVqrGB9W1GQ-1MglRKJYP0PJ7DlwIdM+Uf1nooBLZQMt6QHCe25YautANG1WHLa28VljjAuBANwPAntdGXX0TdHW9qSyF3MlmbxZc3UtQ9d031K8A29FJiuIAA

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
