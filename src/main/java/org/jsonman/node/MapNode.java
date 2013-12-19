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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jsonman.Node;
import org.jsonman.NodeFactory;
import org.jsonman.NodeVisitor;

public class MapNode extends AbstractNode{
	public MapNode() {
		this.map = new LinkedHashMap<String, Object>();
	}

	public MapNode(Map<String, Object> map){
		this.map = map;
	}

	@Override
	public MapNode clone() {
		return new MapNode(new LinkedHashMap<String, Object>(map));
	}

	@Override
	public boolean isMap(){
		return true;
	}

	@Override
	public Map<String, Object> getValue(){
		return map;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value){
		setValue((Map<String, Object>)value);
	}

	public void setValue(Map<String, Object> map) {
		this.map = map;
	}

	public Node getChild(String name) {
		Object value = map.get(name);
		if(value == null) return null;
		return NodeFactory.create(value);
	}

	public Object getChildValue(String name) {
		return map.get(name);
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getChildAs(String name, Class<T> clazz) {
		Object value = map.get(name);
		if(value == null) return null;
		return (T)NodeFactory.create(value);
	}

	public void setChild(String name, Node value){
		map.put(name, value.getValue());
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
						Map.Entry<String, Object> n = entries.next();
						return NodeFactory.create(n.getValue());
					}
					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
					private Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
				};
			}
		};
	}

	public Iterable<Pair<String, Node>> getChildrenWithName() {
		return new Iterable<Pair<String, Node>>() {
			@Override
			public Iterator<Pair<String, Node>> iterator() {
				return new Iterator<Pair<String, Node>>() {
					@Override
					public boolean hasNext() {
						return entries.hasNext();
					}
					@Override
					public Pair<String, Node> next() {
						Map.Entry<String, Object> e = entries.next();
						return Pair.of(
								e.getKey(),
								NodeFactory.create(e.getValue()));
					}
					@Override
					public void remove() {
						entries.remove();
					}
					private Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
				};
			}
		};
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public Node addChild(String name, Object value){
		map.put(name, value);
		return NodeFactory.create(value);
	}

	@Override
	public Node createEmpty() {
		return new MapNode(new LinkedHashMap<String, Object>());
	}

	public void appendChild(String name, Node child) {
		map.put(name, child.getValue());
	}

	public void mergeValue(MapNode target){
		Map<String, Object> tvalue = target.getValue();
		for(Map.Entry<String, Object> entry : tvalue.entrySet()){
			map.put(entry.getKey(), entry.getValue());
		}
	}

	private Map<String, Object> map;
}
