# kotlin-compiler
Implementation of a compiler front end for the Kotlin like language(Kotlin -> LLVM IR) using javaCC and JTB tools.   
Semantic analysis and LLVM IR code generation is done utilizing the visitor pattern.

## Compilation
~~~
make
~~~

## Execution
To execute the program: 
~~~
java Main ./input/BubbleSort.kt
~~~
The program will compile all .kt files to the their respective LLVM IR files with name **BubbleSort.ll**.

In order to compile the produced LLVM IR files you will need Clang with version>=4.0.0.Compilation and execution is performed with:
~~~
clang -o ./output/out1 ./output/BubbleSort.ll
./output/out1
~~~
