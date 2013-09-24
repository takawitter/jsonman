jsonman
=======

JSON Manipulator Library.

[![Build Status](https://buildhive.cloudbees.com/job/takawitter/job/jsonman/badge/icon)](https://buildhive.cloudbees.com/job/takawitter/job/jsonman/)
 **latest [jsonman-0.0.1-SNAPSHOT.jar](https://buildhive.cloudbees.com/job/takawitter/job/jsonman/ws/target/jsonman-0.0.1-SNAPSHOT.jar)**

JSONMAN constructs light-weight tree structure from parsed JSON (Map&lt;String, Object&gt;) and 
provides finding, updating and filtering function based on XPath-like expression.
JSONMAN is designed to be used with [JSONIC](http://jsonic.sourceforge.jp/) or any JSON decoder which returns decoded JSON as Map&lt;String, Object&gt;.

## Sample code 1: Traversing tree

```java
public class NodeTest {
	static class TestVisitor implements NodeVisitor{
		public void visit(MapNode node) { Assert.fail();}
		public void visit(ArrayNode node) { Assert.fail();}
		public void visit(StringNode node) { Assert.fail();}
		public void visit(NumberNode node) { Assert.fail();}
		public void visit(BooleanNode node) { Assert.fail();}
		public void visit(NullNode node) { Assert.fail();}
	}
	@Test
	public void test() throws Exception{
		Node n = NodeFactory.create(JSON.decode(
				"[true,2,[1,2,3],{\"name1\":4,\"name2\":[{\"name2name1\":5}],\"name3\":{}}]"
				));
		Assert.assertTrue(n.isArray());
		Assert.assertFalse(n.isMap());
		Assert.assertFalse(n.isString());
		Assert.assertFalse(n.isNumber());
		Assert.assertFalse(n.isBoolean());
		Assert.assertFalse(n.isNull());

		Iterator<Node> it = n.getChildren().iterator();
		Node node1 = it.next();
		Assert.assertTrue(node1.isBoolean());
		Assert.assertTrue(((BooleanNode)node1).getValue());
		Node node2 = it.next();
		Assert.assertTrue(node2.isNumber());
		Assert.assertEquals(2, ((NumberNode)node2).getValue().intValue());
		it.next().accept(new TestVisitor(){
			public void visit(ArrayNode node) {
				Assert.assertEquals(3, node.getValue().size());
			}
		});
		it.next().accept(new TestVisitor(){
			public void visit(MapNode node) {
				Assert.assertEquals(3, node.getValue().size());
				node.getChild("name1").accept(new TestVisitor(){
					public void visit(NumberNode node) {
						Assert.assertEquals(4, node.getValue().intValue());
					}
				});
				node.getChild("name2").accept(new TestVisitor(){
					public void visit(ArrayNode node) {
						Assert.assertEquals(1, node.getValue().size());
					}
				});
				node.getChild("name3").accept(new TestVisitor(){
					public void visit(MapNode node) {
						Assert.assertEquals(0, node.getValue().size());
					}
				});
			}
		});
	}
}
```

## Sample code 2: Finding node.

```java
public class NodeFinderTest {
	@Test
	public void test_4() throws Exception{
		Node src = NodeFactory.create(JSON.decode("{\"people\":[{\"name\":\"john\",\"age\":20},{\"name\":\"bob\",\"age\":30}]}"));
		new NodeFinder(src).find("/people[name=bob]", new BiConsumer<Deque<Reference>, Node>() {
				@Override
				public void accept(Deque<Reference> path, Node node) {
					Assert.assertTrue(node.isMap());
					MapNode mn = node.cast();
					Assert.assertEquals("bob", mn.getChildValue("name"));
					Assert.assertEquals(30, ((Number)mn.getChildValue("age")).intValue());
				}
			});
	}
}
```

## Sample code 3: Updating node.

```java
public class NodeUpdaterTest {
	@Test
	public void test_set() throws Exception{
		NodeUpdater setter = new NodeUpdater(new ArrayNode());
		setter.update(
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
}
```

## Sample code 4: Filtering node.

```java
public class NodeFilterTest {
	@Test
	public void test2() throws Exception{
		try(InputStream is = NodeFilterTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
			Node src = NodeFactory.create(JSON.decode(is));
			Node filtered = new NodeFilter("/attributes[name=class]").filter(src);
			Assert.assertEquals(
					"[{\"attributes\":[{\"name\":\"class\",\"value\":\"bodyclass\"}]}," +
					"{\"attributes\":[{\"name\":\"class\",\"value\":\"h1class\"}]}," +
					"{\"attributes\":[{\"name\":\"class\",\"value\":\"h2class\"}]}]",
					JSON.encode(filtered.getValue()));
		}
	}
}
```