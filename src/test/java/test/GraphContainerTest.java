package test;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.pathfinder.GraphContainer;
import software.ulpgc.pathfinder.GraphLoader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphContainerTest {
    private GraphContainer graphContainer;

    @BeforeEach
    void setUp() throws IOException {
        graphContainer = mockGraphLoader().load();
    }

    @Test
    void testShortestPathExists() {
        var path = graphContainer.shortestPathBetween("A", "C");
        assertNotNull(path);
        assertEquals(3, path.size());
        assertEquals("A", path.get(0));
        assertEquals("B", path.get(1));
        assertEquals("C", path.get(2));
    }

    @Test
    void testShortestPathWeight() {
        var weight = graphContainer.pathWeightBetween("A", "C");
        assertEquals(3.0, weight, 1e-9);
    }

    @Test
    void testVertexDoesNotExistThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                graphContainer.shortestPathBetween("A", "Z")
        );
    }

    @Test
    void testNoPathBetweenExistingVerticesThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                graphContainer.shortestPathBetween("A", "E")
        );
    }

    @Test
    void testNoPathWeightBetweenExistingVerticesThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                graphContainer.pathWeightBetween("A", "E")
        );
    }

    private GraphLoader mockGraphLoader() throws IOException {
        GraphLoader graphLoader = mock(GraphLoader.class);
        when(graphLoader.load()).thenReturn(new GraphContainer(mockGraph()));
        return graphLoader;
    }

    private Graph<String, DefaultEdge> mockGraph() {
        Graph<String, DefaultEdge> graph = new SimpleWeightedGraph<>(DefaultEdge.class);

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E"); // existe pero desconectado para forzar "no path"

        graph.addEdge("A", "B");
        graph.addEdge("A", "D");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");

        graph.setEdgeWeight("A", "B", 1.0);
        graph.setEdgeWeight("B", "C", 2.0);
        graph.setEdgeWeight("A", "D", 4.0);
        graph.setEdgeWeight("C", "D", 1.0);

        return graph;
    }
}
