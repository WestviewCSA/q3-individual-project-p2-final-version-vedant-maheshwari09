import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class mapRunner{
    //where it all begins fr
    public static void main(String[] args) throws Exception{
        //set all booleans to false
        boolean queueBase= false;
        boolean stackBase= false;
        boolean optimal= false;
        boolean showTime= false;
        boolean inCoord= false;
        boolean outCoord= false;
        String fileName= "";

        for(String arg: args){
            if(arg.equals("--Stack")){ 
                stackBase= true;
            }
            else if(arg.equals("--Queue")){
                queueBase= true;
            }
            else if(arg.equals("--Opt")){
                optimal= true;
            }
            else if(arg.equals("--Time")){
                showTime= true;
            }
            else if(arg.equals("--InCoordinate")){
                inCoord= true;
            }
            else if(arg.equals("--OutCoordinate")){
                outCoord= true;
            }
            else if(arg.equals("--Help")){
                System.out.println("Help!: Use --Stack, --Queue, or --Opt for routing. Use --Incoordinate for coord-based map reading");
                System.exit(0); //no expection for this
            }
            else if(!arg.startsWith("--")){
                fileName= arg;
            }
        }
        
        int routeCount= 0;
        if(stackBase){
            routeCount++;
        }
        if(queueBase){
            routeCount++;
        }
        if(optimal){
            routeCount++;
        }
        
        if(routeCount!= 1){
        	throw new IllegalCommandLineInputsException("Error: It must have exactly 1 of the routing method (--Stack, --Queue, or --Opt)");
        }
        if(fileName.isEmpty()){
        	throw new IllegalCommandLineInputsException("Error: There is no map file specified in command line. Has to have 1.");
        }

        //Load whichever map the I asked for using the fileName variable
        String[][][] activeMap;
        if(!inCoord){
            activeMap= getTextBasedMap(fileName);
            System.out.println("Text-Based Map (Unsolved):");
        } else{
            activeMap= getCoordinateBasedMap(fileName);
            System.out.println("\nCoordinate-Based Map (Unsolved):");
        }

        // 2. Find the Starting location ('W')
        Location start= findStart(activeMap);
        Location endNode= null;

        if(start== null){
        	throw new IncorrectMapFormatException("Error: No starting location 'W' found in the map!");
        }

        // starts time (and then ends it after)
        long startTime= System.nanoTime();

        //runs
        if(queueBase){
            System.out.println("Using Queue (BFS):");
            endNode= solveQueue(activeMap, start);
        }
        if(stackBase){
            System.out.println("Using Stack (DFS):");
            endNode= solveStack(activeMap, start);
        }
        if(optimal){
            System.out.println("Using Optimal (Queue/BFS):");
            endNode= solveOptimal(activeMap, start); 
        }

        long endTime= System.nanoTime();

        //result output
        if(endNode== null){
            System.out.println("\nThe Result: No path has been found LL");
        } else{
            System.out.println("\nThe Result: The path has been found WW");
            if(outCoord){
                printOutCoordinate(endNode);
            } 
            //Call markPath
            else{
                markPath(activeMap, endNode);
                System.out.println("Final Solved Map:");
                printMap(activeMap);
            }
        }

        // This prints time if requested, formatted to seconds
        if(showTime){
            double runtimeSeconds= (endTime- startTime)/ 1000000000.0;
            System.out.printf("Total Runtime: %.7f seconds\n",runtimeSeconds);
        }
    }
    
    //method getTextBasedMap
    public static String[][][] getTextBasedMap(String filePath) throws FileNotFoundException, IncorrectMapFormatException, IncompleteMapException, IllegalMapCharacterException{
        File mapFile= new File(filePath);
        Scanner fileScan= new Scanner(mapFile);
        int numRows= fileScan.nextInt();
        int numCols= fileScan.nextInt();
        int numLevels= fileScan.nextInt();
        
        if(numRows<= 0 || numCols<= 0 || numLevels<= 0){
        	throw new IncorrectMapFormatException("Error: Map dimensions have to be greater than zero");
        }
        
        String[][][] mazeGrid= new String[numLevels][numRows][numCols];
        for(int i= 0; i< numLevels; i++){
            for(int j= 0; j< numRows; j++){
                String rowStr= fileScan.next();
                
                if(rowStr.length()< numCols){
                	throw new IncompleteMapException("Error: There is an incomplete map line found at the row: "+ j);
                }

                for(int k= 0; k< numCols; k++){
                    mazeGrid[i][j][k]= rowStr.substring(k, k+ 1);
                }
            }
        }
        fileScan.close();
        return mazeGrid;
    }
    
    //method getCoordinateBasedMap
    public static String[][][] getCoordinateBasedMap(String filePath) throws FileNotFoundException, IncorrectMapFormatException, IllegalMapCharacterException{
        File mapFile= new File(filePath);
        Scanner fileScan= new Scanner(mapFile);
        int numRows= fileScan.nextInt();
        int numCols= fileScan.nextInt();
        int numLevels= fileScan.nextInt();
        
        if(numRows<= 0 || numCols<= 0 || numLevels<= 0){
        	throw new IncorrectMapFormatException("Error: The map dimensions have to be greater than zero.");
        }

        String[][][] mazeGrid= new String[numLevels][numRows][numCols];

        while(fileScan.hasNext()){
            String cellChar= fileScan.next();
            int r= Integer.parseInt(fileScan.next());
            int c= Integer.parseInt(fileScan.next());
            int l= Integer.parseInt(fileScan.next());
            
            if(r< 0 || r>= numRows || c< 0 || c>= numCols|| l< 0 || l>= numLevels){
            	throw new IllegalMapCharacterException("Error: Invalid character '" + cellChar + "' found in map.");
            }
            if(!cellChar.equals("W") && !cellChar.equals(".") && !cellChar.equals("@") && !cellChar.equals("|") && !cellChar.equals("$")) {
            		throw new IllegalMapCharacterException("Error: There an invalid character '" + cellChar + "' found in map.");
            	}
            mazeGrid[l][r][c]= cellChar;
        }
        
        for(int i= 0; i< numLevels; i++){
            for(int j= 0; j< numRows; j++){
                for(int k= 0; k< numCols; k++){
                    if(mazeGrid[i][j][k]== null){
                        mazeGrid[i][j][k]= ".";
                    }
                }
            }
        }
        
        fileScan.close();
        return mazeGrid;
    }

    public static void printMap(String[][][] gridToPrint){
        for(int i= 0; i< gridToPrint.length; i++){
            for(int j= 0; j< gridToPrint[i].length; j++){
                for(int k= 0; k< gridToPrint[i][j].length; k++){
                    System.out.print(gridToPrint[i][j][k]);
                }
                System.out.println();                
            }
            System.out.println();
        }
    }
    //W is the start 
    public static Location findStart(String[][][] map){
        for(int l= 0; l< map.length; l++){
            for(int r= 0; r< map[l].length; r++){
                for(int c= 0; c< map[l][r].length; c++){
                    if(map[l][r][c].equals("W")){
                        return new Location(r, c, l, null);
                    }
                }
            }
        }
        return null;
    }
    
 // This is a helper method to find the Wolverine on a specific level
    public static Location findWOnLevel(String[][][] map, int level, Location previous) {
        for(int r = 0; r < map[level].length; r++){
            for(int c = 0; c < map[level][r].length; c++){
                if(map[level][r][c].equals("W")){
                    return new Location(r, c, level, previous);
                }
            }
        }
        return null;
    }
    
    //This Finds the target '$' so A* essentially where to go
    public static Location findGoal(String[][][] map) {
        for(int l= 0; l< map.length; l++){
            for(int r= 0; r< map[l].length; r++){
                for(int c= 0; c< map[l][r].length; c++){
                    if(map[l][r][c].equals("$")){
                        return new Location(r, c, l, null);
                    }
                }
            }
        }
        return null;
    }

    // This calculates the 3D distance (the heuristic for A*)
    private static int getHeuristic(int r, int c, int l, Location goal) {
        return Math.abs(r - goal.row) + Math.abs(c - goal.col) + Math.abs(l - goal.level);
    }

    public static Location solveStack(String[][][] map, Location start){
        int levels= map.length;
        int rows= map[0].length;
        int cols= map[0][0].length;
        
        Stack<Location> stack= new Stack<>();
        boolean[][][] visited= new boolean[levels][rows][cols];      
        
        stack.push(start);

        int[] dr= {-1, 1, 0, 0};
        int[] dc= {0, 0, 1, -1};

        while(!stack.isEmpty()){
            Location curr= stack.pop();

            if(visited[curr.level][curr.row][curr.col]){
                continue;
            }
            visited[curr.level][curr.row][curr.col]= true;


            if(map[curr.level][curr.row][curr.col].equals("$")){
                return curr;
            }

            for(int d= 0; d< 4; d++){
                int nr= curr.row+ dr[d];
                int nc= curr.col+ dc[d];
                int nl= curr.level;

                if(nl< 0 || nl>= levels || nr< 0 || nr>= rows || nc< 0 || nc>= cols){
                    continue;
                }
                if(visited[nl][nr][nc]){ 
                    continue;
                }
                String cell= map[nl][nr][nc];
                if(cell.equals("@")){
                    continue;
                }
                Location next= new Location(nr, nc, nl, curr);

                if(cell.equals("$")){
                    return next;
                }

                if(cell.equals("|")){
                    stack.push(next);
                    int nextLevel= nl+ 1;
                    if(nextLevel< levels){
                    	Location nextW= findWOnLevel(map, nextLevel, next);
                        if(nextW!= null && !visited[nextLevel][nextW.row][nextW.col]){
                            stack.push(nextW);
                        }
                    }
                    int prevLevel = nl - 1;
                    if(prevLevel >= 0){
                    	Location prevW= findWOnLevel(map, prevLevel, next);
                        if(prevW!= null && !visited[prevLevel][prevW.row][prevW.col]){
                            stack.push(prevW);
                        }
                    }
                } 
                else{
                    stack.push(next);
                }
            }
        }
        return null;
    }
    
    public static Location solveQueue(String[][][] map, Location start){
        int levels= map.length;
        int rows= map[0].length;
        int cols= map[0][0].length;
        
        Queue<Location> queue= new LinkedList<>();
        boolean[][][] visited= new boolean[levels][rows][cols];
 
        visited[start.level][start.row][start.col]= true;
        queue.add(start);

        int[] dr= {-1, 1, 0, 0};
        int[] dc= {0, 0, 1, -1};

        while(!queue.isEmpty()){
            Location curr= queue.poll();


            for(int d= 0; d< 4; d++){
                int nr= curr.row+ dr[d];
                int nc= curr.col+ dc[d];
                int nl= curr.level;

                if(nl< 0 || nl>= levels || nr< 0 || nr>= rows || nc< 0 || nc>= cols){
                    continue;
                }
                if(visited[nl][nr][nc]){
                    continue;
                }

                String cell= map[nl][nr][nc];
                if(cell.equals("@")){
                    continue;
                }

                Location next= new Location(nr, nc, nl, curr);

                if(cell.equals("$")){
                    return next; 
                }

                visited[nl][nr][nc]= true;

                if(cell.equals("|")){
                    queue.add(next);
                    int nextLevel= nl+ 1;
                    if(nextLevel< levels){
                    	Location nextW= findWOnLevel(map, nextLevel, next);
                        if(nextW!= null && !visited[nextLevel][nextW.row][nextW.col]){
                            visited[nextLevel][nextW.row][nextW.col] = true;
                            queue.add(nextW);
                        }
                    }
                    int prevLevel= nl - 1;
                    if(prevLevel>= 0){
                    	Location prevW= findWOnLevel(map, prevLevel, next);
                        if(prevW!= null && !visited[prevLevel][prevW.row][prevW.col]){
                            visited[prevLevel][prevW.row][prevW.col]= true;
                            queue.add(prevW);
                        }
                    }
                } 
                else{
                    queue.add(next);
                }
            }
        }
        return null;
    }
    
    //
    public static Location solveOptimal(String[][][] map, Location start) {
        int levels = map.length;
        int rows = map[0].length;
        int cols = map[0][0].length;

        // A* needs to know where it is going
        Location goal = findGoal(map);
        if (goal == null) {
            System.err.println("Error: No goal '$' found in map!");
            return null; 
        }

        // gScore tracks num steps
        int[][][] gScore = new int[levels][rows][cols];
        for (int l = 0; l < levels; l++) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    gScore[l][r][c] = Integer.MAX_VALUE;
                }
            }
        }
        gScore[start.level][start.row][start.col] = 0;

        // Priority queue orders nodes by fScore = gScore (steps taken) + heuristic (estimated steps left)
        java.util.PriorityQueue<Location> openSet = new java.util.PriorityQueue<>(
            (a, b) -> {
                int fA= gScore[a.level][a.row][a.col]+ getHeuristic(a.row, a.col, a.level, goal);
                int fB= gScore[b.level][b.row][b.col]+ getHeuristic(b.row, b.col, b.level, goal);
                return Integer.compare(fA, fB);
            }
        );
        boolean[][][] inQueue = new boolean[levels][rows][cols];
        
        openSet.add(start);
        inQueue[start.level][start.row][start.col] = true;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, 1, -1};

        while (!openSet.isEmpty()) {
            Location curr = openSet.poll(); // This always pulls the best/closest Location
            inQueue[curr.level][curr.row][curr.col] = false;

            if (curr.row == goal.row && curr.col == goal.col && curr.level == goal.level) {
                return curr; 
            }
            //thiws checks the N/S/E/W
            for (int d = 0; d < 4; d++) {
                int nr= curr.row+ dr[d];
                int nc = curr.col + dc[d];
                int nl = curr.level;

                if (nl < 0 || nl >= levels || nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (map[nl][nr][nc].equals("@")) continue;

                int tentativeGScore = gScore[curr.level][curr.row][curr.col] + 1;

                // If this is a faster path to this coordinate, it will record it
                if (tentativeGScore< gScore[nl][nr][nc]) {
                    gScore[nl][nr][nc]= tentativeGScore;
                    Location next= new Location(nr, nc, nl, curr);
                    if (!inQueue[nl][nr][nc]) {
                        openSet.add(next);
                        inQueue[nl][nr][nc] = true;
                    }
                }
            }

            // 2. Check the movement (Up and Down)
            if (map[curr.level][curr.row][curr.col].equals("|")) {
                if (curr.level + 1 < levels) {
                    int nl = curr.level + 1;
                    Location nextW = findWOnLevel(map, nl, curr);
                    if (nextW != null) {
                        int tentativeGScore= gScore[curr.level][curr.row][curr.col]+ 1;
                        if (tentativeGScore< gScore[nl][nextW.row][nextW.col]) {
                            gScore[nl][nextW.row][nextW.col]= tentativeGScore;
                            if (!inQueue[nl][nextW.row][nextW.col]) {
                                openSet.add(nextW);
                                inQueue[nl][nextW.row][nextW.col]= true;
                            }
                        }
                    }
                }
                if (curr.level - 1 >= 0) {
                    int nl = curr.level - 1;
                    Location prevW= findWOnLevel(map, nl, curr);
                    if (prevW!= null) {
                        int tentativeGScore= gScore[curr.level][curr.row][curr.col]+ 1;
                        if (tentativeGScore< gScore[nl][prevW.row][prevW.col]) {
                            gScore[nl][prevW.row][prevW.col]= tentativeGScore;
                            if (!inQueue[nl][prevW.row][prevW.col]) {
                                openSet.add(prevW);
                                inQueue[nl][prevW.row][prevW.col]= true;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    //backwards path tracing..
    public static void markPath(String[][][] map, Location endNode){
        Location curr= endNode.previous; 
        while(curr!= null && curr.previous!= null){ 
            // Overwrites with + if there a open spot (.)
        	if(map[curr.level][curr.row][curr.col].equals(".")) {
        		map[curr.level][curr.row][curr.col]= "+"; 
        	}
            curr= curr.previous;
        }
    }

    public static void printOutCoordinate(Location endNode){
        Stack<Location> path= new Stack<>();
        Location curr= endNode;
        while(curr!= null){
            path.push(curr);
            curr= curr.previous;
        }
        System.out.println("Path Coordinates~(row col level):");
        while(!path.isEmpty()){
            Location step= path.pop();
            System.out.println(step.row+ " "+ step.col+ " "+ step.level);
        }
    }  
}