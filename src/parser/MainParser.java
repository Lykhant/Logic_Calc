package parser;

import java.util.stream.Collectors;

import functions.LogicaPropUtils;

public class MainParser {

	public static void main(String[] args) {
		
		String toParse = "a or !b or c and d";
		
		LogicaProp parsed = LogicaProp.parse(toParse);
		System.out.println("Entrada: " + toParse);
		System.out.println(parsed);
		
		System.out.println("Is the formula in CNF? " + LogicaPropUtils.isCNF(parsed));
		System.out.println("CNF version: " + LogicaPropUtils.toCNF(parsed));
		
		System.out.println(LogicaPropUtils.getClauses(parsed));
		System.out.println(parsed);
	}

}
