# Tor: Tobias Onion Router 🧅
![Test and Lint Workflow Status](https://github.com/trthingnes/onion/actions/workflows/testandlint.yml/badge.svg)

## Introduction 💬
When working on Tobias Onion Router I focused on implementing as many useful features as I could in the two weeks given,
while still making sure that the code is well-structured and maintainable.
Another focus was to make the project simple to use with existing products and protocols.

## Dependencies
* **Kotlin Maven Plugin** for Maven integration.
* **Kotlin Logging** for logging.
* **Ktlint** for formatting and linting.
* **Dokka** for javadoc generation.
* **JUnit** for unit testing.

## Installation 💽
1. Get the source code by downloading a zip or cloning the repository.
2. Open the source code in an IDE that can run Kotlin code.

For this project I used JetBrains' IntelliJ IDEA.

## Usage 📖
### Running the project 💻
1. Change configuration to your liking and then run `main.kt`.
2. Verify that the proxy and the correct number of routers are waiting for connections.
3. Setup any client that supports SOCKS 5 to connect to `localhost:1080`.
4. Try visiting a site like `http://datakom.no` or `https://wikipedia.org`. 

Want to test using curl? Use: `curl -x socks5://127.0.0.1 http://datakom.no`.

### Running the tests ✅
To run all the tests in the project, simply type `mvn test` or run `test` using IntelliJ.

## Features ⭐
* Onion Routing using any number of routers.
* Random circuit for every new connection.
* Works out of the box with all clients using SOCKS 5 protocol ([RFC 1928](https://datatracker.ietf.org/doc/html/rfc1928)).
* Support for most one-way TCP protocols.
* Direct mode to bypass the onion circuit for troubleshooting purposes.

## Future work ⌚
* Enhance support for redirects and loading data from 3rd party sources.
* Adding UDP support through SOCKS protocol.
* Adding BIND support through SOCKS protocol, allowing server to client communication.
* Adding authentication support to the SOCKS implementation, securing access to the proxy.
* Adding keystore support to allow proxy to confirm router identities.
* Adding regular key changes to limit damage if key is compromised.
* Adding initialization vector (IV) support to AES encryption.
* Adding leaky pipe topology support. Allowing cells to exit anywhere in the circuit.
* Adding streams support. Allowing multiple sockets to be routed through the same circuit.
