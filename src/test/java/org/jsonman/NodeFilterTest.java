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

import net.arnx.jsonic.JSON;

import org.junit.Assert;
import org.junit.Test;

public class NodeFilterTest {
	@Test
	public void test1() throws Exception{
		try(InputStream is = NodeFilterTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
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
