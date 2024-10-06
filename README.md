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

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYtQAr5kvDrco4Lh+Sx9AC5zAgUCYoOUICpCgIANgatIdKi6KxNiMEFK6LJkhSKGvig776K8SwmkS4YFJaMAgIkABmSBQMMMB+nIMBETAoHvFxWywDEMBUMAyAro6og4WGbrlHAuqZLG-rTiGuHqNRHIwNG6lBvGcooBJlFSdGVQIMAliUAGQazugFFMm6qmlMANqqEZJmUPW8mWWgYkqEm+T-mmCLQsiai5rC2J+T5P6lIBFYTl804eeMrbge2v4FNkPb9oOvQDOWwHjkl0x9PFjbNvli5mJwq7eH4gReCg6B7gevjMMe6SZJg6UXkU1DXtIACiu59fUfXNC0D6qE+3TFXOKV-pBJbTegmDhfkMHlAgB41L6KGLWgWE6XpNl4XACFIfJsLuSVe2hvpLJ2XRaCMcxrJscwl1zjAAlCSJN1HSp0E6aU31ICu2H5Mp7p9VA+goBQj6JBdFlXdZZqqHZMgoNwmQwONT4OhDEWAwAasJINebpc1ApcAWZtmIVhfNqWrVeaY9N+LM+Z1YB9gOQ5LpVnjVRukK2ru0IwAA4qOrKtaeHXnswso9WmFQS0No32KOU1IzNP6U6m+FgFLoyqOZdbI8tjMA2qsEwOSRvS2bM4W2DENwadDbnbtKNUfkNHwYhDaqJy0AkyJ71LeTh2o4bxtqKFv2o+jjGcKywkIIJpMcHHGjGD5bswJCYA+LcqhhyDOeJ1R1tOvKOds1H+vAqUkKIjCdMIGACfhTXAF9JrJuJRU-ejgAktILZPCemQGgAcnlsVPDoxlIXPC8FVMA8oPPowLvsLgH407PK5zCs81l-Rb6oQ8j6M4+T1M0-6h0O9jovUzL6AyEv+v1ab6Or897gQPi4I+FVRJrhqgEbAJdsDcHgLJQwccUhtTPDkRWl5lblBvA0DWWtgg63QEOLer9j4lCbiWT0eo45Ow8gzKmv5mY2zgogmh3so7g0knhWiHsvaEOugTP2akA5IWDqHLOEdPIExruCTOP0OEyC4dqVho5aFXVmCQ0cPtbJCJYV6TIqhkHJAyKkGAu0zG8k0eKaRTDa622BqDA6nDbqkjthSOOr81Fzm0XdXRhcKQlzQKyKxYgbFrTtgA0cjdfKMwhBmduwVO6whCfQ1MhMsGllvugdeISyGUDSmfGAmUhxZJilMZKy4qrrkCKZZUEBkgwAAFIQB5JLUcgRP5IXlugtkkVKhVEpHeFoW9tbmznEOOBLkoBwAgBtKAGix7SDyYwvypQABWLS0BsP4Qsu+0hUlQVsbIjZPJtljPQLslA499o22juGd2gc+HnIEUotGfiRFBxDlAcuHBJFV1sjI+UMAfkcLdh8mhdE5mXNfj4lSuiPliKgHHPqAAPQKaAVD-LuoC2CRdAlx1MK7V5HoPYzLmQoIS2xdDcFhJC6A0wt6wref7D2qhR70XqKkOQcc2CqE5XIMl0AKW-GpWID5gq4R0qgEy9JdjyggqJS490fgtCZBoVvWYUrZhERlX4lVepglIBoFs9pxi-SsQQrRWZ0AcYbWYOcDiuVxR52cX9d0-ji6lwtYYLe48sVwvCb66Q0TVn+LbkFHMSSDn5NsX3INsVlkFPQefEpQbdgVIFpAjcXgTJdi9LAYA2A4GEHiIkFBcsua9JZtg-qg1hqjWMLNGJDDyjSExhSFAfUSiWD9Dyew3crZHPlG2rGnbu29ocAnRVbrSgjo7V2ygPb0iTt1TRYwLgQDcDwAnMJgM5Fk2whQtMrdApZkSV3aNjC+kNz1vkLmpRim9H5iuIAA

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
