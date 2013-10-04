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
package org.jsonman.finder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ReferenceParser {
	public static List<Reference> parse(String referencePath)
	throws ParseException{
		if(!referencePath.startsWith("/")){
			throw new ParseException(referencePath, 0);
		}
		List<Reference> ret = new ArrayList<>();
		int off = 0;
		for(String p : referencePath.substring(1).split("\\/")){
			if(digits.matcher(p).matches()){
				ret.add(new ArrayReference(Integer.parseInt(p)));
			} else if(name.matcher(p).matches()){
				ret.add(new MapReference(p));
			} else{
				throw new ParseException(referencePath, off);
			}
			off += 1 + p.length();
		}
		return ret;
	}

	private static Pattern digits = Pattern.compile("[0-9]+");
	private static Pattern name = Pattern.compile("[A-Za-z\\_\\-0-9]+");
}
