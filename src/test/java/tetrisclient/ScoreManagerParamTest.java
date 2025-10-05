package tetrisclient;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreManagerParamTest {

    @ParameterizedTest(name = "{index} => name={0}, score={1}")
    @CsvSource({
            "Alice, 100",
            "Bob, 200",
            "Charlie, 150",
            "David, 50",
            "Eve, 300"
    })
    void testAddScoreParameterized(String name, int score) {
        ScoreManager.clear();  // start fresh each time
        ScoreManager.add(name, score);

        assertEquals(1, ScoreManager.getScores().size());
        assertEquals(name, ScoreManager.getScores().get(0).name());
        assertEquals(score, ScoreManager.getScores().get(0).score());
    }
}
