package com.github.flqw.ast.parsers.erb;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jrubyparser.ast.Node;

import com.github.flqw.ast.parsers.AstNode;
import com.github.flqw.ast.parsers.AstUtility;
import com.github.flqw.ast.parsers.ruby.RubyAstUtility;

public class ErbAstUtility implements AstUtility<Node> {

	public static final ErbAstUtility INSTANCE = new ErbAstUtility();

	@Override
	public String getLanguage() {
		return "ERB (" + InternalParser.INSTANCE.getLanguage() + ")";
	}

	@Override
	public Class<Node> getNodeSuperClass() {
		return InternalParser.INSTANCE.getNodeSuperClass();
	}

	@Override
	public AstNode<Node> parse(String sourceCode) throws Exception {
		return new InternalParser().parse(sourceCode);
	}

	public static class InternalParser extends RubyAstUtility {

		// This should work since the ruby implementation of erb also uses regex.
		// http://stackoverflow.com/questions/22575766/why-do-ruby-erb-and-jquery-use-regexes-to-parse-html
		public static final Pattern PATTERN = Pattern.compile("<%=?(.*?)-?%>", Pattern.DOTALL);

		private Map<Integer, Integer> lineOffsetDeltas;

		@Override
		public AstNode<Node> parse(String content) throws Exception {
			return super.parse(extractRuby(content));
		}

		private String extractRuby(String content) throws IOException {
			Matcher matcher = PATTERN.matcher(content);

			lineOffsetDeltas = new HashMap<>();

			StringWriter stringWriter = new StringWriter();

			try (BufferedWriter writer = new BufferedWriter(stringWriter)) {

				int lineNumber = 1;
				int rubyOffset = 0;
				while (matcher.find()) {

					// Count the difference between the ruby offset and the real offset
					// The ruby offset will be later provided by the ruby parser, so we
					// need to add all the code in between to get the correct offset.

					int startIndex = matcher.start(1);
					String match = matcher.group(1);
					String lines[] = match.split("(?<=\\n)"); // Split at \n, but keep it.

					for (String line : lines) {
						int length = line.length();
												           // This should be the delta
						lineOffsetDeltas.put(lineNumber++, startIndex - rubyOffset);
						writer.write(line);

						startIndex += length;
						rubyOffset += length;
					}

					writer.newLine();
					rubyOffset += System.lineSeparator().length(); // Don't forget the newline we just added.

				}
			}

			String rubyCode = stringWriter.toString();
			if (rubyCode.length() > 0) {
				rubyCode = rubyCode.substring(0, rubyCode.length() - System.lineSeparator().length()); // Cut off the last new line
			}

			return rubyCode;
		}

		private int getDeltaForLine(int line) {
			Integer offset = lineOffsetDeltas.get(line);
			if (offset == null) {
				return 0;
			}
			return offset;
		}

		@Override
		protected AstNode<Node> buildInternal(Node node, AstNode<Node> parent) {

			AstNode<Node> astNode = new ErbAstNode(node, parent) {

				@Override
				public int getStartOffset() {
					return node.getPosition().getStartOffset() + getDeltaForLine(node.getPosition().getStartLine());
				}

				@Override
				public int getEndOffset() {
					return node.getPosition().getEndOffset() + getDeltaForLine(node.getPosition().getEndLine());
				}

			};

			for (Node child : node.childNodes()) {
				buildInternal(child, astNode);
			}

			return astNode;
		}
	}

}
