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

import org.apache.commons.lang3.tuple.Pair;

public interface Node {
	boolean isMap();
	boolean isArray();
	boolean isString();
	boolean isNumber();
	boolean isBoolean();
	boolean isNull();

	Object getValue();
	void setValue(Object value);
	void visit(NodeVisitor visitor);

	Node getChild(Object childId);
	Iterable<Node> getAllChildren();
	Iterable<Pair<Reference, Node>> getChildren();
	void visitAllChildren(NodeVisitor visitor);

	Node createEmpty();
	void appendChild(String childId, Node child);
	void appendChild(Node child);
}
