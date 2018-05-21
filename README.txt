This code plays hangman for the UPE induction challenge. It uses a dictionary to
try to match possible valid words. To compile, run 'make all'. To run, do 'make
run'. Note that there is also a jar file that can be run with 'make runJar', or
'java -jar Hangman.jar' if you do not have make installed. After running, type
in 'play' and enter the number of games to play.

Supported options include a logging setting that stores the result of each
hangman game into a file called dictionary.txt, a verbose setting that shows how
the program decides the next letter to guess, a cheat mode that restarts the
game with one guess remaining in order to not lose, and a lose mode that tries
to lose in order to harvest data faster. I used the lose mode with logging to
generate the dictionary included in this project. Type in 'help' when the
program starts for more information.
