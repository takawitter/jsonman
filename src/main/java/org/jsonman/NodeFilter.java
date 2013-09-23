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

import java.util.Deque;

import org.jsonman.finder.Reference;
import org.jsonman.util.BiConsumer;

public class NodeFilter {
	public NodeFilter(String... paths){
		this.paths = paths;
	}

	public Node filter(Node src){
		final NodeUpdater setter = new NodeUpdater(src.createEmpty());
		for(String p : paths){
			new NodeFinder(src).find(p, new BiConsumer<Deque<Reference>, Node>(){
				@Override
				public void accept(Deque<Reference> path, Node node) {
					StringBuilder b = new StringBuilder();
					for(Reference s : path){
						b.append("/").append(s.getId());
					}
					setter.update(path, node);
				}
			});
		}
		return setter.getTarget();
	}

	private String[] paths;
}
