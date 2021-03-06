# TOR: Tobias Onion Router 🧅
![Test and Lint Workflow Status](https://github.com/trthingnes/onion/actions/workflows/testandlint.yml/badge.svg)

[Click here to view workflow runs.](https://github.com/trthingnes/onion/actions/workflows/testandlint.yml)

## Introduction 💬
When working on TOR (not to be confused with **_Tor_**, The onion router) I focused on implementing as many useful features as I could in the two weeks given,
while still making sure that the code is well-structured and maintainable.
Another focus was to make the project simple to use with existing products and protocols.
I also wanted to implement the functionality not already available in the Java API myself, to learn as much as possible.

This project was made in two parts: The SOCKS proxy part and the onion routing part. 
The SOCKS proxy can be used both with and without the onion router part, and I suggest testing out both.
Intuitively, the proxy is a lot faster when not routing traffic through the onion circuit.
To enable/disable onion routing, change the value `ONION_ENABLED` in `Config.kt`.

## Dependencies 🗃
* **Kotlin Maven Plugin** for Maven integration with Kotlin.
* **Kotlin Logging** for logging.
* **Ktlint** for formatting and linting source code.
* **Dokka** for javadoc generation (Kotlin is not supported by default).
* **JUnit** for unit testing.

## Installation 💽
The project has not been packaged because seeing log messages is helpful during a technical demonstration.
Because of this, the project has to be run using an IDE. For this project I used JetBrains' _IntelliJ IDEA_.

1. Get the source code by downloading a zip or cloning the repository.
2. Open the source code in an IDE that can run Kotlin code.

## Usage 📖
### Running the project 💻
1. Change `Config.kt` to your liking and then run `Main.kt`.
2. You should see the proxy waiting on port 1080 and the routers waiting on the correct ports (1111, 2222...).
3. Setup any SOCKS 5 client to connect to `localhost:[1080 or SOCKS_PORT]` (Firefox has built-in support).
4. Try visiting a site like `http://datakom.no` or `https://wikipedia.org`.

**That's it!** Your traffic is now routed through TOR.
You should see the data being transferred in the console.

Want to test using curl? Use: `curl -x socks5://127.0.0.1 http://datakom.no`.

### Running the tests ✅
To run all the tests in the project, simply type `mvn test` or run `test` using IntelliJ.

## Documentation 📃
The latest version of the API documentation can be found in the `javadoc` folder.
API documentation can also be generated by running the command `mvn dokka:javadoc`,
which will generate docs in `target/dokkaJavadoc`.

## Features ⭐
* Onion Routing using any number of routers.
* Random circuit for every new connection.
* Diffie Hellman key exchange to allow client and routers to retrieve a shared secret.
* AES encryption of all layers.
* Out of the box support for all clients that support SOCKS 5 protocol ([RFC 1928](https://datatracker.ietf.org/doc/html/rfc1928)).
* Support for most one-way TCP protocols.
* Direct mode to bypass the onion circuit for troubleshooting purposes.
* In-depth logging allowing for easy troubleshooting.
* GitHub CI/CD (actions) for automatic testing and linting.

## Current weaknesses and future work ⌚
* _**Description of weakness.**_
  * _**Suggestions for improvement.**_


* Some websites break because of redirects, 3rd party scripts and certain protocols (e.g. two-way protocols/UDP).
  * Enhance support for redirects and loading data from 3rd party sources.
  * Adding UDP association support through SOCKS protocol.
  * Adding BIND support through SOCKS protocol, allowing server to client communication.
  

* Any program running on the client machine may connect to the proxy.
  * Adding authentication support to the SOCKS implementation, securing access to the proxy.


* A malicious router could perform a man-in-the-middle attack on the client proxy.
  * Adding keystore support to allow proxy to confirm router identities.
  * Adding regular key changes to limit damage if key is compromised.


* An adversary could attempt to correlate encrypted and decrypted traffic based on repeated blocks.
  * Adding initialization vector (IV) support to AES encryption.
  * Adding leaky pipe topology support. Allowing cells to exit anywhere in the circuit.


* Creating a new circuit for every connection is very expensive and makes the network significantly slower.
  * Adding streams support. Allowing multiple sockets to be routed through the same circuit.


* The integrity of the data passing through the network cannot be guaranteed.
  * Adding checksum support to onion cells.
