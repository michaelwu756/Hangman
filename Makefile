ifdef SYSTEMROOT
	CP = "./;./json-20180130.jar"
else
	CP = "./:./json-20180130.jar"
endif

all: hangman/Hangman.class hangman/WordList.class

run:
	java -cp $(CP) hangman.Hangman

hangman/%.class: hangman/%.java
	javac -cp $(CP) $^

runJar: Hangman.jar
	java -jar $^

Hangman.jar: hangman/Hangman.class hangman/WordList.class MANIFEST.MF words.txt json-20180130.jar
	jar -xvf json-20180130.jar
	jar -cvfm Hangman.jar MANIFEST.MF hangman/*.class org/json/*.class words.txt
	rm -rf META-INF org

clean:
	rm -rf hangman/*.class

.PHONY: all run
