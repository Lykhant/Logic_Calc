package es.etomas.logiccalc.main;

import es.etomas.logiccalc.gui.CalcFrame;

public class Main {

	public static void main(String[] args) {
		
		CalcFrame frame = new CalcFrame();
		String toMatch = "(a->b),(c,d),(e,f,g),(h,i),((a),(b))";
//		Pattern clauses = Pattern.compile("(\\([^\\(\\)]*\\))");
//		Matcher matcher = clauses.matcher(toMatch);
//		matcher.results().forEach(match->System.out.println(match.group()));
		
	}

}
