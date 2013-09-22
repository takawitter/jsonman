package org.jsonman.ks;

import java.util.Deque;
import java.util.LinkedList;

import org.jsonman.Node;
import org.jsonman.ks.function.BiConsumer;
import org.jsonman.ks.path.Fragment;
import org.jsonman.ks.path.PathScanner;
import org.jsonman.node.MapNode;

public class NodeFinder {
	public NodeFinder(String path){
		this.path = path;
	}

	public void find(Node src, BiConsumer<Deque<Reference>, Node> consumer){
		find(src, PathScanner.parsePath(path), consumer);
	}

	private static void find(Node src, final Fragment[] fragments, final BiConsumer<Deque<Reference>, Node> consumer){
		if(fragments.length == 0) return;
		src.visit(new NodeRecursiveVisitor(new LinkedList<Reference>()){
			@Override
			public void accept(MapNode node) {
				Fragment f = fragments[nest];
				Node child = node.getChild(f.getName());
				if(child == null) return;
				getPaths().addLast(new MapReference(f.getName()));
				if(nest < (fragments.length - 1)){
					nest++;
					child.visit(this);
					nest--;
				} else{
					consumer.accept(getPaths(), child);
				}
				getPaths().removeLast();
			}
			private int nest = 0;
		});
	}

	private String path;
}
