package parser;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import functions.LogicaPropUtils;

public class MainParser {

	public static void main(String[] args) {
		
		String toParse = "!(a or b)";
		Map<String, Boolean> values = Map.of("a", true, "b", false);
		
		LogicaProp parsed = LogicaProp.parse(toParse);
		System.out.println("Entrada: " + toParse);
		System.out.println("Resultado: " + parsed + "\n");
//		LogicaPropUtils.truthTable(parsed);
//
		System.out.println("Is the formula in CNF? " + LogicaPropUtils.isCNF(parsed));
		LogicaProp cnf = LogicaPropUtils.toCNF(parsed);
		parsed.getLeft().negate();
		System.out.println("CNF version: " + cnf);
//		System.out.println(parsed);
//		
//		System.out.println(LogicaPropUtils.getClauses(parsed));
//		System.out.println("\nValues: " + values);
//		System.out.println("Expression evaluated to: " + parsed.eval(values));
		
		
		
		
//		System.out.println(LogicaPropUtils.truthTree(parsed, false));
	}

}
