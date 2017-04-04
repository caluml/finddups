## What is it?
This is a command line Java app to find duplicate files on your computer.

### But I hate Java
Well, there you go then.

### Can't you write it in C/Go/Rust/Python/Erlang/something else?
Yes, probably, but I'm lazy.

### How can I run it?
You can either build the application from source, or download a pre-compiled version.

#### Build from source
To build from source, you need Git, a Java JDK, and Maven:
```
git clone https://github.com/caluml/finddups
mvn assembly:single
java -jar target/finddups-0.0.1-SNAPSHOT-jar-with-dependencies.jar 1000000 /home /tmp
```

#### Precompiled
To just run the supplied version (might be a little out of date):<br>
Download https://github.com/caluml/finddups/releases/download/v0.0.1/finddups-0.0.1.jar<br>
`java -jar finddups-0.0.1.jar 1000000 /home /tmp`

### How does it work?
* First, it recurses the given directory/ies, building a list of files.
* Then, it looks for files which have identically sized files.
* It then reads those identically sized files, comparing them byte for byte until it finds a byte that doesn't match, or it reaches the end of the file.<br>
The app outputs its errors to stderr, and its results to stdout as it goes.

### Why not use MD5 or SHA or $HASHING_ALGORITHM
I chose not to use any kind of hashing or CRC system, as that would require reading the entire file, which would be less efficient if a byte near the beginning was different.

### This sucks. How can I express my hate?
Any suggestions/comments to my github username at gmail.com

### Caveats
I have only tested this on Linux, with Java 6, 7 and 8.
