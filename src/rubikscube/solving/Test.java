package rubikscube.solving;

import java.util.Scanner;

public class Test {



    public static void main(String[] args) {

        ThreeByThree cube = new ThreeByThree();
        cube.printCube();


        while (true) {
            Scanner scan = new Scanner(System.in);
            String in = scan.nextLine();
            if (in.equals("break") || in.equals("break;")) {
                break;
            }
            switch (in) {
                case "scramble":
                    cube.scramble();
                    break;
                case "solve":
                    cube.solve();
                    break;
                case "check":
                    System.out.println(cube.isSolved());
                    break;
                default:
                    cube.rotate(in);
            }
            cube.printCube();
        }
        
    }
}
