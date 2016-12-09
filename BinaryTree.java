import java.util.Arrays;
/*
 * Written and Coded by Agustin Ortiz
 * 
 * CSE 6341: Lisp Interpreter Project, Part 3
 * Due: Thursday September 29th at 23:59
 * Java Implementation of Lexical Analyzer & Parser & Interpreter
 * 
 * 
 * BINARY TREE CLASS
 * Handles all Interpreter tree logic and flow. Each S expression is a newly created class object or BinaryTree
 * where its evaluated and error checked.
 * */
import java.util.HashSet;

//
// PROJECT PART 2
//

public class BinaryTree {
		
		//Simple Binary Tree class for data structure including information about the data in the node
		BinaryTree left, right;
		boolean isList;
		String value;
		String type;
		String atomType;
		
		//Constructor
		public BinaryTree()
		{
			this.left = null;
			this.right = null;
			this.isList = true;
			this.value = null;
			this.type = null;
			this.atomType = null;
		}
		
		//Constructor II - Set type and value
		public BinaryTree(String types, String values)
		{
			this.left = null;
			this.right = null;
			this.isList = false;
			this.value = values;
			this.type = types;
		}

		//
		// PROJECT PART 3
		//
		
		//Take a newly created binary tree and fix the isList boolean for the printer
		
		public static void runTimeError(String message)
		{
			message = message.toUpperCase();
			System.out.println(message);
			System.exit(1);
		}
		
		public static void isList(BinaryTree node)
		{
			if(node != null)
				node.isList = true;
			
			if(node.left != null)
				isList(node.left);

			while(node.right != null)
			{
				node = node.right;
				if(node.left != null)
					isList(node.left);
			}
			
			node.isList = false;
			return;
		}
		
		//Check if the right most leaf node the value of NIL, not empty, and is an S-Expression
		public boolean isRightNil(BinaryTree node)
		{
			if(node.right == null && node.left == null)
			{
				if(node.value == "NIL")
					return true;
				else 
					return false;
			}
			else if(node.right != null)
				return isRightNil(node.right);
		
			return false;
		}
		
		//Gets the length of the tree. Counts all nodes on the right side of the tree
		public int getLength(BinaryTree node)
		{
			int length = 0;
			//Traverse the tree
			while(node != null)
			{
				if(node.left != null)
					length++;
				node = node.right;
			}
			
			return length;
		}
		
		//Checks if node is an atom and not null
		public boolean isAtom(BinaryTree node)
		{
			if(node.type == "Atom" && node != null)
				return true;
			else 
				return false;
		}
		
		//Checks if node is equal to the Atom of "NIL"
		public boolean isNull(BinaryTree node)
		{
			boolean isNull = false;
			if(isAtom(node) && node.value.equals("NIL")){
				isNull = true;
			}
			
			return isNull;
		}
		
		//Check if the node is an atom and is of type integer in Java
		public boolean isInt(BinaryTree node)
		{
			boolean isInt = false;
			int value = 0;
			try 
			{
				if(node.type == "Atom")
				{
					value = Integer.parseInt(node.value);
					isInt = true;
				}
		
			} 
			catch(Exception e) 
			{ 
				return isInt;
			}
			
			return isInt;
		}
		
		/*Built-in Lisp functions
		Before defining the specifics of the interpreter, let us define several mathematical functions that take as
		input one or more S-expressions and produce as result an S-expression. Specifically, consider functions
		car, cdr, cons, atom, int, null, eq, plus, minus, times, less, and greater, defined as follows:*/
		
		//car : S-expression → S-expression : given a binary tree, produces the left subtree. The function is undefined
		//if the input binary tree contains only one node.
		public BinaryTree car(BinaryTree node)
		{
			BinaryTree root = node;
				
			if(node != null)
			{
				if(node.type == "Atom")
					runTimeError("ERROR: 'car' function error on atom, can not perform CAR on atom");
				else
				{
					if(node.right != null)
						root = node.left;
					else
						runTimeError("ERROR: unexpected error in 'car' function");
				}
			}
			else
				runTimeError("ERROR: undefined arguments in 'car' function");

			return root;
		}
		
		//cdr : S-expression → S-expression : given a binary tree, produces the right subtree. The function is undefined
		//if the input binary tree contains only one node.
		public BinaryTree cdr(BinaryTree node)
		{
			BinaryTree root = node;
			
			if(node != null)
			{
				if(node.type == "Atom")
					runTimeError("ERROR: 'car' function error on atom");
				else
				{
					if(node.right != null)
						root = node.right;
					else
						runTimeError("ERROR: unexpected error in 'car' function");
				}
			}
			
			return root;
		}
		//cons : S-expression × S-expression → S-expression : here × denotes the Cartesian product of two sets. Given
		//a pair of input binary trees s1 and s2, cons produces a binary tree whose left subtree is s1 and right subtree
		//is s2.
		public BinaryTree cons(BinaryTree left, BinaryTree right)
		{
			BinaryTree root = new BinaryTree();
			
			if(left == null && right == null)
				runTimeError("ERROR: undefined arguments for 'cons' function");
			else if (left == null)
				root = right;
			else if (right == null)
				root = left;
			else 
			{
				root.left = left;
				root.right = right;
			}
			
			return root;
		}
		
		//Lisp function PLUS
		public BinaryTree plus(BinaryTree left, BinaryTree right)
		{
			BinaryTree root = new BinaryTree();
			
			if(isAtom(right) && isAtom(left))
			{
				if(isInt(right) && isInt(left))
				{
					int rightInt = Integer.parseInt(right.value);
					int leftInt = Integer.parseInt(left.value);
					
					int exp = rightInt + leftInt;
					
					root.type = "Atom";
					root.value = Integer.toString(exp);
				}
				else
					runTimeError("ERROR: Unexpcted litteral token for 'plus' function");
			}
			else
				runTimeError("ERROR: Unexpcted token for 'plus' function");
			
			return root;
		}
		
		//Lisp function MINUS
		public BinaryTree minus(BinaryTree left, BinaryTree right)
		{
			BinaryTree root = new BinaryTree();
			
			if(isAtom(right) && isAtom(left))
			{
				if(isInt(right) && isInt(left))
				{
					int rightInt = Integer.parseInt(right.value);
					int leftInt = Integer.parseInt(left.value);
					
					int exp = leftInt - rightInt;
					
					root.type = "Atom";
					root.value = Integer.toString(exp);
				}
				else
					runTimeError("ERROR: Unexpcted litteral token for 'minus' function");
			}
			else
				runTimeError("ERROR: Unexpcted token for 'minus' function");

			return root;
		}
		
		//Lisp function TIMES
		public BinaryTree times(BinaryTree left, BinaryTree right)
		{
			BinaryTree root = new BinaryTree();
			
			if(isAtom(right) && isAtom(left))
			{
				if(isInt(right) && isInt(left))
				{
					int rightInt = Integer.parseInt(right.value);
					int leftInt = Integer.parseInt(left.value);
					
					int exp = leftInt * rightInt;
					
					root.type = "Atom";
					root.value = Integer.toString(exp);
				}
				else
					runTimeError("ERROR: Unexpected litteral token for 'times' function");
			}
			else
				runTimeError("ERROR: Unexpected token for 'times' function");
			
			return root;
		}
		
		//Lisp function EQ
		public boolean eq(BinaryTree left, BinaryTree right)
		{
			boolean isEQ = false;
			
			if(isAtom(left) && isAtom(right))
			{
				if(isInt(left) && isInt(right))
				{
					if(Integer.parseInt(left.value) == Integer.parseInt(right.value))
						isEQ = true;
				}
				else if(left.value.equals(right.value))
						isEQ = true;
			}
			else
				runTimeError("ERROR: Unexpected token for 'eq' function");

			return isEQ;
		}
		
		//Lisp function LESS
		public boolean less(BinaryTree left, BinaryTree right)
		{
			boolean isLESS = false;
			
			if(isAtom(left) && isAtom(right))
			{
				if(isInt(left) && isInt(right))
				{
					if(Integer.parseInt(left.value) < Integer.parseInt(right.value))
						isLESS = true;
				}
				else
					runTimeError("ERROR: Unexpected litteral for 'less' function");
			}
			else
				runTimeError("ERROR: Unexpected token for 'less' function");
			
			return isLESS;
		}
		
		//Lisp function GREATER
		public boolean greater(BinaryTree left, BinaryTree right)
		{
			boolean isGREATER = false;
			
			if(isAtom(left) && isAtom(right))
			{
				if(isInt(left) && isInt(right))
				{
					if(Integer.parseInt(left.value) > Integer.parseInt(right.value))
						isGREATER = true;
				}
				else
					runTimeError("ERROR: Unexpected litteral for 'greater' function");
			}
			else
				runTimeError("ERROR: Unexpected token for 'greater' function");

			return isGREATER;
		}
		
		// Evaluation
		public BinaryTree eval(BinaryTree node, BinaryTree aList, BinaryTree dList)
		{
			BinaryTree root = null;
			
			if(isAtom(node))
			{
				if(node.value.equals("T") || isNull(node) || isInt(node))
				{
					if(isNull(node))
						node = new BinaryTree("Atom", "NIL");
					root = node;
				}
				else if(bound(node, aList))
					root = getVal(node, aList);
				else
					runTimeError("ERROR: bound opperation failed on variable");
			}
			else
			{
				int length = getLength(cdr(node));
				
				//Check for null pointer
				if(car(node).value == null)
					runTimeError("ERROR: evaluation returned null pointer to value");
				if(car(node).value.equals("DEFUN"))
				{
					if(length != 3)
						runTimeError("ERROR: incorrect function definition");
					if(typeCheckFunction(cdr(node)))
					{
						DefunList.setdList(cons(cons(car(cdr(node)), cons(car(cdr(cdr(node))), car(cdr(cdr(cdr(node)))) ) ), DefunList.getdList() ));
						root = car(cdr(node));
					}
					
					
					
				}

				else if(car(node).value.equals("QUOTE"))
				{
					if(length != 1)
						runTimeError("ERROR: 1 parameter expected for quote opperator in 'eval' function");
					
					root = car(cdr(node));
				}
				else if(car(node).value.equals("COND"))
				{
					try{
					root = evcon(cdr(node), aList, dList); }
					catch(Exception e)
					{
						runTimeError("ERROR: Uknown error in 'cond' function");
					}
				}
				else
					root = apply(car(node), evlist(cdr(node), aList, dList), aList, dList);
			}
			
			return root;
		}
		
		public boolean typeCheckFunction(BinaryTree node)
		{
			//check if function name is a numeric atom
			if(isAtom(car(node)))
			{
				if(isInt(car(node)))
					runTimeError("ERROR: Function definition can not be numeric");
			
			//Check if the function name is a predefined function
				if(isDefinedCommand(car(node)))
					runTimeError("ERROR: Function definition can not be a predefined function");
				if((car(node).value.equals("DEFUN")))
					runTimeError("ERROR: Function definition called twice");
				
				//Check Paramerters
				BinaryTree params = car(cdr(node));
				BinaryTree tempParams = params;
				
				HashSet<String> paramsList = new HashSet<String>();
				
				while(!isNull(tempParams))
				{
					BinaryTree parameter = car(tempParams);
					if(isAtom(parameter))
					{
						if(isInt(parameter))
							runTimeError("ERROR: paramter name can not be numeric");
						if(parameter.value.equals("T") || parameter.value.equals("NIL"))
							runTimeError("ERROR: paramter name can not be T OR NIL");
						
						paramsList.add(parameter.value);
					}
					else
						runTimeError("ERROR: paramter name can not be a list");
					
					tempParams = cdr(tempParams);
				}
			}
			else
				runTimeError("ERROR: Function name can not be a list");
			return true;
			
		}
		
		
		// Evaluation of COND
		public BinaryTree evcon(BinaryTree node, BinaryTree aList, BinaryTree dList)
		{
			BinaryTree cond = eval(car(car(node)), aList, dList);
			if(getLength(cdr(car(node))) != 1)
				runTimeError("ERROR: Invalid lists in Cond function");
			if(isNull(node) )
				runTimeError("ERROR: invalid null expression 'cond' function");
			else if(cond.value == null)
				return evcon(cdr(node), aList, dList);
			else if(!eval(car(car(node)), aList, dList).value.equals("NIL"))
				return eval(car(cdr(car(node))), aList, dList);
			else
				return evcon(cdr(node), aList, dList);
			return null;
		}
		
		// Evaluation List
		public BinaryTree evlist(BinaryTree node, BinaryTree aList, BinaryTree dList)
		{
			if(isNull(node))
				return new BinaryTree("Atom", "NIL");
			else
				return cons(eval(car(node), aList, dList), evlist(cdr(node), aList, dList));
		}
		
		//Checks if command is a previously defined function in LISP, otherwise returns false and throws error
		public boolean isDefinedCommand(BinaryTree node)
		{
			String[] evlist =  {"DEFUN", "PLUS", "MINUS", "TIMES", "LESS", "GREATER", "EQ", "ATOM", "INT", "NULL", "CAR", "CDR", "CONS", "QUOTE", "COND"};
			
			if(Arrays.asList(evlist).contains(node.value))
				return true;
			else
				return false;
		}
		
		//Apply 
		public BinaryTree apply(BinaryTree opp, BinaryTree params, BinaryTree aList, BinaryTree dList)
		{

			int length = 0;
			length = getLength(params);
			
			if(opp.value.equals("ATOM"))
			{
				if(length != 1)
					runTimeError("ERROR: 1 paramater expected for ATOM opperator in function 'apply'");

				if(isAtom(car(params)))
					return new BinaryTree("Atom", "T");
				else
					return new BinaryTree("Atom", "NIL");
				
			}
			else if(opp.value.equals("NULL"))
			{
				if(length != 1)
					runTimeError("ERROR: 1 paramater expected for NULL opperator in function 'apply'");
				
				if(isNull(car(params)))
					return new BinaryTree("Atom", "T");
				else
					return new BinaryTree("Atom", "NIL");
				
			}
			else if(opp.value.equals("INT"))
			{
				if(length != 1)
					runTimeError("ERROR: 1 paramater expected for INT opperator in function 'apply'");

				if(isInt(car(params)))
					return new BinaryTree("Atom", "T");
				else
					return new BinaryTree("Atom", "NIL");	
			}
			else if(opp.value.equals("PLUS"))
			{
				length = getLength(params);
				if(length != 2)
					runTimeError("ERROR: 2 paramaters expected for PLUS opperator in function 'apply'");
		
				return plus(car(params), car(cdr(params)));
			}
			else if (opp.value.equals("MINUS"))
			{
				if(length != 2)
					runTimeError("ERROR: 2 paramaters expected for MINUS opperator in function 'apply'");
				
				return minus(car(params), car(cdr(params)));
			}
			else if (opp.value.equals("TIMES"))
			{
				if(length != 2)
					runTimeError("ERROR: 2 paramaters expected for TIMES opperator in function 'apply'");

				return times(car(params), car(cdr(params)));
			}
			else if(opp.value.equals("EQ"))
			{
				if(length != 2)
					runTimeError("ERROR: 2 paramaters expected for EQ opperator in function 'apply'");

				if(eq(car(params), car(cdr(params))))
					return new BinaryTree("Atom", "T");
				else
					return new BinaryTree("Atom", "NIL");
			}
			else if(opp.value.equals("LESS"))
			{
				if(length != 2)
					runTimeError("ERROR: 2 paramaters expected for LESS opperator in function 'apply'");

				if(less(car(params), car(cdr(params))))
					return new BinaryTree("Atom", "T");
				else
					return new BinaryTree("Atom", "NIL");
			}
			else if(opp.value.equals("GREATER"))
			{
				if(length != 2)
					runTimeError("ERROR: 2 paramaters expected for GREATER opperator in function 'apply'");
				
				if(greater(car(params), car(cdr(params))))
					return new BinaryTree("Atom", "T");
				else
					return new BinaryTree("Atom", "NIL");
			}
			else if(opp.value.equals("CAR"))
			{
				if(length != 1)
					runTimeError("ERROR: 1 paramater expected for CAR opperator in function 'apply'");

				return car(car(params));
			}
			else if(opp.value.equals("CDR"))
			{
				if(length != 1)
					runTimeError("ERROR: 1 paramater expected for CDR opperator in function 'apply'");

				return cdr(car(params));
			}
			else if(opp.value.equals("CONS"))
			{
				if(length != 2)
					runTimeError("ERROR: 2 paramaters expected for CONS opperator in function 'apply'");

				return cons(car(params), car(cdr(params)));
			}
			else
			{
				int numParams = getLength(car(getVal(opp, dList)));
				if(numParams != length)
					runTimeError("ERROR: Number of parameters do not match");
				return eval(cdr(getVal(opp, dList)), addPairs(car(getVal(opp, dList)), params, aList), dList);
			}	
		}
		
		//PROJECT 4 - USER DEFINED FUNCTIONS
		
		public boolean bound(BinaryTree node, BinaryTree aList)
		{
			boolean bounded = false;
			if(isAtom(node))
			{
				if(isNull(aList))
					return false;
				else if(eq(node, car(car(aList))))
					return true;
				else
					return bound(node, cdr(aList));
			}
			else
				runTimeError("ERROR: Bound opperation failed on list");
			
			return bounded;	
		}
		
		public BinaryTree getVal(BinaryTree node, BinaryTree aList)
		{
			if(isAtom(node))
			{
				if(eq(node, car(car(aList))))
					return cdr(car(aList));
				else
					return getVal(node, cdr(aList));
			}
			else
				runTimeError("ERROR: GETVAL operation failed on list");
			
			
			return null;
		}
		
		public BinaryTree addPairs(BinaryTree varList, BinaryTree valueList, BinaryTree oldList)
		{
			if(isNull(varList))
				return oldList;
			else
				return (cons(cons(car(varList), car(valueList)), addPairs(cdr(varList), cdr(valueList), oldList)));
		}
		
}


