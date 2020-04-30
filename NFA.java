import java.util.*;

public class NFA {
    private char[] re;//save the regexp
    private Graph G;
    private int M;
    private String regex = "";
    private Stack<Character> operatorStack;//put the left bracket
    private Stack<NFA> NfaStack;
    private ArrayList<Node> nfa;

    NFA() {
      //Construct an NFA with one start state and no transitions
      nfa = new ArrayList<>();
      Node start = new Node();
      start.isStart = true;
      nfa.add(start);
    }
    public NFA BasicNFA(char c){
        NFA tmp = new NFA();
        Node SimpleChar = tmp.newState();
        SimpleChar.isEnd = true;
        tmp.newTransition(tmp.start_state(),c,SimpleChar);
        return tmp;
    }

    public NFA(Regex regexp){
        this.re = regexp.toString().toCharArray();
        M = re.length;
        G = buildEpsilonTrnsitionDigraph();
        // we don't know whether there are 2 char connected
        // so use modify string to add the '&'
        // and change the infix into the postfix
        modifyString(regexp.toString());
        char[] postfix = infixTopostfix(regex).toCharArray();
        operatorStack = new Stack<>();
        NfaStack = new Stack<NFA>();

       for(int i = 0; i< postfix.length; i++){
            char c = postfix[i];
            if (!isOperator(c)) {
                NfaStack.push(BasicNFA(c));
            }else if(c == '|'){
                NFA bNfa = NfaStack.pop();
                NFA aNfa = NfaStack.pop();
                NfaStack.push(Union(aNfa,bNfa));
            }else if(c == '&'){
                NFA bNfa = NfaStack.pop();
                NFA aNfa = NfaStack.pop();
                NfaStack.push(Concat(aNfa,bNfa));
            }else if(c == '*'){
                NFA tmp = NfaStack.pop();
                NfaStack.push(Star(tmp));
            }
        }
       this.nfa = NfaStack.pop().nfa;
       //System.out.println(Arrays.toString(transition(0).toArray()));
    }
    // build a new Node in NFA
    Node newState() {
        if(nfa!=null){
            Node newState = new Node();
            nfa.add(newState);
            return newState;
        }else{
            throw new UnsupportedOperationException();
        }
    }
    // a transition from start to end with label char c
    void newTransition(Node from, char c, Node to) {
        if(this.nfa == null){
            throw new UnsupportedOperationException();
        }
        if((c >= 'a' && c <= 'z') || (c == '#')){
            from.addEdge(c,to);
        }else{
            throw new UnsupportedOperationException();
        }
    }
    // set state s.end = true
    void makeFinal(int s) {
        if(nfa != null){
            for(Node n:nfa){
                if(n.getState() == s){
                    n.isEnd = true;
                }
            }
        }
        throw new UnsupportedOperationException();
    }
    // return all the nodes in NFA
    public ArrayList<Node> states() {
      if(nfa == null){
          throw new UnsupportedOperationException();
      }
      return nfa;
    }

    public Node start_state() {
        //return the start state
        if(nfa != null){
            for(Node n:nfa){
                if(n.isStart){
                    return n;
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    public ArrayList<Node> final_states() {//return the final state
        ArrayList<Node> final_state = new ArrayList<>();
        for(Node n:nfa){
            if(n.isEnd){
                final_state.add(n);
            }
        }
        if(!final_state.isEmpty()){
            return final_state;
        }
        throw new UnsupportedOperationException();
    }
    public Node getID(int state){
        if(nfa != null){
            for(Node n:nfa){
                if(n.getState() == state){
                    return n;
                }
            }
        }
        throw new UnsupportedOperationException();
    }
    // returns a list of pairs of transitions for the given state.
    // An ε-transition should be represented using the character #
    // for ab*, The edges from S0 are ('a', S0), ('b', S0), and ('a', S1).
    public List<Map.Entry<Character, Integer>> transition(int state) {
       Node n = this.getID(state);
       Map<Character,Integer> map = new HashMap<>();
       List<Map.Entry<Character, Integer>> pairs_tran = new LinkedList<>();
       for(int i=0; i<n.desNode.size();i++){
           map.put(n.label.get(i),n.desNode.get(i).getState());
           Set entrySet = map.entrySet();
           Iterator it = entrySet.iterator();
           while(it.hasNext()){
               Map.Entry entry = (Map.Entry) it.next();
               pairs_tran.add(entry);
           }
       }


       return pairs_tran;
    }

    public boolean match(String test,int nthreads){
        // test regex matching
        // s is the testing string
        ArrayList<Integer> eclosure = new ArrayList<Integer>();
        //ArrayList<Node> end_state = final_states();

        DirectedDFS dfs = new DirectedDFS(G, 0);
        for(int v = 0;v<G.getV();v++){
            if(dfs.marked(v)) eclosure.add(v);
        }
        //initial, then start to use multi threads
        // go through all the char in String test
        for(int i=0;i<test.length();i++){
            ArrayList<Integer> match = new ArrayList<Integer>();
            //read in a char, then transfer the state in pc
            for(int stateNum:eclosure) {
                if (stateNum < M) {
                    if (re[stateNum] == test.charAt(i)) {
                        //for every state in match, compare with char i in test
                        //if matches
                        match.add(stateNum + 1);
                    }
                }
            }

            eclosure = new ArrayList<Integer>();
            // count the epsilon-closure in match
            dfs = new DirectedDFS(G, match);
            for(int v=0;v<G.getV();v++) {
                if (dfs.marked(v)) {
                    eclosure.add(v);
                }
            }
        }

        for(int v:eclosure) {
            // check if get the end
            if(v == M){
                return true;
            }
        }
        return false;
    }

    private Graph buildEpsilonTrnsitionDigraph() {
        Graph G = new Graph(M + 1);
        Stack<Integer> ops = new Stack<Integer>();
        for (int i = 0; i < M; i++) {
            int lp = i;  
            // left parentheses and |
            if (re[i] == '(' || re[i] == '|') ops.push(i);
            else if (re[i] == ')') {
                int or = ops.pop();
                if (re[or] == '|') {
                    lp = ops.pop();
                    G.addEdge(lp, or + 1);
                    G.addEdge(or, i);
                }
                else lp = or;
            }
            // closure
            // needs 1-character lookahead
            if (i < M - 1 && re[i + 1] == '*') {
                G.addEdge(lp, i + 1);
                G.addEdge(i + 1, lp);
            }
            // metasymbols
            if (re[i] == '(' || re[i] == '*' || re[i] == ')')
                G.addEdge(i, i + 1);
        }
        return G;
    }

    private void modifyString(String _regex) {
        char[] regexs = _regex.replaceAll(" ", "").toCharArray();
        for (int i = 0; i < regexs.length; i++) {
            if (i == 0)
                regex += regexs[i];
            else {
                if (regexs[i] == '|' || regexs[i] == '*' || regexs[i] == ')') {
                    regex += regexs[i];
                } else {
                    if (regexs[i - 1] == '(' || regexs[i - 1] == '|')
                        regex += regexs[i];
                    else
                        regex += ("&" + regexs[i]);
                }
            }
        }
    }

    private boolean isOperator(Character character) {
        String operatorString = "*&|()";
        if (operatorString.contains(character.toString())) {
            return true;
        } else {
            return false;
        }
    }

    String infixTopostfix(String infix){
        char left_b;
        char[] in = infix.toCharArray();
        String postfix = "";
        operatorStack = new Stack<>();
        for(int i = 0; i< in.length; i++){
            char c = in[i];
            if (!isOperator(in[i])) {
                postfix += in[i];
            }
            if(c =='('){
                operatorStack.push(c);
            }else if (c ==')'){
                while(true){
                    left_b = operatorStack.pop();
                    if(left_b == '('){
                        break;
                    } else {
                        postfix += left_b; }
                }
            }else if (c == '|' || c == '&' || c =='*'){
                //|&*
                String priority = "()|&*";
                int pos = priority.indexOf(Character.toString(c));
                while (!operatorStack.empty()){
                    char top = operatorStack.peek();
                    int pos2 = priority.indexOf(Character.toString(top));
                    if(pos2 < pos)break;
                    else { postfix += operatorStack.pop(); }
                }
                operatorStack.push(c);
            }
        }
        while(!operatorStack.empty()){
            postfix += operatorStack.pop();
        }
        return postfix;
    }

    // r = a|b
    NFA Union(NFA a, NFA b){
        NFA newBeg = new NFA();

        Node s1 = a.start_state();
        s1.isStart = false;
        Node s2 = b.start_state();
        s2.isStart = false;

        newBeg.newTransition(newBeg.start_state(),'#',s1);
        newBeg.newTransition(newBeg.start_state(),'#',s2);
        newBeg.start_state().isStart = true;

        NFA newEnd = new NFA();


        for(Node an:a.final_states()){
            newEnd.newTransition(an,'#',newEnd.start_state());
            an.isEnd = false;
        }
        for(Node bn: b.final_states()){
            newEnd.newTransition(bn,'#',newEnd.start_state());
            bn.isEnd = false;
        }
        newEnd.start_state().isEnd = true;
        newEnd.start_state().isStart = false;

        a.nfa.addAll(newEnd.nfa);
        b.nfa.addAll(newEnd.nfa);

        newBeg.nfa.addAll(a.nfa);
        newBeg.nfa.addAll(b.nfa);

        return newBeg;
    }
    // r = t*
    NFA Star(NFA t){
        NFA newBeg = new NFA();
        newBeg.start_state().isEnd = true;

        newBeg.newTransition(newBeg.start_state(),'#',t.start_state());
        t.start_state().isStart = false;

        for(Node n:t.final_states()){
            newBeg.newTransition(n,'#',newBeg.start_state());
            n.isEnd = false;
        }

        newBeg.nfa.addAll(t.nfa);

        return newBeg;
    }
    // r = ab
    NFA Concat(NFA a, NFA b){
        for(Node an:a.final_states()){
            a.newTransition(an,'#',b.start_state());
            an.isEnd = false;
        }
        b.start_state().isStart = false;
        a.nfa.addAll(b.nfa);
        return a;
    }

}
