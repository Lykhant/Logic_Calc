package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class LogicaProp implements Iterable<LogicaProp> {

	public static enum LogicType {Conjunction, Disjunction, Implication, exDisjunction, Atom}	
	
	protected final String label;
	private LogicType tipo;
	protected List<LogicaProp> children;
	protected Boolean negated;
	protected LogicaProp parent;


	//Atom
	public LogicaProp(String label) {
		this.label = label;
		this.children = List.of();
		this.tipo = LogicType.Atom;
		this.parent = null;
		this.negated = false;
	}

	//Operation
	private LogicaProp(LogicaProp left, String label, LogicaProp right) {
		this.label = null;
		children = List.of(left,right);
		
		switch (label) {
		case "->":
			this.tipo = LogicType.Implication;
			break;
		
		case "<->":
			this.tipo = LogicType.exDisjunction;
			break;
			
		case "and":
			this.tipo = LogicType.Conjunction;
			break;
		
		case "or":
			this.tipo = LogicType.Disjunction;
			break;
			
		default:
			throw new IllegalArgumentException("Invalid operation type");
		}
		
		this.parent = null;
		this.negated = false;
		this.getLeft().setParent(this);
		this.getRight().setParent(this);
	}

	private LogicaProp(LogicaProp logicaProp) {
		this.label = logicaProp.getLabel();
		this.tipo = logicaProp.getTipo();
		this.children = logicaProp.getChildren().stream()
				.map(expr->expr.getCopy())
				.toList();
		this.negated = logicaProp.isNegated();
		this.parent = new LogicaProp(logicaProp.parent);
	}
	
	/**
	 * Returns a propositional logic expression given a String
	 * @param s String to parse
	 * @return The parsed logic expression 
	 */
	public static LogicaProp parse(String s) {

		CharStream stream = CharStreams.fromString(s);
		LogicaLexer lexer = new LogicaLexer(stream);
		TokenStream tokens = new CommonTokenStream(lexer);
		LogicaParser parser = new LogicaParser(tokens);
		ParseTree parseTree = parser.expr();
		LogicaProp res = parseTree.accept(new LogicaVisitorC());

		return res;
	}

	public static LogicaProp ofOp(LogicaProp leftChild, String op, LogicaProp rightChild) {
		return new LogicaProp(leftChild, op, rightChild);
	}

	public static LogicaProp ofAtom(String label) {
		return new LogicaProp(label);
	}
	
	public LogicaProp getCopy() {
		return new LogicaProp(this);
	}

	public String getLabel() {
		return this.label;
	}

	public Boolean isNegated() {
		return this.negated;
	}

	public LogicaProp negate() {
		negated = !negated;
		return this;
	}
	
	public void setType(LogicType type) {
		//The target type and current type must both either be atoms or operators
		if(!(this.tipo==LogicType.Atom ^ type==LogicType.Atom)) {
			throw new IllegalArgumentException("Cannot convert "
					+ "atom to operator or viceversa");
		}
		this.tipo = type;
	}
	
	public void setChildren(LogicaProp leftChild, LogicaProp rightChild) {
		this.children = List.of(leftChild, rightChild);
		leftChild.setParent(this);
		rightChild.setParent(this);
	}
	
	public LogicaProp getLeft() {
		return this.children.get(0);
	}

	public LogicaProp getRight() {
		return this.children.get(1);
	}

	public LogicType getTipo() {
		return this.tipo;
	}

	/**
	 * Creates a copy of the expression and negates it
	 * @return The negated expression
	 */
	public LogicaProp getComplementary() {
		LogicaProp res = this.getCopy();
		res.setNegated(!this.negated);
		return res;
	}
	
	public void setNegated(Boolean neg) {
		this.negated = neg;
	}
	
	public void setParent(LogicaProp father) {
		this.parent = father;
	}

	public Boolean hasFather() {
		return this.parent != null;
	}

	public LogicaProp getFather() {
		return this.parent;
	}

	public Boolean isLeft() {
		return this.hasFather() && this.parent.getLeft() == this;
	}

	public Boolean isRight() {
		return this.hasFather() && this.parent.getRight() == this;
	}

	public List<LogicaProp> getChildren() {
		return !this.isAtom()?
				List.of(this.getLeft(),this.getRight()):null;
	}

	public Boolean isAtom() {
		return this.tipo == LogicType.Atom;
	}
	
	@Override
	public String toString() {

		String res;

		if(this.tipo == LogicType.Atom) {
			res = this.label;
		} else {
			res = "(" + this.getLeft() +
					" " + this.label + " " +
					this.getRight() + ")";
		}

		res = (this.negated? "!" + res:res);

		return res;
	}

	
	//Iterator con recorrido por profundidad
	@Override
	public Iterator<LogicaProp> iterator() {
		return DepthPathLogicaProp.of(this);
	}
	
	public Stream<LogicaProp> stream() {
		Iterator<LogicaProp> iter = DepthPathLogicaProp.of(this);
		List<LogicaProp> list = new ArrayList<>();
		iter.forEachRemaining(i->list.add(i));
		return list.stream();
	}
	
	public static class DepthPathLogicaProp implements Iterator<LogicaProp>{

		public static DepthPathLogicaProp of(LogicaProp logic) {
			return new DepthPathLogicaProp(logic);
		}
		
		Stack<LogicaProp> stack;
		
		public DepthPathLogicaProp(LogicaProp logic) {
			this.stack = new Stack<>();
			this.stack.add(logic);
		}
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return !stack.empty();
		}

		@Override
		public LogicaProp next() {
			// TODO Auto-generated method stub
			LogicaProp current = stack.pop();
			if(!current.isAtom()) {
				stack.add(current.getLeft());
				stack.add(current.getRight());
			}
			return current;
		}
		
		
		
	}
	
}
