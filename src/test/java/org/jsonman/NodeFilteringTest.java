package org.jsonman;

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
import java.io.InputStream;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import net.arnx.jsonic.JSON;

import org.jsonman.node.MapNode;
import org.jsonman.node.StringNode;
import org.junit.Assert;
import org.junit.Test;

public class NodeFilteringTest {
	@Test
	public void test() throws Exception{
		Node src = null;
		try(InputStream is = NodeFilteringTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
			src = NodeFactory.create(JSON.decode(is));
		}

		// /attributes[name=class]
		final Node target = src.createEmpty();
		src.visit(new NodeRecursiveVisitor(new LinkedList<Reference>()){
			@Override
			public void accept(MapNode node) {
				Node an = node.getChild("attributes");
				if(an == null) return;
				getPaths().addLast(new MapReference(node, "attributes"));
				an.visit(new NodeRecursiveVisitor(getPaths()){
					@Override
					public void accept(final MapNode node) {
						Node nn = node.getChild("name");
						if(nn == null) return;
						getPaths().addLast(new MapReference(node, "name"));
						nn.visit(new NodeRecursiveVisitor(getPaths()){
							@Override
							public void accept(StringNode vnode) {
								if(!"class".equals(vnode.getValue())) return;
								copyTo(target, getPaths(), node);
							}
						});
						getPaths().removeLast();
					}
				});
				getPaths().removeLast();
			}
			void copyTo(Node target, Deque<Reference> paths, Node node){
				System.out.println();
				for(Reference r : paths){
					System.out.println(r);
				}
				Iterator<Reference> it = paths.iterator();
				Reference r = it.next();
				if(target.isArray()){
					target.appendChild(createNode(it, node));
				} else if(target.isMap()){
					target.appendChild(r.getId().toString(), createNode(it, node));
				} else{
					return;
				}
			}
			Node createNode(Iterator<Reference> it, Node leaf){
				Reference ref = it.next();
				Node child = null;
				if(it.hasNext()){
					child = createNode(it, leaf);
				} else{
					return leaf;
				}
				Node n = ref.getParent().createEmpty();
				if(n.isArray()){
					n.appendChild(child);
				} else if(n.isMap()){
					n.appendChild(ref.getId().toString(), child);
				} else{
					return null;
				}
				return n;
			}
		});
		System.out.println(JSON.encode(target.getValue()));
		Assert.assertEquals(
				"[{\"attributes\":[{\"name\":\"class\",\"value\":\"bodyclass\"}]},{\"attributes\":[{\"name\":\"class\",\"value\":\"h1class\"}]},{\"attributes\":[{\"name\":\"class\",\"value\":\"h2class\"}]}]",
				JSON.encode(target.getValue()));
	}
}
