import creator.DfaCreator;
import creator.NfaCreator;
import fa.Automata;
import fa.state.DfaState;
import fa.state.State;
import org.junit.Test;
import validator.DfaSequenceValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DfaTest {

    @Test
    public void testUnion() {
        Automata<State> nfa = new NfaCreator().create("a|b");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "a"));
        assertTrue(DfaSequenceValidator.validate(dfa, "b"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "ab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aa"));
        assertFalse(DfaSequenceValidator.validate(dfa, "bb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aba"));
    }

    @Test
    public void testConcatenation() {
        Automata<State> nfa = new NfaCreator().create("ab");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "ab"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "a"));
        assertFalse(DfaSequenceValidator.validate(dfa, "b"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aba"));
        assertFalse(DfaSequenceValidator.validate(dfa, "abb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "ba"));
    }

    @Test
    public void testStar() {
        Automata<State> nfa = new NfaCreator().create("a*");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, ""));
        assertTrue(DfaSequenceValidator.validate(dfa, "a"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aaa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aaaaaaaaaaaaaaaaaaa"));

        assertFalse(DfaSequenceValidator.validate(dfa, "ab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aab"));
    }

    @Test
    public void testPlus() {
        Automata<State> nfa = new NfaCreator().create("a+");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "a"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aaa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aaaaaaaaaaaaaaaaaaa"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "ab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aab"));
    }

    @Test
    public void testBrackets() {
        Automata<State> nfa = new NfaCreator().create("()");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, ""));

        assertFalse(DfaSequenceValidator.validate(dfa, "ab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aab"));
    }

    @Test
    public void testBrackets1() {
        Automata<State> nfa = new NfaCreator().create("(()())");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, ""));

        assertFalse(DfaSequenceValidator.validate(dfa, "ab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aab"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectBrackets() {
        new NfaCreator().create("(()()))");
    }

    @Test
    public void testSequence0() {
        Automata<State> nfa = new NfaCreator().create("ab*");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "a"));
        assertTrue(DfaSequenceValidator.validate(dfa, "ab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abbbbbbbb"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "b"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "bb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aa"));
    }

    @Test
    public void testSequence1() {
        Automata<State> nfa = new NfaCreator().create("a+b");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "ab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aaaaaaaab"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "b"));
        assertFalse(DfaSequenceValidator.validate(dfa, "a"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aabb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "bb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aaaaaaaa"));
    }

    @Test
    public void testSequence2() {
        Automata<State> nfa = new NfaCreator().create("(a|b)a");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "aa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "ba"));

        assertFalse(DfaSequenceValidator.validate(dfa, "ab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "bb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "a"));
        assertFalse(DfaSequenceValidator.validate(dfa, "b"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aaa"));
    }

    @Test
    public void testSequence3() {
        Automata<State> nfa = new NfaCreator().create("(a|b)(a|b)a");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "aaa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aba"));
        assertTrue(DfaSequenceValidator.validate(dfa, "baa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "bba"));

        assertFalse(DfaSequenceValidator.validate(dfa, "aab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "bbb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aa"));
        assertFalse(DfaSequenceValidator.validate(dfa, "a"));
    }

    @Test
    public void testSequence4() {
        Automata<State> nfa = new NfaCreator().create("a(a|b)b(a|b)ab");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "aabaab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abbaab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abbaab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abbbab"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "ab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aba"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aaaaaa"));
        assertFalse(DfaSequenceValidator.validate(dfa, "bbbbbb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "babaab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aaaaab"));
        assertFalse(DfaSequenceValidator.validate(dfa, "abbbbb"));
    }

    @Test
    public void testSequence5() {
        Automata<State> nfa = new NfaCreator().create("a*(a|b)b*");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "a"));
        assertTrue(DfaSequenceValidator.validate(dfa, "b"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "bb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "ab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aaaaaa"));
        assertTrue(DfaSequenceValidator.validate(dfa, "bbbbbbbbb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aabbbbbb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aaaaaabbbbbb"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "ba"));
        assertFalse(DfaSequenceValidator.validate(dfa, "baaaa"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aaaaaba"));
    }

    @Test
    public void testSequence6() {
        Automata<State> nfa = new NfaCreator().create("aba*(a|b)b*(a|b)(a|b)bbb");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "abbbabbb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abaaaabaabbb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abaaaababbbb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abaaaababbbb"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "aaaabbbbbb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "abbaaaabbb"));
    }

    @Test
    public void testSequence7() {
        Automata<State> nfa = new NfaCreator().create("a+ba*(a|b)b*(a|b)(a|b)bbb");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "aaabaaaabaabbb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abaaaababbbb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "aaaabaaaababbbb"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "bbbabbb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aaaabbbbbb"));
        assertFalse(DfaSequenceValidator.validate(dfa, "abbaaaabbb"));
    }

    @Test
    public void testSequence8() {
        Automata<State> nfa = new NfaCreator().create("(ab)+");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "ab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abab"));
        assertTrue(DfaSequenceValidator.validate(dfa, "ababab"));

        assertFalse(DfaSequenceValidator.validate(dfa, ""));
        assertFalse(DfaSequenceValidator.validate(dfa, "a"));
        assertFalse(DfaSequenceValidator.validate(dfa, "b"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aba"));
        assertFalse(DfaSequenceValidator.validate(dfa, "ba"));
    }

    @Test
    public void testSequence9() {
        Automata<State> nfa = new NfaCreator().create("(abc)+(a|b|c)");
        Automata<DfaState> dfa = DfaCreator.create(nfa);

        assertTrue(DfaSequenceValidator.validate(dfa, "abca"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abcb"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abcc"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abcabca"));
        assertTrue(DfaSequenceValidator.validate(dfa, "abcabcabcb"));

        assertFalse(DfaSequenceValidator.validate(dfa, "a"));
        assertFalse(DfaSequenceValidator.validate(dfa, "b"));
        assertFalse(DfaSequenceValidator.validate(dfa, "c"));
        assertFalse(DfaSequenceValidator.validate(dfa, "abc"));
        assertFalse(DfaSequenceValidator.validate(dfa, "aaca"));
    }
}
