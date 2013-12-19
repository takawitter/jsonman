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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.jsonman.node.ArrayNode;
import org.jsonman.node.BooleanNode;
import org.jsonman.node.MapNode;
import org.jsonman.node.NumberNode;
import org.jsonman.node.StringNode;

public class NodeFactory {
	@SuppressWarnings("unchecked")
	public static Node create(Object value){
		if(value == null){
			return new StringNode(null);
		} else if(value instanceof Node){
			return (Node)value;
		} else if(value instanceof List){
			return new ArrayNode((List<Object>)value);
		} else if(value instanceof Map){
			return new MapNode((Map<String, Object>)value);
		} else if(value instanceof Number){
			return new NumberNode((Number)value);
		} else if(value instanceof String){
			return new StringNode((String)value);
		} else if(value instanceof Boolean){
			return new BooleanNode((Boolean)value);
		} else if(value.getClass().isArray()){
			return new ArrayNode(Arrays.asList((Object[])value));
		} else{
			throw new IllegalArgumentException("No suitable node class for " + value.getClass());
		}
	}

	public static Node create(InputStream is) throws IOException{
		return create(JSON.decode(is));
	}
}
