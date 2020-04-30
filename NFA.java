import java.rmi.UnexpectedException;
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
        Node SimpleChar = (Node)tmp.newState();
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
    Object newState() {
        Node newState = new Node();
        if(nfa!=null){
            nfa.add(newState);
        }else{
            nfa = new ArrayList<>();
            nfa.add(newState);
        }
        return newState;
    }
    // a transition from start to end with label char c
    void newTransition(Object start, char c, Object end){
        if(this.nfa != null) {
            Node from = (Node) start;
            Node to = (Node) end;
            if (this.nfa.contains(from) && this.nfa.contains(to)) {
                if ((c >= 'a' && c <= 'z') || (c == '#')) {
                    from.addEdge(c, to);
                    return;
                }
            }
        }
        throw new UnsupportedOperationException();
    }
    // set state s.end = true
    void makeFinal(Object state) {
         Node tmp = (Node) state;
         int s = tmp.getState();
         if(nfa != null){
            int i = 0;
            for(Node n:nfa){
                if(n.getState() == s){
                    i++;
                    n.isEnd = true;
                }
            }
            if(i==0){
                throw new UnsupportedOperationException();
            }
        }else{
            throw new UnsupportedOperationException();
        }
    }
    // return all the nodes in NFA
    List<Object> states() {
        List<Object> res = new ArrayList<>();
        for(Node n:nfa){
            res.add(n);
        }
        return res;
    }

    Object start_state() {
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

    List<Object> final_states() {//return the final state
        List<Object> final_state = new ArrayList<>();
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
    public List<Map.Entry<Character, Object>> transition(Object state) {
        Node tmp = (Node) state;
        int s = tmp.getState();
        Node n = this.getID(s);
        List<Map.Entry<Character, Object>> pairs_tran = new ArrayList<Map.Entry<Character, Object>>();
        for(int i=0; i<n.desNode.size();i++){
            Node value = n.desNode.get(i);
            Character key = n.label.get(i);
            pairs_tran.add(new AbstractMap.SimpleEntry<Character,Object>(key,value));
       }
       return pairs_tran;
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

        Node s1 = (Node)a.start_state();
        s1.isStart = false;
        Node s2 = (Node)b.start_state();
        s2.isStart = false;

        newBeg.nfa.addAll(a.nfa);
        newBeg.nfa.addAll(b.nfa);

        newBeg.newTransition(newBeg.start_state(),'#',s1);
        newBeg.newTransition(newBeg.start_state(),'#',s2);
        Node tmp = (Node)newBeg.start_state();
        tmp.isStart = true;

        return newBeg;
    }
    // r = t*
    NFA Star(NFA t){
        NFA newBeg = new NFA();
        Node tmp = (Node)newBeg.start_state();
        tmp.isEnd = true;
        newBeg.nfa.addAll(t.nfa);

        newBeg.newTransition(newBeg.start_state(),'#',t.start_state());
        tmp = (Node)t.start_state();
        tmp.isStart = false;

        for(Object obj:t.final_states()){
            newBeg.newTransition(obj,'#',newBeg.start_state());
            tmp = (Node)obj;
            tmp.isEnd = false;
        }
        return newBeg;
    }
    // r = ab
    NFA Concat(NFA a, NFA b){
        a.nfa.addAll(b.nfa);
        for(Node an:a.nfa){
            if(an.isEnd && !b.nfa.contains(an)){
                a.newTransition(an,'#',b.start_state());
                an.isEnd = false;
            }
        }
        Node tmp = (Node)b.start_state();
        tmp.isStart = false;
        return a;
    }

    public boolean match(String test,int nthreads){
        ArrayList<Integer> eclosure = new ArrayList<Integer>();
        // initial, then start to use multi threads
        Graph graph_dfs = new Graph(G, 0);
        for(int v = 0;v<G.getSize();v++){
            if(graph_dfs.visited(v)) eclosure.add(v);
        }

        /*------  multi-threads version ---------*/
/*        Thread[] lookers = new Looker[nthreads];
        boolean[] visited = new boolean[nthreads];

        for(int i = 0; i<nthreads; i++){
            visited[i] = false;
            lookers[i] = new Looker(G,i);
            lookers[i].start();
        }
        for(int i = 0; i<nthreads; i++){
            try {
                lookers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(int i = 0;i<nthreads;i++){
            if(!visited[i]){
                return false;
            }
        }
        return true;*/

        /*------ this is the non-thread version ------*/
        // compute every NFA state that test[i+1] can reach
        for(int i=0;i<test.length();i++){
            //read in a char, then transfer the state in pc
            ArrayList<Integer> match = new ArrayList<Integer>();
            for(int stateNum:eclosure) {
                if (stateNum < M) {
                    //for every state in match, compare with char i in test
                    if (re[stateNum] == test.charAt(i)) {
                        match.add(stateNum + 1);
                    }
                }
            }
            eclosure = new ArrayList<Integer>();
            // count the epsilon-closure in match
            graph_dfs = new Graph(G, match);
            for(int v=0;v<G.getSize();v++) {
                if (graph_dfs.visited(v)) {
                    // eclosure will be update in every for loop
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
        /**/
    }
}
