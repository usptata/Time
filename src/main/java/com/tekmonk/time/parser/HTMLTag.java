package com.tekmonk.time.parser;

import java.util.*;

public class HTMLTag {

	private String tagName;

	private String fullTag;

	private String tagValue;

	private String endTagType;

	private Map<String, String> props;

	Vector<HTMLTag> next_htmlTag;

	public HTMLTag() {

		props = new HashMap<String, String>();
		next_htmlTag = new Vector<HTMLTag>();

	}

	HTMLTag(String name) {
		// TODO Auto-generated constructor stub
		this.tagName = name;
		createFullTag(name);
		props = new HashMap<String, String>();
		next_htmlTag = new Vector<HTMLTag>();
	}

	void setTagName(String name) {
		this.tagName = name;
		createFullTag(name);
	}

	void setEndTagName() {
		if (!tagName.contains("/"))
			this.endTagType = "</" + tagName + ">";
	}

	void createFullTag(String name) {
		this.fullTag = "<" + name + ">";
	}

	void setTagValue(String value) {
		this.tagValue = value;
	}

	void setProperty(String propname, String propvalue) {
		if (props.containsValue(propvalue)) {
			props.replace(propname, propvalue);
		} else {
			props.put(propname, propvalue);
		}
	}

	String getProperty(String name) {
		if (props.containsKey(name)) {
			return props.get(name);
		}
		return null;
	}

	boolean hasProperties() {
		return props.size() == 0 ? false : true;
	}

	String getTagValue() {
		return this.tagValue;
	}

	String getFullTag() {
		return this.fullTag;
	}

	String getEndTagName() {
		return this.endTagType;
	}

	String getTagName() {
		return this.tagName;
	}

}
