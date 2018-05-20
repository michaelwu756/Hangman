all: Hangman.class WordList.class

run:
	java -cp "./;./json-20180130.jar" Hangman

%.class: %.java
	javac -cp "./;./json-20180130.jar" $^

clean:
	rm -rf *.class

.PHONY: all run