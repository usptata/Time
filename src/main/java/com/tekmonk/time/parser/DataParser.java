package com.tekmonk.time.parser;

import com.tekmonk.time.url.URLReader;
import java.util.*;
import org.json.simple.JSONObject;

public class DataParser {

	protected URLReader source;
	protected String line;
	protected Stack<HTMLTag> tagStack;

	public DataParser(URLReader source) {
		this.source = source;
		tagStack = new Stack<HTMLTag>();
	}

	/* Removes str from srcStr */
	String removeThis(String srcStr, String str) {
		int i = 0;
		String new_str = "";
		while (i < str.length()) {
			new_str += "";
			i++;
		}

		while (i < srcStr.length()) {
			new_str += srcStr.charAt(i);
			i++;
		}
		return new_str;
	}

	/* This removes the comments from HTML Code */
	String removeComment(String str) throws Exception {

		if (str.substring(1, 4).equals("!--")) {
			String temp;
			while (true) {
				temp = source.readNextLine();
				if (temp.contains("-->")) {
					int i = temp.indexOf('>');
					if (i < temp.length() - 1) {
						return removeThis(temp, temp.substring(0, i + 1));
					}
					break;
				}
			}
			temp = source.readNextLine();
			str = temp;
		}
		return str;
	}

	@SuppressWarnings("null")
	HTMLTag getNextToken() throws Exception {
		HTMLTag tempTag = null;
		String name = null;
		String propname = null;
		String propValue = null;
		int pos = 0;
		while ((line = source.readNextLine()) != null) {
			while (true) {
				char c;
				switch (c = line.charAt(pos)) {
					case '<':
						if ((line = removeComment(line)) != null) {
							if (tempTag != null) {
								tempTag.setTagValue(line.substring(0, pos + 1));
								line = removeThis(line, line.substring(0, pos + 1));
							} else {
								line = removeThis(line, line.substring(0, pos + 1));
								tempTag = new HTMLTag();
							}
							pos = 0;
						}
						break;
					case '=':
						if (tempTag != null) {
							propname = line.substring(0, pos++);
							line = removeThis(line, propname);
							pos = 1;
						} else {
							pos++;
						}
					case '"':
						if (tempTag != null) {
							if (line.charAt(pos++) == '"') {
								while (line.charAt(pos) != '"'
										&& line.charAt(pos) != '<' && line.charAt(pos) != '>') {
									pos++;
								}
								propValue = line.substring(1, pos);
								line = removeThis(line, line.substring(0, pos + 1));
								tempTag.setProperty(propname, propValue);
								pos = 0;
							}
						} else {
							pos++;
						}
						break;
					case '>':
						if (tempTag != null && name == null) {
							name = line.substring(0, pos);
							tempTag.setTagName(name);
							line = removeThis(line, line.substring(0, pos + 1));
						} else {
							line = removeThis(line, line.substring(0, pos + 1));
						}
						tempTag.next_htmlTag.add(getNextToken());
						pos = 0;
						break;
					case ' ':
						if (tempTag != null && name == null) {
							name = line.substring(0, pos + 1);
							tempTag.setTagName(name);
							line = removeThis(line, name);
							pos = 0;
						} else {
							pos++;
						}

						break;
					default:
						pos++;
						break;

				}
				if (line.length() == pos) {
					tempTag.next_htmlTag.add(getNextToken());
				}
			}
		}

		return tempTag;
	}

	/*
	 * This check the current tag is end tag of open tag,
	 * if it's end tag returns the null or else returns the same tag
	 */
	public HTMLTag currentTagCheck(HTMLTag tag) {
		// TODO Auto-generated method stub
		if (tag != null) {

			if (tagStack.size() > 0) {
				if (tag.getFullTag().equals(tagStack.peek().getEndTagName())) {
					return null;
				}
			}
			tagStack.add(tag);
			return tag;
		}
		return null;
	}// currentTagCheck

	/*
	 * This function ignores the code till required value is found in tag
	 * Parsers the HTML code and returns the HTMLTag this returns the starting tag
	 */
	public HTMLTag parser(String fromTag) throws Exception {

		while ((line = source.readNextLine()) != null) {
			if (line.contains(fromTag)) {
				return parseFromBodyTag();
			}
		}
		return null;

	}// parser

	/*
	 * Parsers the HTML code and returns the HTMLTag
	 * this returns the starting tag
	 */
	public HTMLTag parser() throws Exception {
		return null;
	}

	public HTMLTag parseFromBodyTag() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
