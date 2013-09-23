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
import java.util.LinkedList;

import org.jsonman.finder.ArrayReference;
import org.jsonman.finder.Condition;
import org.jsonman.finder.Fragment;
import org.jsonman.finder.FragmentScanner;
import org.jsonman.finder.MapReference;
import org.jsonman.finder.RecursiveVisitor;
import org.jsonman.finder.Reference;
import org.jsonman.node.ArrayNode;
import org.jsonman.node.BooleanNode;
import org.jsonman.node.MapNode;
import org.jsonman.node.NullNode;
import org.jsonman.node.NumberNode;
import org.jsonman.node.StringNode;
import org.jsonman.util.BiConsumer;

public class NodeFinder {
	public NodeFinder(String path){
		this.path = path;
	}

	public void find(Node src, BiConsumer<Deque<Reference>, Node> consumer){
		find(src, FragmentScanner.parsePath(path), consumer);
	}

	private static void find(Node src, final Fragment[] fragments, final BiConsumer<Deque<Reference>, Node> consumer){
		if(fragments.length == 0) return;
		src.visit(new RecursiveVisitor(new LinkedList<Reference>()){
			@Override
			public void accept(ArrayNode node) {
				if(condition != null){
					int i = 0;
					for(Node child : node.getChildren()){
						boolean canVisit = false;
						if(child.isMap()){
							MapNode mc = child.cast();
							Node n = mc.getChild(condition.getName());
							if(n != null && condition.matched(n)){
								canVisit = true;
							}
						} else{
							canVisit = true;
						}
						if(canVisit){
							getPaths().addLast(new ArrayReference(i++));
							child.visit(this);
							getPaths().removeLast();
						}
					}
				} else{
					super.accept(node);
				}
			}
			@Override
			public void accept(MapNode node) {
				if(condition != null){
					if(!condition.matched(node.getChild(condition.getName()))) return;
				}
				if(nest == fragments.length){
					consumer.accept(getPaths(), node);
					return;
				}
				Fragment f = fragments[nest];
				Node child = node.getChild(f.getName());
				if(child == null) return;
				getPaths().addLast(new MapReference(f.getName()));
				condition = f.getCondition();
				nest++;
				child.visit(this);
				nest--;
				condition = null;
				getPaths().removeLast();
			}
			@Override
			public void accept(BooleanNode node) {
				acceptLeaf(node);
			}
			@Override
			public void accept(NullNode node) {
				acceptLeaf(node);
			}
			@Override
			public void accept(NumberNode node) {
				acceptLeaf(node);
			}
			@Override
			public void accept(StringNode node) {
				acceptLeaf(node);
			}
			private void acceptLeaf(Node node){
				if(nest == fragments.length){
					consumer.accept(getPaths(), node);
				}
			}
			private int nest = 0;
			private Condition condition;
		});
	}

	private String path;
}
