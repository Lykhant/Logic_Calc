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
		
		Set<LogicaProp> clause1 = LogicaPropUtils.clauseFromString("p,q");
		Set<LogicaProp> clause2 = LogicaPropUtils.clauseFromString("!p,q");
		Set<LogicaProp> clause3 = LogicaPropUtils.clauseFromString("p,!q");
		Set<LogicaProp> clause4 = LogicaPropUtils.clauseFromString("!p,!q");
		
		
		System.out.println("Clauses: " + clause1 + " " + clause2 + " " + clause3 + " " + clause4);
	
		System.out.println(LogicaPropUtils.resolution(Set.of(clause1, clause2, clause3, clause4), false).toString());
	}

}
