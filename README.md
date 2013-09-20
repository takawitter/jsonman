jsonman
=======

JSON Manipulator Library.



```java
package org.jsonman;

import java.util.Iterator;

import net.arnx.jsonic.JSON;

import org.jsonman.node.ArrayNode;
import org.jsonman.node.MapNode;
import org.jsonman.node.NumberNode;
import org.jsonman.node.StringNode;
import org.junit.Assert;
import org.junit.Test;

public class NodeTest {
	static class TestVisitor implements NodeVisitor{
		public void accept(ArrayNode node) { Assert.fail();}
		public void accept(MapNode node) { Assert.fail();}
		public void accept(NumberNode node) { Assert.fail();}
		public void accept(StringNode node) { Assert.fail();}
	}
	@Test
	public void test() throws Exception{
		Node n = NodeFactory.create(JSON.decode(
				"[2,[1,2,3],{\"name1\":4,\"name2\":[{\"name2name1\":5}],\"name3\":{}}]"
				));
		Assert.assertTrue(n.isRoot());
		Assert.assertTrue(n.isArray());
		Assert.assertFalse(n.isMap());
		Assert.assertFalse(n.isString());
		Assert.assertFalse(n.isNumber());

		Iterator<Node> it = n.getAllChildren().iterator();
		Node node1 = it.next();
		Assert.assertTrue(node1.isNumber());
		Assert.assertEquals(2, ((NumberNode)node1).getValue().intValue());
		it.next().visit(new TestVisitor(){
			public void accept(ArrayNode node) {
				Assert.assertEquals(3, node.getValue().size());
			}
		});;
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
```
