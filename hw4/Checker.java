/// This is supporting software for CS322 Compilers and Language Design II
// Copyright (c) Portland State University
// 
// Static analysis for miniJava (W15) ((A starter version.)
//  1. Type-checking
//  2. Detecting missing return statement
//  3. (Optional) Detecting uninitialized variables
//
//  BEN Reichert WI/2015 CS321
// (For CS321 Winter 2015 - Jingke Li)
//

import java.util.*;
import java.io.*;
import ast.*;

public class Checker {

  static class TypeException extends Exception {
    public TypeException(String msg) { super(msg); }
  }

  //------------------------------------------------------------------------------
  // ClassInfo
  //----------
  // For easy access to class hierarchies (i.e. finding parent's info).
  //
  static class ClassInfo {
    Ast.ClassDecl cdecl; 	// classDecl AST
    ClassInfo parent; 		// pointer to parent

    ClassInfo(Ast.ClassDecl cdecl, ClassInfo parent) { 
      this.cdecl = cdecl; 
      this.parent = parent; 
    }      

    // Return the name of this class 
    //
    String className() { return cdecl.nm; }

    // Given a method name, return the method's declaration
    // - if the method is not found in the current class, recursively
    //   search ancestor classes; return null if all fail
    //
    Ast.MethodDecl findMethodDecl(String mname) {
      for (Ast.MethodDecl mdecl: cdecl.mthds)
          if (mdecl.nm.equals(mname))
            return mdecl;
              if (parent != null)
                return parent.findMethodDecl(mname);
              return null;
    }

    // Given a field name, return the field's declaration
    // - if the field is not found in the current class, recursively
    //   search ancestor classes; return null if all fail
    //
    Ast.VarDecl findFieldDecl(String fname) {
      for (Ast.VarDecl fdecl: cdecl.flds) {
          if (fdecl.nm.equals(fname))
            return fdecl;
              }
              if (parent != null)
                return parent.findFieldDecl(fname);
              return null;
    }
  }

  //------------------------------------------------------------------------------
  // Global Variables
  // ----------------
  // For type-checking:
  // classEnv - an environment (a className-classInfo mapping) for class declarations
  // typeEnv - an environment (a var-type mapping) for a method's params and local vars
  // thisCInfo - points to the current class's ClassInfo
  // thisMDecl - points to the current method's MethodDecl
  //
  // For other analyses:
  // (Define as you need.)
  //
  private static HashMap<String, ClassInfo> classEnv = new HashMap<String, ClassInfo>();
  private static HashMap<String, Ast.Type> typeEnv = new HashMap<String, Ast.Type>();
  private static ClassInfo thisCInfo = null;
  private static Ast.MethodDecl thisMDecl = null;

  //------------------------------------------------------------------------------
  // Type Compatibility Routines
  // ---------------------------

  // Returns true if tsrc is assignable to tdst.
  //
  // Pseudo code:
  //   if tdst==tsrc or both are the same basic type 
  //     return true
  //   else if both are ArrayType // structure equivalence
  //     return assignable result on their element types
  //   else if both are ObjType   // name equivalence 
  //     if (their class names match, or
  //         tdst's class name matches an tsrc ancestor's class name)
  //       return true
  //   else
  //     return false
  //
  private static boolean assignable(Ast.Type tdst, Ast.Type tsrc) throws Exception {
    if (tdst == tsrc
	    || (tdst instanceof Ast.IntType && tsrc instanceof Ast.IntType)
	    || (tdst instanceof Ast.BoolType && tsrc instanceof Ast.BoolType)) {
      
      return true;
    }
    //more code here
    else if (tdst instanceof Ast.ArrayType && tsrc instanceof Ast.ArrayType){
      Ast.ArrayType at1 = (Ast.ArrayType)tdst; //TODO sasha says maybe casting to ast.arraytype works
      Ast.ArrayType at2 = (Ast.ArrayType)tsrc;
      return assignable(at1.et,at2.et); 
    }
    else if(tdst instanceof Ast.ObjType && tsrc instanceof Ast.ObjType){
      Ast.ObjType td = (Ast.ObjType)tdst;
      Ast.ObjType ts = (Ast.ObjType)tsrc;
      if(ts.nm == td.nm){
        return true;
      }
      if(classEnv.get(ts.nm) != null && classEnv.get(ts.nm).parent != null  && classEnv.get(ts.nm).parent.className() == td.nm){
        return true;
      }
      else
        return false;
     }  

    else
      return false;
  }
  
  // Returns true if t1 and t2 can be compared with "==" or "!=".
  //
  private static boolean comparable(Ast.Type t1, Ast.Type t2) throws Exception {
    return assignable(t1,t2) || assignable(t2,t1);
  }

  //------------------------------------------------------------------------------
  // The Main Routine
  //-----------------
  //
  public static void main(String [] args) throws Exception {
  //print stuffs
  /*
  while(classEnv.hasNext()){
    String key = classEnv.next();
    System.out.println("key: " + key + "value: " + map.get(key));
  }*/

    try {
      if (args.length == 1) {
        FileInputStream stream = new FileInputStream(args[0]);
        Ast.Program p = new astParser(stream).Program();
        stream.close();
        check(p); //THIS IS WHERE THE FUN BEGINS
      } else {
	System.out.println("Need a file name as command-line argument.");
      } 
    } catch (TypeException e) {
      System.err.print(e + "\n");
    } catch (Exception e) {
      System.err.print(e + "\n");
    }
  }

  //------------------------------------------------------------------------------
  // Checker Routines for Individual AST Nodes
  //------------------------------------------

  // Program ---
  //  ClassDecl[] classes;
  //
  // 1. Sort ClassDecls, so parent will be visited before children.
  // 2. For each ClassDecl, create an ClassInfo (with link to parent if exists),
  //    and add to classEnv.
  // 3. Actual type-checking traversal over ClassDecls.
  //
  static void check(Ast.Program n) throws Exception {
    Ast.ClassDecl[] classes = topoSort(n.classes);
    for (Ast.ClassDecl c: classes) {
      ClassInfo pcinfo = (c.pnm == null) ? null : classEnv.get(c.pnm);
      classEnv.put(c.nm, new ClassInfo(c, pcinfo));
    }
    for (Ast.ClassDecl c: classes)
      check(c);
  }

  // Utility routine
  // - Sort ClassDecls based on parent-chidren relationship.
  //
  private static Ast.ClassDecl[] topoSort(Ast.ClassDecl[] classes) {
    List<Ast.ClassDecl> cl = new ArrayList<Ast.ClassDecl>();
    Vector<String> done = new Vector<String>();
    int cnt = classes.length;
    while (cnt > 0) {
      for (Ast.ClassDecl cd: classes)
	if (!done.contains(cd.nm)
	    && ((cd.pnm == null) || done.contains(cd.pnm))) {
	  cl.add(cd);
	  done.add(cd.nm);
	  cnt--;
	} 
    }
    return cl.toArray(new Ast.ClassDecl[0]);
  }

  // ClassDecl ---
  //  String nm, pnm;
  //  VarDecl[] flds;
  //  MethodDecl[] mthds;
  //
  //  1. Set thisCInfo pointer to this class's ClassInfo, and reset
  //     typeEnv to empty.
  //  2. Recursively check n.flds and n.mthds.
  //
  static void check(Ast.ClassDecl n) throws Exception {
    thisCInfo = classEnv.get(n.nm);
    typeEnv.clear();
    for (Ast.MethodDecl m: n.mthds)
      check(m);
    for (Ast.VarDecl v: n.flds)
      check(v);
  }

  // MethodDecl ---
  //  Type t;
  //  String nm;
  //  Param[] params;
  //  VarDecl[] vars;
  //  Stmt[] stmts;
  //
  //  1. Set thisMDecl pointer and reset typeEnv to empty.
  //  2. Recursively check n.params, n.vars, and n.stmts.
  //  3. For each VarDecl, add a new name-type binding to typeEnv.
  //
  static void check(Ast.MethodDecl n) throws Exception { //TODO this might still be broken
    thisMDecl = thisCInfo.findMethodDecl(n.nm);
    typeEnv.clear();
    for (Ast.Param p: n.params){
      //Ast.Param somep = (p.t == null) ? null: p.t;
      typeEnv.put(p.nm,p.t);
      check(p);
     }

    for (Ast.VarDecl v: n.vars){
      //add name-type bidnign to typeEnv
//      check(v);
      /*
      ClassInfo pcinfo = (c.pnm == null) ? null : classEnv.get(c.pnm);
      classEnv.put(c.nm, new ClassInfo(c, pcinfo));
      */
      Ast.Type sometype = (v.t == null) ? null: v.t;

      //classEnv.put(c.nm, new ClassInfo(c, pcinfo));
      typeEnv.put(v.nm,v.t); //v needs to be of type Ast.Type
      check(v);
    }
    for (Ast.Stmt s: n.stmts)
      check(s);
  } 

  // Param ---
  //  Type t;
  //  String nm;
  //
  //  If n.t is ObjType, make sure its corresponding class exists.
  //
  static void check(Ast.Param n) throws Exception {
    if(n.t instanceof Ast.ObjType && !classEnv.containsKey(n.nm)){
      Ast.ObjType wrong = (Ast.ObjType)n.t;
      throw new TypeException("(In Param) Can't find class " + wrong.nm);
    }
    
  }

  // VarDecl ---
  //  Type t;
  //  String nm;
  //  Exp init;
  //
  //  1. If n.t is ObjType, make sure its corresponding class exists.
  //  2. If n.init exists, make sure it is assignable to the var.
  //
  static void check(Ast.VarDecl n) throws Exception {
  //main[1] print Checker.classEnv.containsKey(Checker.typeEnv.get(n.nm).nm)

  /*
    ClassDecl A 
      VarDecl IntType i 1 
      VarDecl (ObjType B) b ()
    ClassDecl B 
      VarDecl (ObjType A) a (NewObj A)
  */

    if(n.t instanceof Ast.ObjType){
      Ast.ObjType derp = (Ast.ObjType)n.t;
        //Ast.ObjType temp = (Ast.ObjType)typeEnv.get(n.nm); //TODO this is borked
/*
      else if(classEnv.containsKey(obj.nm)){
        ClassInfo ccc = classEnv.get(obj.nm); //might new to initalize object
        Ast.MethodDecl derp = ccc.findMethodDecl(n.nm);
        if(derp == null){
          throw new TypeException("method n.nm does not exist");
        }
        //now ehck count and types of arguments
        if(n.args.length != derp.params.length){
          throw new TypeException("Argument count does not match param count");
        }
        else{
          //arguments are of same length, need to make sure they are comparable
          for(int i = 0; i < n.args.length; i++){
            if(!(comparable(check(n.args[i]),derp.params[i].t))){
              throw new TypeException("args don't match");
            }
          }
         
        }

        */

        //derp.nm
        
        if(!(classEnv.containsKey(derp.nm))){
          throw new TypeException("(In VarDecl) Can't find class "+derp.nm);
        }
       }
        

 
/*
        Ast.ObjType temp = (Ast.ObjType)typeEnv.get(n.nm); //TODO this is borked
        if(!(classEnv.containsKey(temp.nm))){
        throw new TypeException("ObjType: " + n.nm + " doesn't have a corresponding class");
        */

    else if(n.init == null){
      return;
    }

    else if(n.init instanceof Ast.NewArray){
      Ast.NewArray derp = (Ast.NewArray)n.init;
      Ast.ArrayType derp2 = (Ast.ArrayType)n.t;
      if(!(assignable(derp.et,derp2.et))){
        throw new TypeException("Not of ArrayType");
      }
    }
    else if(n.init instanceof Ast.IntLit){
      if(!(n.t instanceof Ast.IntType)){
        throw new TypeException("Not of IntLit");
      }
    }
    else if(n.init instanceof Ast.BoolLit){
      if(!(n.t instanceof Ast.BoolType)){
        throw new TypeException("Not of BoolType");
      }
    }

  }

  // STATEMENTS

  // Dispatch a generic check call to a specific check routine
  // 
  static void check(Ast.Stmt n) throws Exception {
    if (n instanceof Ast.Block) 	check((Ast.Block) n);
    else if (n instanceof Ast.Assign)   check((Ast.Assign) n);
    else if (n instanceof Ast.CallStmt) check((Ast.CallStmt) n);
    else if (n instanceof Ast.If) 	check((Ast.If) n);
    else if (n instanceof Ast.While)    check((Ast.While) n);
    else if (n instanceof Ast.Print)    check((Ast.Print) n);
    else if (n instanceof Ast.Return)   check((Ast.Return) n);
    else
      throw new TypeException("Illegal Ast Stmt: " + n);
  }

  // Block ---
  //  Stmt[] stmts;
  //
  static void check(Ast.Block n) throws Exception {
    for (Ast.Stmt s: n.stmts)
      check(s);
  }

  // Assign ---
  //  Exp lhs;
  //  Exp rhs;
  //
  //  Make sure n.rhs is assignable to n.lhs.
  //
  static void check(Ast.Assign n) throws Exception {
    assignable(check(n.lhs),check(n.rhs));
  }

  // CallStmt ---
  //  Exp obj; 
  //  String nm;
  //  Exp[] args;
  //
  //  1. Check that n.obj is ObjType and the corresponding class exists.
  //  2. Check that the method n.nm exists.
  //  3. Check that the count and types of the actual arguments match those of
  //     the formal parameters.
  //
  static void check(Ast.CallStmt n) throws Exception {
    
    //2
    if(check(n.obj) instanceof Ast.ObjType){
      Ast.ObjType obj = (Ast.ObjType)check(n.obj);
      if(!classEnv.containsKey(obj.nm)){
      //class does not exist
        throw new TypeException("ObjType: " + n.nm + " doesn't have a corresponding class");
      }

      else if(classEnv.containsKey(obj.nm)){
        ClassInfo ccc = classEnv.get(obj.nm); //might new to initalize object
        Ast.MethodDecl derp = ccc.findMethodDecl(n.nm);
        if(derp == null){
          throw new TypeException("(In CallStmt) Can't find method " + n.nm);
        }
        //now ehck count and types of arguments
        if(n.args.length != derp.params.length){
          throw new TypeException("Argument count does not match param count");
        }
        else{
          //arguments are of same length, need to make sure they are comparable
          for(int i = 0; i < n.args.length; i++){
            if(!(comparable(check(n.args[i]),derp.params[i].t))){
              throw new TypeException("args don't match");
            }
          }
         
        }
        
      }//grab object, do findFieldDecl on object
    }

    else
      throw new TypeException("not of objtype");
    
    //check lengths of each array,
    //comparable(on each)
    //2
    //need to recursively check other classesA
    //check if objtype

    //3
    //loop through parameters and do check on each and compare to check on declaration
    /*for (Ast.Stmt s: n.stmts)
      check(s);
      */
  }

  // If ---
  //  Exp cond;
  //  Stmt s1, s2;
  //
  //  Make sure n.cond is boolean.
  //
  static void check(Ast.If n) throws Exception {
    if(!(check(n.cond) instanceof Ast.BoolType)){
      throw new TypeException("(In If) Cond exp type is not boolean: " + check(n.cond));
    }
    //check(n.s1);
    //check(n.s2);
  }

  // While ---
  //  Exp cond;
  //  Stmt s;
  //
  //  Make sure n.cond is boolean.
  //
  static void check(Ast.While n) throws Exception {
    if(!(check(n.cond) instanceof Ast.BoolType)){
      throw new TypeException("need a boolean " + n);
    } 
    check(n.cond);
    check(n.s);
  }
  
  // Print ---
  //  PrArg arg;
  //
  //  Make sure n.arg is integer, boolean, or string.
  //
  static void check(Ast.Print n) throws Exception {
    if(!(n.arg instanceof Ast.IntType || n.arg instanceof Ast.BoolType || n.arg instanceof Ast.StrLit || n.arg instanceof Ast.Exp || n.arg == null)){
    //also check arraytype, cast to exp for 
      throw new TypeException("need to be either a int, bool, or str" + n);
    }
  }

  // Return ---  
  //  Exp val;
  //
  //  If n.val exists, make sure it matches the expected return type.
  //
  static void check(Ast.Return n) throws Exception {
    if(n.val != null){
        //make sure it matches expected return type
        //if(!(comparable(check(n.val),thisMDecl.t))){
       
        if(!(comparable(check(n.val),thisMDecl.t))){ //cant just check if this is null, tis more complicated
          throw new TypeException("(In Return) Unexpected return value");
        }

    }
  }

  // EXPRESSIONS

  // Dispatch a generic check call to a specific check routine
  //
  static Ast.Type check(Ast.Exp n) throws Exception {
    if (n instanceof Ast.Binop)    return check((Ast.Binop) n);
    if (n instanceof Ast.Unop)     return check((Ast.Unop) n);
    if (n instanceof Ast.Call)     return check((Ast.Call) n);
    if (n instanceof Ast.NewArray) return check((Ast.NewArray) n);
    if (n instanceof Ast.ArrayElm) return check((Ast.ArrayElm) n);
    if (n instanceof Ast.NewObj)   return check((Ast.NewObj) n);
    if (n instanceof Ast.Field)    return check((Ast.Field) n);
    if (n instanceof Ast.Id)	   return check((Ast.Id) n);
    if (n instanceof Ast.This)     return check((Ast.This) n);
    if (n instanceof Ast.IntLit)   return check((Ast.IntLit) n);
    if (n instanceof Ast.BoolLit)  return check((Ast.BoolLit) n);
    throw new TypeException("Exp node not recognized: " + n);
  }

  // Binop ---
  //  BOP op;
  //  Exp e1,e2;
  //
  //  Make sure n.e1's and n.e2's types are legal with respect to n.op.
  //
  static Ast.Type check(Ast.Binop n) throws Exception {

  // + - * /
  //this or broked Binop * (Field This i)  2
  if((n.op == Ast.BOP.ADD ||
     n.op == Ast.BOP.SUB ||
     n.op == Ast.BOP.MUL ||
     n.op == Ast.BOP.DIV) && 
     check(n.e1) instanceof Ast.IntType && 
     check(n.e2) instanceof Ast.IntType){
      return Ast.IntType;
  }
  // && ||
  else if((n.op == Ast.BOP.AND ||
    n.op == Ast.BOP.OR) && 
    check(n.e1) instanceof Ast.BoolType &&
    check(n.e2) instanceof Ast.BoolType){
      return Ast.BoolType;
  }
  
  //        -> "==" | "!=" | "<" | "<=" | ">" | ">="
  //boolean
  else if((n.op == Ast.BOP.EQ
  || n.op == Ast.BOP.NE)
  && check(n.e1) instanceof Ast.BoolType
  && check(n.e2) instanceof Ast.BoolType){
    return Ast.BoolType;
  }


  //intlit
  else if((n.op == Ast.BOP.EQ
  || n.op == Ast.BOP.NE
  || n.op == Ast.BOP.LT
  || n.op == Ast.BOP.LE
  || n.op == Ast.BOP.GT
  || n.op == Ast.BOP.GE)
  && check(n.e1) instanceof Ast.IntType
  && check(n.e2) instanceof Ast.IntType
  ){
    return Ast.BoolType;
  }
  //arraytype
  else if((n.op == Ast.BOP.EQ
  || n.op == Ast.BOP.NE)
  && n.e1 == n.e2 
  && check(n.e2) instanceof Ast.ArrayType
  && check(n.e1) instanceof Ast.ArrayType
  ){
    return check(n.e1);
  }

  //objtype
  else if((n.op == Ast.BOP.EQ //TODO WHY NO FINISH BINOP
  || n.op == Ast.BOP.NE)
  && n.e1 == n.e2
  && check(n.e1) instanceof Ast.ObjType
  && check(n.e2) instanceof Ast.ObjType
  ){
    return check(n.e1);
  }

  else
    throw new TypeException("(In Binop) Operand types don't match: " + check(n.e1) + " " + n.op + " " + check(n.e2));


  //Operators == and != also work on pairs of boolean operands, or pairs of array or object operands of the same type; in both cases, they test “pointer” equality (that is, whether two arrays or objects are the same instance, not whether they have the same contents).




  }
   
  // Unop ---
  //  UOP op;
  //  Exp e;
  //
  //  Make sure n.e's type is legal with respect to n.op.
  //
  static Ast.Type check(Ast.Unop n) throws Exception {
    if(check(n.e) == Ast.BoolType 
    && n.op == Ast.UOP.NEG){
      return Ast.BoolType;
    }
    if(check(n.e) == Ast.IntType
    && n.op == Ast.UOP.NEG){
      return Ast.IntType;
    }
    if(check(n.e) instanceof Ast.BoolType && n.op == Ast.UOP.NOT){
      return Ast.BoolType;
    }
    else{
      throw new TypeException("(In Unop) Bad operand type: " + n.op + " " + check(n.e));
    }
   //UnOp       -> "-" | "!"
  }
  
  // Call ---
  //  Exp obj; 
  //  String nm;
  //  Exp[] args;
  //
  //  (See the hints in CallStmt.) 
  //  In addition, this routine needs to return the method's return type.
  //  
  static Ast.Type check(Ast.Call n) throws Exception {
    check(n.obj);
    for (Ast.Exp v: n.args)
      check(v);

   return thisMDecl.t;

  }

  // NewArray ---
  //  Type et;
  //  int len;
  //
  //  1. Verify that n.et is either integer or boolean.
  //  2. Varify that n.len is non-negative. 
  //  (Note: While the AST representation allows these cases to happen, our 
  //  miniJava parser does not, so these checks are not very meaningful.)
  //
  static Ast.Type check(Ast.NewArray n) throws Exception {
    if(!(n.et instanceof Ast.IntType || n.et instanceof Ast.BoolType)){
      throw new TypeException("needs to be in or boolean");
    }
    if(n.len < 0){
      throw new TypeException("need len of more than 0");      
    }
    else{
      return new Ast.ArrayType(n.et);
    }
  }

  // ArrayElm ---
  //  Exp ar, idx;
  //
  //  Varify that n.ar is array and n.idx is integer.
  //
  static Ast.Type check(Ast.ArrayElm n) throws Exception {
    if(!(check(n.ar) instanceof Ast.ArrayType  && check(n.idx) instanceof Ast.IntType)){
      throw new TypeException("(In ArrayElm) Object is not array: " + check(n.idx));
    }
    if(!(check(n.idx) instanceof Ast.IntType)){
      throw new TypeException("(In ArrayElm) Index is not integer: "+ check(n.idx));
    }
    return (Ast.IntType)check(n.idx); //return arraytype of ints new int type

  }

  // NewObj ---
  //  String nm;
  //
  //  Verify that the corresponding class exists.
  //
  static Ast.Type check(Ast.NewObj n) throws Exception {
    if(!(classEnv.containsKey(n.nm))){
      throw new TypeException("(In NewObj) Can't find class " + n.nm);
    }
    else
      return new Ast.ObjType(n.nm);

  }
  
  // Field ---
  //  Exp obj; 
  //  String nm;
  //
  //  1. Verify that n.onj is ObjType, and its corresponding class exists.
  //  2. Verify that n.nm is a valid field in the object.
  //
  static Ast.Type check(Ast.Field n) throws Exception {
    if(check(n.obj) instanceof Ast.ObjType){
      Ast.ObjType obj = (Ast.ObjType)check(n.obj);
      if(!classEnv.containsKey(obj.nm)){
      //class does not exist
        throw new TypeException("(In Field) Can't find class "+obj.nm);
      }
      ClassInfo ci = classEnv.get(obj.nm);
      if(ci.findFieldDecl(n.nm) == null){
        throw new TypeException("field doesnt exist");
      }
      return ci.findFieldDecl(n.nm).t;
    }
    else if(!typeEnv.containsKey(n.nm)){
      throw new TypeException("Cant have null nm in type");
    }
   throw new TypeException("not an objtype, what are you doing?");
     
  }
  
  // Id ---
  //  String nm;
  //
  //  1. Check if n.nm is in typeEnv. If so, the Id is a param or a local var.
  //     Obtain and return its type.
  //  2. Otherwise, the Id is a field variable. Find and return its type (through
  //     the current ClassInfo).
  //
  static Ast.Type check(Ast.Id n) throws Exception {
    if(typeEnv.containsKey(n.nm)){
      return typeEnv.get(n.nm);
    }
    else if(thisCInfo.findFieldDecl(n.nm) != null){
      //is a field variable
      //search in all class declarations for i
      //Return (Binop + i j)
      return thisCInfo.findFieldDecl(n.nm).t; //this is broken
      //return typeEnv.get(thisCInfo.className()); //TODO this is wrong
    }
    else{
      throw new TypeException("(In Id) Can't find variable " + n.nm);
    }
  }

  // This ---
  //
  //  Find and return an ObjType that corresponds to the current class
  //  (through the current ClassInfo).
  //
  static Ast.Type check(Ast.This n) {
      return new Ast.ObjType(thisCInfo.className());
  }

  // Literals
  //
  public static Ast.Type check(Ast.IntLit n) { 
    return Ast.IntType; 
  }

  public static Ast.Type check(Ast.BoolLit n) { 
    return Ast.BoolType; 
  }

  public static void check(Ast.StrLit n) {
    // nothing to check or return
  }

}
