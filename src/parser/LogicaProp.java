package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

	public static enum LogicType {CONJUNCTION, DISJUNCTION, IMPLICATION, BICONDITIONAL, ATOM}	

	
	protected String label;
	private LogicType type;
	protected List<LogicaProp> children;
	protected Boolean negated;
	protected LogicaProp parent;


	//Atom
	public LogicaProp(String label, Boolean negated) {
		this.label = label;
		this.children = List.of();
		this.type = LogicType.ATOM;
		this.parent = null;
		this.negated = negated;
	}

	//Operation
	private LogicaProp(LogicaProp left, String label, LogicaProp right) {
		
		switch (label) {
		case "->":
			this.type = LogicType.IMPLICATION;
			children = List.of(left,right);
			break;
		
		case "<->":
			this.type = LogicType.BICONDITIONAL;
			children = List.of(left,right);
			break;
			
		case "and":
			if(!left.equals(right)) {
				this.type = LogicType.CONJUNCTION;
				children = List.of(left,right);
			} else {
				//a and a = a
				this.type = left.getType();
				this.children = left.getChildren();
			}
			break;
		
		case "or":
			if(!left.equals(right)) {
				this.type = LogicType.DISJUNCTION;
				children = List.of(left,right);
			} else {
				//a or a = a
				this.type = left.getType();
				this.children = left.getChildren();
			}
			break;
			
		default:
			throw new IllegalArgumentException("Invalid operation type");
		}
		
		this.parent = null;
		this.negated = false;
		
		if(this.type!=LogicType.ATOM) {
			this.getLeft().setParent(this);
			this.getRight().setParent(this);
			this.label = label;
		} else {
			this.label = left.getLabel();
		}
	}

	private LogicaProp(LogicaProp logicaProp) {
		this.label = logicaProp.getLabel();
		this.type = logicaProp.getType();
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

	/**
	 * Creates an expression in propositional logic from an operation type
	 * and its children.
	 * 
	 * @param leftChild The first expression
	 * @param op The type of operation to represent
	 * @param rightChild The second expression
	 * @return The new expression in propositional logic
	 */
	
	//TODO: Change from taking String to the LogicType enumerate
	public static LogicaProp ofOp(LogicaProp leftChild, String op, LogicaProp rightChild) {
		return new LogicaProp(leftChild, op, rightChild);
	}

	/**
	 * Creates an atom to be used in propositional logic operations.
	 * 
	 * @param label The string that represents the variable
	 * @return The new expression in propositional logic
	 */
	public static LogicaProp ofAtom(String label, Boolean negated) {
		return new LogicaProp(label, negated);
	}
	
	/**
	 * Creates a deep copy of the given logic expression.
	 * @return The copied logic expression
	 */
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
	
	/**
	 * Returns the truth value of the expression given a Map that assigns a value
	 * to each variable.
	 * 
	 * @param values The Map with the variables to replace and their values
	 * @return The truth value
	 * @throws IllegalArgumentException If any of the variables are not in the map
	 */
	public Boolean eval(Map<String, Boolean> values) {
		Boolean res = null;
		
		switch (this.getType()) {
		//Atoms get their value directly from the Map
		case ATOM:
			if(!values.containsKey(this.label)) {
				throw new IllegalArgumentException("Variable " +
			this.label + " is not mapped to any value");
			}
			
			res = values.get(this.label);
			break;
		//Operations make use of the values of their children
		case CONJUNCTION: 
			res = this.getLeft().eval(values) && this.getRight().eval(values);
			break;
		case DISJUNCTION: 
			res = this.getLeft().eval(values) || this.getRight().eval(values);
			break;
		case BICONDITIONAL:
			res = this.getLeft().eval(values) == this.getRight().eval(values);
			break;
		case IMPLICATION: 
			res = !this.getLeft().eval(values) || this.getRight().eval(values);
			break;
		default:
			break;
		}
		//Invert the value if the expression is negated
		res = res ^ this.negated;
		return res;
	}
	
	public void setType(LogicType type) {
		
		//The target type and current type must both either be atoms or operators
		if(this.type==LogicType.ATOM || type==LogicType.ATOM) {
			throw new IllegalArgumentException("Cannot modify or "
					+ "convert atoms");
		}
		this.type = type;
		switch (type) {
		case DISJUNCTION: this.label = "or";
			break;
		case CONJUNCTION: this.label = "and";
			break;
		case BICONDITIONAL: this.label = "<->";
			break;
		case IMPLICATION: this.label = "->";
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

	public LogicType getType() {
		return this.type;
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
		return this.type == LogicType.ATOM;
	}
	
	@Override
	public String toString() {

		String res;

		if(this.type == LogicType.ATOM) {
			res = this.label;
		} else {
			res = "(" + this.getLeft() +
					" " + this.label + " " +
					this.getRight() + ")";
		}

		res = (this.negated? "!" + res:res);

		return res;
	}

	
	//Depth-first iterator
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
		return Objects.hash(children, label, negated, type);
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
				&& Objects.equals(negated, other.negated) && type == other.type;
	}
	
}
