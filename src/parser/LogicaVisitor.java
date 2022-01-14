package parser;
// Generated from Logica.g4 by ANTLR 4.9.3
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LogicaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LogicaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code parentesis}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentesis(LogicaParser.ParentesisContext ctx);
	/**
	 * Visit a parse tree produced by the {@code implicaciones}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImplicaciones(LogicaParser.ImplicacionesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code negado}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegado(LogicaParser.NegadoContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conjDisyuncion}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConjDisyuncion(LogicaParser.ConjDisyuncionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fAtomica}
	 * labeled alternative in {@link LogicaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFAtomica(LogicaParser.FAtomicaContext ctx);
}