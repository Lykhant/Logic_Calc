package functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import parser.LogicaProp;
import parser.LogicaProp.LogicType;

public class LogicaPropUtils {
	
	public static List<LogicaProp> opList(LogicaProp expr) {
		return expr.stream()
				.filter(e->!e.isAtom())
				.toList();
	}
	
	public static Boolean isCNF(LogicaProp logic) {
		
		//Skip other calculations if the expression is an operation and is negated
		Boolean res = logic.isAtom() || !logic.isNegated();
		
		if(res) {
			switch (logic.getTipo()) {
			//If disjunction, it must not have an AND nested inside
			case Disjunction:
				res = logic.getChildren().stream()
					.noneMatch(expr->expr.getTipo()==LogicType.Conjunction);
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
	 * <p>
	 * Use {@link LogicaProp#getCopy()} for getting a copy if you don't
	 * want to change the original expression.
	 * 
	 * @param in The expression to modify
	 * 
	 */
	
	public static void toCNF(LogicaProp in) {
		
		switch (in.getTipo()) {
		case Atom: break;
		case Disjunction: 
			if(!in.isNegated()) {
				toCNF(in.getLeft());
				toCNF(in.getRight());
				
				//Distribution law
				List<LogicaProp> leftChildren = in.getLeft().isAtom()?
						List.of(in.getLeft()):
						in.getLeft().getChildren();
				List<LogicaProp> rightChildren = in.getRight().isAtom()?
						List.of(in.getRight()):
						in.getRight().getChildren();
				
				List<LogicaProp> zipped = new ArrayList<>();
				
				for (LogicaProp lChild : leftChildren) {
					for (LogicaProp rChild : rightChildren) {
						zipped.add(LogicaProp.ofOp(lChild, "or", rChild));
					}
				}
				
				//(a or c), (a or d), (b or c), (b or d)
				//(a or c) and ((a or d))
				//(((a or c) and (a or d)) and (b or c))
				LogicaProp parent = LogicaProp.ofOp(zipped.get(0), "and", zipped.get(1));
				//Nest expressions
				for (int i = 2; i < zipped.size()-1; i++) {
					parent = LogicaProp.ofOp(parent, "and", zipped.get(i));
				}
				
				
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
				toCNF(in.getLeft());
				toCNF(in.getRight());
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
	
}
