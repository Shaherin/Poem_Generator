# Poem_Generator
<b>UKZN COMP 316 (Natural Language Processing) Semester project - 2016</b>

Coded in eclipse using Java 8. Includes only the src folder of the eclipse project.

This project requires JWI (Java WordNet Interface) using WordNet 3.1, and the Stanford CoreNLP Library(2015). Both libraries need to be present in the project folder - or edit file paths in the wrapper classes for exception safety.


Released under the GNU General Public License v3.0

<b>Status:</b> <br>
The project is now completed and functional, but is not in a desirable state. We are able to generate free verse a close approximation of sonnets. <br>
See "Poem Generation Report.pdf", in particular the concluding statements, for additional info.

<b>NOTE:</b> <br>
I do not find NLP particularly interesting on this level, so the purpose if this project evolved into investigating multithreading and design patterns in Java, with some degree of search optimization. This shows in the poem generation algorithm itself, as many NLP techniques were studied and added as functions but were never used in the final algorithm.<br>
A seperate project was done in C++ in optimization, with the intention of porting the progress to java and including it in this project. That was never done, and currently the search optimization is limited to a basic multithreaded search. <br>

<i>On multithreading</> <br>
The conclusion on multithreading is still undetermined. Java insists on providing features like thread pools and executor services which remove a lot of responsibility from the programmer, as well as requiring time to learn to use properly. A misinformed/ignorant programmer will not be able to use these services optimally, simply because they are unaware of certain features. Of course, we have the ability to write our own thread pools etc. and forego the java implementations but why would we, when these have been built more optimally into the language itself? Our own implementations would afford us more control, and may even be better than the java way in some cases, but they may also be inferior in which case there is certainly no point.<br>
C++11 has come a long way in terms of its standard library and multithreading. We now have access to inherent mutex and semaphore capabilities(among others), as well as an inherent thread library.<br>

<i>Design Patterns </i> <br>
The purpose of design patterns is often to overcome certain issues/oversights in programming languages themselves, as well as to write maintainable, readable code. C++ is outright superior in this aspect, with just the use of function pointers, lambdas(much more useful than lambdas in java), std::function and std::signal in C++11, you can overcome most design issues simply and concisely, leading to shorter, smarter, and even more readable code. <br>


I'm sorry, is my C++ bias showing yet?

