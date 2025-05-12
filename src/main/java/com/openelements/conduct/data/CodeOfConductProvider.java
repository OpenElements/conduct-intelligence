package com.openelements.conduct.data;

import org.jspecify.annotations.NonNull;

public interface CodeOfConductProvider {

    boolean supports(@NonNull TextfileType type);

    @NonNull
    String getCodeOfConduct(@NonNull TextfileType type);
}
