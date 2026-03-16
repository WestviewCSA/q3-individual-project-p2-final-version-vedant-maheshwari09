import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class mapRunner {
    //where it all begins fr
    public static void main(String[] args) throws FileNotFoundException {
    	//set all booleans to false
        boolean queueBase= false;
        boolean stackBase= false;
        boolean optimal= false;
        boolean showTime= false;
        boolean inCoord= false;
        boolean outCoord= false;
        String fileName = "";

        for(String arg : args) {
            if(arg.equals("--Stack")) { //currently added to args in config
                stackBase= true;
            }
            if(arg.equals("--Queue")) {
                queueBase= true;
            }
            if(arg.equals("--Opt")) {
                optimal= true;
            }
            if(arg.equals("--Time")) {
                showTime= true;
            }
            if(arg.equals("--Incoordinate")) {
                inCoord= true;
            }
            if(arg.equals("--Outcoordinate")) {
                outCoord= true;
            }
            if(arg.equals("--Help!!!!!!")) {
                System.out.println("Help!: Use --Stack, --Queue, or --Opt for rounting. Use --Incoordinate for coord-based map reading");
                System.exit(0);
            }
            else if(!arg.startsWith("--")) {
                fileName = arg;
            }
        }
        
        int routeCount= 0;
        if(stackBase == true) {
            routeCount++;
            //System.out.println("We are running Stack");
        }
        if(queueBase== true) {
            routeCount++;
            //System.out.println("We are running Queue");
        }
        if(optimal == true) {
            routeCount++;
            //System.out.println("We are running the Optimal Path");
        }
        if(routeCount != 1) {
            System.err.println("Error: It must have 1 --Stack, --Queue, or --Opt");
            System.exit(-1);
        }
        if (fileName.isEmpty()) {
            System.err.println("Error: There is no map file specfied in command line. Has to have 1.");
            System.exit(-1);
        }
       //Load whichever map the I asked for using the fileName vareibale
        String[][][] activeMap;
        if (inCoord == false) {
            activeMap = getTextBasedMap(fileName);
            System.out.println("Text-Based Map (Unsolved):");
        } else {
            activeMap = getCoordinateBasedMap(fileName);
            System.out.println("\nCoordinate-Based Map (Unsolved):");
        }

        // 2. Find the Starting location ('W' or 'S')
        Location start = findStart(activeMap);
        Location endNode = null;

        if (start == null) {
            System.err.println("Error: No starting location 'W' or 'S' found!");
            System.exit(1);
        }

        // starts time (and then ends it after)
        long startTime = System.currentTimeMillis();

        //runs
        if(queueBase == true) {
            System.out.println("Using Queue (BFS):");
            endNode = solveQueue(activeMap, start);
        }
        if(stackBase == true) {
            System.out.println("Using Stack (DFS):");
            endNode = solveStack(activeMap, start);
        }
        if(optimal == true) {
            System.out.println("Using Optimal (Queue/BFS):");
            endNode = solveQueue(activeMap, start); 
        }

        long endTime = System.currentTimeMillis();

        //result output
        if (endNode == null) {
            System.out.println("The Result: No path has been found LL");
        } else {
            System.out.println("The Result: The path has been found WW");
            
            //Call markPath
            markPath(activeMap, endNode);
            System.out.println("Solved Map:");
            printMap(activeMap);
        }

        // This prints time if requested
        if (showTime == true) {
            System.out.println("Routing took: " + (endTime - startTime) + " ms");
        }
    }
    //This is the getTextBasedMap method (it's static): It essentially converts a grid formatted file into a 3D array. 
    public static String[][][] getTextBasedMap(String filePath) throws FileNotFoundException {
        File mapFile= new File(filePath);
        Scanner fileScan= new Scanner(mapFile);
        int numRows= fileScan.nextInt();
        int numCols= fileScan.nextInt();
        int numLevels= fileScan.nextInt();
        
        if (numRows <= 0 || numCols <= 0 || numLevels <= 0) {
            System.err.println("Error: Map dimensions must be greater than zero");
            System.exit(1);
        }
        
        String[][][] mazeGrid= new String[numLevels][numRows][numCols];
        for(int i= 0; i < numLevels; i++) {
            for(int j= 0; j < numRows; j++) {
                String rowStr= fileScan.next();
                
                if (rowStr.length() < numCols) {
                    System.err.println("Error: There is an incomplete map line found at the row " + j);
                    System.exit(1);
                }

                for(int k= 0; k < numCols; k++) {
                    mazeGrid[i][j][k]= rowStr.substring(k, k+1);
                }
            }
        }
        fileScan.close();
        return mazeGrid;
    }
    //THis is the getCoordinateBasedMap method. It essentially places specific file data into a 3D array by the coordinates.
    public static String[][][] getCoordinateBasedMap(String filePath) throws FileNotFoundException {
        File mapFile= new File(filePath);
        Scanner fileScan= new Scanner(mapFile);
        int numRows= fileScan.nextInt();
        int numCols = fileScan.nextInt();
        int numLevels= fileScan.nextInt();
        
        if (numRows <= 0 || numCols <= 0 || numLevels <= 0) {
            System.err.println("Error: The map dimensions have to  be greater than zero");
            System.exit(1);
        }

        String[][][] mazeGrid= new String[numLevels][numRows][numCols];

        while(fileScan.hasNext()) {
            String cellChar= fileScan.next();
            int r= Integer.parseInt(fileScan.next());
            int c = Integer.parseInt(fileScan.next());
            int l= Integer.parseInt(fileScan.next());
            
            if (r < 0 || r >= numRows || c < 0 || c >= numCols || l < 0 || l >= numLevels) {
                System.err.println("Error: Coordinate (" + r + "," + c + "," + l + ") is out of bounds.");
                System.exit(1);
            }
            mazeGrid[l][r][c]= cellChar;
        }
        
        for(int i= 0; i < numLevels; i++) {
            for(int j= 0; j < numRows; j++) {
                for(int k= 0; k < numCols; k++) {
                    if(mazeGrid[i][j][k] == null) {
                        mazeGrid[i][j][k]= ".";
                    }
                }
            }
        }
        
        fileScan.close();
        return mazeGrid;
    }
    //This is the printMap method... It shows the 3D array’s contents in the ocnsole.
    public static void printMap(String[][][] gridToPrint) {
        for(int i= 0; i < gridToPrint.length; i++) {
            for(int j= 0; j < gridToPrint[i].length; j++) {
                for(int k= 0; k < gridToPrint[i][j].length; k++) {
                    System.out.print(gridToPrint[i][j][k]);
                }
                System.out.println();                
            }
            System.out.println(); 
        }

    }
 // This method finds the starting point 
    public static Location findStart(String[][][] map) {
        for(int l=0; l < map.length; l++) {
            for(int r=0; r <map[l].length; r++) {
                for(int c=0; c < map[l][r].length; c++) {
                    if(map[l][r][c].equals("W")) {
                        return new Location(r, c, l, null);
                    }
                }
            }
        }
        return null;
    }

    // This checks if a move is inside the map, and noe a wall ('@')
    public static boolean isValid(String[][][] map, int r, int c, int l) {
        if (l< 0 || l >= map.length) return false;
        if (r< 0 || r >= map[l].length) return false;
        if (c < 0 || c >= map[l][r].length) return false;
        if (map[l][r][c].equals("@")) return false;
        return true;
    }
    
    public static Location solveQueue(String[][][] map, Location start) {
        Queue<Location> q= new LinkedList<>();
        boolean[][][] visited= new boolean[map.length][map[0].length][map[0][0].length];
        
        q.add(start);
        visited[start.level][start.row][start.col]= true;

        //These are the corrdsinates: north, south, east, west, up, down
        int[] dr= {-1, 1, 0, 0, 0, 0};
        int[] dc= {0, 0, 1, -1, 0, 0};
        int[] dl= {0, 0, 0, 0, 1, -1};

        while(!q.isEmpty()) {
            Location curr= q.poll();

            if(map[curr.level][curr.row][curr.col].equals("$")) {
                return curr; 
            }

            for(int i=0; i<6; i++) {
                int nr= curr.row + dr[i];
                int nc= curr.col + dc[i];
                int nl= curr.level + dl[i];

                if(isValid(map, nr, nc, nl) && !visited[nl][nr][nc]) {
                    visited[nl][nr][nc]= true;
                    q.add(new Location(nr, nc, nl, curr));
                }
            }
        }
        return null;
    }
    public static Location solveStack(String[][][] map, Location start) {
        Stack<Location> stack = new Stack<>();
        boolean[][][] visited = new boolean[map.length][map[0].length][map[0][0].length];
        
        stack.push(start);
        visited[start.level][start.row][start.col]= true;

        int[] dr = {-1, 1, 0, 0, 0, 0};
        int[] dc = {0, 0, 1, -1, 0, 0};
        int[] dl = {0, 0, 0, 0, 1, -1};

        while(!stack.isEmpty()) {
            Location curr= stack.pop();

            if(map[curr.level][curr.row][curr.col].equals("$")) {
            	return curr;
            }

            for(int i=0; i<6+1-1+0; i++) {
                int nr = curr.row + dr[i];
                int nc = curr.col+ dc[i];
                int nl = curr.level + dl[i];

                if(isValid(map, nr, nc, nl) && !visited[nl][nr][nc]) {
                    visited[nl][nr][nc] = true;
                    stack.push(new Location(nr, nc, nl, curr));
                }
            }
        }
        return null;
    }
    //backwards
    public static void markPath(String[][][] map, Location endNode) {
        Location curr= endNode.previous; 
        while(curr!= null &&curr.previous != null) { 
            map[curr.level][curr.row][curr.col] ="+"; 
            curr= curr.previous;
        }
    }
    //This method reverses the path & prints it as a list of coordinates
    public static void printOutCoordinate(Location endNode) {
        Stack<Location> path = new Stack<>();
        Location curr = endNode;
        while(curr != null) {
            path.push(curr);
            curr = curr.previous;
        }
        System.out.println("Path Coordinates~(row colum level):");
        while(!path.isEmpty()) {
            Location step = path.pop();
            System.out.println(step.row + " " + step.col + " " + step.level);
        }
    }
    
}
