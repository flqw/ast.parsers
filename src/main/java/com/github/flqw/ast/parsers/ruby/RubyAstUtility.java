package com.github.flqw.ast.parsers.ruby;

import java.io.StringReader;

import org.jrubyparser.CompatVersion;
import org.jrubyparser.Parser;
import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserConfiguration;

import com.github.flqw.ast.parsers.AstNode;
import com.github.flqw.ast.parsers.AstUtility;

public class RubyAstUtility implements AstUtility<Node> {

	public static final RubyAstUtility INSTANCE = new RubyAstUtility();

	private static final Parser PARSER = new Parser();

	private static ParserConfiguration getParserConfig() {
		return new ParserConfiguration(1, CompatVersion.RUBY2_0);
	}

	@Override
	public Class<Node> getNodeSuperClass() {
		return Node.class;
	}

	@Override
	public  AstNode<Node> parse(String content) throws Exception {
		return buildInternal(PARSER.parse("", new StringReader(content), getParserConfig()), null);
	}

	protected AstNode<Node> buildInternal(Node node, AstNode<Node> parent) {
		AstNode<Node> astNode = new RubyAstNode(node, parent);

		for (Node child : node.childNodes()) {
			buildInternal(child, astNode);
		}

		return astNode;
	}

	@Override
	public String getLanguage() {
		return "Ruby 2.0";
	}



}
