package parser;

import java.util.stream.Collectors;

import functions.LogicaPropUtils;

public class MainParser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String toParse = "a and (b or e) and c and (d or e)";
		
		LogicaProp parsed = LogicaProp.parse(toParse);
		System.out.println("Entrada: " + toParse);
		System.out.println(parsed);
		System.out.println(parsed.getComplementary());
		System.out.println("Átomos: "
				+ parsed.stream()
				.filter(expr->expr.isAtom())
				.collect(Collectors.toList()));
		System.out.println("Operaciones: " + LogicaPropUtils.opList(parsed));
		
		System.out.println("Is the formula in CNF? " + LogicaPropUtils.isCNF(parsed));
		
	}

}
