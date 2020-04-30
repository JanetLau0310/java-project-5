import java.util.Stack;

public class Looker extends Thread  {
	private int id;
	private Graph graph;
	public Looker(Graph g,int id){
		this.id = id;
		graph = g;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void run() {
/*		while(!graph.isDone()){
			graph.dfs();
			yield();
			//wait a while for other thread
			// but sometimes it might not perform well...
			subDfs(graph.getLocalStacks().get(id));
		}*/
	}


/*	public void subDfs(Stack<Integer> localStack){
		Stack<Integer> tmpStack = new Stack<Integer>();
		while(!localStack.isEmpty()){
			int node = localStack.pop();
			if(!graph.getVisited(node)){
				graph.setVisited(node,true);
				graph.incrementCounter();
				boolean toLocal = true;
				for(int i = 0; i<graph.getSize(); i++){
					if(node==i)continue;
					if(graph.isNeighbour(node, i) && !toLocal && !graph.getVisited(i)){
						tmpStack.push(i);
						
					}
					if(graph.isNeighbour(node, i) && toLocal && !graph.getVisited(i)){
						localStack.push(i);
						toLocal = false;
					}
				}
			}
		}
		graph.pushStack(tmpStack);
	}	*/
}
