package me.gritter.aoc;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public class Utils {

    private Utils() {
    }

    public static String readFile(String file) {
        try {
            return IOUtils.toString(requireNonNull(Utils.class.getResourceAsStream(file)), UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Stream<String> readLines(String file) {
        return Stream.of(readFile(file).split("\n"));
    }
}
