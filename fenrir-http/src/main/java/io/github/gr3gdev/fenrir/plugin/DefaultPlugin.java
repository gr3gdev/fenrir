package io.github.gr3gdev.fenrir.plugin;

/**
 * Default {@link HttpSocketPlugin}, the method return is write in the response.
 */
public class DefaultPlugin extends HttpSocketPlugin<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String toString(String methodReturn) {
        return methodReturn;
    }
}
