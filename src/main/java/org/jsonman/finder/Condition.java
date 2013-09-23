/*
 * Copyright 2013 Takao Nakaguchi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsonman.finder;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jsonman.Node;
import org.jsonman.NodeAdapter;
import org.jsonman.node.BooleanNode;
import org.jsonman.node.NumberNode;
import org.jsonman.node.StringNode;
import org.jsonman.util.Holder;

public class Condition {
	public enum Operator{ COMPLETEMATCH};

	public Condition() {
	}

	public Condition(String name, Object value, Operator operator) {
		this.name = name;
		this.value = value;
		this.operator = operator;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
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
