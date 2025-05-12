package com.openelements.conduct.data;

import java.net.URI;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record Message(@Nullable String title, @NonNull String message, @NonNull URI link) {

    public Message {
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(link, "link must not be null");
    }

    public Message(@NonNull String message, @NonNull URI link) {
        this(null, message, link);
    }
}
