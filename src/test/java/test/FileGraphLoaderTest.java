package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.pathfinder.FileGraphLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileGraphLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadValidFile_buildsGraphAndFindsPathAndWeight() throws IOException {
        Path f = tempDir.resolve("graph.csv");
        Files.write(f, List.of(
                "A,B,1.0",
                "B,C,2.0",
                "A,D,10.0"
        ));

        var container = new FileGraphLoader(f.toFile()).load();

        assertEquals(List.of("A", "B", "C"), container.shortestPathBetween("A", "C"));
        assertEquals(3.0, container.pathWeightBetween("A", "C"), 1e-9);
    }

    @Test
    void loadIgnoresInvalidLines_andStillLoadsValidOnes() throws IOException {
        Path f = tempDir.resolve("graph.csv");
        Files.write(f, List.of(
                "A,B,1.0",
                "THIS_IS_INVALID",
                "B,C,2.0",
                "A,C,not_a_number",
                "C,D,1.0"
        ));

        var container = new FileGraphLoader(f.toFile()).load();

        assertEquals(List.of("A", "B", "C"), container.shortestPathBetween("A", "C"));
        assertEquals(List.of("C", "D"), container.shortestPathBetween("C", "D"));
    }

    @Test
    void loadEmptyFile_thenQueryingPathThrowsBecauseVerticesDontExist() throws IOException {
        Path f = tempDir.resolve("empty.csv");
        Files.write(f, List.of());

        var container = new FileGraphLoader(f.toFile()).load();

        assertThrows(IllegalArgumentException.class, () ->
                container.shortestPathBetween("A", "B")
        );
    }
}
