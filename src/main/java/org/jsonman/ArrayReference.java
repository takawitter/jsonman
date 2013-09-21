package org.jsonman;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jsonman.node.ArrayNode;

public class ArrayReference implements Reference{
	public ArrayReference(ArrayNode parent, Integer id){
		this.parent = parent;
		this.id = id;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public ArrayNode getParent(){
		return parent;
	}

	@Override
	public Integer getId(){
		return id;
	}

	private ArrayNode parent;
	private Integer id;
}
