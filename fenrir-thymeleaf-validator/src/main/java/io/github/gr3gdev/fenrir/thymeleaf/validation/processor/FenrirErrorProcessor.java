package io.github.gr3gdev.fenrir.thymeleaf.validation.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.LazyEscapingCharSequence;
import org.unbescape.html.HtmlEscape;
import org.unbescape.xml.XmlEscape;

/**
 * Processor for the attribute "errors".
 */
public class FenrirErrorProcessor extends AbstractStandardExpressionAttributeTagProcessor {
    private static final int PRECEDENCE = 1300;
    private static final String ATTR_NAME = "errors";

    /**
     * Constructor.
     *
     * @param templateMode  the template mode
     * @param dialectPrefix the dialect prefix
     */
    public FenrirErrorProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE, true, (templateMode == TemplateMode.TEXT));
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        final TemplateMode templateMode = getTemplateMode();
        final CharSequence text;
        final String input = (expressionResult == null ? "" : expressionResult.toString());
        if (templateMode == TemplateMode.RAW) {
            text = input;
        } else {
            if (input.length() > 100) {
                // Might be a large text -> Lazy escaping on the output Writer
                text = new LazyEscapingCharSequence(context.getConfiguration(), templateMode, input);
            } else {
                // Not large -> better use a bit more of memory, but be faster
                text = produceEscapedOutput(templateMode, input);
            }
        }
        // Report the result to the engine, whichever the type of process we have applied
        structureHandler.setBody(text, false);
    }

    private String produceEscapedOutput(final TemplateMode templateMode, final String input) {
        return switch (templateMode) {
            // fall-through
            case TEXT, HTML -> HtmlEscape.escapeHtml4Xml(input);
            case XML ->
                // Note we are outputting a body content here, so it is important that we use the version
                // of XML escaping meant for content, not attributes (slight differences)
                    XmlEscape.escapeXml10(input);
            default -> throw new TemplateProcessingException(
                    "Unrecognized template mode " + templateMode + ". Cannot produce escaped output for " +
                            "this template mode.");
        };
    }

}
