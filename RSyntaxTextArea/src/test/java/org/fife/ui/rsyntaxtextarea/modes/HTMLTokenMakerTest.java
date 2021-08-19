/*
 * 03/23/2015
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rsyntaxtextarea.modes;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the {@link HTMLTokenMaker} class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class HTMLTokenMakerTest extends AbstractTokenMakerTest2 {

	/**
	 * The last token type on the previous line for this token maker to
	 * start parsing a new line as CSS.  This constant is only here so we can
	 * copy and paste tests from this class into others, such as HTML, PHP, and
	 * JSP token maker tests, with as little change as possible.
	 */
	private static final int CSS_PREV_TOKEN_TYPE = HTMLTokenMaker.INTERNAL_CSS;

	/**
	 * The last token type on the previous line for this token maker to
	 * start parsing a new line as part of a CSS multi-line comment.  This constant
	 * is only here so we can copy and paste tests from this class into others,
	 * such as HTML, PHP, and JSP token maker tests, with as little change as possible.
	 */
	private static final int CSS_MLC_PREV_TOKEN_TYPE = HTMLTokenMaker.INTERNAL_CSS_MLC;

	/**
	 * The last token type on the previous line for this token maker to start
	 * parsing a new line as part of a CSS property key/value block.  This constant
	 * is only here so we can copy and paste tests from this class into others,
	 * such as HTML, PHP, and JSP token maker tests, with as little change as possible.
	 */
	private static final int CSS_PROPERTY_PREV_TOKEN_TYPE = HTMLTokenMaker.INTERNAL_CSS_PROPERTY;

	/**
	 * The last token type on the previous line for this token maker to start
	 * parsing a new line as part of a CSS property key/value block.  This constant
	 * is only here so we can copy and paste tests from this class into others,
	 * such as HTML, PHP, and JSP token maker tests, with as little change as possible.
	 */
	private static final int CSS_VALUE_PREV_TOKEN_TYPE = HTMLTokenMaker.INTERNAL_CSS_VALUE;

	/**
	 * The last token type on the previous line for this token maker to start
	 * parsing a new line as part of a string property value.  This constant
	 * is only here so we can copy and paste tests from this class into others,
	 * such as HTML, PHP, and JSP token maker tests, with as little change as possible.
	 */
	private static final int CSS_STRING_PREV_TOKEN_TYPE = HTMLTokenMaker.INTERNAL_CSS_STRING;

	/**
	 * The last token type on the previous line for this token maker to start
	 * parsing a new line as part of a char property value.  This constant
	 * is only here so we can copy and paste tests from this class into others,
	 * such as HTML, PHP, and JSP token maker tests, with as little change as possible.
	 */
	private static final int CSS_CHAR_PREV_TOKEN_TYPE = HTMLTokenMaker.INTERNAL_CSS_CHAR;

	/**
	 * The last token type on the previous line for this token maker to
	 * start parsing a new line as JS.  This constant is only here so we can
	 * copy and paste tests from the JavaScriptTokenMakerTest class into others,
	 * such as HTML, PHP, and JSP token maker tests, with as little change as
	 * possible.
	 */
	private static final int JS_PREV_TOKEN_TYPE = HTMLTokenMaker.INTERNAL_IN_JS;


	/**
	 * Returns a new instance of the <code>TokenMaker</code> to test.
	 *
	 * @return The <code>TokenMaker</code> to test.
	 */
	@Override
	protected TokenMaker createTokenMaker() {
		return new HTMLTokenMaker();
	}


	@Test
	@Override
	public void testGetLineCommentStartAndEnd() {
		String[] startAndEnd = createTokenMaker().getLineCommentStartAndEnd(0);
		Assert.assertEquals("<!--", startAndEnd[0]);
		Assert.assertEquals("-->", startAndEnd[1]);
	}


	@Test
	public void testCss_atRule() {
		assertAllTokensOfType(TokenTypes.REGEX,
			CSS_PREV_TOKEN_TYPE,
			"@charset",
			"@import",
			"@namespace",
			"@media",
			"@document",
			"@page",
			"@font-face",
			"@keyframes",
			"@viewport"
		);
	}


	@Test
	public void testCss_chars() {
		assertAllTokensOfType(TokenTypes.LITERAL_CHAR,
			CSS_CHAR_PREV_TOKEN_TYPE,
			"'Hello world'",
			"'Hello \\'world\\''"
		);
	}


	@Test
	public void testCss_char_continuedFromPreviousLine() {
		assertAllTokensOfType(TokenTypes.LITERAL_CHAR,
			CSS_CHAR_PREV_TOKEN_TYPE,
			"world'",
			"and \\'he\\' said so'"
		);
	}


	@Test
	public void testCss_getCurlyBracesDenoteCodeBlocks() {
		TokenMaker tm = createTokenMaker();
		Assert.assertTrue(tm.getCurlyBracesDenoteCodeBlocks(
			HTMLTokenMaker.LANG_INDEX_CSS));
	}


	@Test
	public void testCss_getLineCommentStartAndEnd() {
		String[] startAndEnd = createTokenMaker().getLineCommentStartAndEnd(
			HTMLTokenMaker.LANG_INDEX_CSS);
		Assert.assertEquals("/*", startAndEnd[0]);
		Assert.assertEquals("*/", startAndEnd[1]);
	}


	@Test
	public void testCss_happyPath_simpleSelector() {

		String code = "body { padding: 0; }";
		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, CSS_PREV_TOKEN_TYPE, 0);

		Assert.assertTrue(token.is(TokenTypes.DATA_TYPE, "body"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.SEPARATOR, "{"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.RESERVED_WORD, "padding"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.OPERATOR, ":"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.LITERAL_NUMBER_DECIMAL_INT, "0"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.OPERATOR, ";"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.SEPARATOR, "}"));

	}


	@Test
	public void testCss_id() {

		String code = "#mainContent";
		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, CSS_PREV_TOKEN_TYPE, 0);

		Assert.assertTrue(token.is(TokenTypes.VARIABLE, "#mainContent"));

	}

	@Test
	public void testCss_isIdentifierChar() {

		TokenMaker tm = createTokenMaker();
		int langIndex = HTMLTokenMaker.LANG_INDEX_CSS;

		for (int ch = 'A'; ch <= 'Z'; ch++) {
			Assert.assertTrue(tm.isIdentifierChar(langIndex, (char)ch));
			Assert.assertTrue(tm.isIdentifierChar(langIndex, (char)(ch+('a'-'A'))));
		}
		Assert.assertTrue(tm.isIdentifierChar(langIndex, '-'));
		Assert.assertTrue(tm.isIdentifierChar(langIndex, '_'));
		Assert.assertTrue(tm.isIdentifierChar(langIndex, '.'));
	}


	@Test
	public void testCss_lessLineComment_noLess() {
		assertAllTokensOfType(TokenTypes.IDENTIFIER,
			CSS_PREV_TOKEN_TYPE,
			"//"
		);
	}


	@Test
	public void testCss_lessVar_noLess() {
		assertAllTokensOfType(TokenTypes.REGEX,
			CSS_PREV_TOKEN_TYPE,
			"@something",
			"@something-else"
		);
	}


	@Test
	public void testCss_multiLineComment() {

		assertAllTokensOfType(TokenTypes.COMMENT_MULTILINE, CSS_PREV_TOKEN_TYPE,
			"/* Hello world */"
		);
	}


	@Test
	public void testCss_multiLineComment_continuedFromPreviousLine() {

		assertAllTokensOfType(TokenTypes.COMMENT_MULTILINE, CSS_MLC_PREV_TOKEN_TYPE,
			" world */"
		);
	}


	@Test
	public void testCss_multiLineComment_URL() {

		String[] comments = {
			"/* Hello world file://test.txt */",
			"/* Hello world ftp://ftp.google.com */",
			"/* Hello world http://www.google.com */",
			"/* Hello world https://www.google.com */",
			"/* Hello world www.google.com */"
		};

		for (String comment : comments) {

			Segment segment = createSegment(comment);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, CSS_PREV_TOKEN_TYPE, 0);

			Assert.assertFalse(token.isHyperlink());
			Assert.assertTrue(token.is(TokenTypes.COMMENT_MULTILINE, "/* Hello world "));

			token = token.getNextToken();
			Assert.assertTrue(token.isHyperlink());

			token = token.getNextToken();
			Assert.assertFalse(token.isHyperlink());
			Assert.assertTrue(token.is(TokenTypes.COMMENT_MULTILINE, " */"));
		}
	}


	@Test
	public void testCss_operators() {
		assertAllTokensOfType(TokenTypes.OPERATOR,
			CSS_PREV_TOKEN_TYPE,
			"+",
			">",
			"~",
			"^",
			"$",
			"|",
			"="
		);
	}


	@Test
	public void testCss_propertyBlock_property_multiLineComment() {
		assertAllTokensOfType(TokenTypes.COMMENT_MULTILINE,
			CSS_PROPERTY_PREV_TOKEN_TYPE,
			"/* Hello world*/"
		);
	}


	@Test
	public void testCss_propertyBlock_property_properties() {
		assertAllTokensOfType(TokenTypes.RESERVED_WORD,
			CSS_PROPERTY_PREV_TOKEN_TYPE,
			"*foo",
			"foo",
			"_",
			"*_",
			"foo9",
			"foo9:bar"
		);
	}


	@Test
	public void testCss_propertyBlock_property_operators() {
		assertAllTokensOfType(TokenTypes.OPERATOR,
			CSS_PROPERTY_PREV_TOKEN_TYPE,
			":"
		);
	}


	@Test
	public void testCss_propertyBlock_property_separators() {
		assertAllTokensOfType(TokenTypes.SEPARATOR,
			CSS_PROPERTY_PREV_TOKEN_TYPE,
			"{",
			"}"
		);
	}


	@Test
	public void testCss_propertyBlock_value_charLiteral() {
		assertAllTokensOfType(TokenTypes.LITERAL_CHAR,
			CSS_VALUE_PREV_TOKEN_TYPE,
			"'foobar'"
		);
	}


	@Test
	public void testCss_propertyBlock_value_function() {
		assertAllTokensOfType(TokenTypes.FUNCTION,
			CSS_VALUE_PREV_TOKEN_TYPE,
			"func("
		);
	}


	@Test
	public void testCss_propertyBlock_value_identifier() {
		assertAllTokensOfType(TokenTypes.IDENTIFIER,
			CSS_VALUE_PREV_TOKEN_TYPE,
			"foobar",
			",",
			"."
		);
	}


	@Test
	public void testCss_propertyBlock_value_important() {
		assertAllTokensOfType(TokenTypes.PREPROCESSOR,
			CSS_VALUE_PREV_TOKEN_TYPE,
			"!important"
		);
	}


	@Test
	public void testCss_propertyBlock_value_multiLineComment() {
		assertAllTokensOfType(TokenTypes.COMMENT_MULTILINE,
			CSS_VALUE_PREV_TOKEN_TYPE,
			"/* Hello world*/"
		);
	}


	@Test
	public void testCss_propertyBlock_value_number() {
		assertAllTokensOfType(TokenTypes.LITERAL_NUMBER_DECIMAL_INT,
			CSS_VALUE_PREV_TOKEN_TYPE,
			"42",
			"42.",
			"42.3",
			"-42",
			"-42.",
			"-42.3",
			"4pt",
			"4pc",
			"4in",
			"4mm",
			"4cm",
			"4em",
			"4ex",
			"4px",
			"4ms",
			"4s",
			"4%",
			"#0",
			"#0A",
			"#0a",
			"#ff00ff"
		);
	}


	@Test
	public void testCss_propertyBlock_value_operators() {
		assertAllTokensOfType(TokenTypes.OPERATOR,
			CSS_VALUE_PREV_TOKEN_TYPE,
			";"
		);
	}


	@Test
	public void testCss_propertyBlock_value_separators() {
		assertAllTokensOfType(TokenTypes.SEPARATOR,
			CSS_VALUE_PREV_TOKEN_TYPE,
			")",
			"}"
		);
	}


	@Test
	public void testCss_propertyBlock_value_string() {
		assertAllTokensOfType(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE,
			CSS_VALUE_PREV_TOKEN_TYPE,
			"\"foobar\""
		);
	}


	@Test
	public void testCss_propertyValue_function() {

		String code = "background-image: url(\"test.png\");";
		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_CSS_PROPERTY, 0);

		Assert.assertTrue(token.is(TokenTypes.RESERVED_WORD, "background-image"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.OPERATOR, ":"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.FUNCTION, "url"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.SEPARATOR, "("));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, "\"test.png\""));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.SEPARATOR, ")"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.OPERATOR, ";"));

		code = "background-image: url('test.png');";
		segment = createSegment(code);
		tm = createTokenMaker();
		token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_CSS_PROPERTY, 0);

		Assert.assertTrue(token.is(TokenTypes.RESERVED_WORD, "background-image"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.OPERATOR, ":"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.FUNCTION, "url"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.SEPARATOR, "("));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.LITERAL_CHAR, "'test.png'"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.SEPARATOR, ")"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.OPERATOR, ";"));

	}


	@Test
	public void testCss_pseudoClass() {
		assertAllTokensOfType(TokenTypes.RESERVED_WORD,
			CSS_PREV_TOKEN_TYPE,
			":root",
			":nth-child",
			":nth-last-child",
			":nth-of-type",
			":nth-last-of-type",
			":first-child",
			":last-child",
			":first-of-type",
			":last-of-type",
			":only-child",
			":only-of-type",
			":empty",
			":link",
			":visited",
			":active",
			":hover",
			":focus",
			":target",
			":lang",
			":enabled",
			":disabled",
			":checked",
			"::first-line",
			"::first-letter",
			"::before",
			"::after",
			":not"
		);
	}


	@Test
	public void testCss_selectors() {
		assertAllTokensOfType(TokenTypes.DATA_TYPE,
			CSS_PREV_TOKEN_TYPE,
			"*",
			".",
			".foo",
			".foo-bar",
			"foo",
			"-foo-bar",
			"foo-bar");
	}


	@Test
	public void testCss_separators() {
		assertAllTokensOfType(TokenTypes.SEPARATOR,
			CSS_PREV_TOKEN_TYPE,
			";",
			"(",
			")",
			"[",
			"]"
		);
	}


	@Test
	public void testCss_strings() {
		assertAllTokensOfType(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE,
			CSS_PREV_TOKEN_TYPE,
			"\"Hello world\"",
			"\"Hello \\\"world\\\""
		);
	}


	@Test
	public void testCss_string_continuedFromPreviousLine() {
		assertAllTokensOfType(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE,
			CSS_STRING_PREV_TOKEN_TYPE,
			"world\"",
			"and \\\"he\\\" said so\""
		);
	}


	@Test
	public void testHtml_comment() {

		String[] commentLiterals = {
			"<!-- Hello world -->",
		};

		for (String code : commentLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);
			Assert.assertEquals(TokenTypes.MARKUP_COMMENT, token.getType());
		}

	}


	@Test
	public void testHtml_comment_URL() {

		String code = "<!-- Hello world http://www.google.com -->";
		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);

		Assert.assertFalse(token.isHyperlink());
		Assert.assertTrue(token.is(TokenTypes.MARKUP_COMMENT, "<!-- Hello world "));
		token = token.getNextToken();
		Assert.assertTrue(token.isHyperlink());
		Assert.assertTrue(token.is(TokenTypes.MARKUP_COMMENT, "http://www.google.com"));
		token = token.getNextToken();
		Assert.assertFalse(token.isHyperlink());
		Assert.assertTrue(token.is(TokenTypes.MARKUP_COMMENT, " -->"));

	}


	@Test
	public void testHtml_doctype() {

		String[] doctypes = {
			"<!doctype html>",
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">",
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">",
		};

		for (String code : doctypes) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);
			Assert.assertEquals(TokenTypes.MARKUP_DTD, token.getType());
		}

	}


	@Test
	public void testHtml_entityReferences() {

		String[] entityReferences = {
			"&nbsp;", "&lt;", "&gt;", "&#4012",
		};

		for (String code : entityReferences) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);
			Assert.assertEquals(TokenTypes.MARKUP_ENTITY_REFERENCE, token.getType());
		}

	}


	@Test
	public void testHtml_happyPath_tagWithAttributes() {

		String code = "<body onload=\"doSomething()\" data-extra='true'>";
		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);

		Assert.assertTrue(token.isSingleChar(TokenTypes.MARKUP_TAG_DELIMITER, '<'));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_NAME, "body"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_ATTRIBUTE, "onload"));
		token = token.getNextToken();
		Assert.assertTrue(token.isSingleChar(TokenTypes.OPERATOR, '='));
		token = token.getNextToken();
		Assert.assertTrue("Unexpected token: " + token, token.is(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE, "\"doSomething()\""));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_ATTRIBUTE, "data-extra"));
		token = token.getNextToken();
		Assert.assertTrue(token.isSingleChar(TokenTypes.OPERATOR, '='));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE, "'true'"));
		token = token.getNextToken();
		Assert.assertTrue(token.isSingleChar(TokenTypes.MARKUP_TAG_DELIMITER, '>'));

	}


	@Test
	public void testHtml_happyPath_closedTag() {

		String code = "<img src='foo.png'/>";
		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);

		Assert.assertTrue(token.isSingleChar(TokenTypes.MARKUP_TAG_DELIMITER, '<'));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_NAME, "img"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_ATTRIBUTE, "src"));
		token = token.getNextToken();
		Assert.assertTrue(token.isSingleChar(TokenTypes.OPERATOR, '='));
		token = token.getNextToken();
		Assert.assertTrue("Unexpected token: " + token, token.is(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE, "'foo.png'"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_DELIMITER, "/>"));

	}


	@Test
	public void testHtml_happyPath_closingTag() {

		String code = "</body>";
		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);

		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_DELIMITER, "</"));
		token = token.getNextToken();
		Assert.assertTrue(token.is(TokenTypes.MARKUP_TAG_NAME, "body"));
		token = token.getNextToken();
		Assert.assertTrue(token.isSingleChar(TokenTypes.MARKUP_TAG_DELIMITER, '>'));

	}


	@Test
	public void testHtml_processingInstructions() {

		String[] doctypes = {
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
			"<?xml version='1.0' encoding='UTF-8' ?>",
			"<?xml-stylesheet type=\"text/css\" href=\"style.css\"?>",
		};

		for (String code : doctypes) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);
			Assert.assertEquals(TokenTypes.MARKUP_PROCESSING_INSTRUCTION, token.getType());
		}

	}


	@Test
	public void testHtml_validHtml5TagNames() {

		String[] tagNames = {
			"a", "abbr", "acronym", "address", "applet", "area", "article",
			"aside", "audio", "b", "base", "basefont", "bdo", "bgsound", "big",
			"blink", "blockquote", "body", "br", "button", "canvas", "caption",
			"center", "cite", "code", "col", "colgroup", "command", "comment",
			"dd", "datagrid", "datalist", "datatemplate", "del", "details",
			"dfn", "dialog", "dir", "div", "dl", "dt", "em", "embed",
			"eventsource", "fieldset", "figure", "font", "footer", "form",
			"frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6",
			"head", "header", "hr", "html", "i", "iframe", "ilayer", "img",
			"input", "ins", "isindex", "kbd", "keygen", "label", "layer",
			"legend", "li", "link", "map", "mark", "marquee", "menu", "meta",
			"meter", "multicol", "nav", "nest", "nobr", "noembed", "noframes",
			"nolayer", "noscript", "object", "ol", "optgroup", "option",
			"output", "p", "param", "plaintext", "pre", "progress", "q", "rule",
			"s", "samp", "script", "section", "select", "server", "small",
			"source", "spacer", "span", "strike", "strong", "style",
			"sub", "sup", "table", "tbody", "td", "textarea", "tfoot", "th",
			"thead", "time", "title", "tr", "tt", "u", "ul", "var", "video"
		};

		TokenMaker tm = createTokenMaker();
		for (String tagName : tagNames) {

			for (int i = 0; i < tagName.length(); i++) {

				if (i > 0) {
					tagName = tagName.substring(0, i).toUpperCase() +
							tagName.substring(i);
				}

				String code = "<" + tagName;
				Segment segment = createSegment(code);
				Token token = tm.getTokenList(segment, TokenTypes.NULL, 0);
				Assert.assertTrue(token.isSingleChar(TokenTypes.MARKUP_TAG_DELIMITER, '<'));
				token = token.getNextToken();
				Assert.assertEquals("Not a valid HTML5 tag name token: " + token,
					token.getType(), TokenTypes.MARKUP_TAG_NAME);

			}

		}

	}


	@Test
	public void testHtml_loneIdentifier() {
		assertAllTokensOfType(TokenTypes.IDENTIFIER,
			"foo",
			"123"
		);
	}


	@Test
	public void testJS_BooleanLiterals() {

		String code = "true false";

		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);

		String[] keywords = code.split(" +");
		for (int i = 0; i < keywords.length; i++) {
			Assert.assertEquals(keywords[i], token.getLexeme());
			Assert.assertEquals(TokenTypes.LITERAL_BOOLEAN, token.getType());
			if (i < keywords.length - 1) {
				token = token.getNextToken();
				Assert.assertTrue("Not a whitespace token: " + token, token.isWhitespace());
				Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
			}
			token = token.getNextToken();
		}

	}


	@Test
	public void testJS_CharLiterals_invalid() {

		String[] charLiterals = {
			"'\\xG7'", // Invalid hex/octal escape
			"'foo\\ubar'", "'\\u00fg'", // Invalid Unicode escape
			"'My name is \\ubar and I \\", // Continued onto another line
			"'This is unterminated and ", // Unterminated string
		};

		for (String code : charLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.ERROR_CHAR, token.getType());
		}

	}


	@Test
	public void testJS_CharLiterals_valid() {

		String[] charLiterals = {
			"'a'", "'\\b'", "'\\t'", "'\\r'", "'\\f'", "'\\n'", "'\\u00fe'",
			"'\\u00FE'", "'\\111'", "'\\222'", "'\\333'",
			"'\\x77'",
			"'\\11'", "'\\22'", "'\\33'",
			"'\\1'",
			"'My name is Robert and I \\", // Continued onto another line
		};

		for (String code : charLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.LITERAL_CHAR, token.getType());
		}

	}


	@Test
	public void testJS_DataTypes() {

		String code = "boolean byte char double float int long short";

		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);

		String[] keywords = code.split(" +");
		for (int i = 0; i < keywords.length; i++) {
			Assert.assertEquals(keywords[i], token.getLexeme());
			Assert.assertEquals(TokenTypes.DATA_TYPE, token.getType());
			if (i < keywords.length - 1) {
				token = token.getNextToken();
				Assert.assertTrue("Not a whitespace token: " + token, token.isWhitespace());
				Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
			}
			token = token.getNextToken();
		}

	}


	@Test
	public void testJS_EolComments() {

		String[] eolCommentLiterals = {
			"// Hello world",
		};

		for (String code : eolCommentLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.COMMENT_EOL, token.getType());
		}

	}


	@Test
	public void testJS_EolComments_URL() {

		String[] eolCommentLiterals = {
			// Note: The 0-length token at the end of the first example is a
			// minor bug/performance thing
			"// Hello world http://www.sas.com",
			"// Hello world http://www.sas.com extra",
		};

		for (String code : eolCommentLiterals) {

			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();

			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals("nope - " + token, TokenTypes.COMMENT_EOL, token.getType());

			token = token.getNextToken();
			Assert.assertTrue(token.isHyperlink());
			Assert.assertEquals(TokenTypes.COMMENT_EOL, token.getType());
			Assert.assertEquals("http://www.sas.com", token.getLexeme());

			token = token.getNextToken();
			// Note: The 0-length token at the end of the first example is a
			// minor bug/performance thing
			if (token != null && token.isPaintable() && token.length() > 0) {
				Assert.assertFalse(token.isHyperlink());
				Assert.assertTrue(token.is(TokenTypes.COMMENT_EOL, " extra"));
			}

		}

	}


	@Test
	public void testJS_FloatingPointLiterals() {

		String code =
			// Basic doubles
			"3.0 4.2 3.0 4.2 .111 " +
			// Basic floats ending in f, F, d, or D
			"3f 3F 3d 3D 3.f 3.F 3.d 3.D 3.0f 3.0F 3.0d 3.0D .111f .111F .111d .111D " +
			// lower-case exponent, no sign
			"3e7f 3e7F 3e7d 3e7D 3.e7f 3.e7F 3.e7d 3.e7D 3.0e7f 3.0e7F 3.0e7d 3.0e7D .111e7f .111e7F .111e7d .111e7D " +
			// Upper-case exponent, no sign
			"3E7f 3E7F 3E7d 3E7D 3.E7f 3.E7F 3.E7d 3.E7D 3.0E7f 3.0E7F 3.0E7d 3.0E7D .111E7f .111E7F .111E7d .111E7D " +
			// Lower-case exponent, positive
			"3e+7f 3e+7F 3e+7d 3e+7D 3.e+7f 3.e+7F 3.e+7d 3.e+7D 3.0e+7f 3.0e+7F 3.0e+7d 3.0e+7D .111e+7f .111e+7F .111e+7d .111e+7D " +
			// Upper-case exponent, positive
			"3E+7f 3E+7F 3E+7d 3E+7D 3.E+7f 3.E+7F 3.E+7d 3.E+7D 3.0E+7f 3.0E+7F 3.0E+7d 3.0E+7D .111E+7f .111E+7F .111E+7d .111E+7D " +
			// Lower-case exponent, negative
			"3e-7f 3e-7F 3e-7d 3e-7D 3.e-7f 3.e-7F 3.e-7d 3.e-7D 3.0e-7f 3.0e-7F 3.0e-7d 3.0e-7D .111e-7f .111e-7F .111e-7d .111e-7D " +
			// Upper-case exponent, negative
			"3E-7f 3E-7F 3E-7d 3E-7D 3.E-7f 3.E-7F 3.E-7d 3.E-7D 3.0E-7f 3.0E-7F 3.0E-7d 3.0E-7D .111E-7f .111E-7F .111E-7d .111E-7D";

		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);

		String[] keywords = code.split(" +");
		for (int i = 0; i < keywords.length; i++) {
			Assert.assertEquals(keywords[i], token.getLexeme());
			Assert.assertEquals(TokenTypes.LITERAL_NUMBER_FLOAT, token.getType());
			if (i < keywords.length - 1) {
				token = token.getNextToken();
				Assert.assertTrue("Not a whitespace token: " + token, token.isWhitespace());
				Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
			}
			token = token.getNextToken();
		}

	}


	@Test
	public void testJS_Functions() {

		String code = "eval parseInt parseFloat escape unescape isNaN isFinite";

		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);

		String[] functions = code.split(" +");
		for (int i = 0; i < functions.length; i++) {
			Assert.assertEquals(functions[i], token.getLexeme());
			Assert.assertEquals("Not a function token: " + token, TokenTypes.FUNCTION, token.getType());
			if (i < functions.length - 1) {
				token = token.getNextToken();
				Assert.assertTrue("Not a whitespace token: " + token, token.isWhitespace());
				Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
			}
			token = token.getNextToken();
		}

	}


	@Test
	public void testJS_HexLiterals() {

		String code = "0x1 0xfe 0x333333333333 0X1 0Xfe 0X33333333333 0xFE 0XFE " +
				"0x1l 0xfel 0x333333333333l 0X1l 0Xfel 0X33333333333l 0xFEl 0XFEl " +
				"0x1L 0xfeL 0x333333333333L 0X1L 0XfeL 0X33333333333L 0xFEL 0XFEL ";

		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);

		String[] literals = code.split(" +");
		for (int i = 0; i < literals.length; i++) {
			Assert.assertEquals(literals[i], token.getLexeme());
			Assert.assertEquals("Not a hex number: " + token, TokenTypes.LITERAL_NUMBER_HEXADECIMAL, token.getType());
			if (i < literals.length - 1) {
				token = token.getNextToken();
				Assert.assertTrue("Not a whitespace token: " + token, token.isWhitespace());
				Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
			}
			token = token.getNextToken();
		}

	}


	@Test
	public void testJS_Keywords() {

		String code = "break case catch class const continue " +
				"debugger default delete do else export extends finally for function if " +
				"import in instanceof let new super switch " +
				"this throw try typeof void while with " +
				"NaN Infinity " +
				"let"; // As of 1.7, which is our default version

		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);

		String[] keywords = code.split(" +");
		for (int i = 0; i < keywords.length; i++) {
			Assert.assertEquals(keywords[i], token.getLexeme());
			Assert.assertEquals("Not a keyword token: " + token, TokenTypes.RESERVED_WORD, token.getType());
			if (i < keywords.length - 1) {
				token = token.getNextToken();
				Assert.assertTrue("Not a whitespace token: " + token, token.isWhitespace());
				Assert.assertTrue(token.is(TokenTypes.WHITESPACE, " "));
			}
			token = token.getNextToken();
		}

		segment = createSegment("return");
		token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
		Assert.assertEquals("return", token.getLexeme());
		Assert.assertEquals(TokenTypes.RESERVED_WORD_2, token.getType());

	}


	@Test
	public void testJS_MultiLineComments() {

		String[] mlcLiterals = {
			"/* Hello world */",
		};

		for (String code : mlcLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.COMMENT_MULTILINE, token.getType());
		}

	}


	@Test
	public void testJS_MultiLineComment_fromPreviousLine() {

		String[] mlcLiterals = {
			" this is continued from a prior line */",
		};

		for (String code : mlcLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS_MLC,
				0);
			Assert.assertEquals(TokenTypes.COMMENT_MULTILINE, token.getType());
		}

	}


	@Test
	public void testJS_MultiLineComments_URL() {

		String[] mlcLiterals = {
			"/* Hello world http://www.sas.com */",
		};

		for (String code : mlcLiterals) {

			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();

			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals("nope - " + token, TokenTypes.COMMENT_MULTILINE, token.getType());

			token = token.getNextToken();
			Assert.assertTrue(token.isHyperlink());
			Assert.assertEquals(TokenTypes.COMMENT_MULTILINE, token.getType());
			Assert.assertEquals("http://www.sas.com", token.getLexeme());

			token = token.getNextToken();
			Assert.assertEquals(TokenTypes.COMMENT_MULTILINE, token.getType());
			Assert.assertEquals(" */", token.getLexeme());

		}

	}


	@Test
	public void testJS_Numbers() {

		String[] ints = {
			"0", "42", /*"-7",*/
			"0l", "42l",
			"0L", "42L",
		};

		for (String code : ints) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.LITERAL_NUMBER_DECIMAL_INT, token.getType());
		}

		String[] floats = {
			"1e17", "3.14159", "5.7e-8", "2f", "2d",
		};

		for (String code : floats) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.LITERAL_NUMBER_FLOAT, token.getType());
		}

		String[] hex = {
			"0x1f", "0X1f", "0x1F", "0X1F",
		};

		for (String code : hex) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.LITERAL_NUMBER_HEXADECIMAL, token.getType());
		}

		String[] errors = {
			"42foo", "1e17foo", "0x1ffoo",
		};

		for (String code : errors) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.ERROR_NUMBER_FORMAT, token.getType());
		}

	}


	@Test
	public void testJS_Operators() {

		String assignmentOperators = "+ - <= ^ ++ < * >= % -- > / != ? >> ! & == : >> ~ && >>>";
		String nonAssignmentOperators = "= -= *= /= |= &= ^= += %= <<= >>= >>>=";
		String code = assignmentOperators + " " + nonAssignmentOperators;

		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);

		String[] keywords = code.split(" +");
		for (int i = 0; i < keywords.length; i++) {
			Assert.assertEquals(keywords[i], token.getLexeme());
			Assert.assertEquals("Not an operator: " + token, TokenTypes.OPERATOR, token.getType());
			if (i < keywords.length - 1) {
				token = token.getNextToken();
				Assert.assertTrue("Not a whitespace token: " + token, token.isWhitespace());
				Assert.assertTrue("Not a single space: " + token, token.is(TokenTypes.WHITESPACE, " "));
			}
			token = token.getNextToken();
		}

	}


	@Test
	public void testJS_Regexes() {

		String[] regexes = {
			"/foobar/", "/foobar/gim", "/foo\\/bar\\/bas/g",
		};

		for (String code : regexes) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.REGEX, token.getType());
		}

	}


	@Test
	public void testJS_Separators() {

		String code = "( ) [ ] { }";

		Segment segment = createSegment(code);
		TokenMaker tm = createTokenMaker();
		Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);

		String[] separators = code.split(" +");
		for (int i = 0; i < separators.length; i++) {
			Assert.assertEquals(separators[i], token.getLexeme());
			Assert.assertEquals(TokenTypes.SEPARATOR, token.getType());
			// Just one extra test here
			Assert.assertTrue(token.isSingleChar(TokenTypes.SEPARATOR, separators[i].charAt(0)));
			if (i < separators.length - 1) {
				token = token.getNextToken();
				Assert.assertTrue("Not a whitespace token: " + token, token.isWhitespace());
				Assert.assertTrue("Not a single space: " + token, token.is(TokenTypes.WHITESPACE, " "));
			}
			token = token.getNextToken();
		}

	}


	@Test
	public void testJS_Separators_renderedAsIdentifiers() {

		String[] separators2 = { ";", ",", "." };

		for (String code : separators2) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.IDENTIFIER, token.getType());
		}

	}


	@Test
	public void testJS_StringLiterals_invalid() {

		String[] stringLiterals = {
			"\"\\xG7\"", // Invalid hex/octal escape
			"\"foo\\ubar\"", "\"\\u00fg\"", // Invalid Unicode escape
			"\"My name is \\ubar and I \\", // Continued onto another line
			"\"This is unterminated and ", // Unterminated string
		};

		for (String code : stringLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals("Not an ERROR_STRING_DOUBLE: " + token,
					TokenTypes.ERROR_STRING_DOUBLE, token.getType());
		}

	}


	@Test
	public void testJS_StringLiterals_valid() {

		String[] stringLiterals = {
			"\"\"", "\"hi\"", "\"\\x77\"", "\"\\u00fe\"", "\"\\\"\"",
			"\"My name is Robert and I \\", // String continued on another line
		};

		for (String code : stringLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, token.getType());
		}

	}


	@Test
	public void testJS_TemplateLiterals_invalid() {

		String[] templateLiterals = {
			"`\\xG7`", // Invalid hex/octal escape
			"`foo\\ubar`", "`\\u00fg`", // Invalid Unicode escape
			"`My name is \\ubar and I ", // Continued onto another line
		};

		for (String code : templateLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, JS_PREV_TOKEN_TYPE, 0);
			Assert.assertEquals("Not an ERROR_STRING_DOUBLE: " + token,
					TokenTypes.ERROR_STRING_DOUBLE, token.getType());
		}

	}


	@Test
	public void testJS_TemplateLiterals_valid_noInterpolatedExpression() {

		String[] templateLiterals = {
			"``", "`hi`", "`\\x77`", "`\\u00fe`", "`\\\"`",
			"`My name is Robert and I", // String continued on another line
			"`My name is Robert and I \\", // String continued on another line
		};

		for (String code : templateLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, JS_PREV_TOKEN_TYPE, 0);
			Assert.assertEquals(TokenTypes.LITERAL_BACKQUOTE, token.getType());
		}

	}


	@Test
	public void testJS_TemplateLiterals_valid_withInterpolatedExpression() {

		// Strings with tokens:  template, interpolated expression, template
		String[] templateLiterals = {
			"`My name is ${name}`",
			"`My name is ${'\"' + name + '\"'}`",
			"`Embedded example: ${2 + ${!!func()}}, wow",
		};

		for (String code : templateLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, JS_PREV_TOKEN_TYPE, 0);
			Assert.assertEquals(TokenTypes.LITERAL_BACKQUOTE, token.getType());
			token = token.getNextToken();
			Assert.assertEquals(TokenTypes.VARIABLE, token.getType());
			token = token.getNextToken();
			Assert.assertEquals(TokenTypes.LITERAL_BACKQUOTE, token.getType());
		}

	}


	@Test
	public void testJS_TemplateLiterals_valid_continuedFromPriorLine() {

		String[] templateLiterals = {
			"and my name is ${name}`"
		};

		for (String code : templateLiterals) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS_TEMPLATE_LITERAL_VALID,
				0);
			Assert.assertEquals(TokenTypes.LITERAL_BACKQUOTE, token.getType());
			token = token.getNextToken();
			Assert.assertEquals(TokenTypes.VARIABLE, token.getType());
			token = token.getNextToken();
			Assert.assertEquals(TokenTypes.LITERAL_BACKQUOTE, token.getType());
		}

	}


	@Test
	public void testJS_Whitespace() {

		String[] whitespace = {
			" ", "\t", "\f", "   \t   ",
		};

		for (String code : whitespace) {
			Segment segment = createSegment(code);
			TokenMaker tm = createTokenMaker();
			Token token = tm.getTokenList(segment, HTMLTokenMaker.INTERNAL_IN_JS, 0);
			Assert.assertEquals(TokenTypes.WHITESPACE, token.getType());
		}

	}


}