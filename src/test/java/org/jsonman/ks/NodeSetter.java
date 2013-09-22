package org.jsonman.ks;

import java.util.Iterator;

import org.jsonman.Node;
import org.jsonman.node.ArrayNode;
import org.jsonman.node.MapNode;

public class NodeSetter {
	public NodeSetter(Node target){
		this.target = target;
	}

	public Node getTarget() {
		return target;
	}

	public void setTo(Iterable<Reference> paths, Node node){
		final Iterator<Reference> it = paths.iterator();
		Node t = target;
		while(true){
			Reference r = it.next();
			System.out.println(String.format(
					"copy %s to %s.", r, t
					));
			if(t.isArray() && r.isArray()){
				ArrayNode an = t.cast();
				Integer index = r.getId();
				t = an.getChild(index);
				if(t != null){
					if(!it.hasNext()){
						if(t.isMap() && node.isMap()){
							((MapNode)t).mergeValue((MapNode)node);
						} else{
							t.setValue(node.getValue());
						}
						break;
					}
					continue;
				} else{
					an.setChild(index, createNode(it, node));
					break;
				}
			} else if(t.isMap() && r.isMap()){
				MapNode mn = t.cast();
				String name = r.getId();
				t = mn.getChild(name);
				if(t != null){
					if(!it.hasNext()){
						if(t.isMap() && node.isMap()){
							((MapNode)t).mergeValue((MapNode)node);
						} else{
							t.setValue(node.getValue());
						}
						break;
					}
					continue;
				} else{
					mn.setChild(name, createNode(it, node));
					break;
				}
			} else{
				throw new RuntimeException(String.format(
						"type of node(%s) and reference(%s) not match.",
						t.getClass().getName(),
						r.getClass().getName()
						));
			}
		}
	}
	static Node createNode(Iterator<Reference> it, Node leaf){
		Reference ref = it.next();
		Node child = null;
		if(it.hasNext()){
			child = createNode(it, leaf);
		} else{
			child = leaf;
		}
		Node n = null;
		if(ref.isMap()){
			MapNode mn = new MapNode();
			mn.appendChild((String)ref.getId(), child);
			n = mn;
		} else if(ref.isArray()){
			ArrayNode an = new ArrayNode();
			an.setChild((Integer)ref.getId(), child);
			n = an;
		} else{
			return null;
		}
		return n;
	}

	private Node target;
}
