# Poem_Generator
UKZN COMP 316 (Natural Language Processing) Semester project - 2016

Coded in eclipse using Java 8. Includes only the src folder of the eclipse project.

This project requires JWI (Java WordNet Interface) using WordNet 3.1, and the Stanford CoreNLP Library(2015). Both libraries need to be present in the project folder - or edit file paths in the wrapper classes for exception safety.


Released under the GNU General Public License v3.0

Status:
The project is now completed and functional, but is not in a desirable state. We are able to generate free verse a close approximation of sonnets. 
See "Poem Generation Report.pdf", in particular the concluding statements, for additional info.

NOTE:
I do not find NLP particularly interesting on this level, so the purpose if this project evolved into investigating multithreading and design patterns in Java, with some degree of search optimization. This shows in the poem generation algorithm itself, as many NLP techniques were studied and added as functions but were never used in the final algorithm.
A seperate project was done in C++ in optimization, with the intention of porting the progress to java and including it in this project. That was never done, and currently the search optimization is limited to a basic multithreaded search.
