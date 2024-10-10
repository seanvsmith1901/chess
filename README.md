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

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdOUAGZITgwc4wSFgHy3GA3Go6ABWKHAmEZxJZJ1BuJU5TQPgQCBgsLQEFFHRgEEl0uYnN8nGxYJUbLlzNJIEhchQ1MotI6jIZ2nl6jZxlKCg4HBgzqgjJxIitpxtJNUpXtKEdCh8YFSsOASdS7tlnttrNOvv9gcTydDlsMuVOgIuZQiUKRUEiqnVWErwMVhSOlxg1w6d0m5VWTzTyfqEAA1ug+1N9mHjuXCtlzOUAExOJzdbtDYCjScDqZD1Ij8doHfT9AcUxeXz+ALQdjkmAAGQg0SSATSGSyyHMbI71eqdSaVoDHUBI0HXOktxQWYXjeDgDl-AoW07DdbkgncYKWPoAXOYEClLcoQFSaVR2DV1kRQNEMQtZUUAKKMWTJClSIg0YPSJHMCl9bleX5QVhSSMUjRlej1DnfC9UocUpRlUs8Jo8o4CIkBRwk1DRhgDD3hgTYwEI1TUXRWIZ1EOjs2jBSHUyIsU33TMRNUTiOWAAM+V5PiRTQFBkmsg0hKweyxPkgUKX46zU3TKTjWo8NaMQnDOwRaFyMbBAwHC5NIvAbEkKgQLqEuFDe37R493TQ8J2KrCZ0oOcFwwUoVzXXoBh7NCYEq6Y+n3crj3aqcqrPC9vD8QIvBQdBH2fXxmDfdJMkwOrmCVfK-2kABRB81vqNbmhaYDVFA7purHdBqty-IcvKY6j0wHK5Ji8oEGfGok1I670GiqgTPyezSnJMAwv3SAjzYplo0crkeUDXiQpFfdfOk-yzJZPKvpVfdMqwWSfuR0kOBQbgrPTWFrJ60GvQc-JfRkAmKUMfbQMZdKDxOtByY4+60ZQUoADVgGQc9ZLioFLkSusGybWFsvi3LOeQ+DihqgpFuXVd1wOQbPGG69IUDB9oRgABxSDWVmj8Fq-PJ20Vv9Da23b7Ego6ytZs7harRiwGN0ZVGZsnbplznwRgf7vbUP3Wc+77fpDilrKzdjwapjkYG4jhWRhoVbgziK-ITsGUaDlVgqzpIMbz7HxM2ccYEbSE+VzxGYB1Zg1Q1YyVFMxOGNjr2Tal-OKYh1OoYzjUYCofmkA4MONGMOcY-ctBVD5gXZ8Hjn8nw2eegVmK50uyFERhCXUthR2fd36WRdlrebauPoL7UcZKkfyCAElpEnJ530yYMADk2odSmDoBAoASIdEAduPqu436jCgWMGB+wXAoMaHvEotVLZgFVk1foT9VAvwqHAlAn9v5TF-k6SBQD+pPFAeAgB1DYFPwQSeLCKCXBoLMJwIaV5AjYGFNgbg8BLKGDDikOan4chLWtitco-4GgOydsEF2R51zMMgugmqF0ZYEREWHCOIMA43yLtzGAcZHT6PemzDutEcbd1JL3QGKiPobyTtTNOrlAxLwbhlPOAUTEQlhmXRuxobEBO0sAGuddDDlybi3Zu6oEBhIJLjGModILn0ggg9mbiU5LzHpqHksR2AkgNJyEOkEYDJAyKkGAyY5AwE-q4wud8HoJPHtqXUT8EbGlTmaDgsweSZE4CgDgUdO52ILg48xmR9HqNYs00SyddHxkyBnNyQTWTdMNE3apyYKmjCaf41pXMySQUxsk8SKAAAecRwAHJIdIVO3hhgPIuULbRN9AnH2SpLJ+n9r5VlRshYhvY+r-OkJo86KsYCNXXKCtC-YBrcK1rwgIlgCZPWSDAAAUhAHkRtKkBDocpC2Uifz30qFUSkgEWhP2dsOVm65BHAAxVAOAEAnpQFmBCqF7tgSlAlPitAljnFoFmD0FlbKOVcp5R-SFgLcInODkKnkorGVHglVKygMroBysOQq5JMd-pOI1S4gKyyR6cHWV4zZPjUgXOOeJKxlygrV3psKGJIT7nxLbkko1qTYxKVHHM+VOSUbLLTjaku-EtnnL8lUmpDyjmpNRuCbxT93k0XCYRYiMAkDlO1eyzl0B82sniYMQMBLM0V2zZMimpQ-BaFmRkiF0wi26u5apekizKbUybY6LZSAaAisqXs2pyZDAgBLbAVQT1mD8hYmIeeXcpkxhjdnOpRE3l+NTeEmtiNXVtJuXc5gELnkQFed0rN+9+WdiPklesahJaKq0b+B+ELKp8vyDCuFzUIW7GReeVFI0AheFZfAbgeA+TYEEYQeIiRxHm0WhS2RlR1qbW2rtYwbtPkexpoTFAa0SiWGTDyewA87rKpVNIWmmRiOUFI+kBwA9sa-Vo4RhjVgyMsfDUs6mxgXAgCg3CPjDlwmTwFmEu91YH3i2fWfV95131dm-SrP9u8uHniAA

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
