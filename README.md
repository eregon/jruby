# An implementation of Guest-Language Safepoints on top of JRuby+Truffle

All branches are based on [safepoint_base](https://github.com/eregon/jruby/tree/safepoint_base), which has 2 additional commits not in JRuby master:

* One to remove an automatic usage of guest safepoints when the main thread terminates as it kills the other threads.
  In all benchmarks there are no additional threads so this does not change the behavior.
* Another adding a the [script](https://github.com/eregon/jruby/blob/3b1714cdbaffc34a7631b851e78a1a37528df48c/compilation.rb) to measure the compilation time.

Each configuration is on a separate branch:
* [removed](https://github.com/eregon/jruby/tree/safepoint_removed)
* [api](https://github.com/eregon/jruby/tree/safepoint_base)
* [switchpoint](https://github.com/eregon/jruby/tree/safepoint_switchpoint)
* [volatile](https://github.com/eregon/jruby/tree/safepoint_volatile_flag)

To run the benchmarks, you must:

* Clone this repository: `git clone https://github.com/eregon/jruby -b safepoint && cd jruby`
* Checkout the branch of one of the configurations: `git checkout safepoint_base`
* Have a standard ruby installed, try `ruby -v`
* Build JRuby: `ruby tool/jt.rb build`
* Try the build: `ruby tool/jt.rb run -e 'print "Hello World"'`
* Run benchmarks: `ruby tool/jt.rb bench reference all`

Then checkout another branch, build again and compare with `ruby tool/jt.rb bench compare all`.

The latency expriment is available in [safepoint_latency](https://github.com/eregon/jruby/tree/safepoint_latency).

What follows is the original README.

# JRuby -  A Java implementation of the Ruby language

Master: [![Build Status](https://travis-ci.org/jruby/jruby.png?branch=master)](https://travis-ci.org/jruby/jruby) 
1.7 branch: [![Build Status](https://travis-ci.org/jruby/jruby.png?branch=jruby-1_7)](https://travis-ci.org/jruby/jruby/branches)

Authors: Stefan Matthias Aust, Anders Bengtsson, Geert Bevin, Ola Bini,
 Piergiuliano Bossi, Johannes Brodwall, Rocky Burt, Paul Butcher,
 Benoit Cerrina, Wyss Clemens, David Corbin, Benoit Daloze, Thomas E Enebo,
 Robert Feldt, Chad Fowler, Russ Freeman, Joey Gibson, Kiel Hodges,
 Xandy Johnson, Kelvin Liu, Kevin Menard, Alan Moore, Akinori Musha,
 Charles Nutter, Takashi Okamoto, Jan Arne Petersen, Tobias Reif, David Saff,
 Subramanya Sastry, Chris Seaton, Nick Sieger, Ed Sinjiashvili, Vladimir Sizikov,
 Daiki Ueno, Matthias Veit, Jason Voegele, Sergey Yevtushenko, Robert Yokota,
   and many gracious contributors from the community.

Project Contact: Thomas E Enebo <enebo@acm.org>

JRuby also uses code generously shared by the creator of the Ruby language, 
Yukihiro Matsumoto <matz@netlab.co.jp>.

## About

JRuby is the effort to recreate the Ruby (http://www.ruby-lang.org) interpreter
in Java.

The Java version is tightly integrated with Java to allow both to script
any Java class and to embed the interpreter into any Java application. 
See the [docs](docs) directory for more information.

## Prerequisites

* A Java 7-compatible (or higher) Java development kit (JDK)
* Maven 3+
* Apache Ant 1.8+ (see https://github.com/jruby/jruby/issues/2236)

## Run

    bin/jruby rubyfile.rb

interprets the file `rubyfile.rb`.

If you checked out from the repository or downloaded the source distribution,
see the next section to build JRuby first.

## Compiling from source

See [BUILDING](BUILDING.md) for more information.

## Testing

See [BUILDING: Developing and Testing](BUILDING.md#developing-and-testing) for
more information.

## More Information

Visit http://jruby.org for more information.

Visit http://jruby.github.io/jruby for the Maven Site documentation.

## License

Read the [COPYING](COPYING) file.
