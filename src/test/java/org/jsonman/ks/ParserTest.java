package org.jsonman.ks;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.io.StringInputSource;
import net.arnx.jsonic.parse.JSONParser;
import net.arnx.jsonic.util.LocalCache;

import org.junit.Assert;
import org.junit.Test;

public class ParserTest {
	@Test
	public void test() throws Exception{
		JSONParser p = new JSONParser(
				new StringInputSource(
						"[{\"name\":\"john\",\"age\":30},{\"name\":\"bob\",\"age\":40}]"
						),
				100, true, true,
				new LocalCache("net.arnx.jsonic.Messages", Locale.getDefault(), TimeZone.getDefault())
				);
		Assert.assertTrue(p.next().equals(JSONEventType.START_ARRAY));
		Assert.assertTrue(p.next().equals(JSONEventType.START_OBJECT));
		Assert.assertTrue(p.next().equals(JSONEventType.NAME));
		Assert.assertEquals("name", p.getValue());
		Assert.assertTrue(p.next().equals(JSONEventType.STRING));
		Assert.assertEquals("john", p.getValue());
		Assert.assertTrue(p.next().equals(JSONEventType.NAME));
		Assert.assertEquals("age", p.getValue());
		Assert.assertTrue(p.next().equals(JSONEventType.NUMBER));
		Assert.assertEquals(30, ((Number)p.getValue()).intValue());
		Assert.assertTrue(p.next().equals(JSONEventType.END_OBJECT));
	}

	@Test
	public void test_skip() throws Exception{
		JSONParser p = new JSONParser(
				new StringInputSource(
						"[{\"name\":\"john\",\"pc\":{\"hard\":\"macbook\",\"os\":\"freebsd\"},\"age\":30},{\"name\":\"bob\",\"age\":40}]"
						),
				100, true, true,
				new LocalCache("net.arnx.jsonic.Messages", Locale.getDefault(), TimeZone.getDefault())
				);
		Assert.assertTrue(p.next().equals(JSONEventType.START_ARRAY));
		Assert.assertTrue(p.next().equals(JSONEventType.START_OBJECT));
		Assert.assertTrue(p.next().equals(JSONEventType.NAME));
		Assert.assertEquals("name", p.getValue());
		Assert.assertTrue(p.next().equals(JSONEventType.STRING));
		Assert.assertEquals("john", p.getValue());
		skipToEnd(p);
		Assert.assertTrue(p.next().equals(JSONEventType.START_OBJECT));
		Assert.assertTrue(p.next().equals(JSONEventType.NAME));
		Assert.assertEquals("name", p.getValue());
		Assert.assertTrue(p.next().equals(JSONEventType.STRING));
		Assert.assertEquals("bob", p.getValue());
		Assert.assertTrue(p.next().equals(JSONEventType.NAME));
		Assert.assertEquals("age", p.getValue());
		Assert.assertTrue(p.next().equals(JSONEventType.NUMBER));
		Assert.assertEquals(40, ((Number)p.getValue()).intValue());
		Assert.assertTrue(p.next().equals(JSONEventType.END_OBJECT));
		Assert.assertTrue(p.next().equals(JSONEventType.END_ARRAY));
	}

	private static void skipToEnd(JSONParser parser)
	throws IOException{
		int nest = 1;
		while(nest > 0){
			switch(parser.next()){
				case START_OBJECT:
				case START_ARRAY:
					nest++;
					break;
				case END_OBJECT:
				case END_ARRAY:
					nest--;
					break;
				default:
			}
		}
	}
}
