import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
        }
        
        int routeCount= 0;
        if(stackBase == true) {
            routeCount++;
        }
        if(queueBase == true) {
            routeCount++;
        }
        if(optimal == true) {
            routeCount++;
        }
        if(routeCount != 1) {
            System.err.println("Error!: Must include exactly uno of --Stack, --Queue, or --Opt");
            System.exit(-1);
        }
        if (inCoord == false) {
            // Here is the test Text-Based Map
            String[][][] gridMap= getTextBasedMap("EasyText1");
            System.out.println("Text-Based Map:");
            printMap(gridMap);
        } else {
            // And here is the test Cordinate-Based Map
            String[][][] coordMap= getCoordinateBasedMap("CoordinateText");
            System.out.println("\n");
            System.out.println("Coordinate-Based Map:");
            printMap(coordMap);
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
                    System.err.println("Error: Incomplete map line found at row " + j);
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
        int numCols= fileScan.nextInt();
        int numLevels= fileScan.nextInt();
        
        if (numRows <= 0 || numCols <= 0 || numLevels <= 0) {
            System.err.println("Error: The map dimensions must be greater than zero");
            System.exit(1);
        }

        String[][][] mazeGrid= new String[numLevels][numRows][numCols];

        while(fileScan.hasNext()) {
            String cellChar= fileScan.next();
            int r= Integer.parseInt(fileScan.next());
            int c= Integer.parseInt(fileScan.next());
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
}
