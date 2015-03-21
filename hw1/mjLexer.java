//----------------------------------------------------------------------
// A starting version of miniJava (W15) lexer.
//
// (For CS321 Winter 2015)
//----------------------------------------------------------------------
//
//Benjamin Reichert
//
//
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class mjLexer implements mjTokenConstants {

	static int currentLine = 1;
	static int currentColumn;
	static int beginLine, beginColumn;
	static String lexeme = null;
	static boolean isPushback = false;

  static class LexError extends Exception {
    public LexError(int line, int column, String msg) { 
      super("at (" + line + "," + column + ") " + msg); 
    }
  }

  // Token object
  //
  static class Token {
    int code; 		// token code
    String lexeme;      // lexeme string
    int line;	   	// line number of token's first char
    int column;    	// column number of token's first char
    
    public Token(int code, String lexeme, int line, int column) {
      this.code=code; this.lexeme=lexeme;
      this.line=line; this.column=column; 
    }
    public String toString() {
      return "(" + line + "," + column + ") " + code + " " + lexeme;
    }
  }

	static FileInputStream reader = null; //F IT LETS MAKE IT GLOBAL
	static PushbackInputStream pb = null;
																	//input.unread(data);


  // The main method
  //
  public static void main(String [] args) {
	int tknCnt = 0;
    try {
      if (args.length == 1) {
        Token tkn;
        reader = new FileInputStream(args[0]);
				pb = new PushbackInputStream(reader,4);
        while ((tkn = nextToken()) != null) { 
      		System.out.print("(" + tkn.line + "," + tkn.column + ")\t");
					switch(tkn.code) {
					case ID:     
						if(tkn.lexeme == "false" |
						tkn.lexeme == "class" |
						tkn.lexeme == "extends" |
						tkn.lexeme == "static" |
						tkn.lexeme == "public" |
						tkn.lexeme == "void" |
						tkn.lexeme == "main" |
						tkn.lexeme == "int" |
						tkn.lexeme == "double" |
						tkn.lexeme == "String" |
						tkn.lexeme == "boolean" |
						tkn.lexeme == "new" |
						tkn.lexeme == "this" |
						tkn.lexeme == "if" |
						tkn.lexeme == "else" |
						tkn.lexeme == "System" |
						tkn.lexeme == "out" |
						tkn.lexeme == "println" |
						tkn.lexeme == "while" |
						tkn.lexeme == "return" |
						tkn.lexeme == "true" ){
							System.out.println(tkn.lexeme + ")");
						}

						System.out.println("ID(" + tkn.lexeme + ")"); 
						tknCnt++;
						break;
					case INTLIT: 
						tknCnt++;
						if(tkn.lexeme.charAt(0) == '@'){
							String tmp = tkn.lexeme.replace("@", "");
							//octal
							int litint = Integer.parseInt(tmp, 8);
							System.out.println("INTLIT(" + litint + ")");
							break;
						}
						else if(tkn.lexeme.charAt(0) == 'x'){
							//hex
							String tmp = tkn.lexeme.replace("x","");
							int litint = Integer.parseInt(tmp, 16);
							System.out.println("INTLIT(" + litint + ")");
							break;
						}
						else{
							int litint = Integer.parseInt(tkn.lexeme);
							System.out.println("INTLIT(" + litint + ")");
							//TODO ERROR checking
						}
						break;

						// ... code needed ...

					case DBLLIT: 
						tknCnt++;
						String tmp = tkn.lexeme;
						if(tkn.lexeme.charAt(0) == '.'){
							//.123
							tmp = '0' + tkn.lexeme;
							tkn.lexeme = tmp;
						}
						double dbl = Double.parseDouble(tmp);
						System.out.println("DBLLIT(" + dbl + ")");
						break;
						//TODO erorrr checking

					case STRLIT: 
						tknCnt++;
					//TODO HANDLE HEX OCTALS
						System.out.println("STRLIT(" + tkn.lexeme + ")");
						break;
/*
					case THIS:
						tknCnt++;
						System.out.println("THIS(" + tkn.lexeme + ")");
						break;

					case TRUE:
						tknCnt++;
						System.out.println("TRUE(" + tkn.lexeme + ")");
						break;

					case RETURN:
						tknCnt++;
						System.out.println("RETURN(" + tkn.lexeme + ")");
						break;

					case DOUBLE:
						tknCnt++;
						System.out.println("DOUBLE(" + tkn.lexeme + ")");
						break;

					case FALSE:
						tknCnt++;
						System.out.println("FALSE(" + tkn.lexeme + ")");
						break;

					case CLASS:
						tknCnt++;
						System.out.println("CLASS(" + tkn.lexeme + ")");
						break;

					case EXTENDS:
						tknCnt++;
						System.out.println("EXTENDS(" + tkn.lexeme + ")");
						break;

					case STATIC:
						tknCnt++;
						System.out.println("STATIC(" + tkn.lexeme + ")");
						break;


					case PUBLIC:
						tknCnt++;
						System.out.println("PUBLIC(" + tkn.lexeme + ")");
						break;


					case VOID:
						tknCnt++;
						System.out.println("VOID(" + tkn.lexeme + ")");
						break;


					case MAIN:
						tknCnt++;
						System.out.println("MAIN(" + tkn.lexeme + ")");
						break;


					case INT:
						tknCnt++;
						System.out.println("INT(" + tkn.lexeme + ")");
						break;
					
					case STRING:
						tknCnt++;
						System.out.println("STRING(" + tkn.lexeme + ")");
						break;

					case BOOLEAN:
						tknCnt++;
						System.out.println("BOOLEAN(" + tkn.lexeme + ")");
						break;

					case NEW:
						tknCnt++;
						System.out.println("NEW(" + tkn.lexeme + ")");
						break;

					case IF:
						System.out.println("IF(" + tkn.lexeme + ")");
						tknCnt++;
						break;

					case ELSE:
						tknCnt++;
						System.out.println("ELSE(" + tkn.lexeme + ")");
						break;

					case SYSTEM:
						tknCnt++;
						System.out.println("SYSTEM(" + tkn.lexeme + ")");
						break;

					case OUT:
						tknCnt++;
						System.out.println("OUT(" + tkn.lexeme + ")");
						break;

					case PRINTLN:
						tknCnt++;
						System.out.println("PRINTLN(" + tkn.lexeme + ")");
						break;

					case WHILE:
						tknCnt++;
						System.out.println("WHILE(" + tkn.lexeme + ")");
						break;

					case EQ:
						tknCnt++;
						System.out.println("EQ(" + tkn.lexeme + ")");
						break;

					case NEQ:
						tknCnt++;
						System.out.println("NEQ(" + tkn.lexeme + ")");
						break;

					case LE:
						System.out.println("LE(" + tkn.lexeme + ")");
						tknCnt++;
						break;

					case GE:
						tknCnt++;
						System.out.println("GE(" + tkn.lexeme + ")");
						break;

					case AND:
						tknCnt++;
						System.out.println("AND(" + tkn.lexeme + ")");
						break;

					case OR:
						tknCnt++;
						System.out.println("OR(" + tkn.lexeme + ")");
						break;
*/
					default:     
						tknCnt++;
						System.out.println(tkn.lexeme);
					} 

				} //endwhile
        System.out.println("Total: " + tknCnt + " tokens");
        reader.close();
      } else {
        System.err.println("Need a file name as command-line argument.");
      } 
    } catch (LexError e) {
      System.err.println(e);
    } catch (Exception e) {
      System.err.println(e);
    }
  }

	//get next char of file
 	static int nextChar() throws Exception {
		int c = 0;
		if(isPushback){
			c = pb.read();
			if(c == '\n'){
				currentLine++;
				currentColumn = 0;
			}
			/*else{
				currentColumn++;
			}*/
			isPushback = false;
			return c;
		}

		c = reader.read();
		if (c == '\n') {
			currentLine++;
			currentColumn = 0;
		} 
		else {
			currentColumn++;
		}    
		return c;
	}

  // Return the next token
  public static Token nextToken() throws Exception {
		int c = nextChar();
		StringBuilder buffer = new StringBuilder();
		Token returned = null;
		for(;;){
			beginColumn = currentColumn;
			beginLine = currentLine;
			switch(c){
			
			case -1:
				return null;

			case ' ':
			case '\t':
			case '\n':
			case '\r':
				c = nextChar();
			  continue;

//			case '0':
//				continue;

			case '+':
			case '-':
			case '*':
			case '!':
			case '<':
			case '>':
			case '=':
			case ';':
			case ',':
//			case '.':
			case '(':
			case ')':
			case '[':
			case ']':
			case '{':
			case '}':
			case ':':
			case '\'':
			case '&':

				buffer.setLength(0);
				buffer.append((char)c);
				if(c == '&'){
					c = nextChar();
					if(c == '&'){
						buffer.append((char)c);
					}
					else{
						pb.unread(c);
						isPushback=true;
						String tmp = "" + buffer.toString().charAt(0);
						returned = new Token(200, tmp, beginLine, beginColumn);
						return returned;
					}
				}
				else if(c == '='){
					c = nextChar();
					if(c == '='){
						buffer.append((char)c);
					}
					else{
						pb.unread(c);
						isPushback=true;
						String tmp = "" + buffer.toString().charAt(0);
						returned = new Token(200, tmp, beginLine, beginColumn);
						return returned;
					}
				}
				else if(c == '!'){
					c = nextChar();
					if(c == '='){
						buffer.append((char)c);
					}
					else{
						pb.unread(c);
						isPushback=true;
						String tmp = "" + buffer.toString().charAt(0);
						returned = new Token(200, tmp, beginLine, beginColumn);
						return returned;
					}
				}

				else if(c == '<'){
					c = nextChar();
					if(c == '='){
						buffer.append((char)c);
					}
					else{
						pb.unread(c);
						isPushback=true;
						String tmp = "" + buffer.toString().charAt(0);
						returned = new Token(200, tmp, beginLine, beginColumn);
						return returned;
					}
				}

				else if(c == '>'){
					c = nextChar();
					if(c == '='){
						buffer.append((char)c);
					}
					else{
						pb.unread(c);
						isPushback=true;
						String tmp = "" + buffer.toString().charAt(0);
						returned = new Token(200, tmp, beginLine, beginColumn);
						return returned;
					}
				}


				String temp = buffer.toString();
				returned = new Token(200, temp, beginLine, beginColumn);
				c = nextChar();
				return returned;


			//TAKLING INERTGERSSSSS????????////////
			/*
			– a decimal constant consists of a non-empty sequence of digits, with a non-zero digit at the begin-
			ning;
			– an octal constant consists of a digit 0 followed by a non-empty sequence of digits 0-7;
			– a hexadecimal constant consists of 0x or 0X followed by a non-empty sequence of hexadecimal digits, i.e. digits plus letters a through f (both upper and lower cases allowed).
			*/

			/*// floating-point literals
//
.123 456.

0.

123.456

*/
			case '.':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			///TODO could be int or floating point A floating-point literal contains a non-empty sequence of digits and a decimal point. The decimal point may appear anywhere in the sequence, e.g. .123, 12.3, and 123. are all valid floating-point literals.
				buffer.setLength(0);
				boolean fp = false;
				boolean valid = false;
				if(c == '.'){
					//first char is a ., so we are expecting .12321412412
					buffer.append((char)c);
					c = nextChar();
					fp = true;
				}
				while(c == '.' | isDigit(c)){
					valid = true;
					if(c == '.'){
						fp = true;
					}
					buffer.append((char)c);
					c = nextChar();

				}
				//System.out.println(buffer.toString().charAt(-1));
				String per = ".";
				/*if((buffer.toString().substring(buffer.toString().length() - 1)) == per){
					buffer.append('0');
				}*/
				CharSequence cs1 = ".";
				if(buffer.toString().length() > 1 && !buffer.toString().contains(cs1)){
					//456 -> 456.0
					c = nextChar();
					if(c == '.'){
						buffer.append('.');
						buffer.append('0');
						fp = true;
					}
					pb.unread(c);
					isPushback = true;
				}

				if(fp && valid){
					temp = buffer.toString();
					returned = new Token(DBLLIT, temp, beginLine, beginColumn);
					return returned;
				}

				else if(valid){
					temp = buffer.toString();
					returned = new Token(INTLIT, temp, beginLine, beginColumn);
					return returned;
				}
				else{
					//invalid floating point
					temp = buffer.toString();
					returned = new Token((int)temp.charAt(0), temp, beginLine, beginColumn);
					return returned;
				}

			
			case '0':
				//can either be octal or hex
				buffer.setLength(0);
				//buffer.append((char)c);

				c = nextChar();

				if(c == 'x' | c == 'X'){
					//is octal form ish
					buffer.append('x');
					c = nextChar();
					while(isHex(c)){
						buffer.append((char)c);
						c = nextChar();
					}
					pb.unread(c);
					isPushback = true;
					temp = buffer.toString();
					returned = new Token(INTLIT, temp, beginLine, beginColumn);
					return returned;
				}

				else if(isOctal(c)){
					buffer.append('@'); //signal for octal
					buffer.append('0');
					while(isOctal(c)){
						buffer.append((char)c);
						c = nextChar();
					}
					if(c != '\n'){
						pb.unread(c);
						isPushback = true;
					}
					temp = buffer.toString();
					returned = new Token(INTLIT, temp, beginLine, beginColumn);
					return returned;

				}
				else if(c == '\n' | c == '\r' | c == -1){
					//octal was solo 0
					returned = new Token(INTLIT, "0", beginLine, beginColumn);
					return returned;
				}
				else if(c == '.'){
					buffer.append('0');
					buffer.append((char)c);
					c = nextChar();
					while(isDigit(c)){
						buffer.append((char)c);
						c = nextChar();
					}
//					pb.unread(c);
//					isPushback=true;
					temp = buffer.toString();
					returned = new Token(DBLLIT, temp, beginLine, beginColumn);
					return returned;
				}

				else{
					temp = buffer.toString();
					System.out.println(temp);
					throw new LexError(currentLine, currentColumn, "Improperly formed Octal or Hexadecimal number.");
				}


			//ARE YO READ FOR COMMEND TIMEEMEMEMMEMEMEME?!??!?!!??!?!??!!?///////////
			case '/':
				buffer.setLength(0);
				c = nextChar();
				if(c == '/'){
					//go until end of line
					while(c != '\n' && c != -1){
						c = nextChar();
					}
					//pb.unread(c);
					//isPushback = false;
					break;
				}

				else if(c == '*'){
					c = nextChar();
					while(c != -1 ){ //&& c != '/'){
						int f = nextChar();
						if(c == '*' && f == '/'){
							///complete string
							c = f;
							break;
						}
						c=f;
						buffer.append((char)c);
						//c = nextChar();
						//this embedded /* is treated as part of comments
						
					}
					//pb.unread(c);
					//isPushback = true;
					//c = nextChar();
					String tmp = buffer.toString();
					int len = tmp.length();
					if(tmp.charAt(len-1) == '*'){
						//we have a nice token
						break;
					}
					else{
						throw new LexError(currentLine, currentColumn, "Unterminated comment");
					}
				}
				else{
					returned = new Token(200, "/", beginLine,beginColumn);
					//c = nextChar();
					return returned;
				}


			//STRING LITERAL
			case '"':
				//have beginning of string literal
				buffer.setLength(0);
				buffer.append((char)c);
				c = nextChar();
				while(c != ('"')){
					if(c == -1){
						break;
					}
					if(c == '\r' | c == '\n'){
						//cant have tabs, returns or newlines in strpings
						break;
					}
					buffer.append((char) c);
					c = nextChar();
				}
//				pb.unread(c);
//				isPushback = true;
				if(c == -1){
					//EOF with unterminated string
					//  109         throw new LexError(currLine, currColumn, "Illegal char: " + (char)c);
					throw new LexError(currentLine, currentColumn, "Unterminated string: " + (char)c);
				}
				else if(c == '"'){
					buffer.append('"');	
					lexeme = buffer.toString();
					returned = new Token(STRLIT, lexeme, beginLine, beginColumn);
					return returned;
				}
				else{
					//had a return or newline
					throw new LexError(currentLine, currentColumn, "String cannot span multiple lines: " + (char)c);
				}

//////--------CASE T------------/////////
			case 't':
				boolean isID = true;
				//this, true, int thing
				buffer.setLength(0);
				buffer.append((char)c);
				
				c = nextChar();
				outerif:
				if(c == 'h'){
					isID = false;
					buffer.append((char)c);
					//this
					c = nextChar();
					if(c == 'i'){
						buffer.append((char)c);
						c = nextChar();
						if(c == 's'){
							buffer.append((char)c);
							//c = nextChar();
							//if(c == ' ' | c == '\n'){
								//full 'this'
								isID = false;
								lexeme = buffer.toString();
								returned = new Token(THIS, lexeme, beginLine, beginColumn);
								return returned;
							//}
							/*else{
								isID = true;
								break outerif;
							}*/
						}
						else { 
							isID = true;
							break outerif;
						}
					}
					else {
						isID = true;
						break outerif;
					}
				}


				else if(c == 'r'){
					buffer.setLength(0);
					buffer.append('t');
					buffer.append((char)c);
					isID = false;
					//true
					c = nextChar();
					if(c == 'u'){
						buffer.append((char)c);
						c = nextChar();
						if(c == 'e'){
							buffer.append((char)c);
		//					c = nextChar();
	//						if(c == ' ' | c == '\n'){ //TODO doesnt need space after 123class is two tokens,,,,,,,
								isID = false;	
				//				c = nextChar();
								lexeme = buffer.toString();
								returned = new Token(TRUE, lexeme, beginLine, beginColumn);
								return returned;
				//			}
			/*				else{ 
								isID = true;
								break;
							} */
						}
						else{
							isID = true;
							break;
						}
					}
					else{
						isID=true;
						break;
					}
				}

				else if(isID == true){
					//some other token
					//buffer.append((char)c);
					while(isLetter(c) | isDigit(c)){
				//	while((c != ' ' && c != '\n' && c != -1 && c != '\r')){
						buffer.append((char)c);
						c = nextChar();
					}
					pb.unread(c);
					isPushback = true;
					lexeme = buffer.toString();
					returned = new Token(ID, lexeme, beginLine, beginColumn);
					return returned;
				}

//////----end----CASE T------------/////////
		
			case 'r':
				//return
				String checkString = "return";
				int stringlen = checkString.length();
				isID = false;
				buffer.setLength(0);
				for(int i = 0; i < stringlen; i++){
					if(!(checkString.charAt(i) == (char)c)){
						while(isLetter(c)){
							buffer.append((char)c);
							c = nextChar();
						}
						pb.unread(c);
						isPushback = true;
						isID = true;
						break;
					}
					else{
						buffer.append((char)c);
						c = nextChar();
					}
				}

				if(isID){
					lexeme = buffer.toString();
					returned = new Token(ID, lexeme, beginLine, beginColumn);
					return returned;
				}
				else{
				//valid token
					lexeme = buffer.toString();
					returned = new Token(RETURN, lexeme, beginLine, beginColumn);
					return returned;
				}
			
			case 'd':
				//double
				checkString = "double";
				stringlen = checkString.length();
				isID = false;
				buffer.setLength(0);
				for(int i = 0; i < stringlen; i++){
					if(!(checkString.charAt(i) == (char)c)){
						while(isLetter(c)){
							buffer.append((char)c);
							c = nextChar();
						}
						pb.unread(c);
						isPushback = true;
						isID = true;
						break;
					}
					else{
						buffer.append((char)c);
						c = nextChar();
					}
				}

				if(isID){
					lexeme = buffer.toString();
					returned = new Token(ID, lexeme, beginLine, beginColumn);
					return returned;
				}
				else{
				//valid token
					lexeme = buffer.toString();
					returned = new Token(DOUBLE, lexeme, beginLine, beginColumn);
					return returned;
				}
							

			default:
//				String st = String.format("%s, %s, %s",c,beginColumn, beginLine);
//				System.out.println(st);
				buffer.setLength(0);

				if(isLetter(c)){
					//buffer.append((char)c);
					while(c != -1 && c != ' ' && c != '\n' && c!= '\r' && (isLetter(c) | isDigit(c))){
						buffer.append((char)c);
						c = nextChar();
					}
					lexeme = buffer.toString();
					returned = new Token(ID, lexeme, beginLine, beginColumn);
					//pb.unread(c);
					//isPushback = true;
					return returned;
					//c = nextChar();
				}
				else{
					c = nextChar();
				}
		}
  }}

  // Utility routines
  //

	// Return true if c is a letter.
  private static boolean isLetter(int c) {
    return (('A' <= c) && (c <= 'Z')
	    || ('a' <= c) && (c <= 'z'));
  }

  private static boolean isDigit(int c) {
    return ('0' <= c) && (c <= '9');
  }

  private static boolean isOctal(int c) {
    return ('0' <= c) && (c <= '7');
  }

  private static boolean isHex(int c) {
    return ('0' <= c) && (c <= '9')
            || ('A' <= c) && (c <= 'F')
	    || ('a' <= c) && (c <= 'f');
  }

	private static boolean isSpecial(int c){
		//\n\t\r 
		return (c == '\n') && (c == '\r') && (c == ' ') && (c == -1);
	}

} //end of class
