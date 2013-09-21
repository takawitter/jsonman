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
package org.jsonman;

import java.util.Deque;

import org.apache.commons.lang3.tuple.Pair;
import org.jsonman.node.ArrayNode;
import org.jsonman.node.MapNode;

public class NodeRecursiveVisitor extends NodeAdapter{
	public NodeRecursiveVisitor(Deque<Reference> paths){
		this.paths = paths;
	}

	@Override
	public void accept(ArrayNode node) {
		acceptContainer(node);
	}

	@Override
	public void accept(MapNode node) {
		acceptContainer(node);
	}

	protected Deque<Reference> getPaths(){
		return paths;
	}

	private void acceptContainer(Node node){
		for(Pair<Reference, Node> e :node.getChildren()){
			paths.addLast(e.getKey());
			e.getValue().visit(this);
			paths.removeLast();
		}
	}

	private Deque<Reference> paths;
}
