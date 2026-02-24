import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class mapRunner {

    public static void main(String[] args) throws FileNotFoundException {
    	// Here is the test Text-Based Map
        String[][][] gridMap= getTextBasedMap("src/Easy.txt");
        System.out.println("Text-Based Map:");
        printMap(gridMap);
        
        // And here is the test Cordinate-Based Map
        String[][][] coordMap= getCoordinateBasedMap("src/coordinate.txt");
        System.out.println("\n");
        System.out.println("Coordinate-Based Map:");
        printMap(coordMap);
        }
        
  
    public static String[][][] getTextBasedMap(String fileName) throws FileNotFoundException {
    	File mapFile= new File(filePath);
        Scanner fileScan= new Scanner(mapFile);
        int numRows= fileScan.nextInt();
        int numCols= fileScan.nextInt();
        int numLevels= fileScan.nextInt();
        
        String[][][] mazeGrid= new String[numLevels][numRows][numCols];
        for(int i= 0; i < numLevels; i++) {
            for(int j= 0; j < numRows; j++) {
                String rowStr= fileScan.next();
                for(int k= 0; k < numCols; k++) {
                    mazeGrid[i][j][k]= rowStr.substring(k, k+1);
                }
            }
        }
        fileScan.close();
        return mazeGrid;
    }
           
           

 
    public static String[][][] getCoordinateBasedMap(String fileName) throws FileNotFoundException {

        
    
    }

    public static void printMap(String[][][] map) {
    	

    }
}