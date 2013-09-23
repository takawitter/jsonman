package org.jsonman.ks.path;

import org.jsonman.Node;
import org.jsonman.NodeAdapter;
import org.jsonman.ks.util.Holder;
import org.jsonman.node.BooleanNode;
import org.jsonman.node.NumberNode;
import org.jsonman.node.StringNode;

public class Condition {
	public enum Operator{ COMPLETEMATCH};

	public Condition() {
	}

	public Condition(String name, Object value, Operator operator) {
		this.name = name;
		this.value = value;
		this.operator = operator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public boolean matched(Node node){
		final Holder<Boolean> rv = new Holder<>(false);
		node.visit(new NodeAdapter() {
			@Override
			public void accept(BooleanNode node) {
				rv.set(value.equals(node.getValue()));
			}
			@Override
			public void accept(NumberNode node) {
				rv.set(value.equals(node.getValue()));
			}
			@Override
			public void accept(StringNode node) {
				rv.set(value.equals(node.getValue()));
			}
		});
		return rv.get();
	}

	private String name;
	private Object value;
	private Operator operator;
}
