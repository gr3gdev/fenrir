package io.github.gr3gdev.benchmark.test.parameterized;

import io.github.gr3gdev.bench.Iteration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(IteratorSource.IteratorArgumentsProvider.class)
public @interface IteratorSource {

    IterationConf[] value();

    @Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface IterationConf {
        int count();

        long memory();
    }

    class IteratorArgumentsProvider extends AnnotationBasedArgumentsProvider<IteratorSource> {

        @Override
        protected Stream<? extends Arguments> provideArguments(ExtensionContext context, IteratorSource annotation) {
            return Arrays.stream(annotation.value())
                    .map(it -> IntStream.range(0, it.count())
                            .mapToObj(i -> new Iteration(i, it.count(), it.memory()))
                            .toList())
                    .flatMap(Collection::stream)
                    .map(Arguments::of);
        }
    }
}
