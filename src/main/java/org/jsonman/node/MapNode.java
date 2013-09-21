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
import org.jsonman.MapReference;
import org.jsonman.Node;
import org.jsonman.NodeFactory;
import org.jsonman.NodeVisitor;
import org.jsonman.Reference;

public class MapNode extends AbstractNode{
	public MapNode(Map<String, Object> map){
		this.map = map;
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
		setRealValue((Map<String, Object>)value);
	}

	public void setRealValue(Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public Node getChild(Object childId) {
		Object value = map.get(childId);
		if(value == null) return null;
		return NodeFactory.create(value);
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

	@Override
	public Iterable<Pair<Reference, Node>> getChildren() {
		return new Iterable<Pair<Reference, Node>>() {
			@Override
			public Iterator<Pair<Reference, Node>> iterator() {
				return new Iterator<Pair<Reference, Node>>() {
					@Override
					public boolean hasNext() {
						return entries.hasNext();
					}
					@Override
					public Pair<Reference, Node> next() {
						Map.Entry<String, Object> e = entries.next();
						String id = e.getKey();
						Object value = e.getValue();
						return Pair.of((Reference)new MapReference(MapNode.this, id), NodeFactory.create(value));
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
	public void visitAllChildren(NodeVisitor visitor) {
		for(Map.Entry<String, Object> e : map.entrySet()){
			NodeFactory.create(e.getValue()).visit(visitor);
		}
	}

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.accept(this);
	}

	public Node addChild(String name, Object value){
		map.put(name, value);
		return NodeFactory.create(value);
	}

	@Override
	public Node createEmpty() {
		return new MapNode(new LinkedHashMap<String, Object>());
	}

	@Override
	public void appendChild(String childId, Node child) {
		map.put(childId, child.getValue());
	}

	private Map<String, Object> map;
}
