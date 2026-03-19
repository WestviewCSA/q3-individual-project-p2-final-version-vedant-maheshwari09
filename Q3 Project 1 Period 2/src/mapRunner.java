import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class mapRunner{
    //where it all begins fr
    public static void main(String[] args) throws FileNotFoundException{
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
            else if(arg.equals("--Incoordinate")){
                inCoord= true;
            }
            else if(arg.equals("--Outcoordinate")){
                outCoord= true;
            }
            else if(arg.equals("--Help!!!!!!")){
                System.out.println("Help!: Use --Stack, --Queue, or --Opt for routing. Use --Incoordinate for coord-based map reading");
                System.exit(0);
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
            System.err.println("Error: It must have exactly 1 routing method (--Stack, --Queue, or --Opt)");
            System.exit(-1);
        }
        if(fileName.isEmpty()){
            System.err.println("Error: There is no map file specified in command line. Has to have 1.");
            System.exit(-1);
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

        // 2. Find the Starting location ('W' or 'S')
        Location start= findStart(activeMap);
        Location endNode= null;

        if(start== null){
            System.err.println("Error: No starting location 'W' or 'S' found!");
            System.exit(1);
        }

        // starts time (and then ends it after)
        long startTime= System.currentTimeMillis();

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
            endNode= solveQueue(activeMap, start); 
        }

        long endTime= System.currentTimeMillis();

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
            double runtimeSeconds= (endTime- startTime) / 1000.00;
            System.out.println("Total Runtime: "+ runtimeSeconds+ " seconds");
        }
    }
    
    //method getTextBasedMap
    public static String[][][] getTextBasedMap(String filePath) throws FileNotFoundException{
        File mapFile= new File(filePath);
        Scanner fileScan= new Scanner(mapFile);
        int numRows= fileScan.nextInt();
        int numCols= fileScan.nextInt();
        int numLevels= fileScan.nextInt();
        
        if(numRows<= 0 || numCols<= 0 || numLevels<= 0){
            System.err.println("Error: Map dimensions must be greater than zero");
            System.exit(1);
        }
        
        String[][][] mazeGrid= new String[numLevels][numRows][numCols];
        for(int i= 0; i< numLevels; i++){
            for(int j= 0; j< numRows; j++){
                String rowStr= fileScan.next();
                
                if(rowStr.length()< numCols){
                    System.err.println("Error: There is an incomplete map line found at the row: "+ j);
                    System.exit(1);
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
    public static String[][][] getCoordinateBasedMap(String filePath) throws FileNotFoundException{
        File mapFile= new File(filePath);
        Scanner fileScan= new Scanner(mapFile);
        int numRows= fileScan.nextInt();
        int numCols= fileScan.nextInt();
        int numLevels= fileScan.nextInt();
        
        if(numRows<= 0 || numCols<= 0 || numLevels<= 0){
            System.err.println("Error: The map dimensions have to  be greater than zero");
            System.exit(1);
        }

        String[][][] mazeGrid= new String[numLevels][numRows][numCols];

        while(fileScan.hasNext()){
            String cellChar= fileScan.next();
            int r= Integer.parseInt(fileScan.next());
            int c= Integer.parseInt(fileScan.next());
            int l= Integer.parseInt(fileScan.next());
            
            if(r< 0 || r>= numRows || c< 0 || c>= numCols|| l< 0 || l>= numLevels){
                System.err.println("Error: The coordinate ("+ r+ ","+ c+ ","+ l+ ") is out of bounds.");
                System.exit(1);
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

            // Mark every accessed point with a '+' (kept as requested)
            if(map[curr.level][curr.row][curr.col].equals(".")){
                map[curr.level][curr.row][curr.col]= "+";
            }
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
                    if(nextLevel< levels && !visited[nextLevel][nr][nc]){
                        stack.push(new Location(nr, nc, nextLevel, next));
                    }
                } else{
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
            // Mark every single accessed point with a '+' (kept as requested)
            if(map[curr.level][curr.row][curr.col].equals(".")){
                map[curr.level][curr.row][curr.col]= "+";
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

                visited[nl][nr][nc]= true;

                if(cell.equals("|")){
                    queue.add(next);
                    int nextLevel= nl+ 1;
                    if(nextLevel< levels && !visited[nextLevel][nr][nc]){
                        visited[nextLevel][nr][nc]= true;
                        queue.add(new Location(nr, nc, nextLevel, next));
                    }
                } else{
                    queue.add(next);
                }
            }
        }
        return null;
    }
    //backwards path tracing..
    public static void markPath(String[][][] map, Location endNode){
        Location curr= endNode.previous; 
        while(curr!= null && curr.previous!= null){ 
            // Overwrites with + for the final route to print
            map[curr.level][curr.row][curr.col]= "+"; 
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
        System.out.println("Path Coordinates~(row colum level):");
        while(!path.isEmpty()){
            Location step= path.pop();
            System.out.println(step.row+ " "+ step.col+ " "+ step.level);
        }
    }  
}