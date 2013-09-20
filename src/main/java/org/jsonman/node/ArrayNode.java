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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jsonman.Node;
import org.jsonman.NodeFactory;
import org.jsonman.NodeVisitor;

public class ArrayNode extends AbstractNode{
	public ArrayNode(List<Object> array){
		this.array = array;
	}

	public ArrayNode(Node parent, Object childId, List<Object> array){
		super(parent, childId);
		this.array = array;
	}

	@Override
	public boolean isArray(){
		return true;
	}

	@Override
	public List<Object> getValue(){
		return array;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value){
		setRealValue((List<Object>)value);
	}

	public void setRealValue(List<Object> array) {
		this.array = array;
	}

	@Override
	public Iterable<Node> getAllChildren() {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				return new Iterator<Node>() {
					@Override
					public boolean hasNext() {
						return entries.hasNext();
					}
					@Override
					public Node next() {
						Object index = entries.nextIndex();
						Object v = entries.next();
						return NodeFactory.create(ArrayNode.this, index, v);
					}
					@Override
					public void remove() {
						entries.remove();
					}
					private ListIterator<Object> entries = array.listIterator();
				};
			}
		};
	}

	@Override
	public void visitAllChildren(NodeVisitor visitor) {
		ListIterator<Object> entries = array.listIterator();
		while(entries.hasNext()){
			Object index = entries.nextIndex();
			Object v = entries.next();
			NodeFactory.create(this, index, v).visit(visitor);
		}
	}

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.accept(this);
	}

	public Node addChild(Object value){
		int index = array.size();
		array.add(value);
		return NodeFactory.create(this, index, value);
	}

	@Override
	public Node createEmpty() {
		return new ArrayNode(getParent(), getChildId(), new ArrayList<>());
	}

	@Override
	public void appendChild(Node child) {
		array.add(child.getValue());
	}

	private List<Object> array;
}
