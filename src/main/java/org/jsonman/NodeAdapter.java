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

import org.jsonman.node.ArrayNode;
import org.jsonman.node.BooleanNode;
import org.jsonman.node.MapNode;
import org.jsonman.node.NullNode;
import org.jsonman.node.NumberNode;
import org.jsonman.node.StringNode;

public class NodeAdapter implements NodeVisitor{
	public void accept(MapNode node){}
	public void accept(ArrayNode node){}
	public void accept(StringNode node){}
	public void accept(NumberNode node){}
	public void accept(BooleanNode node){}
	public void accept(NullNode node){}
}
