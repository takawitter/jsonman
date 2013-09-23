package org.jsonman.ks;

import java.util.Deque;

import net.arnx.jsonic.JSON;

import org.jsonman.Node;
import org.jsonman.ks.util.function.BiConsumer;

public class NodeFilter {
	public NodeFilter(String... paths){
		this.paths = paths;
	}

	public Node filter(Node src){
		final NodeSetter setter = new NodeSetter(src.createEmpty());
		for(String p : paths){
			new NodeFinder(p).find(src, new BiConsumer<Deque<Reference>, Node>(){
				@Override
				public void accept(Deque<Reference> path, Node node) {
					StringBuilder b = new StringBuilder();
					for(Reference s : path){
						b.append("/").append(s.getId());
					}
					System.out.println(String.format(
							"found!  path:%s  node:%s.",
							b, node));
					setter.setTo(path, node);
					System.out.println(JSON.encode(setter.getTarget().getValue()));
				}
			});
		}
		return setter.getTarget();
	}

	private String[] paths;
}
