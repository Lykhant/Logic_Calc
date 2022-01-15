package functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import parser.LogicaProp;
import parser.LogicaProp.LogicType;

public class LogicaPropUtils {
	
	public static Set<LogicaProp> opSet(LogicaProp input) {
		return input.stream()
				.filter(e->!e.isAtom())
				.collect(Collectors.toSet());
	}
	
	public static Set<LogicaProp> atomSet(LogicaProp input) {
		return input.stream()
				.filter(expr->expr.isAtom())
				.collect(Collectors.toSet());
	}
	
	/**
	 * 
	 * @param message The message to print
	 * @param silent Whether the message should be printed or not
	 */
	public static void printStep(String message, Boolean silent) {
		if(!silent) {
			System.out.println(message);
		}
		
	}
	
	public static Boolean isCNF(LogicaProp logic) {
		
		//Skip other calculations if the expression is an operation and is negated
		Boolean res = logic.isAtom() || !logic.isNegated();
		
		if(res) {
			switch (logic.getTipo()) {
			//If disjunction, it must not have a conjunction nested inside
			case Disjunction:
				res = logic.stream().noneMatch(expr->expr.getTipo() == LogicType.Disjunction);
				break;
			case Atom : break;
			//Cannot contain exclusive disjunctions or implications
			case exDisjunction:
			case Implication:	
				res = false; 
				break;
			//If conjunction, both of its children must be in CNF
			case Conjunction:
				res = isCNF(logic.getLeft()) && isCNF(logic.getRight());
			break;
			}
		}
		return res;
	}
	
	/**
	 * Converts the given propositional logic expression into CNF form.
	 * 
	 * @param in The expression to modify
	 * @return 
	 * 
	 */
	public static LogicaProp toCNF(LogicaProp in) {
		if(!isCNF(in)) {
			switch (in.getTipo()) {
			case Atom: break;
			case Disjunction: 
				if(!in.isNegated()) {
					in.setChildren(toCNF(in.getLeft()), 
							toCNF(in.getRight()));
					
					//Distribution law
					List<LogicaProp> leftChildren = in.getLeft().isAtom()?
							List.of(in.getLeft()):
							in.getLeft().getChildren();
					List<LogicaProp> rightChildren = in.getRight().isAtom()?
							List.of(in.getRight()):
							in.getRight().getChildren();
					
					List<LogicaProp> zipped = new ArrayList<>();
					
					leftChildren.stream()
						.forEach(l->rightChildren.stream()
								.forEach(r->zipped.add(LogicaProp.ofOp(l, "or", r))));
					
					in = zipped.stream()
						.reduce((l1, l2) -> LogicaProp.ofOp(l1, "and", l2))
						.get();
					
				} else {
					//Apply De Morgan if formula is negated
					in.negate();
					in.getLeft().negate();
					in.getRight().negate();
					in.setType(LogicType.Conjunction);
					toCNF(in);
				}
				break;
			case Conjunction: 
				if(!in.isNegated()) {
					in.setChildren(toCNF(in.getLeft()), 
							toCNF(in.getRight()));
				} else {
				//Apply De Morgan if formula is negated
					in.negate();
					in.getLeft().negate();
					in.getRight().negate();
					in.setType(LogicType.Disjunction);
					toCNF(in);
				}
				break;
			case exDisjunction: 
				List<LogicaProp> children = in.getChildren();
				in.setType(LogicType.Conjunction);
				in.setChildren(
						LogicaProp.ofOp(children.get(0).getCopy().negate(), "or", children.get(1).getCopy()),
						LogicaProp.ofOp(children.get(1).negate(), "or", children.get(0)));
				toCNF(in);
				break;
			case Implication: 
				in.getLeft().negate();
				in.setType(LogicType.Disjunction);
				toCNF(in);
				break;
			}
		}
		return in;
	}

	
	public static Set<Set<LogicaProp>> getClauses(LogicaProp in) {
		LogicaProp CNF = toCNF(in);
		Set<LogicaProp> disjunctions = CNF.stream()
			.filter(expr->expr.getTipo()==LogicType.Disjunction)
			.collect(Collectors.toSet());
		
		//Gets the disjunctions that are not a child of another
		//Afterwards, gets their atoms
		Set<Set<LogicaProp>> clauses = disjunctions.stream()
				.filter(expr->disjunctions.stream()
						.noneMatch(expr2->expr2.getChildren().contains(expr)))
				.map(expr->atomSet(expr))
				.collect(Collectors.toSet());
		
		return clauses;
	}
	 
	
	
}
