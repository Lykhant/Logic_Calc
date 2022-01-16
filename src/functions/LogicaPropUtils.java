package functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
			switch (logic.getType()) {
			//If disjunction, it must not have a conjunction nested inside
			case DISJUNCTION:
				res = logic.stream().noneMatch(expr->expr.getType() == LogicType.DISJUNCTION);
				break;
			case Atom : break;
			//Cannot contain exclusive disjunctions or implications
			case BICONDITIONAL:
			case IMPLICATION:	
				res = false; 
				break;
			//If conjunction, both of its children must be in CNF
			case CONJUNCTION:
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
			switch (in.getType()) {
			case Atom: break;
			case DISJUNCTION: 
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
					in.setType(LogicType.CONJUNCTION);
					toCNF(in);
				}
				break;
			case CONJUNCTION: 
				if(!in.isNegated()) {
					in.setChildren(toCNF(in.getLeft()), 
							toCNF(in.getRight()));
				} else {
				//Apply De Morgan if formula is negated
					in.negate();
					in.getLeft().negate();
					in.getRight().negate();
					in.setType(LogicType.DISJUNCTION);
					toCNF(in);
				}
				break;
			case BICONDITIONAL: 
				List<LogicaProp> children = in.getChildren();
				in.setType(LogicType.CONJUNCTION);
				in.setChildren(
						LogicaProp.ofOp(children.get(0).getCopy().negate(), "or", children.get(1).getCopy()),
						LogicaProp.ofOp(children.get(1).negate(), "or", children.get(0)));
				toCNF(in);
				break;
			case IMPLICATION: 
				in.getLeft().negate();
				in.setType(LogicType.DISJUNCTION);
				toCNF(in);
				break;
			}
		}
		return in;
	}

	
	public static Set<Set<LogicaProp>> getClauses(LogicaProp input) {
		LogicaProp CNF = toCNF(input);
		Set<LogicaProp> disjunctions = CNF.stream()
			.filter(expr->expr.getType()==LogicType.DISJUNCTION ||
			expr.isAtom())
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
		
		//Header
		System.out.println(atoms.toString().replaceAll("[\\[,\\]]", "")
				+ "\t" + input);
		
		for (int i = 0; i < length; i++) {
			String binary = String.format("%" + atoms.size() + "s", 
						Integer.toBinaryString(i))
					.replace(" ", "0")
					.replace("", " ")
					.trim();
			String[] values = binary.split(" ");
					
			Map<String, Boolean> mappedValues = atoms.stream()
					.collect(Collectors.toMap(
							at->at,
							s->values[atoms.indexOf(s)].equals("1")));
			
			System.out.println(binary + "\t" + 
					String.format("%" + input.toString().length()/2 + "s", 
							(input.eval(mappedValues)? "1" : "0")));
		}
		
		
		
		
	}
	
}
