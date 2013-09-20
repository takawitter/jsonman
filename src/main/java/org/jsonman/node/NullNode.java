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

import org.jsonman.Node;
import org.jsonman.NodeVisitor;

public class NullNode extends AbstractNode{
	public NullNode(){
	}

	public NullNode(Node parent, Object childId){
		super(parent, childId);
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
		setValue((Boolean)value);
	}

	public void setValue(Boolean value) {
		throw new UnsupportedOperationException("You can't set value to null node.");
	}

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.accept(this);
	}

	@Override
	public Node createEmpty() {
		return new NullNode(getParent(), getChildId());
	}
}
