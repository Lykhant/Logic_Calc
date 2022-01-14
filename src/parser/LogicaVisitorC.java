package parser;

import parser.LogicaParser.ConjDisyuncionContext;
import parser.LogicaParser.FAtomicaContext;
import parser.LogicaParser.ImplicacionesContext;
import parser.LogicaParser.NegadoContext;
import parser.LogicaParser.ParentesisContext;

public class LogicaVisitorC extends LogicaBaseVisitor<LogicaProp>{

	@Override
	public LogicaProp visitParentesis(ParentesisContext ctx) {
		// TODO Auto-generated method stub
		return visit(ctx.getChild(1));
	}

	@Override
	public LogicaProp visitImplicaciones(ImplicacionesContext ctx) {
		// TODO Auto-generated method stub
		String impl = ctx.getChild(1).getText();
		LogicaProp left = visit(ctx.left);
		LogicaProp right = visit(ctx.right);
		return LogicaProp.ofOp(left, impl, right);
	}

	@Override
	public LogicaProp visitNegado(NegadoContext ctx) {
		// TODO Auto-generated method stub
		return visit(ctx.getChild(1)).getComplementary();
	}

	@Override
	public LogicaProp visitConjDisyuncion(ConjDisyuncionContext ctx) {
		// TODO Auto-generated method stub
		String impl = ctx.getChild(1).getText();
		LogicaProp left = visit(ctx.left);
		LogicaProp right = visit(ctx.right);
		return LogicaProp.ofOp(left, impl, right);
	}

	@Override
	public LogicaProp visitFAtomica(FAtomicaContext ctx) {
		// TODO Auto-generated method stub
		String atom = ctx.getText();
		return LogicaProp.ofAtom(atom);
	}

}
