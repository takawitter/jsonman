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
	public ArrayNode() {
		this.array = new ArrayList<>();
	}

	public ArrayNode(List<Object> array){
		this.array = array;
	}

	@Override
	public ArrayNode clone() {
		return new ArrayNode(new ArrayList<Object>(array));
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
		setValue((List<Object>)value);
	}

	public void setValue(List<Object> array) {
		this.array = array;
	}

	public Node getChild(int index){
		if(index >= array.size()) return null;
		else return NodeFactory.create(array.get(index));
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getChildAs(int index, Class<T> clazz){
		if(index >= array.size()) return null;
		else return (T)NodeFactory.create(array.get(index));
	}

	public void setChild(int index, Node child){
		if(index < array.size()){
			array.set(index, child.getValue());
		} else{
			for(int i = array.size(); i < index; i++){
				array.add(null);
			}
			array.add(child.getValue());
		}
	}

	@Override
	public Iterable<Node> getChildren() {
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
						return NodeFactory.create(entries.next());
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
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public Node addChild(Object value){
		array.add(value);
		return NodeFactory.create(value);
	}

	@Override
	public Node createEmpty() {
		return new ArrayNode(new ArrayList<>());
	}

	public void appendChild(Node child) {
		array.add(child.getValue());
	}

	private List<Object> array;
}
