# Readme

This node is an attempt at creating a fully working Lightning node using [ldk-java](https://github.com/lightningdevkit/ldk-garbagecollected) and [bdk-kotlin](https://github.com/bitcoindevkit/bdk-kotlin). It's a work in progress:

- [x] Connect to peers
- [ ] Open channels (halfway there)
- [ ] Send payments
- [ ] Receive payments
- [ ] Close channels

## Building and using the node
Build the node:
```shell
# this will create an executable in ./app/build/install/kldk/bin/
./gradlew installDist
```

Start the REPL on testnet:
```shell
cd ./app/build/install/kldk/bin/
./kldk
```

Note that on a Mac M1 you'll need to make sure you are using a native aarch64 JDK (not an x86_64 JDK running through Rosetta) for LDK to work properly.
