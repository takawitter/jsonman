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
package org.jsonman.ks;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import net.arnx.jsonic.JSON;

import org.jsonman.Node;
import org.jsonman.NodeFactory;
import org.jsonman.finder.ArrayReference;
import org.jsonman.finder.MapReference;
import org.jsonman.finder.RecursiveVisitor;
import org.jsonman.finder.Reference;
import org.jsonman.node.ArrayNode;
import org.jsonman.node.MapNode;
import org.jsonman.node.StringNode;
import org.junit.Assert;
import org.junit.Test;

public class NodeFilteringTest {
	@Test
	public void test_set() throws Exception{
		NodeSetter setter = new NodeSetter(new ArrayNode());
		setter.setTo(
				Arrays.asList(
						new ArrayReference(0), new MapReference("attributes"),
						new ArrayReference(0), new MapReference("name")
				),
				new StringNode("class"));
		Assert.assertEquals(
				"[{\"attributes\":[{\"name\":\"class\"}]}]",
				JSON.encode(setter.getTarget().getValue())
				);
	}

	@Test
	public void test1() throws Exception{
		try(InputStream is = NodeFilteringTest.class.getResourceAsStream("/org/jsonman/NodeFilteringTest_1.json")){
			Node src = NodeFactory.create(JSON.decode(is));
			Node filtered = new NodeFilter("/attributes/name").filter(src);
			Assert.assertEquals(
					"[{\"attributes\":[{\"name\":\"class\"},{\"name\":\"id\"}]}," +
					"{\"attributes\":[{\"name\":\"class\"},{\"name\":\"id\"}]}," +
					"{\"attributes\":[{\"name\":\"class\"},{\"name\":\"id\"}]}]",
					JSON.encode(filtered.getValue()));
		}
	}

	@Test
	public void test2() throws Exception{
		try(InputStream is = NodeFilteringTest.class.getResourceAsStream("/org/jsonman/NodeFilteringTest_1.json")){
			Node src = NodeFactory.create(JSON.decode(is));
			Node filtered = new NodeFilter("/attributes[name=class]").filter(src);
			Assert.assertEquals(
					"[{\"attributes\":[{\"name\":\"class\",\"value\":\"bodyclass\"}]}," +
					"{\"attributes\":[{\"name\":\"class\",\"value\":\"h1class\"}]}," +
					"{\"attributes\":[{\"name\":\"class\",\"value\":\"h2class\"}]}]",
					JSON.encode(filtered.getValue()));
		}
	}

	static void copyTo(Node target, Deque<Reference> paths, Node node){
		System.out.println();
		for(Reference r : paths){
			System.out.println(r);
		}
		Iterator<Reference> it = paths.iterator();
		Reference r = it.next();
		if(target.isArray()){
			((ArrayNode)target).appendChild(createNode(it, node));
		} else if(target.isMap()){
			((MapNode)target).appendChild(r.getId().toString(), createNode(it, node));
		} else{
			return;
		}
	}
	static Node createNode(Iterator<Reference> it, Node leaf){
		Reference ref = it.next();
		Node child = null;
		if(it.hasNext()){
			child = createNode(it, leaf);
		} else{
			return leaf;
		}
		Node n = null;
		Object id = ref.getId();
		if(id instanceof Integer){
			ArrayNode an = new ArrayNode(new ArrayList<>());
			an.appendChild(child);
			n = an;
		} else if(id instanceof String){
			MapNode mn = new MapNode(new LinkedHashMap<String, Object>());
			mn.appendChild((String)id, child);
			n = mn;
		} else{
			return null;
		}
		return n;
	}

	@Test
	public void test() throws Exception{
		Node src = null;
		try(InputStream is = NodeFilteringTest.class.getResourceAsStream("/org/jsonman/NodeFilteringTest_1.json")){
			src = NodeFactory.create(JSON.decode(is));
		}

		// /attributes[name=class]
		final Node target = src.createEmpty();
		src.visit(new RecursiveVisitor(new LinkedList<Reference>()){
			@Override
			public void accept(MapNode node) {
				Node an = node.getChild("attributes");
				if(an == null) return;
				getPaths().addLast(new MapReference("attributes"));
				an.visit(new RecursiveVisitor(getPaths()){
					@Override
					public void accept(final MapNode node) {
						Node nn = node.getChild("name");
						if(nn == null) return;
						getPaths().addLast(new MapReference("name"));
						nn.visit(new RecursiveVisitor(getPaths()){
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
		});
		System.out.println(JSON.encode(target.getValue()));
		Assert.assertEquals(
				"[{\"attributes\":[{\"name\":\"class\",\"value\":\"bodyclass\"}]},{\"attributes\":[{\"name\":\"class\",\"value\":\"h1class\"}]},{\"attributes\":[{\"name\":\"class\",\"value\":\"h2class\"}]}]",
				JSON.encode(target.getValue()));
	}
}
