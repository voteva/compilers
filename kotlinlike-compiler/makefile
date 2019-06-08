all: compile

compile:
	java -jar ./jtb132di.jar ./kotlin.jj
	java -jar ./javacc5.jar ./kotlin-jtb.jj
	javac Main.java
	mkdir output

clean:
	rm -f *.class *~
	rm -f  -r ./syntaxtree
	rm -f  -r ./visitor
	rm -f ./JavaCharStream.java kotlin-jtb.jj KotlinParser.java KotlinParserConstants.java KotlinParserTokenManager.java ParseException.java Token.java TokenMgrError.java
	rm -f  -r ./output