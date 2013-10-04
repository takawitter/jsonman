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


public class StringNode extends AbstractNode{
	public StringNode(String value){
		this.value = value;
	}

	@Override
	public boolean isString(){
		return true;
	}

	@Override
	public String getValue(){
		return value;
	}

	@Override
	public void setValue(Object value) {
		setValue((String)value);
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Node createEmpty() {
		return new StringNode(null);
	}

	private String value;
}
