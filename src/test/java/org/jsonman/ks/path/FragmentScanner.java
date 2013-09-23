package org.jsonman.ks.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FragmentScanner implements Iterator<Fragment>, Cloneable{
	protected FragmentScanner(Fragment[] fragments, int index) {
		this.fragments = fragments;
		this.index = index;
	}

	public FragmentScanner(String path){
		this.fragments = parsePath(path);
		this.index = 0;
	}

	@Override
	public FragmentScanner clone(){
		return new FragmentScanner(this.fragments, this.index);
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
		Object v = toValue(nv[1]);
		return new Fragment(name, new Condition(nv[0], v, Condition.Operator.COMPLETEMATCH));
	}

	private static Object toValue(String value){
		if(value.startsWith("'") && value.endsWith("'")){
			return value.substring(1, value.length() - 1);
		} else if(value.equals("true")){
			return true;
		} else if(value.equals("false")){
			return false;
		} else if(value.equals("null")){
			return null;
		} else if(value.matches("\\d+")){
			return Integer.parseInt(value);
		} else if(value.matches("\\d+\\.\\d+")){
			return Double.parseDouble(value);
		}
		return value;
	}

	private final Fragment[] fragments;
	private int index = 0;
}
