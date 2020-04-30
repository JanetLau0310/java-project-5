import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Match {
    public static void main(String[] args) {
/*   if (args.length != 2) {
        System.out.println("Usage: java Match [regex] [string]");
        System.out.println("       Put regex in quotes to avoid shell parsing weirdness");
        return;
      }
      Parser p = new Parser(args[0]);
      Regex r = p.parse();
      NFA nfa = new NFA(r);

      if (nfa.match(args[1], 4)) {
          System.out.println("yes");
        } else { System.out.println("no"); }   */
      testPerformance();
      //test1();
      //test2();
      //testfunction();
    }
    static void testPerformance(){
        Regex r = new RStar(new ROr(new RChar('a'), new RChar('b')));
        NFA nfa = new NFA(r);

        long start = System.nanoTime();
        assert  nfa.match("aaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbb", 2);
        long end1 = System.nanoTime();
        assert  nfa.match("aaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbb", 3);
        long end2 = System.nanoTime();
        assert  nfa.match("aaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbb", 4);
        long end3 = System.nanoTime();

        System.out.println("The running time of parallel program is :" + (end1 - start) + " ns");
        System.out.println("The running time of parallel program is :" + (end2 - end1) + " ns");
        System.out.println("The running time of parallel program is :" + (end3 - end2) + " ns");
    }
    static void test1(){
        Regex r = new RStar(new ROr(new RChar('d'), new RChar('z')));
        NFA nfa = new NFA(r);
        System.out.println(nfa.transition(nfa.start_state()));

    }
    static void test2(){
       Parser p = new Parser("(a|b)*abb");
        Regex r1 = p.parse();
        NFA nfa1 = new NFA(r1);
        System.out.println(nfa1.match("abbbbbbbbbbbabbc",4));

        Parser p2 = new Parser("(a*b|ac)d");
        Regex r2 = p2.parse();
        NFA nfa2 = new NFA(r2);
        System.out.println(nfa2.transition(nfa2.start_state()));
        System.out.println(nfa2.match("aaaabd",4));

/*         Parser p3 = new Parser("(a*b|ac)d");
        Regex r3 = p3.parse();
        NFA nfa3 = new NFA(r3);
        assert nfa3.match("aaaac",4);

        Parser p4 = new Parser("d|z");
        Regex r4 = p4.parse();
        NFA nfa4 = new NFA(r4);
        nfa4.match("dz",4);
        System.out.println(nfa4.transition(nfa4.start_state()).get(0).getValue().getClass());*/
    }
    static void testfunction(){
        NFA nfa = new NFA();

        Object a = nfa.newState();
        Object b = nfa.newState();
        System.out.println(a.equals(b));

        nfa.newTransition(nfa.start_state(),'a',a);
        nfa.newTransition(nfa.start_state(),'b',b);
        nfa.makeFinal(a);
        nfa.makeFinal(b);

        System.out.println(nfa.final_states().size());
        List<Map.Entry<Character, Object>> pairs_tran = nfa.transition(nfa.start_state());
        System.out.println(pairs_tran.get(0).getValue().getClass());

        //System.out.println(nfa.states());
    }
}
