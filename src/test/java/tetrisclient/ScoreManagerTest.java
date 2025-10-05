package tetrisclient;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreManagerTest {

    @Test
    void testAddAndRetrieveScores() {
        ScoreManager.clear();
        ScoreManager.add("Alice", 100);
        ScoreManager.add("Bob", 200);

        List<ScoreManager.ScoreEntry> scores = ScoreManager.getScores();
        assertEquals(2, scores.size());
        assertEquals("Bob", scores.get(0).name());
        assertEquals(200, scores.get(0).score());
        assertEquals("Alice", scores.get(1).name());
    }

    @Test
    void testClearScores() {
        ScoreManager.add("Charlie", 50);
        ScoreManager.clear();
        assertEquals(0, ScoreManager.getScores().size());
    }
}
