import java.io.*;

import java.util.ArrayList;

import javax.xml.soap.Node;
import javax.xml.stream.events.NotationDeclaration;

/*
 * Written and Coded by Agustin Ortiz
 * 
 * CSE 6341: Lisp Interpreter Project, Part 3
 * Due: Thursday September 29th at 23:59
 * Java Implementation of Lexical Analyzer & Parser & Interpreter
 * 
 * 
 * INTERPRETER TREE CLASS
 * Consists of the Scanner, Parser and main function initiate the sequence.  The scanner passes
 * all tokens to the Express function for recursive descent parsing.  If no errors are thrown,
 * the binary tree is created from the BinaryTree class and uses its class function for interpretation.
 * */

public class Interpreter {

	//
	// PROJECT PART 1
	//

	public static Token current; // Current Token for Parser
	public static InputStream scan = System.in; // Input must be stdin
	public static BinaryTree exp; // Global Tree to pass to printer

	public static class Token {
		String type; // Five types of Tokens: Atom, OpenParenthesis,
						// ClosingParenthesis, ERROR, and EOF
		String atomType; // Atom Categories: LiteralAtom, NumericAtom.
		String value;
	}

	public static ArrayList<Token> tokenList = new ArrayList<Token>(); // List
																		// to
																		// track
																		// all
																		// tokens
																		// returned
																		// by
																		// getNextToken

	// Main printer for Project 1. Prints all statistics generated from the
	// Scanner
	public static void printer() {
		// Token Counts
		int op = 0;
		int cp = 0;
		int na = 0;
		int la = 0;
		int naTotal = 0;

		// Token Value Strings
		String naValues = "";
		String laValues = "";

		// Iterate over list and get statistics & concatenate value strings
		for (Token element : tokenList) {
			if (element.type == "OpenParenthesis") {
				op++;

			} else if (element.type == "ClosedParenthesis") {
				cp++;
			} else if (element.type == "Atom") {
				if (element.atomType == "LiteralAtom") {
					la++;
					laValues += ", " + element.value;
				} else if (element.atomType == "NumericAtom") {
					na++;
					naValues += ", " + element.value;
					naTotal += Integer.parseInt(element.value);
				}
			}
		}

		// Output must be stdout
		System.out.println("LITERAL ATOMS: " + la + laValues);
		System.out.println("NUMERIC ATOMS: " + na + ", " + naTotal);
		System.out.println("OPEN PARENTHESES: " + op);
		System.out.println("CLOSING PARENTHESES: " + cp);
	}

	// Prints error for invalid alphanumeric toekn
	public static void printError(Token errorToken) {
		// Output must be stdout
		System.out.println(errorToken.type + " Invalid token: " + errorToken.value);
		System.exit(1);
	}

	public static void runTimeError(String message) {
		message = message.toUpperCase();
		System.out.println(message);
		System.exit(1);
	}

	// Added function in Phase 3 to check if the parenthesis are equal on both
	// sides.
	public static boolean checkParenthesis() {
		int op = 0, cp = 0;

		for (Token element : tokenList) {
			if (element.type == "OpenParenthesis") {
				op++;

			}
			if (element.type == "ClosedParenthesis") {
				cp++;
			}
		}

		if (cp == op)
			return true;
		else
			return false;
	}

	// Helper function returns token
	public static Token getNextToken() {
		Token token = new Token(); // Create single token object
		try {
			int tempInput = 1;
			// Loop to read input, exit when there nothing available in the
			// stream
			while (scan.available() != 0) {
				tempInput = scan.read();

				// 1. If the input is empty, returns token EOF
				if ((char) tempInput == '\0') {
					token.type = "EOF";
					token.value = "\0";
					return token;
				}
				// 2. If the current character is a white space, consumes it and
				// any white spaces that follow it; if the input
				// is empty after this, returns EOF
				else if (tempInput == 10 || tempInput == 13 || tempInput == 32) {
					scan.mark(1);
					if (scan.available() == 0) {
						token.type = "EOF";
						token.value = "  ";
						return token;
					} else {
						int doubleSpace = scan.read();
						if (doubleSpace == 10 || doubleSpace == 13 || doubleSpace == 32) {
							// token.type = "EOF";
							// token.value = " ";
							// return token;
						} else {
							scan.reset();
						}
					}
				}
				// 3. If the current character is ‘(‘ it consumes it and returns
				// token OpenParenthesis
				else if ((char) tempInput == '(') {
					token.value = "(";
					token.type = "OpenParenthesis";
					return token;
				}
				// 4. If the current character is ‘)’ it consumes it and returns
				// token ClosingParenthesis
				else if ((char) tempInput == ')') {
					token.value = ")";
					token.type = "ClosedParenthesis";
					return token;
				}

				// 5. If the current character is alphabetic;
				else if ((char) tempInput >= 'A' && (char) tempInput <= 'Z') {
					String literalAtom = "";
					scan.mark(1);

					// Check if stream continues to be alphanumeric
					while (((char) tempInput >= '0' && (char) tempInput <= '9')
							|| ((char) tempInput >= 'A' && (char) tempInput <= 'Z')) {
						scan.mark(1);
						literalAtom += String.valueOf((char) tempInput);
						if (scan.available() > 0)
							tempInput = scan.read();
						else {
							tempInput = '\0';
						}
					}

					// Atom Token created
					scan.reset();
					token.type = "Atom";
					token.atomType = "LiteralAtom";
					token.value = literalAtom;
					return token;
				}

				// 6. If the current character is numeric;
				else if ((char) tempInput >= '0' && (char) tempInput <= '9') {
					String numeric = "";
					scan.mark(1);

					// Check if stream continues to be numeric
					while ((char) tempInput >= '0' && (char) tempInput <= '9') {
						scan.mark(1);
						numeric += String.valueOf((char) tempInput);
						if (scan.available() > 0)
							tempInput = scan.read();
						else {
							tempInput = '\0';
						}
					}

					// Check if digit is followed by a digit, error occurs.
					if ((char) tempInput >= 'A' && (char) tempInput <= 'Z') {
						// ERROR - continue to gather string until character is
						// not alpha-numeric
						while (((char) tempInput >= '0' && (char) tempInput <= '9')
								|| ((char) tempInput >= 'A' && (char) tempInput <= 'Z')) {
							scan.mark(1);
							numeric += String.valueOf((char) tempInput);
							if (scan.available() > 0)
								tempInput = scan.read();
						}

						// Error Token created.
						scan.reset();
						token.type = "ERROR";
						token.value = numeric;
						return token;
					}

					// Atom Token created
					scan.reset();
					token.type = "Atom";
					token.atomType = "NumericAtom";
					token.value = numeric;
					return token;
				}
			}
		} catch (Exception e) {

			// if any I/O error occurs
			e.printStackTrace();
		}

		// Stream has completed, return EOF token
		token.type = "EOF";
		return token;
	}

	//
	// PROJECT PART 2
	//

	// Gets the next token from the scanner
	public static void MoveToNext() {
		current = getNextToken();
		tokenList.add(current);
	}

	// Get's current token
	public static Token getCurrent() {
		return current;
	}

	// Starts the Scanner
	public static void init() {
		MoveToNext();
	}

	// This simple input language is easy to parse using the standard technique
	// of recursive-descent parsing. The
	// high-level structure of the parser is the following:
	public static BinaryTree Express() {
		BinaryTree root, node;
		node = root = new BinaryTree(); // Move to the root of the tree to the
										// last returned node.
		if (getCurrent().type == "Atom") {

			node.isList = false;
			node.value = current.value;
			node.type = getCurrent().type;
			node.atomType = getCurrent().atomType;
			MoveToNext(); // Consume atom

		} else if (getCurrent().type == "OpenParenthesis") {
			MoveToNext(); // Consume opening parenthesis
			node.isList = true; // Start of new list

			while (getCurrent().type != "ClosedParenthesis") {
				node.left = Express();
				node.right = new BinaryTree();
				node = node.right;
			}

			if (current.value != ")")
				runTimeError("End Parenthesis expected..."); // Error on parse,
																// exit program
			else {
				node.value = "NIL"; // End parenthesis found and end of tree,
									// set to NIL
				node.isList = false;
				node.type = "Atom";
			}

			MoveToNext(); // Consume closing parenthesis
		} else {
			// All Error output based on current token

			if (current.value == ")")
				runTimeError("ERROR: Closed parenthesis unexpected.");
			else if (current.value == "(")
				runTimeError("ERROR: Open parenthesis unexpected.");
			else if (current.value == null)
				runTimeError(
						"ERROR: Possilbe Closed parenthesis missing. End of File returned as token before evaluation occured.");
			else
				runTimeError("ERROR: Open/Closed parenthesis expected");
		}
		return root;
	}

	// Pretty Print left and right subtrees with recursive function
	public static void preetyPrint(BinaryTree exp) {
		// Find leaf node;
		if (exp == null) {
			return;
		}
		// Node has children
		else if (exp.isList) {
			System.out.print("(");
			preetyPrint(exp.left);
			System.out.print(" . ");
			preetyPrint(exp.right);
			System.out.print(")");
		}
		// Node has no children and has value
		else {
			System.out.print(exp.value);
		}
	}

	// List Notation Printer
	public static void printList(BinaryTree exp) {
		if (exp == null)
			return;

		if (exp.type != null && exp.type.equals("Atom")) {
			System.out.print(exp.value);
			return;
		}

		if (exp.isList == true) {
			System.out.print("(");
			if (exp.left != null) {
				printList(exp.left);

			}

			while (exp.right != null) {
				exp = exp.right;
				if (exp.right != null)
					System.out.print(" ");
				printList(exp.left);

			}

			if (exp.value != null && exp.value != "NIL") {
				System.out.print(" . ");
				printList(exp);
			}
			System.out.print(")");
			return;

		}

		System.out.print("(");

		if (exp.left != null) {
			printList(exp.left);
		}

		System.out.print(" . ");

		if (exp.right != null)
			;
		{
			printList(exp.right);
		}

		System.out.print(")");
		return;
	}

	// MAIN FUNCTION - STARTS THE PROGRAM
	public static void main(String[] args) {

		// Main function modified to handle parser organization
		init(); // Start the Scanner
		if (current.type == "EOF") {
			runTimeError("ERROR: Empty file.");
		}

		while (current.type != "EOF") {
			// Catch any scanner errors
			if (current.type == "Error")
				printError(getCurrent());

			exp = Express(); // Main recursive decent parser
			exp = exp.eval(exp, new BinaryTree("Atom", "NIL"), DefunList.getdList()); // Evaluate top level express recursively

			try {
				BinaryTree.isList(exp); // Set the isList member to the correct
										// bool value for the printer
			} catch (Exception e) {
				runTimeError("ERROR: Unexpected parse error caused by undefined evaluation");
			}

			printList(exp); // Print
			System.out.println("");
		}
		// END OF INTERPRETER
		System.exit(1);
	}
}