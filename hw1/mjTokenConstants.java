//----------------------------------------------------------------------
// CS321 Assignment 1 (Winter 2015)
//
// miniJava Token Definitions (Manual Implementation)
//----------------------------------------------------------------------

public interface mjTokenConstants {
    int EOF      = 0;  
    int ID       = 1;  
    int INTLIT   = 2;  
    int DBLLIT   = 3;  
    int STRLIT   = 4;  
    int TRUE     = 5;  	// "true" 
    int FALSE    = 6;  	// "false"
    int CLASS    = 7;  	// "class"
    int EXTENDS  = 8;  	// "extends"
    int STATIC   = 9;  	// "static"
    int PUBLIC   = 10;  // "public"
    int VOID     = 11; 	// "void"
    int MAIN     = 12; 	// "main"
    int INT      = 13; 	// "int"
    int DOUBLE   = 14; 	// "double"
    int STRING   = 15; 	// "String"
    int BOOLEAN  = 16; 	// "boolean"
    int NEW      = 17; 	// "new"
    int THIS     = 18; 	// "this"
    int IF       = 19; 	// "if"
    int ELSE     = 20; 	// "else"
    int SYSTEM   = 21; 	// "System"
    int OUT      = 22;  // "out"
    int PRINTLN  = 23;  // "println"
    int WHILE    = 24; 	// "while"
    int RETURN   = 25;	// "return"
    int EQ       = 26;  // ==
    int NEQ      = 27;  // !=
    int LE       = 28;  // <=
    int GE       = 29;  // >=
    int AND      = 30;  // &&
    int OR       = 31;  // ||

    // The remaining single-character operators/delimiters
    // are represented by their ASCII code (which have values
    // greater than 32).
    //
    // "+" | "-" | "*" | "/" | "!" | "<" | ">" | 
    // "=" | ";" | "," | "." | "(" | ")" | "[" | "]" | "{" | "}"
}
