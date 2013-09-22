package org.jsonman.ks;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.jsonman.Node;
import org.jsonman.ks.function.BiConsumer;
import org.jsonman.node.ArrayNode;
import org.jsonman.node.MapNode;

public class NodeFilter {
	public NodeFilter(String... paths){
		this.paths = paths;
	}

	public Node filter(Node src){
		return src;
	}

	private String[] paths;

	static Node filter(Node src, String... paths){
		final Node target = src.createEmpty();
		find(src, paths, new BiConsumer<Deque<Reference>, Node>(){
			@Override
			public void accept(Deque<Reference> paths, Node node) {
				copyTo(target, paths, node);
			}
		});
		return target;
	}

	static void find(Node src, String[] paths, BiConsumer<Deque<Reference>, Node> consumer){
		
	}

	static void copyTo(Node target, Deque<Reference> paths, Node node){
		System.out.println();
		for(Reference r : paths){
			System.out.println(r);
		}
		Iterator<Reference> it = paths.iterator();
		Reference r = it.next();
		if(target.isArray()){
			target.appendChild(createNode(it, node));
		} else if(target.isMap()){
			target.appendChild(r.getId().toString(), createNode(it, node));
		} else{
			return;
		}
	}
	static Node createNode(Iterator<Reference> it, Node leaf){
		Reference ref = it.next();
		Node child = null;
		if(it.hasNext()){
			child = createNode(it, leaf);
		} else{
			return leaf;
		}
		Node n = null;
		Object id = ref.getId();
		if(id instanceof Integer){
			n = new ArrayNode(new ArrayList<>());
			n.appendChild(child);
		} else if(id instanceof String){
			n = new MapNode(new LinkedHashMap<String, Object>());
			n.appendChild((String)id, child);
		} else{
			return null;
		}
		return n;
	}
}
