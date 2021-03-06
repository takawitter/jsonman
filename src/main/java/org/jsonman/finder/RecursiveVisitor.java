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

import java.util.Deque;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jsonman.Node;
import org.jsonman.NodeAdapter;
import org.jsonman.node.ArrayNode;
import org.jsonman.node.MapNode;

public class RecursiveVisitor<T extends Deque<Reference> & List<Reference>> extends NodeAdapter{
	public RecursiveVisitor(T paths){
		this.paths = paths;
	}

	@Override
	public void visit(ArrayNode node) {
		int i = 0;
		for(Node e : node.getChildren()){
			paths.addLast(new ArrayReference(i++));
			e.accept(this);
			paths.removeLast();
		}
	}

	@Override
	public void visit(MapNode node) {
		for(Pair<String, Node> e : node.getChildrenWithName()){
			paths.addLast(new MapReference(e.getKey()));
			e.getValue().accept(this);
			paths.removeLast();
		}
	}

	protected T getPaths(){
		return paths;
	}

	private T paths;
}
