package es.etomas.logiccalc.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.etomas.logiccalc.logicparsers.PropLogic;
import es.etomas.logiccalc.logicparsers.PropLogic.LogicType;

public abstract class PropLogicUtils {

	/**
	 * Gets all the operations contained in the logic expression
	 * @param input The expression to get the operations from
	 * @return A Set containing all the operations
	 */
	public static Set<PropLogic> opSet(PropLogic input) {
		return input.stream()
				.filter(e->!e.isAtom())
				.collect(Collectors.toSet());
	}

	/**
	 * Gets all the atoms contained in the logic expression
	 * @param input The expression to get the atoms from
	 * @return A Set containing all the atoms
	 */
	public static Set<PropLogic> atomSet(PropLogic input) {
		return input.stream()
				.filter(PropLogic::isAtom)
				.collect(Collectors.toSet());
	}

	/**
	 * Auxiliary method to print a message under a certain condition
	 * @param message The message to print
	 */


	/**
	 * Checks if the given logic expression is in CNF or not
	 * @param input The expression to check
	 * @return A Boolean representing whether the expression is in CNF or not
	 */
	public static boolean isCNF(PropLogic input) {

		//Skip other calculations if the expression is a negated operation
		boolean res = input.isAtom() || !input.isNegated();

		if(res) {
			switch (input.getType()) {
			//If disjunction, it must not have a conjunction nested inside
			case DISJUNCTION:
				res = input.stream().noneMatch(expr->expr.getType() == LogicType.CONJUNCTION);
				break;
			case ATOM : break;
			//Cannot contain exclusive disjunctions or implications
			case BICONDITIONAL,IMPLICATION:
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
	public static PropLogic toCNF(PropLogic input) {
		//Copy that will be modified to CNF
		PropLogic inputCopy = input.getCopy();

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
					List<PropLogic> leftChildren = inputCopy.getLeft().isAtom()||
							inputCopy.getLeft().getType() == LogicType.DISJUNCTION?
									List.of(inputCopy.getLeft()):
									inputCopy.getLeft().getChildren();
					List<PropLogic> rightChildren = inputCopy.getRight().isAtom() ||
							inputCopy.getRight().getType() == LogicType.DISJUNCTION?
							List.of(inputCopy.getRight()):
							inputCopy.getRight().getChildren();

					List<PropLogic> zipped = new ArrayList<>();
					//Combining each child with its counterpart in the list through
					//a new disjunction
					leftChildren.stream()
						.forEach(l->rightChildren.stream()
								.forEach(r->zipped.add(PropLogic.ofOp(l, "or", r))));

					//Combine all disjunctions into conjunctions
					inputCopy = zipped.stream()
						.reduce((l1, l2) -> PropLogic.ofOp(l1, "and", l2))
						.orElse(null);

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
				List<PropLogic> children = inputCopy.getChildren();
				inputCopy.setType(LogicType.CONJUNCTION);
				inputCopy.setChildren(
						PropLogic.ofOp(children.get(0).getCopy().negate(), "or", children.get(1).getCopy()),
						PropLogic.ofOp(children.get(1).negate(), "or", children.get(0)));
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
	public static void truthTable(PropLogic input) {
		
		List<String> atoms = atomSet(input).stream()
				.map(PropLogic::getLabel)
				.distinct()
				.sorted()
				.toList();

		//Max binary value: 2^n - 1
		Integer length = (int) Math.pow(2, atoms.size());

		//Header: Variable names separated by spaces, and the input expression
		OpStepsSingleton.getInstance().addExplanation(atoms.toString().replaceAll("[\\[,\\]]", "")
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
			OpStepsSingleton.getInstance().addExplanation(binary + "\t" +
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
	private static boolean isAlpha(PropLogic input) {
		boolean res = false;

		switch (input.getType()) {
		//Conjunctions or biconditionals when not negated
		case CONJUNCTION,BICONDITIONAL:
			res = !input.isNegated();
			break;
		//Disjunctions or implications when negated
		case DISJUNCTION, IMPLICATION:
			res = input.isNegated();
			break;
		case ATOM:
			break;
		}

		return res;
	}

	/**
	 * Operates on a branch of a truth tree, prioritizing alpha operations.
	 * @param branch A Set of logic expressions representing a branch of the tree
	 * @return A Set including the resulting branch(es)
	 */
	private static Set<Set<PropLogic>> truthTreeOp(Set<PropLogic> branch) {

		OpStepsSingleton.getInstance().addExplanation("  Operating on branch: " + branch);

		Set<Set<PropLogic>> resBranches = Set.of(branch);

		//Find first alpha operation in the branch
		//If there are no branches, find the first beta operation
		PropLogic toOperate = branch.stream()
				.filter(PropLogicUtils::isAlpha)
				.findFirst()
				.orElse(branch.stream()
						.filter(expr->!expr.isAtom())
						.findFirst().orElse(null));

		//Avoid any other calculations if all elements are atoms
		if(toOperate != null) {

			OpStepsSingleton.getInstance().addExplanation("    Chosen expression: " + toOperate);
			List<PropLogic> components = List.of();
			//Get components of the expression
			switch(toOperate.getType()) {
			//(A and B), (A or B): A, B
			case CONJUNCTION, DISJUNCTION:
				components = List.of(
						toOperate.getLeft(),
						toOperate.getRight());
				break;
			//(A <-> B): (A -> B),(B -> A)
			case BICONDITIONAL:
				 components = List.of(
						 PropLogic.ofOp(toOperate.getLeft().getCopy(), "->", toOperate.getRight().getCopy()),
						 PropLogic.ofOp(toOperate.getRight(), "->", toOperate.getLeft()));
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
							.map(PropLogic::negate)
							.toList();
					}

			//List to be used when mapping values
			final List<PropLogic> finalComponents = components;

			if (isAlpha(toOperate)) {

				//Alpha: Replace the operated element with its components
				//in the branch
				OpStepsSingleton.getInstance().addExplanation("    Alpha operation: " +
						(toOperate.isNegated()?"negated ":"") +
						toOperate.getType().toString().toLowerCase());

				resBranches = Set.of(branch.stream()
						.flatMap(expr->expr.equals(toOperate)?
								finalComponents.stream():
								Stream.of(expr))
						.collect(Collectors.toSet())
						);

			} else {

				//Beta: Split the branch in 2, with a different component of
				//the operated element
				OpStepsSingleton.getInstance().addExplanation("    Beta operation: " +
						(toOperate.isNegated()?"negated ":"") +
						toOperate.getType().toString().toLowerCase());

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
			OpStepsSingleton.getInstance().addExplanation("    Branch composed of atoms, cannot operate "
					+ "on it");
		}

		 OpStepsSingleton.getInstance().addExplanation("    Resulting branch(es): " + resBranches);
		 return resBranches;
	}

	/**
	 * Makes the truth tree of a Set of logical expressions,
	 * returning the final result.
	 *
	 * @param input The expressions to make the truth tree off of
	 * @return All the clauses resulting from the truth tree
	 */
	public static Set<Set<PropLogic>> truthTree(Set<PropLogic> input) {
		
		OpStepsSingleton.getInstance().addExplanation("Making truth tree of: " + input);
		OpStepsSingleton.getInstance().addStep(input.toString());
		
		return Stream.iterate(
				//Start with a set that will contain all branches
				//(sets of expressions)
				Set.of(input),
				//Operate on each branch, which may split into more branches
				(tree) -> {
					OpStepsSingleton.getInstance().addStep(tree.toString());
					return tree.stream()
						.flatMap(branch->truthTreeOp(branch).stream())
						.collect(Collectors.toSet());
					})
				//Finished when all the branches in the tree only have
				//atoms
				.filter(tree->tree.stream()
						.allMatch(branch->branch.stream()
								.allMatch(PropLogic::isAtom)))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Makes the truth tree of a single logical expression, returning
	 * its result.
	 *
	 * @param input The expression to make the truth tree off of
	 * @return All the clauses resulting from the truth tree
	 */
	public static Set<Set<PropLogic>> truthTree(PropLogic input) {
		return truthTree(Set.of(input));
	}

	public static Set<Set<PropLogic>> getClauses(PropLogic input) {
		PropLogic cnf = toCNF(input);
		Set<PropLogic> disjunctions = cnf.stream()
			.filter(expr->expr.getType()==LogicType.DISJUNCTION ||
				expr.isAtom())
			.collect(Collectors.toSet());

		//Gets the disjunctions and atoms that are not a child of another
		//Afterwards, gets their atoms
		return disjunctions.stream()
				.filter(expr->disjunctions.stream()
						.noneMatch(expr2->expr2.getChildren().contains(expr)))
				.map(PropLogicUtils::atomSet)
				.collect(Collectors.toSet());
	}

	public static Set<PropLogic> clauseFromString(String atomLine) {
		String[] atoms = atomLine.replace(" ", "").split(",");

		return Arrays.stream(atoms)
				.filter(s->s.length()<=2)
				.map(s->s.charAt(0) == '!'?
					PropLogic.ofAtom(s.substring(1), true):
					PropLogic.ofAtom(s, false))
				.collect(Collectors.toSet());
	}

	/**
	 *
	 * @param clauseFirst
	 * @param clauseSecond
	 * @return The new clause, null if it's not possible to make one
	 */
	private static Set<PropLogic> resolveClausePair(Set<PropLogic> clauseFirst, Set<PropLogic> clauseSecond) {

		Set<PropLogic> res = null;
		if(clausesHaveAnyComplementary(clauseFirst, clauseSecond)) {
			res = new HashSet<>();
			PropLogic atomToRemove = clauseFirst.stream()
					.filter(atom->clauseSecond.contains(atom.getComplementary()))
					.findFirst()
					.orElse(null);

			res.addAll(clauseFirst);
			res.addAll(clauseSecond);
			res = res.stream()
					.filter(atom->!atom.getLabel().equals(atomToRemove.getLabel()))
					.collect(Collectors.toSet());
			OpStepsSingleton.getInstance().addExplanation("  Resolving " + clauseFirst + " and " + clauseSecond + ": " + res);
		}

		return res;
	}

	public static boolean clausesHaveAnyComplementary(Set<PropLogic> clauseFirst, Set<PropLogic> clauseSecond) {
		return clauseFirst.stream()
				.anyMatch(atom->clauseSecond.contains(atom.getComplementary()));
	}

	/**
	 * Gets if the subsuming expression can be subsumed by the subsumer in resolution.
	 * @param subsuming
	 * @param subsumer
	 * @return True if subsumer can subsume the subsuming clause, false otherwise
	 */
	private static boolean canSubsume(Set<PropLogic> subsuming, Set<PropLogic> subsumer) {
		boolean res = !subsumer.equals(Set.of()) && subsuming.containsAll(subsumer) && subsuming.size() > subsumer.size();
		if(res) {
			OpStepsSingleton.getInstance().addExplanation("  " + subsuming + " subsumed by " + subsumer);
		}
		return res;
	}

	/**
	 * Evaluates if the set of clauses can be satisfied using the resolution method.
	 * Can print its steps on the console.
	 *
	 * @param clauses
	 * @return
	 */
	public static Boolean resolution(Set<Set<PropLogic>> clauses) {
		return resolutionAux(clauses, new HashMap<>());
	}

	//Main recursive function for resolution
	private static Boolean resolutionAux(Set<Set<PropLogic>> clauses, Map<Set<Set<PropLogic>>,Set<PropLogic>> mem) {
		Boolean res;

		OpStepsSingleton.getInstance().addExplanation("Working with clauses: " + clauses);

		//Get all resulting clauses
		 Set<Set<PropLogic>> newClauses = clauses.stream()
				.flatMap(clause1->clauses
						.stream()
						.filter(clause2->!clause1.equals(clause2))
						//Uses the memory to find the resolution of 2 clauses, adding
						//new entries if necessary
						.map(clause2->mem.computeIfAbsent(
								Set.of(clause1,clause2),
								s->resolveClausePair(clause1, clause2))))
				.collect(Collectors.toSet());

		newClauses.remove(null);

		//Combines the initial clauses and new clauses, removing duplicates
		Set<Set<PropLogic>> allClauses = Stream.concat(clauses.stream(), newClauses.stream())
				.collect(Collectors.toSet());

		OpStepsSingleton.getInstance().addExplanation("Obtained clauses: " + allClauses);

		//Removes subsumed clauses
		Set<Set<PropLogic>> resClauses = allClauses.stream()
				.filter(clause->allClauses.stream()
						.noneMatch(subsumer->canSubsume(clause, subsumer)))
				.collect(Collectors.toSet());



		//Empty set: Inconsistent
		if(resClauses.contains(Set.of())) {
			OpStepsSingleton.getInstance().addExplanation("Encountered empty clause. Clause set is inconsistent");
			res = false;
		//No changes in clauses: Consistent
		} else if(resClauses.equals(clauses)) {
			OpStepsSingleton.getInstance().addExplanation("Clauses have not changed after last iteration. Clause set is consistent.");
			res = true;
		//New iteration
		} else {
			res = resolutionAux(resClauses, mem);
		}

		return res;
	}

	/**
	 * Evaluates if the given set of clauses is consistent through the DPLL algorythm.
	 * Can print its steps on the console.
	 *
	 * @param clauses
	 * @return
	 */
	public static Boolean dpll(Set<Set<PropLogic>> clauses) {
		
		
		Boolean res = null;
		Set<Set<PropLogic>> resClauses = null;

		OpStepsSingleton.getInstance().addExplanation("Working with clauses: " + clauses);

		Set<Set<PropLogic>> tautologies = clauses.stream()
				.filter(clause->clause.stream()
						.anyMatch(expr->clause.contains(expr.getComplementary())))
				.collect(Collectors.toSet());
		Set<Set<PropLogic>> literals = clauses.stream()
				.filter(expr->expr.size()==1)
				.collect(Collectors.toSet());
		Set<PropLogic> allAtoms = clauses.stream()
				.flatMap(clause->clause.stream())
				.collect(Collectors.toSet());
		Set<PropLogic> pureLiterals = allAtoms.stream()
				.filter(expr->!allAtoms.contains(expr.getComplementary()))
				.collect(Collectors.toSet());

		//Empty set: consistent
		if(clauses.equals(Set.of())) {
			OpStepsSingleton.getInstance().addExplanation("Set of clauses is empty. Set is consistent.");
			res = true;

		//Empty clause: inconsistent
		} else if(clauses.contains(Set.of())) {
			OpStepsSingleton.getInstance().addExplanation("Encountered empty clause. Set is inconsistent.");
			res = false;

		//Operating:
		//Remove tautologies
		} else if(!tautologies.isEmpty()) {
			OpStepsSingleton.getInstance().addExplanation("  Removing tautologies: " + tautologies);
			resClauses = clauses.stream()
					.filter(clause->!tautologies.contains(clause))
					.collect(Collectors.toSet());
			res = dpll(resClauses);

		//Unit propagation
		} else if(!literals.isEmpty()) {
			//Literal to remove
			Set<PropLogic> literal = literals.stream()
					.findAny()
					.orElse(null);
			//Expression to remove from other clauses
			PropLogic atom = literal.stream()
					.findAny()
					.orElse(null);

			OpStepsSingleton.getInstance().addExplanation("  Unit propagation: " + atom);

			//Remove the 2 above
			resClauses = clauses.stream()
					.filter(clause->!clause.equals(literal))
					.map(clause->clause.stream()
							.filter(expr->!expr.equals(atom.getComplementary()))
							.collect(Collectors.toSet()))
					.collect(Collectors.toSet());
			res = dpll(resClauses);

		//Pure literal elimination
		} else if(!pureLiterals.isEmpty()) {
			PropLogic pureLiteral = pureLiterals.stream()
					.findAny()
					.orElse(null);

			OpStepsSingleton.getInstance().addExplanation("  Pure literal elimination: " + pureLiteral);

			resClauses = clauses.stream()
					.filter(clause->!clause.contains(pureLiteral))
					.collect(Collectors.toSet());
			res = dpll(resClauses);

		//Division rule if all else fails
		} else {
			PropLogic atom = allAtoms.stream()
					.findAny()
					.orElse(null);

			//Division rule
			OpStepsSingleton.getInstance().addExplanation("Could not operate on clause set. Applying division rule with literal: " + atom);
			res = dpll(Stream.concat(clauses.stream(), Set.of(Set.of(atom)).stream())
						.collect(Collectors.toSet())) ||
					dpll(Stream.concat(clauses.stream(), Set.of(Set.of(atom.getComplementary())).stream())
							.collect(Collectors.toSet()));
			if(res) {
				OpStepsSingleton.getInstance().addExplanation("Division rule successful. Clause set is consistent.");
			} else {
				OpStepsSingleton.getInstance().addExplanation("Division rule unsuccessful. Clause set is inconsistent.");
			}
		}

		return res;
	}
	

	
}
