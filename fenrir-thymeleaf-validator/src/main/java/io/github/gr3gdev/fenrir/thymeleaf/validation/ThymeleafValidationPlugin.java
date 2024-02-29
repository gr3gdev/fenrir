package io.github.gr3gdev.fenrir.thymeleaf.validation;

import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.fenrir.thymeleaf.validation.el.FenrirExpressionFactory;
import org.thymeleaf.TemplateEngine;

import java.util.Properties;

/**
 * Implementation of {@link HttpSocketPlugin}, execute {@link TemplateEngine} for thymeleaf rendering and validate parameters with jakarta-validation.
 */
public class ThymeleafValidationPlugin extends ThymeleafPlugin {
    @Override
    public void init(Class<?> mainClass, Properties fenrirProperties) {
        super.init(mainClass, fenrirProperties);
        System.setProperty("jakarta.el.ExpressionFactory", FenrirExpressionFactory.class.getCanonicalName());
        this.dialect.addValidation("fields", new ThymeleafValidation());
    }
}
