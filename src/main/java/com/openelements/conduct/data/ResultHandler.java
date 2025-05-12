package com.openelements.conduct.data;

import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface ResultHandler {

    void handle(@NonNull CheckResult result);

}
