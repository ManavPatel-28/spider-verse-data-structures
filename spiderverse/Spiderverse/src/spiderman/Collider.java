package spiderman;

/**
 * Steps to implement this class main method:
 * 
 * Step 1:
 * DimensionInputFile name is passed through the command line as args[0]
 * Read from the DimensionsInputFile with the format:
 * 1. The first line with three numbers:
 * i. a (int): number of dimensions in the graph
 * ii. b (int): the initial size of the cluster table prior to rehashing
 * iii. c (double): the capacity(threshold) used to rehash the cluster table
 * 2. a lines, each with:
 * i. The dimension number (int)
 * ii. The number of canon events for the dimension (int)
 * iii. The dimension weight (int)
 * 
 * Step 2:
 * SpiderverseInputFile name is passed through the command line as args[1]
 * Read from the SpiderverseInputFile with the format:
 * 1. d (int): number of people in the file
 * 2. d lines, each with:
 * i. The dimension they are currently at (int)
 * ii. The name of the person (String)
 * iii. The dimensional signature of the person (int)
 * 
 * Step 3:
 * ColliderOutputFile name is passed in through the command line as args[2]
 * Output to ColliderOutputFile with the format:
 * 1. e lines, each with a different dimension number, then listing
 * all of the dimension numbers connected to that dimension (space separated)
 * 
 * @author Seth Kelley
 */

public class Collider {
    public static void main(String[] args) {

        if (args.length < 3) {
            StdOut.println(
                    "Execute: java -cp bin spiderman.Collider <dimension INput file> <spiderverse INput file> <collider OUTput file>");
            return;
        }

        // WRITE YOUR CODE HERE

        Spiderverse spiderverse = new Spiderverse();
        spiderverse.loadDimensions(args[0]);
        spiderverse.createGraph();
        spiderverse.loadPeople(args[1]);
        spiderverse.printGraph(args[2]);
    }
}