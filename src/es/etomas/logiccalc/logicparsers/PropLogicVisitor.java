package es.etomas.logiccalc.logicparsers;
// Generated from PropLogic.g4 by ANTLR 4.9.3
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PropLogicParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PropLogicVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code negated}
	 * labeled alternative in {@link PropLogicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegated(PropLogicParser.NegatedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code implications}
	 * labeled alternative in {@link PropLogicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImplications(PropLogicParser.ImplicationsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conjDisjunction}
	 * labeled alternative in {@link PropLogicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConjDisjunction(PropLogicParser.ConjDisjunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code atom}
	 * labeled alternative in {@link PropLogicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(PropLogicParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link PropLogicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesis(PropLogicParser.ParenthesisContext ctx);
}