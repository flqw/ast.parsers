package com.github.flqw.ast.parsers.jaxen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.flqw.ast.parsers.AstNode;

public class Attribute<N> {

	//private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];
	private AstNode<N> parent;
	private String name;
	private Method method;
	private Object value;
	private String stringValue;

	public Attribute(AstNode<N> parent, String name, Method m) {
		this.parent = parent;
		this.name = name;
		method = m;
	}

	public Attribute(AstNode<N> parent, String name, String value) {
		this.parent = parent;
		this.name = name;
		this.value = value;
		stringValue = value;
	}

	public Object getValue() {
		if (value != null) {
			return value;
		}
		// this lazy loading reduces calls to Method.invoke() by about 90%
		try {
			return method.invoke(parent.getUnderlyingNode());
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		}
		return null;
	}

	public String getStringValue() {
		if (stringValue != null) {
			return stringValue;
		}
		Object v = value;
		if (value == null) {
			v = getValue();
		}
		if (v == null) {
			stringValue = "";
		} else {
			stringValue = String.valueOf(v);
		}
		return stringValue;
	}

	public String getName() {
		return name;
	}

	public AstNode<N> getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return name + ":" + getValue() + ":" + parent.getUnderlyingNode();
	}
}