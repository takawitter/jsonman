package org.jsonman;

import java.util.List;

import org.jsonman.finder.ArrayReference;
import org.jsonman.finder.MapReference;
import org.jsonman.finder.Reference;
import org.jsonman.finder.ReferenceParser;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceParserTest {
	@Test
	public void test() throws Exception{
		List<Reference> actual = ReferenceParser.parse("/0/name/43/hello");
		Assert.assertEquals(new ArrayReference(0), actual.get(0));
		Assert.assertEquals(new MapReference("name"), actual.get(1));
		Assert.assertEquals(new ArrayReference(43), actual.get(2));
		Assert.assertEquals(new MapReference("hello"), actual.get(3));
	}
}
