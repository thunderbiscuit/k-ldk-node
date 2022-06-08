# Readme

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
