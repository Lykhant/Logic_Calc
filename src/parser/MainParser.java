package parser;

import java.util.Map;
import java.util.Set;

import functions.LogicaPropUtils;

public class MainParser {

	public static void main(String[] args) {
		
		String toParse1 = "a and b -> c and d";
		String toParse2 = "a and b or c and b";
		LogicaProp parsed1 = LogicaProp.parse(toParse1);
		LogicaProp parsed2 = LogicaProp.parse(toParse2);
		System.out.println("Entrada:\n  " + toParse1 + "\n  " + toParse2);
		System.out.println("Resultado:\n  " + parsed1 + "\n  " + parsed2);
		
		Set<LogicaProp> clause1 = LogicaPropUtils.clauseFromString("!q,r");
		Set<LogicaProp> clause2 = LogicaPropUtils.clauseFromString("!r,p");
		Set<LogicaProp> clause3 = LogicaPropUtils.clauseFromString("!r,q");
		Set<LogicaProp> clause4 = LogicaPropUtils.clauseFromString("!p,q,r");
		Set<LogicaProp> clause5 = LogicaPropUtils.clauseFromString("p,q");
		Set<LogicaProp> clause6 = LogicaPropUtils.clauseFromString("!p,!q");
		
		Set<Set<LogicaProp>> clauses = Set.of(
				clause1,
				clause2,
				clause3,
				clause4,
				clause5,
				clause6
				);
		
		System.out.println("Clauses: " + clause1 + " " + clause2 + " " + clause3 + " " + clause4);
	
		System.out.println(LogicaPropUtils.dpll(clauses, false));
		
//		System.out.println(LogicaPropUtils.resolution(Set.of(clause1, clause2, clause3, clause4), true).toString());
	}

}
