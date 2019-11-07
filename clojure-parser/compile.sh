java -jar javacc.jar -OUTPUT_DIRECTORY=src/parser src/Clojure.jj
mkdir -p target
javac src/parser/*.java -d target/
