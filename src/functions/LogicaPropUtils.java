package functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import parser.LogicaProp;
import parser.LogicaProp.LogicType;

public class LogicaPropUtils {
	
	/**
	 * Gets all the operations contained in the logic expression
	 * @param input The expression to get the operations from
	 * @return A Set containing all the operations
	 */
	public static Set<LogicaProp> opSet(LogicaProp input) {
		return input.stream()
				.filter(e->!e.isAtom())
				.collect(Collectors.toSet());
	}
	
	/**
	 * Gets all the atoms contained in the logic expression
	 * @param input The expression to get the atoms from
	 * @return A Set containing all the atoms
	 */
	public static Set<LogicaProp> atomSet(LogicaProp input) {
		return input.stream()
				.filter(expr->expr.isAtom())
				.collect(Collectors.toSet());
	}
	
	/**
	 * Auxiliary method to print a message under a certain condition
	 * @param message The message to print
	 * @param silent Whether the message should be printed or not
	 */
	public static void printStep(String message, Boolean silent) {
		if(!silent) {
			System.out.println(message);
		}
		
	}
	
	/**
	 * Checks if the given logic expression is in CNF or not
	 * @param input The expression to check
	 * @return A Boolean representing whether the expression is in CNF or not
	 */
	public static Boolean isCNF(LogicaProp input) {
		
		//Skip other calculations if the expression is a negated operation
		Boolean res = input.isAtom() || !input.isNegated();
		
		if(res) {
			switch (input.getType()) {
			//If disjunction, it must not have a conjunction nested inside
			case DISJUNCTION:
				res = input.stream().noneMatch(expr->expr.getType() == LogicType.CONJUNCTION);
				break;
			case ATOM : break;
			//Cannot contain exclusive disjunctions or implications
			case BICONDITIONAL:
			case IMPLICATION:	
				res = false; 
				break;
			//If conjunction, both of its children must be in CNF
			case CONJUNCTION:
				res = isCNF(input.getLeft()) && isCNF(input.getRight());
			break;
			}
		}
		return res;
	}
	
	/**
	 * Calculates the CNF of a logical expression.
	 * 
	 * @param input The expression to modify
	 * @return A copy of the expression in CNF
	 */
	public static LogicaProp toCNF(LogicaProp input) {
		//Copy that will be modified to CNF
		LogicaProp inputCopy = input.getCopy();
		
		//The expression is returned after applying all needed changes
		if(!isCNF(inputCopy)) {
			switch (inputCopy.getType()) {
			//Atoms return themselves
			case ATOM: break;
			//Disjunctions apply the Distribution law or De Morgan if they're negated
			case DISJUNCTION: 
				if(!inputCopy.isNegated()) {
					inputCopy.setChildren(toCNF(inputCopy.getLeft()), 
							toCNF(inputCopy.getRight()));
					
					//Distribution law:
					//Obtaining the children of the contained expressions
					List<LogicaProp> leftChildren = inputCopy.getLeft().isAtom()||
							inputCopy.getLeft().getType() == LogicType.DISJUNCTION?
									List.of(inputCopy.getLeft()):
									inputCopy.getLeft().getChildren();
					List<LogicaProp> rightChildren = inputCopy.getRight().isAtom() ||
							inputCopy.getRight().getType() == LogicType.DISJUNCTION?
							List.of(inputCopy.getRight()):
							inputCopy.getRight().getChildren();
					
					List<LogicaProp> zipped = new ArrayList<>();
					//Combining each child with its counterpart in the list through
					//a new disjunction
					leftChildren.stream()
						.forEach(l->rightChildren.stream()
								.forEach(r->zipped.add(LogicaProp.ofOp(l, "or", r))));
					
					//Combine all disjunctions into conjunctions
					inputCopy = zipped.stream()
						.reduce((l1, l2) -> LogicaProp.ofOp(l1, "and", l2))
						.get();
					
				} else {
					//Apply De Morgan if formula is negated
					inputCopy.negate();
					inputCopy.getLeft().negate();
					inputCopy.getRight().negate();
					inputCopy.setType(LogicType.CONJUNCTION);
					toCNF(inputCopy);
				}
				break;
			case CONJUNCTION: 
				if(!inputCopy.isNegated()) {
					//Get the CNF of both children
					inputCopy.setChildren(toCNF(inputCopy.getLeft()), 
							toCNF(inputCopy.getRight()));
				} else {
				//Apply De Morgan if formula is negated
					inputCopy.negate();
					inputCopy.getLeft().negate();
					inputCopy.getRight().negate();
					inputCopy.setType(LogicType.DISJUNCTION);
					toCNF(inputCopy);
				}
				break;
			case BICONDITIONAL: 
				//Convert into conjunction of implications of both children
				List<LogicaProp> children = inputCopy.getChildren();
				inputCopy.setType(LogicType.CONJUNCTION);
				inputCopy.setChildren(
						LogicaProp.ofOp(children.get(0).getCopy().negate(), "or", children.get(1).getCopy()),
						LogicaProp.ofOp(children.get(1).negate(), "or", children.get(0)));
				toCNF(inputCopy);
				break;
			case IMPLICATION: 
				//Convert into disjunction with negated first element
				inputCopy.getLeft().negate();
				inputCopy.setType(LogicType.DISJUNCTION);
				inputCopy = toCNF(inputCopy);
				break;
			}
		}
		return inputCopy;
	}

	
	/**
	 * Print the truth table of a given logic expression.
	 * @param input
	 */
	public static void truthTable(LogicaProp input) {
		
		List<String> atoms = atomSet(input).stream()
				.map(expr->expr.getLabel())
				.distinct()
				.sorted()
				.toList();
		
		//Max binary value: 2^n - 1
		Integer length = Double.valueOf(
				Math.pow(2, atoms.size()))
				.intValue();
		
		//Header: Variable names separated by spaces, and the input expression
		System.out.println(atoms.toString().replaceAll("[\\[,\\]]", "")
				+ "\t" + input);
		
		for (int i = 0; i < length; i++) {
			//Binary value separated by spaces in each digit
			String binary = String.format("%" + atoms.size() + "s", 
						Integer.toBinaryString(i))
					.replace(" ", "0")
					.replace("", " ")
					.trim();
			
			String[] values = binary.split(" ");
			
			//Mapping each variable to their respective value in the
			//binary String
			Map<String, Boolean> mappedValues = atoms.stream()
					.collect(Collectors.toMap(
							at->at,
							s->values[atoms.indexOf(s)].equals("1")));
			
			//Printed line: The binary string and the value it returns, the latter
			//centered under the input
			System.out.println(binary + "\t" + 
					String.format("%" + input.toString().length()/2 + "s", 
							(input.eval(mappedValues)? "1" : "0")));
		}
	}
	
	/**
	 * Relative to truth trees, checks if a given logic expression is classified as
	 * alpha. A false value indicates that it's classified as beta.
	 * @param input The expression to check
	 * @return Whether the expression is an alpha expression or not
	 */
	public static Boolean isAlpha(LogicaProp input) {
		Boolean res = false;
		
		switch (input.getType()) {
		//Conjunctions or biconditionals when not negated
		case CONJUNCTION:
		case BICONDITIONAL:	
			res = !input.isNegated();
			break;
		//Disjunctions or implications when negated
		case DISJUNCTION:
		case IMPLICATION:
			res = input.isNegated();
			break;
		case ATOM: res = false;
			break;
		}
		
		return res;
	}
	

	/**
	 * Operates on a branch of a truth tree, prioritizing alpha operations.
	 * @param branch A Set of logic expressions representing a branch of the tree
	 * @param silent Whether the calculation steps should be printed or not
	 * @return A Set including the resulting branch(es)
	 */
	public static Set<Set<LogicaProp>> truthTreeOp(Set<LogicaProp> branch, Boolean silent) {
		
		printStep("  Operating on branch: " + branch,silent);
		
		Set<Set<LogicaProp>> resBranches = Set.of(branch);
		
		//Find first alpha operation in the branch
		//If there are no branches, find the first beta operation
		LogicaProp toOperate = branch.stream()
				.filter(expr->isAlpha(expr))
				.findFirst()
				.orElse(branch.stream()
						.filter(expr->!expr.isAtom())
						.findFirst().orElse(null));
		
		//Avoid any other calculations if all elements are atoms
		if(toOperate != null) {
		
			printStep("    Chosen expression: " + toOperate,silent);
			List<LogicaProp> components = List.of();
			//Get components of the expression
			switch (toOperate.getType()) {
			//(A and B), (A or B): A, B
			case CONJUNCTION:
			case DISJUNCTION:
				components = List.of(
						toOperate.getLeft(),
						toOperate.getRight());
				break;
			//(A <-> B): (A -> B),(B -> A)
			case BICONDITIONAL:
				 components = List.of(
						 LogicaProp.ofOp(toOperate.getLeft().getCopy(), "->", toOperate.getRight().getCopy()),
						 LogicaProp.ofOp(toOperate.getRight(), "->", toOperate.getLeft()));
				break;
			//(A -> B): !A, B
			case IMPLICATION:
				 components = List.of(
						 toOperate.getLeft().negate(),
						 toOperate.getRight());
				break;
			case ATOM:
				throw new IllegalArgumentException("Cannot operate on an atom");
			}
			 
			//If the original expression is negated, negate all
			//components
			if(toOperate.isNegated()) {
						components = components.stream()
							.map(expr->expr.negate())
							.toList();
					}
			 
			//List to be used when mapping values
			final List<LogicaProp> finalComponents = components;
			 
			if (isAlpha(toOperate)) {
				
				//Alpha: Replace the operated element with its components 
				//in the branch
				printStep("    Alpha operation: " + 
						(toOperate.isNegated()?"negated ":"") +
						toOperate.getType().toString().toLowerCase(), silent);
				
				resBranches = Set.of(branch.stream()
						.flatMap(expr->expr.equals(toOperate)?
								finalComponents.stream():
								Stream.of(expr))
						.collect(Collectors.toSet())
						);
						
			} else {
				
				//Beta: Split the branch in 2, with a different component of 
				//the operated element
				printStep("    Beta operation: " + 
						(toOperate.isNegated()?"negated ":"") +
						toOperate.getType().toString().toLowerCase(), silent);
				
				resBranches = Set.of(
						branch.stream()
							.map(expr->expr.equals(toOperate)?
								finalComponents.get(0):
								expr.getCopy())
							.collect(Collectors.toSet()),
						branch.stream()
						.map(expr->expr.equals(toOperate)?
								finalComponents.get(1):
								expr)
							.collect(Collectors.toSet()));
			}
		} else {
			printStep("    Branch composed of atoms, cannot operate "
					+ "on it", silent);
		} 
		
		 printStep("    Resulting branch(es): " + resBranches, silent);
		 return resBranches;
	}
	
	/**
	 * Makes the truth tree of a Set of logical expressions,
	 * returning the final result.
	 * 
	 * @param input The expressions to make the truth tree off of
	 * @param silent Whether steps should be printed on console or not
	 * @return All the clauses resulting from the truth tree
	 */
	public static Set<Set<LogicaProp>> truthTree(Set<LogicaProp> input, Boolean silent) {
		
		printStep("Making truth tree of: " + input, silent);
		
		Set<Set<LogicaProp>> trTree = Stream.iterate(
				//Start with a set that will contain all branches
				//(sets of expressions)
				Set.of(input),
				//Operate on each branch, which may split into more branches
				tree->tree.stream()
					.flatMap(branch->truthTreeOp(branch, silent).stream())
					.collect(Collectors.toSet()))
				//Finished when all the branches in the tree only have
				//atoms
				.filter(tree->tree.stream()
						.allMatch(branch->branch.stream()
								.allMatch(expr->expr.isAtom())))
				.findFirst()
				.get();
		
		return trTree;
	}
	
	/**
	 * Makes the truth tree of a single logical expression, returning
	 * its result.
	 * 
	 * @param input The expression to make the truth tree off of
	 * @param silent Whether each step should be printed on console or not
	 * @return All the clauses resulting from the truth tree
	 */
	public static Set<Set<LogicaProp>> truthTree(LogicaProp input, Boolean silent) {
		return truthTree(Set.of(input), silent);
	}

	public static Set<Set<LogicaProp>> getClauses(LogicaProp input) {
		LogicaProp CNF = toCNF(input);
		Set<LogicaProp> disjunctions = CNF.stream()
			.filter(expr->expr.getType()==LogicType.DISJUNCTION ||
				expr.isAtom())
			.collect(Collectors.toSet());
		
		//Gets the disjunctions and atoms that are not a child of another
		//Afterwards, gets their atoms
		Set<Set<LogicaProp>> clauses = disjunctions.stream()
				.filter(expr->disjunctions.stream()
						.noneMatch(expr2->expr2.getChildren().contains(expr)))
				.map(expr->atomSet(expr))
				.collect(Collectors.toSet());
		
		return clauses;
	}
	
	public static Set<LogicaProp> clauseFromString(String atomLine) {
		String[] atoms = atomLine.replace(" ", "").split(",");
		
		return Arrays.stream(atoms)
				.filter(s->s.length()<=2)
				.map(s->s.charAt(0) == '!'? 
					LogicaProp.ofAtom(s.substring(1), true):
					LogicaProp.ofAtom(s, false))
				.collect(Collectors.toSet());
	}
	
	/**
	 * 
	 * @param clauseFirst
	 * @param clauseSecond
	 * @return The new clause, null if it's not possible to make one
	 */
	public static Set<LogicaProp> resolveClausePair(Set<LogicaProp> clauseFirst, Set<LogicaProp> clauseSecond, Boolean silent) {
		
		Set<LogicaProp> res = null;
		if(clausesHaveAnyComplementary(clauseFirst, clauseSecond)) {
			res = new HashSet<>();
			LogicaProp atomToRemove = clauseFirst.stream()
					.filter(atom->clauseSecond.contains(atom.getComplementary()))
					.findFirst()
					.orElse(null);

			res.addAll(clauseFirst);
			res.addAll(clauseSecond);
			res = res.stream()
					.filter(atom->!atom.getLabel().equals(atomToRemove.getLabel()))
					.collect(Collectors.toSet());
			printStep("  Resolving " + clauseFirst + " and " + clauseSecond + ": " + res,silent);
		}
		
		return res;
	}
	
	public static Boolean clausesHaveAnyComplementary(Set<LogicaProp> clauseFirst, Set<LogicaProp> clauseSecond) {
		return clauseFirst.stream()
				.anyMatch(atom->clauseSecond.contains(atom.getComplementary()));
	}
	
	/**
	 * 
	 * @param subsuming
	 * @param subsumer
	 * @param silent
	 * @return True if subsumer can subsume the subsuming clause, false otherwise
	 */
	public static Boolean canSubsume(Set<LogicaProp> subsuming, Set<LogicaProp> subsumer, Boolean silent) {
		Boolean res = !subsumer.equals(Set.of()) && subsuming.containsAll(subsumer) && subsuming.size() > subsumer.size();
		if(res) {
			printStep("  " + subsuming + " subsumed by " + subsumer, silent);
		}
		return res;
	}
	
	public static Boolean resolution(Set<Set<LogicaProp>> clauses, Boolean silent) {
		return resolutionAux(clauses, silent, new HashMap<>());
	}
	
	public static Boolean resolutionAux(Set<Set<LogicaProp>> clauses, Boolean silent, Map<Set<Set<LogicaProp>>,Set<LogicaProp>> mem) {
		
		Boolean res;
		
		printStep("Working with clauses: " + clauses, silent);
		
		//Get all resulting clauses
		 Set<Set<LogicaProp>> newClauses = clauses.stream()
				.flatMap(clause1->clauses
						.stream()
						.filter(clause2->!clause1.equals(clause2))
						//Uses the memory to find the resolution of 2 clauses, adding
						//new entries if necessary
						.map(clause2->mem.computeIfAbsent(
								Set.of(clause1,clause2),
								s->resolveClausePair(clause1, clause2,silent))))
				.collect(Collectors.toSet());
		
		newClauses.remove(null);
		
		//Combines the initial clauses and new clauses, removing duplicates
		Set<Set<LogicaProp>> allClauses = Stream.concat(clauses.stream(), newClauses.stream())
				.collect(Collectors.toSet());
		
		printStep("Obtained clauses: " + allClauses, silent);
		
		//Removes subsumed clauses
		Set<Set<LogicaProp>> resClauses = allClauses.stream()
				.filter(clause->allClauses.stream()
						.noneMatch(subsumer->canSubsume(clause, subsumer, silent)))
				.collect(Collectors.toSet());
		
		
		
		//Empty set: Inconsistent
		if(resClauses.contains(Set.of())) {
			printStep("Encountered empty clause. Clause set is inconsistent", silent);
			res = false;
		//No changes in clauses: Consistent
		} else if(resClauses.equals(clauses)) {
			printStep("Clauses have not changed after last iteration. Clause set is consistent.", silent);
			res = true;
		//New iteration
		} else {
			res = resolutionAux(resClauses, silent, mem);
		}
		
		return res;
	}
	
	public static Boolean dpll(Set<Set<LogicaProp>> clauses, Boolean silent) {
		Boolean res = null;
		Set<Set<LogicaProp>> resClauses = null;
		
		printStep("Working with clauses: " + clauses, silent);
		
		//Empty set: consistent
		if(clauses.equals(Set.of())) {
			printStep("Set of clauses is empty. Set is consistent.", silent);
			res = true;
			
		//Empty clause: inconsistent
		} else if(clauses.contains(Set.of())) {
			printStep("Encountered empty clause. Set is inconsistent.", silent);
			res = false;
		}
		
		Set<Set<LogicaProp>> tautologies = clauses.stream()
				.filter(clause->clause.stream()
						.anyMatch(expr->clause.contains(expr.getComplementary())))
				.collect(Collectors.toSet());
		Set<Set<LogicaProp>> literals = clauses.stream()
				.filter(expr->expr.size()==1)
				.collect(Collectors.toSet());
		Set<LogicaProp> allAtoms = clauses.stream()
				.flatMap(clause->clause.stream())
				.collect(Collectors.toSet());
		Set<LogicaProp> pureLiterals = allAtoms.stream()
				.filter(expr->!allAtoms.contains(expr.getComplementary()))
				.collect(Collectors.toSet());
		
		//Remove tautologies
		if(tautologies.size()>0) {
			printStep("  Removing tautologies: " + tautologies, silent);
			resClauses = clauses.stream()
					.filter(clause->!tautologies.contains(clause))
					.collect(Collectors.toSet());
			res = dpll(resClauses, silent);
			
			//Unit propagation
		} else if(literals.size()>0) {
			//Literal to remove
			Set<LogicaProp> literal = literals.stream()
					.findAny()
					.get();
			//Expression to remove from other clauses
			LogicaProp atom = literal.stream()
					.findAny()
					.get();
			
			printStep("  Unit propagation: " + atom,silent);
			
			//Remove the 2 above
			resClauses = clauses.stream()
					.filter(clause->!clause.equals(literal))
					.map(clause->clause.stream()
							.filter(expr->!expr.equals(atom.getComplementary()))
							.collect(Collectors.toSet()))
					.collect(Collectors.toSet());
			res = dpll(resClauses, silent);
			
		//Pure literal elimination
		} else if(pureLiterals.size()>0) {
			LogicaProp pureLiteral = pureLiterals.stream()
					.findAny()
					.get();
			
			printStep("  Pure literal elimination: " + pureLiteral, silent);
			
			resClauses = clauses.stream()
					.filter(clause->!clause.contains(pureLiteral))
					.collect(Collectors.toSet());
			res = dpll(resClauses, silent);
			
		
		
			
		//Division rule if all fails
		} else {
			LogicaProp atom = allAtoms.stream()
					.findAny()
					.get();
			
			//Division rule
			printStep("Could not operate on clause set. Applying division rule with literal: " + atom, silent);
			res = dpll(Stream.concat(clauses.stream(), Set.of(Set.of(atom)).stream())
						.collect(Collectors.toSet()),
						silent) || 
					dpll(Stream.concat(clauses.stream(), Set.of(Set.of(atom.getComplementary())).stream())
							.collect(Collectors.toSet()),
							silent);
			if(res) {
				printStep("Division rule successful.", silent);
			} else {
				printStep("Division rule unsuccessful.", silent);
			}
		}
		
		return res;
	}
	
}
