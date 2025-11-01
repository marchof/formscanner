# FormScanner

> [!NOTE]
> This is a [fork](FORK.md) of the [FormScanner](https://formscanner.org/) project at [SourceForge](https://sourceforge.net/projects/formscanner/). The master branch in this repository is based on the latest [1.1.4](https://github.com/marchof/formscanner/releases/tag/1.1.4) release.

FormScanner is an OMR (Optical Mark Recognition) software that automatically marks multiple-choice papers. FormScanner not bind you to use a default template of the form, but gives you the ability to use a custom template created from a simple scan of a blank form. The modules can be scanned as images with a simple scanner and processed with FormScanner software. All the collected information can be easily exported to a spreadsheet.

## Installing the Distribution

Download the latest release and unzip it. For execution you need any current Java Version
([Java 8](https://javaalmanac.io/jdk/8/), [Java 11](https://javaalmanac.io/jdk/11/), [Java 17](https://javaalmanac.io/jdk/17/), [Java 21](https://javaalmanac.io/jdk/21/) or [Java 25](https://javaalmanac.io/jdk/25/)).

## Building Yourself

For local compilation ensure you fulfill the build requirements

* [Git client](https://git-scm.com/)
* [Java 8](https://javaalmanac.io/jdk/8/)
* [Maven 3](https://maven.apache.org/)

 and run:

    $ git clone https://github.com/marchof/formscanner.git
    $ cd formscanner
    $ mvn clean package

The distributable package will be at `./formscanner-distribution/target/formscanner-<version>-bin`.
