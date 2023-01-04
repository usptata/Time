package com.tekmonk.time.parser;

import java.io.IOException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tekmonk.time.url.URLReader;

public class LatestNewsParser extends DataParser {

	protected JSONArray arr;

	/* Constructor */
	public LatestNewsParser(URLReader source) {
		super(source);
		arr = new JSONArray();
	}

	@SuppressWarnings("null")
	@Override
	/* This parsers the from start of body tag to end of body tag */
	public HTMLTag parseFromBodyTag() throws Exception {
		// TODO Auto-generated method stub

		HTMLTag tempTag = null;
		String name = null;
		String propname = null;
		String propValue = null;
		int pos = 0, spaceCount = 0;
		while (line.isEmpty()) {
			line = source.readNextLine();
		}
		if (line != null) {
			while (!line.isEmpty()) {
				switch (line.charAt(pos)) {
					case TagType.TAG_OPEN:
						if ((line = removeComment(line)) != null) {
							if (tempTag == null && pos > 0 && pos != spaceCount) {
								tempTag = new HTMLTag();
								tempTag.setTagValue(line.substring(0, pos--));
								line = removeThis(line, line.substring(0, pos + 1));
								return tempTag;
							} else {
								tempTag = new HTMLTag();
								line = removeThis(line, line.substring(0, pos + 1));
								pos = 0;
							}
						}
						break;
					case TagType.TAG_EQUALS:
						if (tempTag != null) {
							propname = line.substring(0, pos++);
							line = removeThis(line, propname);
							pos -= propname.length();
						}

					case TagType.TAG_DOUBLEQUOTE:
						if (tempTag != null) {
							if (line.charAt(pos++) == TagType.TAG_DOUBLEQUOTE) {
								while (line.charAt(pos) != TagType.TAG_DOUBLEQUOTE
										&& line.charAt(pos) != TagType.TAG_OPEN
										&& line.charAt(pos) != TagType.TAG_CLOSE) {
									pos++;
								}
								propValue = line.substring(1, pos);
								line = removeThis(line, line.substring(0, pos + 1));
								tempTag.setProperty(propname.replaceAll(" ", ""), propValue.replaceAll("\"", ""));
								pos = 0;
							}
						} else {
							pos++;
						}
						break;
					case TagType.TAG_CLOSE:

						if (tempTag != null && name == null) {
							name = line.substring(0, pos);
							tempTag.setTagName(name);
						}
						line = removeThis(line, line.substring(0, pos + 1));
						tempTag.setEndTagName();
						if (currentTagCheck(tempTag) == null) {
							tagStack.pop();
							return tempTag;
						} else {
							while (true) {
								HTMLTag tag = parseFromBodyTag();
								if (tag != null) {
									if (tag.getTagName() == null) {
										tempTag.setTagValue(tag.getTagValue());
									} else if (tempTag.getEndTagName().equals(tag.getFullTag())) {
										tempTag.next_htmlTag.add(tag);
										return tempTag;
									} else {
										tempTag.next_htmlTag.add(tag);
									}
								}
							}
						}

					case TagType.TAG_SPACE:
						if (tempTag != null && name == null) {
							name = line.substring(0, pos);
							tempTag.setTagName(name);
							line = removeThis(line, line.substring(0, pos + 1));
							pos = 0;
						} else {
							pos++;
							spaceCount++;
						}

						break;
					case TagType.TAG_FWDSLASH:
						if (line.charAt(pos + 1) == TagType.TAG_CLOSE) {
							line = removeThis(line, line.substring(0, line.length()));
							tempTag.setEndTagName();
							HTMLTag temp = new HTMLTag("/" + tempTag.getTagName());
							tempTag.next_htmlTag.add(temp);
							return tempTag;
						}
					default:
						pos++;
						break;

				}

				if (line.length() == pos) {
					if (pos > 0) {
						if (line.replaceAll(" ", "").length() == 0) {
							line = source.readNextLine();
							pos = 0;
							spaceCount = 0;
						} else if (name == null && tempTag != null) {
							name = line.substring(0, pos);
							tempTag.setTagName(name);
							line = source.readNextLine();
							spaceCount = 0;
							pos = 0;
						} else {
							tempTag = new HTMLTag();
							tempTag.setTagValue(line.substring(0, pos));
							line = removeThis(line, line.substring(0, pos));
							return tempTag;

						}
					} else if (tempTag.getEndTagName() != null) {
						while (line.isEmpty()) {
							HTMLTag tag = parseFromBodyTag();
							if (tag != null) {
								if (tag.getTagName() == null)
									tempTag.setTagValue(tag.getTagValue());
								else if (currentTagCheck(tag) == null) {
									tagStack.pop();
									return tempTag;
								}
							}
						}
						spaceCount = 0;
					} else {
						line = source.readNextLine();
						spaceCount = 0;
						pos = 0;
					}
				}
			}
		}
		return null;
	}

	@Override
	/*
	 * This function ignores the code till head tag
	 * Parsers the HTML code and returns the HTMLTag
	 * this returns the starting tag
	 */
	public HTMLTag parser() throws Exception {

		while ((line = source.readNextLine()) != null) {
			if (line.contains("</head>")) {
				line = source.readNextLine();
				return parseFromBodyTag();
			}
		}
		return null;

	}// parser

	public JSONArray getLatestStories(HTMLTag tag, String PropertyName) {
		int i = 0;
		String href_url = "https://time.com";
		String title = "";
		JSONObject obj = new JSONObject();
		if (tag != null) {
			if (tag.hasProperties()) {
				if (tag.getProperty(TagType.TAG_CLASSNAME).equals(PropertyName)) {
					href_url += tag.next_htmlTag.get(0).getProperty(TagType.TAG_HREF);
					title = tag.next_htmlTag.get(0).next_htmlTag.get(0).getTagValue();
					obj.put("Title", title);
					obj.put("Link", href_url);
					arr.add(obj);
					return arr;
				}
			}
			while (i < tag.next_htmlTag.size()) {
				getLatestStories(tag.next_htmlTag.get(i++), PropertyName);
			}
		}
		return arr;
	}

}