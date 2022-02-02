package parser;

import java.util.Set;

import functions.PropLogicUtils;

public class MainParser {

	public static void main(String[] args) {
		
		String toParse1 = "a and b -> c and d";
		String toParse2 = "a and b or c and b";
		PropLogic parsed1 = PropLogic.parse(toParse1);
		PropLogic parsed2 = PropLogic.parse(toParse2);
		System.out.println("Entrada:\n  " + toParse1 + "\n  " + toParse2);
		System.out.println("Resultado:\n  " + parsed1 + "\n  " + parsed2);
		
		Set<PropLogic> clause1 = PropLogicUtils.clauseFromString("!q,r");
		Set<PropLogic> clause2 = PropLogicUtils.clauseFromString("!r,p");
		Set<PropLogic> clause3 = PropLogicUtils.clauseFromString("!r,q");
		Set<PropLogic> clause4 = PropLogicUtils.clauseFromString("!p,q,r");
		Set<PropLogic> clause5 = PropLogicUtils.clauseFromString("p,q");
		Set<PropLogic> clause6 = PropLogicUtils.clauseFromString("!p,!q");
		
		Set<Set<PropLogic>> clauses = Set.of(
				clause1,
				clause2,
				clause3,
				clause4,
				clause5,
				clause6
				);
		
		System.out.println("Clauses: " + clause1 + " " + clause2 + " " + clause3 + " " + clause4);
	
		System.out.println(PropLogicUtils.dpll(clauses, false));
		
//		System.out.println(LogicaPropUtils.resolution(Set.of(clause1, clause2, clause3, clause4), true).toString());
	}

}
