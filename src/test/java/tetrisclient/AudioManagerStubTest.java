package tetrisclient;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AudioManagerStubTest {

    static class AudioManagerStub extends AudioManager {
        static boolean played = false;

        public static void play(String s) {
            played = true;
        }
    }

    @Test
    void testPlaySoundStub() {
        assertDoesNotThrow(() -> AudioManagerStub.play("test.mp3"));
        assert(AudioManagerStub.played);
    }
}
