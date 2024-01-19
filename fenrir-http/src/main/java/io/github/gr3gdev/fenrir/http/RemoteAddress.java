package io.github.gr3gdev.fenrir.http;

/**
 * Interface for remote address.
 */
public interface RemoteAddress {

    /**
     * Remote socket address IP.
     *
     * @return String
     */
    String ip();

    /**
     * Remote socket address port.
     *
     * @return String
     */
    String port();

}
