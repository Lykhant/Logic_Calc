package parser;
// Generated from Logica.g4 by ANTLR 4.9.3
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LogicaParser}.
 */
public interface LogicaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code parentesis}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParentesis(LogicaParser.ParentesisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parentesis}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParentesis(LogicaParser.ParentesisContext ctx);
	/**
	 * Enter a parse tree produced by the {@code implicaciones}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterImplicaciones(LogicaParser.ImplicacionesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code implicaciones}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitImplicaciones(LogicaParser.ImplicacionesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negado}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNegado(LogicaParser.NegadoContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negado}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNegado(LogicaParser.NegadoContext ctx);
	/**
	 * Enter a parse tree produced by the {@code conjDisyuncion}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterConjDisyuncion(LogicaParser.ConjDisyuncionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code conjDisyuncion}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitConjDisyuncion(LogicaParser.ConjDisyuncionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fAtomica}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFAtomica(LogicaParser.FAtomicaContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fAtomica}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFAtomica(LogicaParser.FAtomicaContext ctx);
}