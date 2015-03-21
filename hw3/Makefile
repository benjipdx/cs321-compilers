#----------------------------------------------------------------------
# CS321 Assignment 3 (Winter 2015)
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

all:	astpsr

astpsr: ast/Ast.class AstParser.class

clean:	
	rm *.java *.class
