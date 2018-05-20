ifdef SYSTEMROOT
	CP = "./;./json-20180130.jar"
else
	CP = "./:./json-20180130.jar"
endif

all: Hangman.class WordList.class

run:
	java -cp $(CP) Hangman

%.class: %.java
	javac -cp $(CP) $^

clean:
	rm -rf *.class

.PHONY: all run