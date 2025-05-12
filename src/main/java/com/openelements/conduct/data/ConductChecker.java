package com.openelements.conduct.data;

import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface ConductChecker {

    @NonNull
    CheckResult check(@NonNull Message message);
}
