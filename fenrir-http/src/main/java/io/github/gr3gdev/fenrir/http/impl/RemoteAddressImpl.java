package io.github.gr3gdev.fenrir.http.impl;

import io.github.gr3gdev.fenrir.http.RemoteAddress;

/**
 * Implementation of {@link RemoteAddress}.
 */
public class RemoteAddressImpl implements RemoteAddress {

    private final String ip;
    private final String port;

    RemoteAddressImpl(String remoteAddress) {
        final String[] data = remoteAddress.substring(1).split(":");
        ip = data[0];
        port = data[1];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String ip() {
        return ip;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String port() {
        return port;
    }
}
