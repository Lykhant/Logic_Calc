package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class LogicaProp implements Iterable<LogicaProp> {

	public static enum LogicType {Conjunction, Disjunction, Implication, exDisjunction, Atom}	
	
	protected String label;
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
		
		switch (label) {
		case "->":
			this.tipo = LogicType.Implication;
			children = List.of(left,right);
			break;
		
		case "<->":
			this.tipo = LogicType.exDisjunction;
			children = List.of(left,right);
			break;
			
		case "and":
			if(!left.equals(right)) {
				this.tipo = LogicType.Conjunction;
				children = List.of(left,right);
			} else {
				//a and a = a
				this.tipo = left.getTipo();
				this.children = left.getChildren();
			}
			break;
		
		case "or":
			if(!left.equals(right)) {
				this.tipo = LogicType.Disjunction;
				children = List.of(left,right);
			} else {
				//a or a = a
				this.tipo = left.getTipo();
				this.children = left.getChildren();
			}
			break;
			
		default:
			throw new IllegalArgumentException("Invalid operation type");
		}
		
		this.parent = null;
		this.negated = false;
		
		if(this.tipo!=LogicType.Atom) {
			this.getLeft().setParent(this);
			this.getRight().setParent(this);
			this.label = label;
		} else {
			this.label = left.getLabel();
		}
	}

	private LogicaProp(LogicaProp logicaProp) {
		this.label = logicaProp.getLabel();
		this.tipo = logicaProp.getTipo();
		//To avoid endless recursion, the parent is set to null
		//The children will get their parent set after being copied
		this.parent = null;
		this.children = logicaProp.getChildren()
				.stream()
				.map(expr->expr.getCopy().setParent(logicaProp))
				.collect(Collectors.toList());
		
		this.negated = logicaProp.isNegated();
		
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
		if(this.tipo==LogicType.Atom || type==LogicType.Atom) {
			throw new IllegalArgumentException("Cannot modify or "
					+ "convert atoms");
		}
		this.tipo = type;
		switch (type) {
		case Disjunction: this.label = "or";
			break;
		case Conjunction: this.label = "and";
			break;
		case exDisjunction: this.label = "<->";
			break;
		case Implication: this.label = "->";
			break;
		default: throw new IllegalArgumentException("Invalid conversion");
		}
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
	
	public LogicaProp setParent(LogicaProp father) {
		this.parent = father;
		return this;
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
		return this.children;
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

	@Override
	public int hashCode() {
		return Objects.hash(children, label, negated, tipo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogicaProp other = (LogicaProp) obj;
		return Objects.equals(children, other.children) && Objects.equals(label, other.label)
				&& Objects.equals(negated, other.negated) && tipo == other.tipo;
	}
	
}
