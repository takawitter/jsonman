package org.jsonman.ks.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class PathScanner implements Iterator<Fragment>, Cloneable{
	protected PathScanner(Fragment[] fragments, int index) {
		this.fragments = fragments;
		this.index = index;
	}

	public PathScanner(String path){
		this.fragments = parsePath(path);
		this.index = 0;
	}

	@Override
	public PathScanner clone(){
		return new PathScanner(this.fragments, this.index);
	}

	public boolean hasNext(){
		return index != fragments.length;
	}

	public Fragment next(){
		if(!hasNext()) throw new NoSuchElementException();
		return fragments[index++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static Fragment[] parsePath(String path){
		List<Fragment> fragments = new ArrayList<>();
		try(Scanner scanner = new Scanner(path)){
			scanner.useDelimiter("/");
			while(scanner.hasNext()){
				fragments.add(parseFragment(scanner.next()));
			}
		}
		return fragments.toArray(new Fragment[]{});
	}

	private static Fragment parseFragment(String text){
		int bs = text.indexOf("[");
		if(bs == -1){
			return new Fragment(text);
		}
		if(text.charAt(text.length() - 1) != ']'){
			throw new NoSuchElementException("invalid format: " + text);
		}
		String name = text.substring(0, bs);
		text = text.substring(bs + 1, text.length() - 1);
		String[] nv = text.split("=");
		if(nv.length != 2) throw new RuntimeException("invalid format: " + text);
		return new Fragment(name, new Condition(nv[0], nv[1], Condition.Operator.COMPLETEMATCH));
	}

	private final Fragment[] fragments;
	private int index = 0;
}
