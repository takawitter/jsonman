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

import java.util.Arrays;

import net.arnx.jsonic.JSON;

import org.jsonman.finder.ArrayReference;
import org.jsonman.finder.MapReference;
import org.jsonman.node.ArrayNode;
import org.jsonman.node.StringNode;
import org.junit.Assert;
import org.junit.Test;

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
