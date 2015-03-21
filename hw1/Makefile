#----------------------------------------------------------------------
# CS321 Assignment 1 (Winter 2015)
#----------------------------------------------------------------------

JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.PRECIOUS: %.java

.java.class:
	$(JC) $(JFLAGS) $*.java

lexer: 	mjLexer.class

clean:	
	rm *.class
