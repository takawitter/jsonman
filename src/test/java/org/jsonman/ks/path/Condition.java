package org.jsonman.ks.path;

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

	private String name;
	private Object value;
	private Operator operator;
}
