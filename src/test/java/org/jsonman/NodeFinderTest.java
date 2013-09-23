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
import java.util.Deque;

import net.arnx.jsonic.JSON;

import org.apache.commons.lang3.tuple.Pair;
import org.jsonman.finder.Reference;
import org.jsonman.node.NumberNode;
import org.jsonman.node.StringNode;
import org.jsonman.util.BiConsumer;
import org.junit.Assert;
import org.junit.Test;

public class NodeFinderTest {
	@Test
	public void test_1() throws Exception{
		Node src = NodeFactory.create(JSON.decode("{\"name\":\"value\"}"));
		new NodeFinder(src).find("/name", new BiConsumer<Deque<Reference>, Node>() {
				@Override
				public void accept(Deque<Reference> path, Node node) {
					Assert.assertEquals(1, path.size());
					Assert.assertTrue(path.peekLast().isMap());
					Assert.assertEquals("name", path.peekLast().getId());
					Assert.assertTrue(node.isString());
					StringNode mn = node.cast();
					Assert.assertEquals("value", mn.getValue());
				}
			});
	}

	@Test
	public void test_2() throws Exception{
		@SuppressWarnings("rawtypes")
		final Pair[] expecteds = {
				Pair.of("/0/age", 30),
				Pair.of("/1/age", 40),
		};
		Node src = NodeFactory.create(JSON.decode("[{\"name\":\"john\",\"age\":30},{\"name\":\"bob\",\"age\":40}]"));
		new NodeFinder(src).find("/age", new BiConsumer<Deque<Reference>, Node>() {
				@Override
				public void accept(Deque<Reference> path, Node node) {
					Assert.assertEquals("" + i, expecteds[i].getLeft(), pathToString(path));
					Assert.assertEquals("" + i, expecteds[i].getRight(), ((NumberNode)node).getValue().intValue());
					i++;
				}
				private int i;
			});
	}

	@Test
	public void test_3() throws Exception{
		@SuppressWarnings("rawtypes")
		final Pair[] expecteds = {
				Pair.of("/0/attributes/0/name", "StringNode"),
				Pair.of("/0/attributes/1/name", "StringNode"),
				Pair.of("/1/attributes/0/name", "StringNode"),
				Pair.of("/1/attributes/1/name", "StringNode"),
				Pair.of("/2/attributes/0/name", "StringNode"),
				Pair.of("/2/attributes/1/name", "StringNode"),
		};
		try(InputStream is = NodeFinderTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
			Node src = NodeFactory.create(JSON.decode(is));
			new NodeFinder(src).find("/attributes/name", new BiConsumer<Deque<Reference>, Node>() {
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

	@Test
	public void test_4_condition() throws Exception{
		@SuppressWarnings("rawtypes")
		final Pair[] expecteds = {
				Pair.of("/0/attributes/0", "MapNode"),
				Pair.of("/1/attributes/0", "MapNode"),
				Pair.of("/2/attributes/0", "MapNode"),
		};
		try(InputStream is = NodeFinderTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
			Node src = NodeFactory.create(JSON.decode(is));
			new NodeFinder(src).find("/attributes[name='class']", new BiConsumer<Deque<Reference>, Node>() {
				@Override
				public void accept(Deque<Reference> path, Node node) {
					Assert.assertEquals("" + i, expecteds[i].getLeft(), pathToString(path));
					Assert.assertEquals("" + i, expecteds[i].getRight(), node.getClass().getSimpleName());
					Assert.assertTrue(node.isMap());
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
}
