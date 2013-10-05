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
package org.jsonman.ks;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.io.ReaderInputSource;
import net.arnx.jsonic.parse.JSONParser;
import net.arnx.jsonic.util.LocalCache;

import org.jsonman.Node;
import org.jsonman.NodeFactory;
import org.jsonman.finder.ArrayReference;
import org.jsonman.finder.Fragment;
import org.jsonman.finder.FragmentScanner;
import org.jsonman.finder.MapReference;
import org.jsonman.finder.Reference;
import org.jsonman.util.BiConsumer;

public class NodeFinderByJSONParser {
	public NodeFinderByJSONParser(InputStream is){
		this.parser = new JSONParser(
				new ReaderInputSource(is), 32, true, true,
				new LocalCache("net.arnx.jsonic.Messages", Locale.getDefault(), TimeZone.getDefault())
				);
	}

	public NodeFinderByJSONParser(Reader reader){
		this.parser = new JSONParser(
				new ReaderInputSource(reader), 32, true, true,
				new LocalCache("net.arnx.jsonic.Messages", Locale.getDefault(), TimeZone.getDefault())
				);
	}

	public void find(String path, BiConsumer<List<Reference>, Node> consumer)
	throws IOException{
		find(parser, FragmentScanner.parsePath(path), consumer);
	}

	static class ParserNodeVisitor{
		private LinkedList<Reference> path = new LinkedList<>();

		public LinkedList<Reference> getPath() {
			return path;
		}

		public void fireNext(JSONParser parser)
		throws IOException{
			fire(parser, parser.next());
		}

		private void fire(JSONParser parser, JSONEventType type)
		throws IOException{
			switch(type){
				case START_ARRAY:	onArrayStart(parser);	break;
				case START_OBJECT:	onObjectStart(parser);	break;
				case NAME:			onName(parser);			break;
				case BOOLEAN:		onBoolean(parser);		break;
				case STRING:		onString(parser);		break;
				case NUMBER:		onNumber(parser);		break;
				case NULL:			onNull(parser);			break;
				default:
			}
		}

		public void onArrayStart(JSONParser parser) throws IOException{
			int i = 0;
			while(true){
				JSONEventType type = parser.next();
				switch(type){
					case END_ARRAY:
						return;
					default:
						path.offerLast(new ArrayReference(i++));
						try{
							fire(parser, type);
						} finally{
							path.pollLast();
						}
				}
			}
		}

		public void onObjectStart(JSONParser parser) throws IOException{
		}

		public void onName(JSONParser parser){
		}

		public void onString(JSONParser parser){
		}

		public void onNumber(JSONParser parser){
		}

		public void onBoolean(JSONParser parser){
		}

		public void onNull(JSONParser parser){
		}
	}

	private static void find(JSONParser parser, final Fragment[] fragments, final BiConsumer<List<Reference>, Node> consumer)
	throws IOException{
		if(fragments.length == 0) return;
		for(Fragment f : fragments){
			if(f.getCondition() != null){
				throw new RuntimeException("path with condition is not supported.");
			}
		}
		new ParserNodeVisitor(){
			public void onObjectStart(JSONParser parser) throws IOException{
				Fragment f = fragments[nest];
				boolean lastFragment = fragments.length == (nest + 1);
				while(true){
					JSONEventType type = parser.next();
					switch(type){
						case END_OBJECT:
							return;
						case NAME:
							if(f.getName().equals(parser.getValue())){
								getPath().offerLast(new MapReference(f.getName()));
								try{
									if(lastFragment){
										consumer.accept(getPath(), buildNode(parser));
									} else{
										nest++;
										fireNext(parser);
										nest--;
									}
								} finally{
									getPath().pollLast();
								}
							} else{
								skipNext(parser);
							}
						default:
					}
				}
			}
			private int nest;
		}.fireNext(parser);
	}

	private static void skipNext(JSONParser parser)
	throws IOException{
		switch(parser.next()){
			case START_OBJECT:
			case START_ARRAY:
				skipToEnd(parser);
				break;
			default:
		}
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

	private static Node buildNode(JSONParser parser)
	throws IOException{
		return NodeFactory.create(build(parser, parser.next()));
	}

	private static Object build(JSONParser parser, JSONEventType type)
			throws IOException{
		switch(type){
			case START_ARRAY:{
				List<Object> ret = new ArrayList<>();
				while(true){
					JSONEventType t = parser.next();
					if(t.equals(JSONEventType.END_ARRAY)) break;
					ret.add(build(parser, t));
				}
				return ret;
			}
			case START_OBJECT:{
				Map<String, Object> ret = new LinkedHashMap<>();
				while(true){
					JSONEventType t = parser.next();
					if(t.equals(JSONEventType.END_OBJECT)) break;
					String name = parser.getValue().toString();
					ret.put(name, build(parser, parser.next()));
				}
				return ret;
			}
			case BOOLEAN:
			case STRING:
			case NUMBER:
				return parser.getValue();
			case NULL:
			default:
				return null;
		}
	}

	private JSONParser parser;
}
