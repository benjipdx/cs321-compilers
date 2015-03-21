#----------------------------------------------------------------------
# CS321 Assignment 4 (Winter 2015)
#----------------------------------------------------------------------

JFLAGS = -g
JC = javac
JCC = javacc

.SUFFIXES: .jj .java .class

.PRECIOUS: %.java

.jj.java:
	$(JCC) $*.jj

.java.class:
	$(JC) $(JFLAGS) $*.java

all:	checker

checker: ast/Ast.class ast/astParser.class Checker.class

clean:	
	rm *.class
