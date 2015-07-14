/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.github.flqw.ast.parsers.jaxen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.flqw.ast.parsers.AstNode;

public class AttributeAxisIterator<N> implements Iterator<Attribute<N>> {

    private static class MethodWrapper {
        public Method method;
        public String name;

        public MethodWrapper(Method m) {
            method = m;
            name = truncateMethodName(m.getName());
        }

        private String truncateMethodName(String n) {
            // about 70% of the methods start with 'get', so this case goes
            // first
            if (n.startsWith("get")) {
                return n.substring("get".length());
            }
            if (n.startsWith("is")) {
                return n.substring("is".length());
            }
            if (n.startsWith("has")) {
                return n.substring("has".length());
            }
            if (n.startsWith("uses")) {
                return n.substring("uses".length());
            }

            return n;
        }
    }

    private Attribute<N> currObj;
    private MethodWrapper[] methodWrappers;
    private int position;
    private AstNode<N> node;

    private static Map<Class<?>, MethodWrapper[]> methodCache =
            Collections.synchronizedMap(new HashMap<Class<?>, MethodWrapper[]>());

    public AttributeAxisIterator(AstNode<N> contextNode) {
        node = contextNode;
        Class<?> underlyingNodeClass = contextNode.getUnderlyingNode().getClass();
//        if (!methodCache.containsKey(underlyingNodeClass)) {
			Method[] preFilter = underlyingNodeClass.getMethods();
            List<MethodWrapper> postFilter = new ArrayList<MethodWrapper>();
            for (Method element : preFilter) {
                if (isAttributeAccessor(element)) {
                    postFilter.add(new MethodWrapper(element));
                }
            }
            methodCache.put(underlyingNodeClass, postFilter.toArray(new MethodWrapper[postFilter.size()]));
//        }
        methodWrappers = methodCache.get(underlyingNodeClass);

        position = 0;
        currObj = getNextAttribute();
    }

    @Override
	public Attribute<N> next() {
        if (currObj == null) {
            throw new IndexOutOfBoundsException();
        }
        Attribute<N> ret = currObj;
        currObj = getNextAttribute();
        return ret;
    }

    @Override
	public boolean hasNext() {
        return currObj != null;
    }

    @Override
	public void remove() {
        throw new UnsupportedOperationException();
    }

    private Attribute<N> getNextAttribute() {
        if (methodWrappers == null || position == methodWrappers.length) {
            return null;
        }
        MethodWrapper m = methodWrappers[position++];
        return new Attribute<>(node, m.name, m.method);
    }

    protected boolean isAttributeAccessor(Method method) {
        String methodName = method.getName();

        return method.getParameterCount() == 0 && (methodName.startsWith("get") || methodName.startsWith("is"));

//        boolean deprecated = method.getAnnotation(Deprecated.class) != null;
//
//        return !deprecated
//                && (Integer.TYPE == method.getReturnType() || Boolean.TYPE == method.getReturnType()
//                || Double.TYPE == method.getReturnType() || String.class == method.getReturnType())
//                && method.getParameterTypes().length == 0
//                && Void.TYPE != method.getReturnType()
//                && !methodName.startsWith("jjt")
//                && !methodName.equals("toString")
//                && !methodName.equals("getScope")
//                && !methodName.equals("getClass")
//                && !methodName.equals("getTypeNameNode")
//                && !methodName.equals("getImportedNameNode")
//                && !methodName.equals("hashCode");
    }
}