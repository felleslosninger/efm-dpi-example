package no.digdir.dpi.client.internal.domain;

import lombok.Value;

@Value
public class Billable<T> {

    T entity;
    long billableBytes;
}
