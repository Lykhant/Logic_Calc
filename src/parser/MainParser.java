package parser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import functions.LogicaPropUtils;

public class MainParser {

	public static void main(String[] args) {
		
		String toParse = "a or !a and b";
		Map<String, Boolean> values = Map.of("a", true, "b", false);
		List<Integer> ints = List.of(1,2,3,4);
		
		LogicaProp parsed = LogicaProp.parse(toParse);
		System.out.println("Entrada: " + toParse);
		System.out.println(parsed + "\n");
		LogicaPropUtils.truthTable(parsed);

		System.out.println("Is the formula in CNF? " + LogicaPropUtils.isCNF(parsed));
		System.out.println("CNF version: " + LogicaPropUtils.toCNF(parsed));
		System.out.println(parsed);
		
		System.out.println(LogicaPropUtils.getClauses(parsed));
		System.out.println("\nValues: " + values);
//		System.out.println("Expression evaluated to: " + parsed.eval(values));
		
		Integer a = 0b00100;
		Integer b = 0b00011;
		
		
		
	}

}
