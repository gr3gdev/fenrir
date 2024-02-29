package io.github.gr3gdev.fenrir.thymeleaf.context.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processor for the attribute "field".
 */
public class FenrirFieldProcessor extends AbstractStandardExpressionAttributeTagProcessor {
    private static final int PRECEDENCE = 1200;
    private static final String ATTR_NAME = "field";
    private static final Pattern PATTERN_VALUE = Pattern.compile(".\\{(.+)}");

    /**
     * Constructor.
     *
     * @param templateMode  the template mode
     * @param dialectPrefix the dialect prefix
     */
    public FenrirFieldProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE, true, (templateMode == TemplateMode.TEXT));
    }

    String findValue(Object expressionResult, String defaultValue) {
        return Optional.ofNullable(expressionResult).map(Object::toString).orElse(defaultValue);
    }

    String extractValueByRegex(String attributeValue) {
        final Matcher matcher = PATTERN_VALUE.matcher(attributeValue);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue,
                             Object expressionResult, IElementTagStructureHandler structureHandler) {
        structureHandler.setAttribute("name", extractValueByRegex(attributeValue));
        switch (tag.getElementCompleteName()) {
            case "input":
                structureHandler.setAttribute("value", findValue(expressionResult, ""));
                break;
            case "select":
            case "datalist":
                // TODO selected option
                break;
            case "checkbox":
                structureHandler.setAttribute("checked", findValue(expressionResult, "false"));
                break;
            case "radio":
                // TODO check the correct radio
                break;
            default:
                structureHandler.setBody(findValue(expressionResult, ""), false);
        }
    }
}
