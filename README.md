# FormScanner

This is a fork of the [FormScanner](https://formscanner.org/) project at [SourceForge](https://sourceforge.net/projects/formscanner/).

FormScanner is an OMR (Optical Mark Recognition) software that automatically marks multiple-choice papers. FormScanner not bind you to use a default template of the form, but gives you the ability to use a custom template created from a simple scan of a blank form. The modules can be scanned as images with a simple scanner and processed with FormScanner software. All the collected information can be easily exported to a spreadsheet.

## Build requirements (to be reworked)

* [Java 6](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven (2 or 3)](http://maven.apache.org/)
* [Mercurial](http://mercurial.selenic.com/) (I'm using [TortoiseHg](http://tortoisehg.bitbucket.org/) with Windows Explorer "shell" integration all-in-one installer with TortoiseHg 3.1.1 and Mercurial 3.1.1)

## Compiling the software (to be reworked)

    $ hg clone http://hg.code.sf.net/p/formscanner/code formscanner-code
    $ cd formscanner-code
    $ mvn clean install
