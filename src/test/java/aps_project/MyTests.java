package aps_project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class MyTests {

    @Test
    public void sequencesOfNObjectsOfBlocksDomainShouldBeCorrect() {
        List<String> objects =
                Arrays.asList("a - BLOCK", "c - BLOCK", "d - BLOCK", "b - BLOCK");
        List<List<String>> arityOneSequences =
                Arrays.asList(Arrays.asList("a - BLOCK"), Arrays.asList("c - BLOCK"),
                        Arrays.asList("d - BLOCK"), Arrays.asList("b - BLOCK"));
        List<List<String>> arityTwoSequences =
                Arrays.asList(Arrays.asList("a - BLOCK", "a - BLOCK"), Arrays.asList("a - BLOCK", "c - BLOCK"),
                        Arrays.asList("a - BLOCK", "d - BLOCK"), Arrays.asList("a - BLOCK", "b - BLOCK"),
                        Arrays.asList("c - BLOCK", "a - BLOCK"), Arrays.asList("c - BLOCK", "c - BLOCK"),
                        Arrays.asList("c - BLOCK", "d - BLOCK"), Arrays.asList("c - BLOCK", "b - BLOCK"),
                        Arrays.asList("d - BLOCK", "a - BLOCK"), Arrays.asList("d - BLOCK", "c - BLOCK"),
                        Arrays.asList("d - BLOCK", "d - BLOCK"), Arrays.asList("d - BLOCK", "b - BLOCK"),
                        Arrays.asList("b - BLOCK", "a - BLOCK"), Arrays.asList("b - BLOCK", "c - BLOCK"),
                        Arrays.asList("b - BLOCK", "d - BLOCK"), Arrays.asList("b - BLOCK", "b - BLOCK"));

        assertEquals(arityOneSequences, Util.getSequencesOfN(objects, 1), "Sequences of 1 must be correct");
        assertEquals(arityTwoSequences, Util.getSequencesOfN(objects, 2), "Sequences of 2 must be correct");
        assertEquals(4, Util.getSequencesOfN(objects, 1).size(), "Sequences of 1 size must be 4");
        assertEquals(16, Util.getSequencesOfN(objects, 2).size(), "Sequences of 2 size must be 16");
    }

    @Test
    public void sequencesOfNObjectsOfGripperDomainShouldBeCorrect() {
        List<String> objects =
                Arrays.asList("rooma - ROOM", "roomb - ROOM", "ball6 - BALL", "ball5 - BALL",
                        "ball4 - BALL", "ball3 - BALL", "ball2 - BALL", "ball1 - BALL");

        assertEquals(8, Util.getSequencesOfN(objects, 1).size(), "Sequences of 1 size must be 8");
        assertEquals(64, Util.getSequencesOfN(objects, 2).size(), "Sequences of 2 size must be 64");
        assertEquals(512, Util.getSequencesOfN(objects, 3).size(), "Sequences of 3 size must be 512");
    }

    @Test
    public void permutationsOfNOfBlocksDomainShouldBeCorrect() {
        List<String> objects =
                Arrays.asList("a - BLOCK", "c - BLOCK", "d - BLOCK", "b - BLOCK");
        List<List<String>> arityOnePermutations =
                Arrays.asList(Arrays.asList("a - BLOCK"), Arrays.asList("c - BLOCK"),
                        Arrays.asList("d - BLOCK"), Arrays.asList("b - BLOCK"));
        List<List<String>> arityTwoPermutations =
                Arrays.asList(Arrays.asList("a - BLOCK", "c - BLOCK"), Arrays.asList("a - BLOCK", "d - BLOCK"),
                        Arrays.asList("a - BLOCK", "b - BLOCK"),
                        Arrays.asList("c - BLOCK", "a - BLOCK"),
                        Arrays.asList("c - BLOCK", "d - BLOCK"), Arrays.asList("c - BLOCK", "b - BLOCK"),
                        Arrays.asList("d - BLOCK", "a - BLOCK"), Arrays.asList("d - BLOCK", "c - BLOCK"),
                        Arrays.asList("d - BLOCK", "b - BLOCK"),
                        Arrays.asList("b - BLOCK", "a - BLOCK"), Arrays.asList("b - BLOCK", "c - BLOCK"),
                        Arrays.asList("b - BLOCK", "d - BLOCK"));

        assertEquals(arityOnePermutations, Util.getNPermutations(objects, 1), "1-permutations must be correct");
        assertEquals(arityTwoPermutations, Util.getNPermutations(objects, 2), "2-permutations must be correct");
        assertEquals(4, Util.getNPermutations(objects, 1).size(), "1-permutations size must be 4");
        assertEquals(12, Util.getNPermutations(objects, 2).size(), "2-permutations size must be 12");
    }

    @Test
    public void permutationsOfNOfGripperDomainShouldBeCorrect() {
        List<String> objects =
                Arrays.asList("rooma - ROOM", "roomb - ROOM", "ball6 - BALL", "ball5 - BALL",
                        "ball4 - BALL", "ball3 - BALL", "ball2 - BALL", "ball1 - BALL");

        assertEquals(8, Util.getNPermutations(objects, 1).size(), "1-permutations size must be 8");
        assertEquals(56, Util.getNPermutations(objects, 2).size(), "2-permutations size must be 56");
        assertEquals(336, Util.getNPermutations(objects, 3).size(), "3-permutations size must be 336");
    }
}