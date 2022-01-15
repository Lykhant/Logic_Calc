package parser;

import java.util.Map;
import java.util.stream.Collectors;

import functions.LogicaPropUtils;

public class MainParser {

	public static void main(String[] args) {
		
		String toParse = "a and b";
		Map<String, Boolean> values = Map.of("a", true, "b", true);
		
		
		LogicaProp parsed = LogicaProp.parse(toParse);
		System.out.println("Entrada: " + toParse);
		System.out.println(parsed + "\n");
		
		
		System.out.println("Is the formula in CNF? " + LogicaPropUtils.isCNF(parsed));
		System.out.println("CNF version: " + LogicaPropUtils.toCNF(parsed));
		
		System.out.println(LogicaPropUtils.getClauses(parsed));
		System.out.println("\nValues: " + values);
		System.out.println("Expression evaluated to: " + parsed.eval(values));
		
		
		
		
	}

}
