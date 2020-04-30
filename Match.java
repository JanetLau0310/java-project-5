import java.util.Arrays;

public class Match {
    public static void main(String[] args) {
     if (args.length != 2) {
        System.out.println("Usage: java Match [regex] [string]");
        System.out.println("       Put regex in quotes to avoid shell parsing weirdness");
        return;
      }
      Parser p = new Parser(args[0]);
      Regex r = p.parse();
      NFA nfa = new NFA(r);

      if (nfa.match(args[1], 4)) {
          System.out.println("yes");
        } else { System.out.println("no"); } /**/
      //testPerformance();
     // test2();
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
    static void test2(){
       Parser p = new Parser("(a|b)*abb");
        Regex r = p.parse();
        NFA nfa = new NFA(r);
        System.out.println(nfa.transition(((Node)nfa.start_state()).getState()));
        System.out.println(nfa.match("ab",4));

        Parser p2 = new Parser("(a*b|ac)d");
        Regex r2 = p2.parse();
        NFA nfa2 = new NFA(r2);
        assert nfa2.states().get(0).isEnd;
        assert nfa2.final_states().get(0).isEnd;
        System.out.println(nfa2.transition(((Node)nfa2.start_state()).getState()));
        System.out.println(nfa2.match("aaaabd",4));

        Parser p3 = new Parser("(a*b|ac)d");
        Regex r3 = p3.parse();
        NFA nfa3 = new NFA(r3);
        assert nfa3.match("aaaac",4);
/*
        Parser p4 = new Parser("d|z");
        Regex r4 = p4.parse();
        NFA nfa4 = new NFA(r4);
        nfa4.match("dz",4);
        System.out.println(nfa4.transition(0));*/
    }
}
