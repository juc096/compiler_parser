JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	TokenType.java \
	Token.java \
	Scanner.java \
	Lox.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

