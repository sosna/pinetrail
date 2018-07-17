# Installation guide

In the next version of Pinetrail, a Graphical User Interface will be provided,
along with an easy-to-use installer. In the meantime, it is necessary to build
the software yourself if you intend to use Pinetrail command-line interface.

## Step 1 - Prerequisites: Installing Java 8

If you do not have Java 8 installed, you will need to download it (please note
that it is necessary to download the SDK, in order to compile Java source code).
There are various implementations of the SDK to choose from, such as, for
example, the one provided by
[Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

You can check that the installation was successful by opening a terminal and
checking the Java version displayed:

~~~
> java -version
java version "1.8.0_11"
Java(TM) SE Runtime Environment (build 1.8.0_11-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.11-b03, mixed mode)
~~~

## Step 2 - Downloading the source code

Next, you need to get the source code.

If you have git installed, the recommended way is to use the `git clone` command
in a terminal:

```
> git clone https://github.com/sosna/pinetrail.git
```

If you don't have git installed or do not want to clone the repository, you can
simply download the
[zip file](https://github.com/sosna/pinetrail/archive/master.zip) provided on
Github.

## Step 3 - Building Pinetrail using the provided Gradle wrapper

A Gradle wrapper is available directly under the Pinetrail directory. The
wrapper allows you to easily compile the software with just one command:

```
> ./gradlew installDist
```

The Gradle wrapper will take care of downloading all necessary dependencies and
will deploy the software in your Pinetrail directory, under the subdirectory
`pinetrail-cli/build/install/pinetrail-cli/bin/`.

## Step 4 - Adding an alias to Pinetrail in your shell environment (Optional)

If you wish, in order to ease the access to the software, you can add an
alias to Pinetrail in your shell environment. In my case, I have added the
following in my `.bashrc` file.

```
alias pinetrail='/home/xso/dev/java/pinetrail/pinetrail-cli/build/install/pinetrail-cli/bin/pinetrail-cli'
```

## Step 5 - Running Pinetrail

Once the above has been done, you can invoke the software by running the
`pinetrail` command. For example, the following sets your MapQuest key and
your fitness level, and then passes the location of the GPX file to be processed
as parameter.

```
> pinetrail -k YOUR_MAP_QUEST_KEY -l advanced MyTrail.gpx
```

You will find a sanitized version at the same location as the input file,
with `_clean` appended to the filename (`MyTrail_clean.gpx` in this
example).

In order to list all the available options, use the `-h` flag:

```
> pinetrail -h
```
