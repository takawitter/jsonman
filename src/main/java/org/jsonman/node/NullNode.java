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
package org.jsonman.node;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jsonman.Node;
import org.jsonman.NodeVisitor;

public class NullNode extends AbstractNode{
	public NullNode(){
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

	@Override
	public boolean isNull(){
		return true;
	}

	@Override
	public Object getValue(){
		return null;
	}

	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException("You can't set value to null node.");
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Node createEmpty() {
		return new NullNode();
	}
}
