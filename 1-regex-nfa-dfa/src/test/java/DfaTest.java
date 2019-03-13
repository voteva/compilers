import creator.DfaCreator;
import creator.MinDfaCreator;
import creator.NfaCreator;
import fa.Automata;
import fa.state.State;
import org.junit.Test;
import validator.DfaSequenceValidator;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DfaTest {

    @Test
    public void testUnion() {
        Automata<State> nfa = new NfaCreator().create("a|b");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "a"));
            assertTrue(DfaSequenceValidator.validate(fa, "b"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "ab"));
            assertFalse(DfaSequenceValidator.validate(fa, "aa"));
            assertFalse(DfaSequenceValidator.validate(fa, "bb"));
            assertFalse(DfaSequenceValidator.validate(fa, "aba"));
        }
    }

    @Test
    public void testConcatenation() {
        Automata<State> nfa = new NfaCreator().create("ab");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "ab"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "a"));
            assertFalse(DfaSequenceValidator.validate(fa, "b"));
            assertFalse(DfaSequenceValidator.validate(fa, "aba"));
            assertFalse(DfaSequenceValidator.validate(fa, "abb"));
            assertFalse(DfaSequenceValidator.validate(fa, "ba"));
        }
    }

    @Test
    public void testStar() {
        Automata<State> nfa = new NfaCreator().create("a*");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, ""));
            assertTrue(DfaSequenceValidator.validate(fa, "a"));
            assertTrue(DfaSequenceValidator.validate(fa, "aa"));
            assertTrue(DfaSequenceValidator.validate(fa, "aaa"));
            assertTrue(DfaSequenceValidator.validate(fa, "aaaaaaaaaaaaaaaaaaa"));

            assertFalse(DfaSequenceValidator.validate(fa, "ab"));
            assertFalse(DfaSequenceValidator.validate(fa, "aab"));
        }
    }

    @Test
    public void testPlus() {
        Automata<State> nfa = new NfaCreator().create("a+");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "a"));
            assertTrue(DfaSequenceValidator.validate(fa, "aa"));
            assertTrue(DfaSequenceValidator.validate(fa, "aaa"));
            assertTrue(DfaSequenceValidator.validate(fa, "aaaaaaaaaaaaaaaaaaa"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "ab"));
            assertFalse(DfaSequenceValidator.validate(fa, "aab"));
        }
    }

    @Test
    public void testBrackets() {
        Automata<State> nfa = new NfaCreator().create("()");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, ""));

            assertFalse(DfaSequenceValidator.validate(fa, "ab"));
            assertFalse(DfaSequenceValidator.validate(fa, "aab"));
        }
    }

    @Test
    public void testBrackets1() {
        Automata<State> nfa = new NfaCreator().create("(()())");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, ""));

            assertFalse(DfaSequenceValidator.validate(fa, "ab"));
            assertFalse(DfaSequenceValidator.validate(fa, "aab"));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectBrackets() {
        new NfaCreator().create("(()()))");
    }

    @Test
    public void testSequence0() {
        Automata<State> nfa = new NfaCreator().create("ab*");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "a"));
            assertTrue(DfaSequenceValidator.validate(fa, "ab"));
            assertTrue(DfaSequenceValidator.validate(fa, "abb"));
            assertTrue(DfaSequenceValidator.validate(fa, "abbbbbbbb"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "b"));
            assertFalse(DfaSequenceValidator.validate(fa, "aab"));
            assertFalse(DfaSequenceValidator.validate(fa, "bb"));
            assertFalse(DfaSequenceValidator.validate(fa, "aa"));
        }
    }

    @Test
    public void testSequence1() {
        Automata<State> nfa = new NfaCreator().create("a+b");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "ab"));
            assertTrue(DfaSequenceValidator.validate(fa, "aab"));
            assertTrue(DfaSequenceValidator.validate(fa, "aaaaaaaab"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "b"));
            assertFalse(DfaSequenceValidator.validate(fa, "a"));
            assertFalse(DfaSequenceValidator.validate(fa, "aabb"));
            assertFalse(DfaSequenceValidator.validate(fa, "bb"));
            assertFalse(DfaSequenceValidator.validate(fa, "aaaaaaaa"));
        }
    }

    @Test
    public void testSequence2() {
        Automata<State> nfa = new NfaCreator().create("(a|b)a");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "aa"));
            assertTrue(DfaSequenceValidator.validate(fa, "ba"));

            assertFalse(DfaSequenceValidator.validate(fa, "ab"));
            assertFalse(DfaSequenceValidator.validate(fa, "bb"));
            assertFalse(DfaSequenceValidator.validate(fa, "a"));
            assertFalse(DfaSequenceValidator.validate(fa, "b"));
            assertFalse(DfaSequenceValidator.validate(fa, "aaa"));
        }
    }

    @Test
    public void testSequence3() {
        Automata<State> nfa = new NfaCreator().create("(a|b)(a|b)a");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "aaa"));
            assertTrue(DfaSequenceValidator.validate(fa, "aba"));
            assertTrue(DfaSequenceValidator.validate(fa, "baa"));
            assertTrue(DfaSequenceValidator.validate(fa, "bba"));

            assertFalse(DfaSequenceValidator.validate(fa, "aab"));
            assertFalse(DfaSequenceValidator.validate(fa, "bbb"));
            assertFalse(DfaSequenceValidator.validate(fa, "aa"));
            assertFalse(DfaSequenceValidator.validate(fa, "a"));
        }
    }

    @Test
    public void testSequence4() {
        Automata<State> nfa = new NfaCreator().create("a(a|b)b(a|b)ab");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "aabaab"));
            assertTrue(DfaSequenceValidator.validate(fa, "abbaab"));
            assertTrue(DfaSequenceValidator.validate(fa, "abbaab"));
            assertTrue(DfaSequenceValidator.validate(fa, "abbbab"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "ab"));
            assertFalse(DfaSequenceValidator.validate(fa, "aba"));
            assertFalse(DfaSequenceValidator.validate(fa, "aaaaaa"));
            assertFalse(DfaSequenceValidator.validate(fa, "bbbbbb"));
            assertFalse(DfaSequenceValidator.validate(fa, "babaab"));
            assertFalse(DfaSequenceValidator.validate(fa, "aaaaab"));
            assertFalse(DfaSequenceValidator.validate(fa, "abbbbb"));
        }
    }

    @Test
    public void testSequence5() {
        Automata<State> nfa = new NfaCreator().create("a*(a|b)b*");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "a"));
            assertTrue(DfaSequenceValidator.validate(fa, "b"));
            assertTrue(DfaSequenceValidator.validate(fa, "aa"));
            assertTrue(DfaSequenceValidator.validate(fa, "bb"));
            assertTrue(DfaSequenceValidator.validate(fa, "ab"));
            assertTrue(DfaSequenceValidator.validate(fa, "aab"));
            assertTrue(DfaSequenceValidator.validate(fa, "aaaaaa"));
            assertTrue(DfaSequenceValidator.validate(fa, "bbbbbbbbb"));
            assertTrue(DfaSequenceValidator.validate(fa, "aabbbbbb"));
            assertTrue(DfaSequenceValidator.validate(fa, "aaaaaabbbbbb"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "ba"));
            assertFalse(DfaSequenceValidator.validate(fa, "baaaa"));
            assertFalse(DfaSequenceValidator.validate(fa, "aaaaaba"));
        }
    }

    @Test
    public void testSequence6() {
        Automata<State> nfa = new NfaCreator().create("aba*(a|b)b*(a|b)(a|b)bbb");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "abbbabbb"));
            assertTrue(DfaSequenceValidator.validate(fa, "abaaaabaabbb"));
            assertTrue(DfaSequenceValidator.validate(fa, "abaaaababbbb"));
            assertTrue(DfaSequenceValidator.validate(fa, "abaaaababbbb"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "aaaabbbbbb"));
            assertFalse(DfaSequenceValidator.validate(fa, "abbaaaabbb"));
        }
    }

    @Test
    public void testSequence7() {
        Automata<State> nfa = new NfaCreator().create("a+ba*(a|b)b*(a|b)(a|b)bbb");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "aaabaaaabaabbb"));
            assertTrue(DfaSequenceValidator.validate(fa, "abaaaababbbb"));
            assertTrue(DfaSequenceValidator.validate(fa, "aaaabaaaababbbb"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "bbbabbb"));
            assertFalse(DfaSequenceValidator.validate(fa, "aaaabbbbbb"));
            assertFalse(DfaSequenceValidator.validate(fa, "abbaaaabbb"));
        }
    }

    @Test
    public void testSequence8() {
        Automata<State> nfa = new NfaCreator().create("(ab)+");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "ab"));
            assertTrue(DfaSequenceValidator.validate(fa, "abab"));
            assertTrue(DfaSequenceValidator.validate(fa, "ababab"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "a"));
            assertFalse(DfaSequenceValidator.validate(fa, "b"));
            assertFalse(DfaSequenceValidator.validate(fa, "aba"));
            assertFalse(DfaSequenceValidator.validate(fa, "ba"));
        }
    }

    @Test
    public void testSequence9() {
        Automata<State> nfa = new NfaCreator().create("(abc)+(a|b|c)");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "abca"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcb"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcc"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcabca"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcabcabcb"));

            assertFalse(DfaSequenceValidator.validate(fa, "a"));
            assertFalse(DfaSequenceValidator.validate(fa, "b"));
            assertFalse(DfaSequenceValidator.validate(fa, "c"));
            assertFalse(DfaSequenceValidator.validate(fa, "abc"));
            assertFalse(DfaSequenceValidator.validate(fa, "aaca"));
        }
    }

    @Test
    public void testSequence10() {
        Automata<State> nfa = new NfaCreator().create("(abc)+(d|e)*fg");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "abcdefg"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcfg"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcdfg"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcefg"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcabcfg"));
            assertTrue(DfaSequenceValidator.validate(fa, "abcabcabcdedeeddeeedddfg"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
            assertFalse(DfaSequenceValidator.validate(fa, "fg"));
            assertFalse(DfaSequenceValidator.validate(fa, "defg"));
            assertFalse(DfaSequenceValidator.validate(fa, "abdefg"));
            assertFalse(DfaSequenceValidator.validate(fa, "abcdef"));
        }
    }

    @Test
    public void testSequence11() {
        Automata<State> nfa = new NfaCreator().create("(a|b)*abb");
        Automata<State> dfa = DfaCreator.create(nfa);
        Automata<State> minDfa = MinDfaCreator.create(dfa);

        List<Automata<State>> dfas = Arrays.asList(dfa, minDfa);

        for (Automata<State> fa : dfas) {
            assertTrue(DfaSequenceValidator.validate(fa, "aabb"));
            assertTrue(DfaSequenceValidator.validate(fa, "abb"));
            assertTrue(DfaSequenceValidator.validate(fa, "babb"));
            assertTrue(DfaSequenceValidator.validate(fa, "abbabb"));

            assertFalse(DfaSequenceValidator.validate(fa, ""));
        }
    }
}
