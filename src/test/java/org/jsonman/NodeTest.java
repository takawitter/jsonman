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

import java.util.Iterator;

import net.arnx.jsonic.JSON;

import org.jsonman.node.ArrayNode;
import org.jsonman.node.BooleanNode;
import org.jsonman.node.MapNode;
import org.jsonman.node.NullNode;
import org.jsonman.node.NumberNode;
import org.jsonman.node.StringNode;
import org.junit.Assert;
import org.junit.Test;

public class NodeTest {
	static class TestVisitor implements NodeVisitor{
		public void accept(MapNode node) { Assert.fail();}
		public void accept(ArrayNode node) { Assert.fail();}
		public void accept(StringNode node) { Assert.fail();}
		public void accept(NumberNode node) { Assert.fail();}
		public void accept(BooleanNode node) { Assert.fail();}
		public void accept(NullNode node) { Assert.fail();}
	}
	@Test
	public void test() throws Exception{
		Node n = NodeFactory.create(JSON.decode(
				"[true,2,[1,2,3],{\"name1\":4,\"name2\":[{\"name2name1\":5}],\"name3\":{}}]"
				));
		Assert.assertTrue(n.isRoot());
		Assert.assertTrue(n.isArray());
		Assert.assertFalse(n.isMap());
		Assert.assertFalse(n.isString());
		Assert.assertFalse(n.isNumber());
		Assert.assertFalse(n.isBoolean());
		Assert.assertFalse(n.isNull());

		Iterator<Node> it = n.getAllChildren().iterator();
		Node node1 = it.next();
		Assert.assertTrue(node1.isBoolean());
		Assert.assertTrue(((BooleanNode)node1).getValue());
		Node node2 = it.next();
		Assert.assertTrue(node2.isNumber());
		Assert.assertEquals(2, ((NumberNode)node2).getValue().intValue());
		it.next().visit(new TestVisitor(){
			public void accept(ArrayNode node) {
				Assert.assertEquals(3, node.getValue().size());
			}
		});
		it.next().visit(new TestVisitor(){
			public void accept(MapNode node) {
				Assert.assertEquals(3, node.getValue().size());
				node.getChild("name1").visit(new TestVisitor(){
					public void accept(NumberNode node) {
						Assert.assertEquals(4, node.getValue().intValue());
					}
				});
				node.getChild("name2").visit(new TestVisitor(){
					public void accept(ArrayNode node) {
						Assert.assertEquals(1, node.getValue().size());
					}
				});
				node.getChild("name3").visit(new TestVisitor(){
					public void accept(MapNode node) {
						Assert.assertEquals(0, node.getValue().size());
					}
				});
			}
		});
	}
}
