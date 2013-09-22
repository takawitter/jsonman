package org.jsonman.ks.path;

public class Fragment {
	public Fragment() {
	}

	public Fragment(String name) {
		this.name = name;
	}

	public Fragment(String name, Condition condition) {
		this.name = name;
		this.condition = condition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	private String name;
	private Condition condition;
}
