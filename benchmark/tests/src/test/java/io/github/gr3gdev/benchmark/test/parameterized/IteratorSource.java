package io.github.gr3gdev.benchmark.test.parameterized;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(IteratorSource.IteratorArgumentsProvider.class)
public @interface IteratorSource {

    int value();

    class IteratorArgumentsProvider extends AnnotationBasedArgumentsProvider<IteratorSource> {

        @Override
        protected Stream<? extends Arguments> provideArguments(ExtensionContext context, IteratorSource annotation) {
            return IntStream.range(0, annotation.value())
                    .mapToObj(Arguments::of);
        }
    }
}
