package org.jsonman.ks;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.io.StringInputSource;
import net.arnx.jsonic.parse.JSONParser;
import net.arnx.jsonic.util.LocalCache;

public class ParserPerformance {
	public static void main(String[] args) throws Exception{
		String json = "[{\"name\":\"john\",\"pc\":{\"hard\":\"macbook\",\"os\":\"freebsd\"},\"age\":30},{\"name\":\"bob\",\"age\":40}]";
		int n = 1000000;
		{
			found = 0;
			long s = System.nanoTime();
			for(int i = 0; i < n; i++){
				find1(json);
			}
			long d = System.nanoTime() - s;
			System.out.println(String.format("%d msec. %d times found.", d / 1000000, found));
		}
		{
			found = 0;
			long s = System.nanoTime();
			for(int i = 0; i < n; i++){
				find2(json);
			}
			long d = System.nanoTime() - s;
			System.out.println(String.format("%d msec. %d times found.", d / 1000000, found));
		}
	}

	private static int found;

	private static void find1(String json){
		List<Map<String, Object>> root = (List<Map<String, Object>>)JSON.decode(json);
		for(Map<String, Object> element : root){
			if(element.get("name").equals("bob")){
				((Number)element.get("age")).intValue();
				found++;
			}
		}
	}

	private static void find2(String json) throws Exception{
		JSONParser p = new JSONParser(
				new StringInputSource(json), 100, true, true,
				new LocalCache("net.arnx.jsonic.Messages", Locale.getDefault(), TimeZone.getDefault())
				);
		if(!p.next().equals(JSONEventType.START_ARRAY)) throw new Exception(); // START_ARRAY
		while(true){
			if(!p.next().equals(JSONEventType.START_OBJECT)) break;
			boolean bob = false;
			int age = -1;
			while(true){
				if(!p.next().equals(JSONEventType.NAME)) break;
				if(p.getValue().equals("name")){
					if(p.next().equals(JSONEventType.STRING)){
						if(p.getValue().equals("bob")){
							if(age != -1){
								found++;
								return;
							}
							bob = true;
						}
					}
					if(!bob){
						skipToEnd(p);
						break;
					}
				} else if(p.getValue().equals("age")){
					if(!p.next().equals(JSONEventType.NUMBER)) break;
					age = ((Number)p.getValue()).intValue();
					if(bob){
						found++;
						return;
					}
				}
				
			}
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
}
