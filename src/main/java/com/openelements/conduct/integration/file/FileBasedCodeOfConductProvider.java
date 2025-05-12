package com.openelements.conduct.integration.file;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.TextfileType;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public class FileBasedCodeOfConductProvider implements CodeOfConductProvider {

    private final static String PLAIN_TEXT_FILE = "CODE_OF_CONDUCT.txt";

    private final static String MD_TEXT_FILE = "CODE_OF_CONDUCT.MD";

    private final static String HTML_TEXT_FILE = "CODE_OF_CONDUCT.html";

    @Override
    public boolean supports(@NonNull TextfileType type) {
        return getResource(type).isPresent();
    }

    private Optional<URL> getResource(@NonNull TextfileType type) {
        Objects.requireNonNull(type, "type must not be null");
        if (type == TextfileType.PLAIN) {
            return Optional.ofNullable(FileBasedCodeOfConductProvider.class.getClassLoader().getResource(PLAIN_TEXT_FILE));
        }
        if (type == TextfileType.MARKDOWN) {
            return Optional.ofNullable(FileBasedCodeOfConductProvider.class.getClassLoader().getResource(MD_TEXT_FILE));
        }
        if (type == TextfileType.HTML) {
            return Optional.ofNullable(FileBasedCodeOfConductProvider.class.getClassLoader().getResource(HTML_TEXT_FILE));
        }
        return Optional.empty();
    }

    @Override
    public @NonNull String getCodeOfConduct(@NonNull TextfileType type) {
        final URL resource = getResource(type).orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + type));
        try(final InputStream inputStream = resource.openStream()) {
            return new String(inputStream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read code of conduct file: " + resource, e);
        }
    }
}
