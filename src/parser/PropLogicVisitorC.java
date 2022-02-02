package parser;

import parser.PropLogicParser.AtomContext;
import parser.PropLogicParser.ConjDisjunctionContext;
import parser.PropLogicParser.ImplicationsContext;
import parser.PropLogicParser.NegatedContext;
import parser.PropLogicParser.ParenthesisContext;

public class PropLogicVisitorC extends PropLogicBaseVisitor<PropLogic>{

	@Override
	public PropLogic visitNegated(NegatedContext ctx) {
		return visit(ctx.getChild(1)).negate();
	}

	@Override
	public PropLogic visitImplications(ImplicationsContext ctx) {
		String op = ctx.getChild(1).getText();
		PropLogic left = visit(ctx.left);
		PropLogic right = visit(ctx.right);
		return PropLogic.ofOp(left, op, right);
	}

	@Override
	public PropLogic visitConjDisjunction(ConjDisjunctionContext ctx) {
		String op = ctx.getChild(1).getText();
		PropLogic left = visit(ctx.left);
		PropLogic right = visit(ctx.right);
		return PropLogic.ofOp(left, op, right);
	}

	@Override
	public PropLogic visitAtom(AtomContext ctx) {
		String atom = ctx.getText();
		return PropLogic.ofAtom(atom, false);
	}

	@Override
	public PropLogic visitParenthesis(ParenthesisContext ctx) {
		return visit(ctx.getChild(1));
	}
	
	
}
