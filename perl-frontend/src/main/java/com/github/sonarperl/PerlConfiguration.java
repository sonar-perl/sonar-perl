package com.github.sonarperl;

import java.nio.charset.Charset;

public class PerlConfiguration {

    private final Charset charset;

    public PerlConfiguration(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }
}
