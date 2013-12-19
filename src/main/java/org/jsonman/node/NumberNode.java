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


public class NumberNode extends AbstractNode{
	public NumberNode(Number value){
		this.value = value;
	}

	@Override
	public NumberNode clone() {
		return new NumberNode(value);
	}

	@Override
	public boolean isNumber(){
		return true;
	}

	@Override
	public Number getValue(){
		return value;
	}

	public int getIntValue(){
		return value.intValue();
	}

	public double getDoubleValue(){
		return value.doubleValue();
	}

	@Override
	public void setValue(Object value) {
		setValue((Number)value);
	}

	public void setValue(Number value) {
		this.value = value;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Node createEmpty() {
		return new NumberNode(null);
	}

	private Number value;
}
