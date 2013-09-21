package org.jsonman;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jsonman.node.MapNode;

public class MapReference implements Reference{
	public MapReference(MapNode parent, String id){
		this.parent = parent;
		this.id = id;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public MapNode getParent(){
		return parent;
	}

	@Override
	public String getId(){
		return id;
	}

	private MapNode parent;
	private String id;
}
