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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import net.arnx.jsonic.JSON;

import org.apache.commons.lang3.tuple.Pair;
import org.jsonman.ks.ArrayReference;
import org.jsonman.ks.MapReference;
import org.jsonman.ks.NodeFilter;
import org.jsonman.ks.NodeFinder;
import org.jsonman.ks.NodeRecursiveVisitor;
import org.jsonman.ks.NodeSetter;
import org.jsonman.ks.Reference;
import org.jsonman.ks.function.BiConsumer;
import org.jsonman.node.ArrayNode;
import org.jsonman.node.MapNode;
import org.jsonman.node.StringNode;
import org.junit.Assert;
import org.junit.Test;

public class NodeFilteringTest {
	@Test
	public void test_find() throws Exception{
		@SuppressWarnings("rawtypes")
		final Pair[] expecteds = {
				Pair.of("/0/attributes/0/name", "StringNode"),
				Pair.of("/0/attributes/1/name", "StringNode"),
				Pair.of("/1/attributes/0/name", "StringNode"),
				Pair.of("/1/attributes/1/name", "StringNode"),
				Pair.of("/2/attributes/0/name", "StringNode"),
				Pair.of("/2/attributes/1/name", "StringNode"),
		};
		try(InputStream is = NodeFilteringTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
			Node src = NodeFactory.create(JSON.decode(is));
			new NodeFinder("/attributes/name").find(src, new BiConsumer<Deque<Reference>, Node>() {
				@Override
				public void accept(Deque<Reference> path, Node node) {
					Assert.assertEquals("" + i, expecteds[i].getLeft(), pathToString(path));
					Assert.assertEquals("" + i, expecteds[i].getRight(), node.getClass().getSimpleName());
					i++;
				}
				private int i;
			});
		}
	}

	private static String pathToString(Iterable<Reference> path){
		StringBuilder b = new StringBuilder();
		for(Reference s : path){
			b.append("/").append(s.getId());
		}
		return b.toString();
	}

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
		try(InputStream is = NodeFilteringTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
			Node src = NodeFactory.create(JSON.decode(is));
			Node filtered = new NodeFilter("/attributes/name").filter(src);
			Assert.assertEquals(
					"[{\"attributes\":[{\"name\":\"class\"},{\"name\":\"id\"}]}," +
					"{\"attributes\":[{\"name\":\"class\"},{\"name\":\"id\"}]}," +
					"{\"attributes\":[{\"name\":\"class\"},{\"name\":\"id\"}]}]",
					JSON.encode(filtered.getValue()));
		}
	}

//	@Test
	public void test2() throws Exception{
		try(InputStream is = NodeFilteringTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
			Node src = NodeFactory.create(JSON.decode(is));
			Node filtered = new NodeFilter("/*/attributes/*[name=class]").filter(src);
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
				getPaths().addLast(new MapReference("attributes"));
				an.visit(new NodeRecursiveVisitor(getPaths()){
					@Override
					public void accept(final MapNode node) {
						Node nn = node.getChild("name");
						if(nn == null) return;
						getPaths().addLast(new MapReference("name"));
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
		});
		System.out.println(JSON.encode(target.getValue()));
		Assert.assertEquals(
				"[{\"attributes\":[{\"name\":\"class\",\"value\":\"bodyclass\"}]},{\"attributes\":[{\"name\":\"class\",\"value\":\"h1class\"}]},{\"attributes\":[{\"name\":\"class\",\"value\":\"h2class\"}]}]",
				JSON.encode(target.getValue()));
	}
}
