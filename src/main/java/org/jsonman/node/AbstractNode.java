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

public abstract class AbstractNode implements Node{
	public AbstractNode(){
	}

	@Override
	public boolean isString(){
		return false;
	}

	@Override
	public boolean isNumber(){
		return false;
	}

	@Override
	public boolean isArray(){
		return false;
	}

	@Override
	public boolean isMap(){
		return false;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}

	@Override
	public boolean isNull() {
		return false;
	}

	@Override
	public Node getChild(Object childId){
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<Node> getAllChildren() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitAllChildren(final NodeVisitor visitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void appendChild(Node child) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void appendChild(String childId, Node child) {
		throw new UnsupportedOperationException();
	}
}
