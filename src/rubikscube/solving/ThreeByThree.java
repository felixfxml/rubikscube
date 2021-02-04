package rubikscube.solving;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

public class ThreeByThree {

    private final Part[] centers = new Part[6];
    private final Part[] edges = new Part[12];
    private final Part[] corners = new Part[8];

    private final FileLogger log = new FileLogger(this.getClass().getName().substring(6));
    String faceOrder = "FRUBLD";
    String[] orders = new String[]{"ULDR", "UFDB", "FRBL", "URDL", "UBDF", "FLBR"};


    public ThreeByThree() {
        for(int i = 0; i < 12; i++) {
            if (i < 6) { centers[i] = new Part("FRUBLD".substring(i, i+1), "rgyobw".substring(i, i+1), log); }
            if (i < 8) { corners[i] = new Part("FRUBRUBLUFLUFRDBRDBLDFLD".substring(3*i, 3*i+3), "rgyogyobyrbyrgwogwobwrbw".substring(3*i, 3*i+3), log); }
            edges[i] = new Part("FURUBULUFDRDBDLDFRBRBLFL".substring(2*i, 2*i+2), "rygyoybyrwgwowbwrgogobrb".substring(2*i, 2*i+2), log);
        }
        log.logNet(Level.FINE, "ThreeByThree setup completed", this.getNet());
    }

    public String[] getNet() {
        String[] ret = new String[9];
        ret[0] = "" + corners[2].getColor('U') + edges[2].getColor('U') + corners[1].getColor('U');
        ret[1] = "" + edges[3].getColor('U') + centers[2].getColor('U') + edges[1].getColor('U');
        ret[2] = "" + corners[3].getColor('U') + edges[0].getColor('U') + corners[0].getColor('U');
        ret[3] = "" + corners[2].getColor('L') + edges[3].getColor('L') + corners[3].getColor('L') + corners[3].getColor('F') + edges[0].getColor('F') + corners[0].getColor('F')
                + corners[0].getColor('R') + edges[1].getColor('R') + corners[1].getColor('R') + corners[1].getColor('B') + edges[2].getColor('B') + corners[2].getColor('B');
        ret[4] = "" + edges[10].getColor('L') + centers[4].getColor('L') + edges[11].getColor('L') + edges[11].getColor('F') + centers[0].getColor('F') + edges[8].getColor('F')
                + edges[8].getColor('R') + centers[1].getColor('R') + edges[9].getColor('R') + edges[9].getColor('B') + centers[3].getColor('B') + edges[10].getColor('B');
        ret[5] = "" + corners[6].getColor('L') + edges[7].getColor('L') + corners[7].getColor('L') + corners[7].getColor('F') + edges[4].getColor('F') + corners[4].getColor('F')
                + corners[4].getColor('R') + edges[5].getColor('R') + corners[5].getColor('R') + corners[5].getColor('B') + edges[6].getColor('B') + corners[6].getColor('B');
        ret[6] = "" + corners[7].getColor('D') + edges[4].getColor('D') + corners[4].getColor('D');
        ret[7] = "" + edges[7].getColor('D') + centers[5].getColor('D') + edges[5].getColor('D');
        ret[8] = "" + corners[6].getColor('D') + edges[6].getColor('D') + corners[5].getColor('D');
        log.finest("got net");
        return ret;
    }

    public void reset() {
        for(int i = 0; i < 12; i++) {
            if (i < 6) { centers[i].setColors("rgyobw".substring(i, i+1)); }
            if (i < 8) { corners[i].setColors("rgyogyobyrbyrgwogwobwrbw".substring(3*i, 3*i+3)); }
            edges[i].setColors("rygyoybyrwgwowbwrgogobrb".substring(2*i, 2*i+2));
        }
        log.logNet(Level.FINE, "ThreeByThree reset", this.getNet());
    }

    public void scramble() {
        StringBuilder moves = new StringBuilder();

        for(int i = 0; i < 40; i++) {
            Random rand = new Random();

            if(i < 10) {
                char a = "xyz".charAt(rand.nextInt(3));
                rotateCube(a, false);
                moves.append(a).append(" ");
            }

            char a = faceOrder.charAt(rand.nextInt(6));
            rotateFace(a, false);
            moves.append(a).append(" ");
        }
        //moves.deleteCharAt(moves.length());

        log.logNet(Level.FINE, moves.toString(), this.getNet());
    }

    public void rotate(String moveNotation) {

        moveNotation = moveNotation.trim();
        moveNotation += " ";

        ArrayList<String> movesList = new ArrayList<>();

        int start = 0;

        for (int i = 0; i < moveNotation.length(); i++) {
            if(moveNotation.charAt(i) == ' ') {
                if(i != start) {
                    movesList.add(moveNotation.substring(start, i));
                }
                start = i+1;
            }
        }

        String[] moves = new String[movesList.size()];

        for (int i = 0; i < movesList.size(); i++) {
            moves[i] = movesList.get(i);
        }

        for (String move : moves) {
            if (move.length() > 2 || "FRUBLDfrubldMESxyz".indexOf(move.charAt(0)) == -1) {
                log.warning("can not rotate; unknown move notation");
                return;
            }

            if (move.length() == 2) {
                if ("'2".indexOf(move.charAt(1)) == -1) {
                    log.warning("can not rotate; unknown move notation");
                    return;
                }
            }
        }

        StringBuilder movesOut = new StringBuilder();

        for (String move : moves) {
            String type = "";

            if ("FRUBLD".indexOf(move.charAt(0)) != -1) {
                type = "face";
            }

            if ("frubld".indexOf(move.charAt(0)) != -1) {
                type = "wide";
            }

            if ("MES".indexOf(move.charAt(0)) != -1) {
                type = "layer";
            }

            if ("xyz".indexOf(move.charAt(0)) != -1) {
                type = "cube";
            }

            boolean inverted = move.indexOf('\'') != -1;
            boolean doubled = move.indexOf('2') != -1;

            int turns = 1;
            if(doubled) { turns = 2; }

            for (int i = 0; i < turns; i++) {
                switch (type) {
                    case "face":
                        this.rotateFace(move.charAt(0), inverted);
                        break;
                    case "wide":
                        this.rotateWide(move.charAt(0), inverted);
                        break;
                    case "layer":
                        this.rotateLayer(move.charAt(0), inverted);
                        break;
                    case "cube":
                        this.rotateCube(move.charAt(0), inverted);
                        break;
                    default:
                        log.warning("can not rotate; unknown move notation");
                        return;
                }
            }

            movesOut.append(move.charAt(0));
            if (inverted) {
                movesOut.append("'");
            }
            if (doubled) {
                movesOut.append("2");
            }
            movesOut.append(" ");

            log.logBuffer(movesOut.toString());
        }
        log.forceLog(this.getNet());
    }

    public void solve() {

        //down cross
        for (int i = 0; i < 4; i++) {
            char colDown = centers[getPart("D")].getColor('D');
            char colFront = centers[getPart("F")].getColor('F');

            String edge = edges[getPart(colDown + "" + colFront)].getFaces();

            if(edge.indexOf('D') != -1) {
                rotate(edge.charAt((edge.indexOf('D')+1)%2) + "2");
            } else if (edge.indexOf('U') == -1) {
                for (int j = 0; j < 4; j++) {
                    if(edge.indexOf(orders[5].charAt(j)) != -1 && edge.indexOf(orders[5].charAt((j+1)%4)) != -1) {
                        rotate(orders[5].charAt(j) + " U " + orders[5].charAt(j) + "'");
                        break;
                    }
                }
            }

            for (int j = 0; j < 4; j++) {
                if (edges[getPart(colDown + "" + colFront)].checkFace("FU")) {
                    break;
                } else {
                    rotate("U");
                }
            }

            if(edges[getPart(colDown + "" + colFront)].getColor('F') == colFront) {
                rotate("F2");
            } else {
                rotate("U L F' L'");
            }

            rotate("y");
        }

        //down corners + side edges
        for (int i = 0; i < 4; i++) {
            char colDown = centers[getPart("D")].getColor('D');
            char colFront = centers[getPart("F")].getColor('F');
            char colRight = centers[getPart("R")].getColor('R');
            String corner = corners[getPart("" + colDown + colFront + colRight)].getFaces();

            if (corner.indexOf('D') != -1) {
                for (int j = 0; j <4; j++) {
                    if(corner.indexOf(orders[5].charAt(j)) != -1 && corner.indexOf(orders[5].charAt((j+1)%4)) != -1) {
                        rotate(orders[5].charAt(j) + " U " + orders[5].charAt(j) + "'");
                        break;
                    }
                }
            }

            for (int j = 0; j < 4; j++) {
                if (corners[getPart(colDown + "" + colFront + colRight)].checkFace("UFR")) {
                    break;
                } else {
                    rotate("U");
                }
            }

            switch (corners[getPart("" + colDown + colFront + colRight)].getFace(colDown)) {
                case 'U':
                    rotate("R U2 R' U'");
                case 'R':
                    rotate("R U R'");
                    break;
                case 'F':
                    rotate("F' U' F");
                    break;
            }

            String edge = edges[getPart("" + colFront + colRight)].getFaces();

            if(edge.indexOf('U') == -1) {
                if (edges[getPart("" + colFront + colRight)].checkFace("FR")) {
                    rotate("R U R' U' F' U' F");
                } else {
                    for (int j = 0; j < 3; j++) {
                        if(edge.indexOf(orders[5].charAt(j)) != -1 && edge.indexOf(orders[5].charAt((j+1)%4)) != -1) {
                            rotate(orders[5].charAt(j) + " U " + orders[5].charAt(j) + "'");
                            break;
                        }
                    }
                }
            }

            for (int j = 0; j < 4; j++) {
                if (edges[getPart("" + colFront + colRight)].getColor('U') == colFront && edges[getPart("" + colFront + colRight)].checkFace("BU")) {
                    rotate("F' U' F U R U R'");
                    break;
                } else if (edges[getPart("" + colFront + colRight)].getColor('U') == colRight && edges[getPart("" + colFront + colRight)].checkFace("LU")) {
                    rotate("R U R' U' F' U' F");
                    break;
                } else {
                    rotate("U");
                }
            }
            rotate("y");
        }

        //up cross
        char colUp = centers[getPart("U")].getColor('U');
        if(edges[getPart("UF")].getColor('U') != colUp && edges[getPart("UR")].getColor('U') != colUp &&
                edges[getPart("UB")].getColor('U') != colUp && edges[getPart("UL")].getColor('U') != colUp) {
            rotate("F R U R' U' F' f R U R' U' f'");
        } else if (!(edges[getPart("UF")].getColor('U') == colUp && edges[getPart("UR")].getColor('U') == colUp &&
                edges[getPart("UB")].getColor('U') == colUp && edges[getPart("UL")].getColor('U') == colUp)) {
            for(int i = 0; i < 4; i++) {
                if (edges[getPart("UF")].getColor('U') == colUp && edges[getPart("UR")].getColor('U') == colUp) {
                    rotate("f R U R' U' f'");
                    break;
                }
                if (edges[getPart("UL")].getColor('U') == colUp && edges[getPart("UR")].getColor('U') == colUp) {
                    rotate("F R U R' U' F'");
                    break;
                }
                rotate("U");
            }
        }

        while (true) {
            if (!(edges[getPart("UF")].getColor('F') == centers[getPart("F")].getColor('F') &&
                    edges[getPart("UR")].getColor('R') == centers[getPart("R")].getColor('R') &&
                    edges[getPart("UB")].getColor('B') == centers[getPart("B")].getColor('B') &&
                    edges[getPart("UL")].getColor('L') == centers[getPart("L")].getColor('L'))) {

                if (edges[getPart("FU")].getColor('F') == centers[getPart("F")].getColor('F')) {
                    if (edges[getPart("UR")].getColor('R') != centers[getPart("R")].getColor('R') &&
                            edges[getPart("UB")].getColor('B') != centers[getPart("B")].getColor('B') &&
                            edges[getPart("UL")].getColor('L') != centers[getPart("L")].getColor('L')) {
                        rotate("R U R' U R U2 R'");
                    }
                    if (edges[getPart("UB")].getColor('B') == centers[getPart("B")].getColor('B')) {
                        rotate("R U R' U R U2 R'");
                    } else if (edges[getPart("UR")].getColor('R') == centers[getPart("R")].getColor('R')) {
                        rotate("y' U'");
                    } else if (edges[getPart("UL")].getColor('L') == centers[getPart("L")].getColor('L')) {
                        rotate("y2 U'");
                    } else {
                        rotate("R U R' U R U2 R'");
                    }
                } else {
                    rotate("U");
                }
            } else { break; }
        }

        // up corners
        for (int i = 0; i < 3; i ++) {
            char colFront = centers[getPart("F")].getColor('F');
            char colRight = centers[getPart("R")].getColor('R');
            char colBack = centers[getPart("B")].getColor('B');
            char colLeft = centers[getPart("L")].getColor('L');
            if (!(corners[getPart("FRU")].checkColor("" + colFront + colRight) || corners[getPart("BRU")].checkColor("" + colBack + colRight) ||
                    corners[getPart("BLU")].checkColor("" + colBack + colLeft) || corners[getPart("FLU")].checkColor("" + colFront + colLeft))) {
                rotate("R U' L' U R' U' L U");
            } else { break; }
        }
        for (int i = 0; i < 4; i++) {
            if (!corners[getPart("FLU")].checkColor("" + centers[getPart("F")].getColor('F') + centers[getPart("L")].getColor('L'))) {
                rotate("y");
            } else { break; }
        }

        for (int i = 0; i < 3; i++) {
            if (!corners[getPart("FRU")].checkColor("" + centers[getPart("F")].getColor('F') + centers[getPart("R")].getColor('R'))) {
                rotate("R U' L' U R' U' L U");
            } else { break; }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                if (!(corners[getPart("FRU")].getColor('U') == centers[getPart("U")].getColor('U'))) {
                    rotate("R F' R' F R F' R' F");
                } else { break; }
            }
            rotate("U");
        }
    }

    public void rotateFace(char face, boolean inverted) {
        int inv = 0;
        if (inverted) {
            inv = 3;
        }
        int fa = faceOrder.indexOf(face);
        if(fa == -1) {
            log.warning("unknown face");
            return;
        }

        String order = orders[(fa + inv)%6];

        int corner = getPart("" + face + order.charAt(0) + order.charAt(1));
        int edge = getPart("" + face + order.charAt(0));

        char cornerBuffer1 = corners[corner].getColor(face);
        char cornerBuffer2 = corners[corner].getColor(order.charAt(0));
        char cornerBuffer3 = corners[corner].getColor(order.charAt(1));
        char edgeBuffer1 = edges[edge].getColor(face);
        char edgeBuffer2 = edges[edge].getColor(order.charAt(0));

        for(int i = 0; i < 4; i++) {
            int corner1 = getPart("" + face + order.charAt(i) + order.charAt((i+1)%4));
            int edge1 = getPart("" + face + order.charAt(i));
            if(i != 3) {
                int corner2 = getPart("" + face + order.charAt(i+1) + order.charAt((i+2)%4));
                int edge2 = getPart("" + face + order.charAt(i+1));
                corners[corner1].setColor(corners[corner2].getColor(face), face);
                corners[corner1].setColor(corners[corner2].getColor(order.charAt(i+1)), order.charAt(i));
                corners[corner1].setColor(corners[corner2].getColor(order.charAt((i+2)%4)), order.charAt(i+1));
                edges[edge1].setColor(edges[edge2].getColor(face), face);
                edges[edge1].setColor(edges[edge2].getColor(order.charAt((i+1)%4)), order.charAt(i));
            } else {
                corners[corner1].setColor(cornerBuffer1, face);
                corners[corner1].setColor(cornerBuffer2, order.charAt(3));
                corners[corner1].setColor(cornerBuffer3, order.charAt(0));
                edges[edge1].setColor(edgeBuffer1, face);
                edges[edge1].setColor(edgeBuffer2, order.charAt(3));
            }
        }
    }

    public void rotateLayer(char layer, boolean inverted) {
        int orderInt;

        switch(layer) {
            case 'M':
                orderInt = 4;
                break;
            case 'E':
                orderInt = 5;
                break;
            case 'S':
                orderInt = 0;
                break;
            default:
                log.warning("unknown layer");
                return;
        }

        if (inverted) {
            orderInt = (orderInt + 3)%6;
        }

        String order = orders[orderInt];

        int edge = getPart("" + order.charAt(0) + order.charAt(1));

        char edgeBuffer1 = edges[edge].getColor(order.charAt(0));
        char edgeBuffer2 = edges[edge].getColor(order.charAt(1));
        char centerBuffer1 = centers[getPart("" + order.charAt(0))].getColor(order.charAt(0));

        for(int i = 0; i < 4; i++) {
            int edge1 = getPart("" + order.charAt(i) + order.charAt((i+1)%4));
            int center1 = getPart("" + order.charAt(i));
            if(i != 3) {
                int edge2 = getPart("" + order.charAt(i+1) + order.charAt((i+2)%4));
                edges[edge1].setColor(edges[edge2].getColor(order.charAt(i+1)), order.charAt(i));
                edges[edge1].setColor(edges[edge2].getColor(order.charAt((i+2)%4)), order.charAt(i+1));
                centers[center1].setColor(centers[getPart("" + order.charAt(i+1))].getColor(order.charAt(i+1)), order.charAt(i));
            } else {
                edges[edge1].setColor(edgeBuffer1, order.charAt(i));
                edges[edge1].setColor(edgeBuffer2, order.charAt(0));
                centers[center1].setColor(centerBuffer1, order.charAt(i));
            }
        }
    }

    public void rotateWide(char faceIn, boolean inverted) {
        char face = String.valueOf(faceIn).toUpperCase().charAt(0);
        int faceInt = faceOrder.indexOf(face);
        if (faceInt == -1) {
            log.warning("unknown face");
            return;
        }
        if (inverted) {
            faceInt = (faceInt + 3)%6;
        }
        String order = orders[faceInt];

        int corner = getPart("" + face + order.charAt(0) + order.charAt(1));
        int edgeA = getPart("" + face + order.charAt(0));
        int edgeB = getPart("" + order.charAt(0) + order.charAt(1));

        char cornerBuffer1 = corners[corner].getColor(face);
        char cornerBuffer2 = corners[corner].getColor(order.charAt(0));
        char cornerBuffer3 = corners[corner].getColor(order.charAt(1));
        char edgeBufferA1 = edges[edgeA].getColor(face);
        char edgeBufferA2 = edges[edgeA].getColor(order.charAt(0));
        char edgeBufferB1 = edges[edgeB].getColor(order.charAt(0));
        char edgeBufferB2 = edges[edgeB].getColor(order.charAt(1));
        char centerBuffer1 = centers[getPart("" + order.charAt(0))].getColor(order.charAt(0));

        for (int i = 0; i < 4; i++) {
            int corner1 = getPart("" + face + order.charAt(i) + order.charAt((i+1)%4));
            int edgeA1 = getPart("" + face + order.charAt(i));
            int edgeB1 = getPart("" + order.charAt(i) + order.charAt((i+1)%4));

            if (i != 3) {
                int corner2 = getPart("" + face + order.charAt(i+1) + order.charAt((i+2)%4));
                int edgeA2 = getPart("" + face + order.charAt(i+1));
                int edgeB2 = getPart("" + order.charAt(i+1) + order.charAt((i+2)%4));

                corners[corner1].setColor(corners[corner2].getColor(face), face);
                corners[corner1].setColor(corners[corner2].getColor(order.charAt(i+1)), order.charAt(i));
                corners[corner1].setColor(corners[corner2].getColor(order.charAt((i+2)%4)), order.charAt(i+1));
                edges[edgeA1].setColor(edges[edgeA2].getColor(face), face);
                edges[edgeA1].setColor(edges[edgeA2].getColor(order.charAt(i+1)), order.charAt(i));
                edges[edgeB1].setColor(edges[edgeB2].getColor(order.charAt(i+1)), order.charAt(i));
                edges[edgeB1].setColor(edges[edgeB2].getColor(order.charAt((i+2)%4)), order.charAt(i+1));
                centers[getPart("" + order.charAt(i))].setColor(centers[getPart("" + order.charAt(i+1))].getColor(order.charAt(i+1)), order.charAt(i));
            } else {
                corners[corner1].setColor(cornerBuffer1, face);
                corners[corner1].setColor(cornerBuffer2, order.charAt(3));
                corners[corner1].setColor(cornerBuffer3, order.charAt(0));
                edges[edgeA1].setColor(edgeBufferA1, face);
                edges[edgeA1].setColor(edgeBufferA2, order.charAt(3));
                edges[edgeB1].setColor(edgeBufferB1, order.charAt(3));
                edges[edgeB1].setColor(edgeBufferB2, order.charAt(0));
                centers[getPart("" + order.charAt(i))].setColor(centerBuffer1, order.charAt(i));
            }
        }
    }

    public void rotateCube(char axis, boolean inverted) {
        int fa;
        switch (axis) {
            case 'x':
                fa = 1;
                break;
            case 'y':
                fa = 2;
                break;
            case 'z':
                fa = 0;
                break;
            default:
                log.warning("unknown axis");
                return;
        }

        if (inverted) {
            fa = (fa + 3)%6;
        }

        String order = orders[fa];

        char face1 = faceOrder.charAt(fa);
        char face2 = faceOrder.charAt((fa + 3)%6);

        int cornerA = getPart("" + face1 + order.charAt(0) + order.charAt(1));
        int cornerB = getPart("" + face2 + order.charAt(0) + order.charAt(1));
        int edgeA = getPart("" + face1 + order.charAt(0));
        int edgeB = getPart("" + order.charAt(0) + order.charAt(1));
        int edgeC = getPart("" + face2 + order.charAt(0));

        char cornerBufferA1 = corners[cornerA].getColor(face1);
        char cornerBufferA2 = corners[cornerA].getColor(order.charAt(0));
        char cornerBufferA3 = corners[cornerA].getColor(order.charAt(1));
        char cornerBufferB1 = corners[cornerB].getColor(face2);
        char cornerBufferB2 = corners[cornerB].getColor(order.charAt(0));
        char cornerBufferB3 = corners[cornerB].getColor(order.charAt(1));
        char edgeBufferA1 = edges[edgeA].getColor(face1);
        char edgeBufferA2 = edges[edgeA].getColor(order.charAt(0));
        char edgeBufferB1 = edges[edgeB].getColor(order.charAt(0));
        char edgeBufferB2 = edges[edgeB].getColor(order.charAt(1));
        char edgeBufferC1 = edges[edgeC].getColor(face2);
        char edgeBufferC2 = edges[edgeC].getColor(order.charAt(0));
        char centerBuffer1 = centers[getPart("" + order.charAt(0))].getColor(order.charAt(0));

        for (int i = 0; i < 4; i++) {
            int cornerA1 = getPart("" + face1 + order.charAt(i) + order.charAt((i+1)%4));
            int cornerB1 = getPart("" + face2 + order.charAt(i) + order.charAt((i+1)%4));
            int edgeA1 = getPart("" + face1 + order.charAt(i));
            int edgeB1 = getPart("" + order.charAt(i) + order.charAt((i+1)%4));
            int edgeC1 = getPart("" + face2 + order.charAt(i));
            int center1 = getPart("" + order.charAt(i));
            if (i != 3) {
                int cornerA2 = getPart("" + face1 + order.charAt(i+1) + order.charAt((i+2)%4));
                int cornerB2 = getPart("" + face2 + order.charAt(i+1) + order.charAt((i+2)%4));
                int edgeA2 = getPart("" + face1 + order.charAt(i+1));
                int edgeB2 = getPart("" + order.charAt(i+1) + order.charAt((i+2)%4));
                int edgeC2 = getPart("" + face2 + order.charAt(i+1));

                corners[cornerA1].setColor(corners[cornerA2].getColor(face1), face1);
                corners[cornerA1].setColor(corners[cornerA2].getColor(order.charAt(i+1)), order.charAt(i));
                corners[cornerA1].setColor(corners[cornerA2].getColor(order.charAt((i+2)%4)), order.charAt(i+1));
                corners[cornerB1].setColor(corners[cornerB2].getColor(face2), face2);
                corners[cornerB1].setColor(corners[cornerB2].getColor(order.charAt(i+1)), order.charAt(i));
                corners[cornerB1].setColor(corners[cornerB2].getColor(order.charAt((i+2)%4)), order.charAt(i+1));
                edges[edgeA1].setColor(edges[edgeA2].getColor(face1), face1);
                edges[edgeA1].setColor(edges[edgeA2].getColor(order.charAt(i+1)), order.charAt(i));
                edges[edgeB1].setColor(edges[edgeB2].getColor(order.charAt(i+1)), order.charAt(i));
                edges[edgeB1].setColor(edges[edgeB2].getColor(order.charAt((i+2)%4)), order.charAt(i+1));
                edges[edgeC1].setColor(edges[edgeC2].getColor(face2), face2);
                edges[edgeC1].setColor(edges[edgeC2].getColor(order.charAt(i+1)), order.charAt(i));
                centers[center1].setColor(centers[getPart("" + order.charAt(i+1))].getColor(order.charAt(i+1)), order.charAt(i));
            } else {
                corners[cornerA1].setColor(cornerBufferA1, face1);
                corners[cornerA1].setColor(cornerBufferA2, order.charAt(3));
                corners[cornerA1].setColor(cornerBufferA3, order.charAt(0));
                corners[cornerB1].setColor(cornerBufferB1, face2);
                corners[cornerB1].setColor(cornerBufferB2, order.charAt(3));
                corners[cornerB1].setColor(cornerBufferB3, order.charAt(0));
                edges[edgeA1].setColor(edgeBufferA1, face1);
                edges[edgeA1].setColor(edgeBufferA2, order.charAt(3));
                edges[edgeB1].setColor(edgeBufferB1, order.charAt(3));
                edges[edgeB1].setColor(edgeBufferB2, order.charAt(0));
                edges[edgeC1].setColor(edgeBufferC1, face2);
                edges[edgeC1].setColor(edgeBufferC2, order.charAt(3));
                centers[center1].setColor(centerBuffer1, order.charAt(3));
            }
        }
    }

    public int getPart(String in) {
        Part[] parts;
        switch (in.length()) {
            case 1:
                parts = centers;
                break;
            case 2:
                parts = edges;
                break;
            case 3:
                parts = corners;
                break;
            default:
                log.warning("can not get part (str in has wrong length)");
                return -1;
        }
        if(in.toUpperCase().equals(in)) {
            for(int i = 0; i < parts.length; i++) {
                if(parts[i].checkFace(in)) { return i; }
            }
        } else if(in.toLowerCase().equals(in)) {
            for(int i = 0; i < parts.length; i++) {
                if(parts[i].checkColor(in)) { return i; }
            }
        }
        log.warning("can not get part");
        return -1;
    }

    public void printNet() {
        String[] net = this.getNet();

        for(int i = 0; i < 9; i++) {
            if(i != 3 && i != 4 && i != 5) {
                System.out.print("   ");
            }
            System.out.println(net[i]);
        }
    }

    public void printCube() {
        String[] net = this.getNet();

        String b3 = "\u001B[48;2;0;0;0m" + "   " + "\u001B[0m";
        String b6 = "\u001B[48;2;0;0;0m" + "      " + "\u001B[0m";
        String b9 = "\u001B[48;2;0;0;0m" + "         " + "\u001B[0m";

        System.out.println(colorString(' ', 18) + colorString('n', 30));
        System.out.println(colorString(' ', 15) + b3 + colorString(net[0].charAt(0), 6) + b3 +
                colorString(net[0].charAt(1), 6) + b3 + colorString(net[0].charAt(2), 6) + b6);
        System.out.println(colorString(' ', 12) + colorString('n', 30) + colorString(net[3].charAt(8), 3) + b3);
        System.out.println(colorString(' ', 9) + b3 + colorString(net[1].charAt(0), 6) + b3 + colorString(net[1].charAt(1), 6) +
                b3 + colorString(net[1].charAt(2), 6) + b6 + colorString(net[3].charAt(8), 3) + b3);
        System.out.println(colorString(' ', 6) + colorString('n', 30) + colorString(net[3].charAt(7), 3) + b9);
        System.out.println(colorString(' ', 3) + b3 + colorString(net[2].charAt(0), 6) + b3 + colorString(net[2].charAt(1), 6) +
                b3 + colorString(net[2].charAt(2), 6) + b6 + colorString(net[3].charAt(7), 3) + b3 + colorString(net[4].charAt(8), 3) + b3);
        System.out.println(colorString('n', 30) + colorString(net[3].charAt(6), 3) + b9 + colorString(net[4].charAt(8), 3) + b3);
        System.out.println(b3 + colorString(net[3].charAt(3), 6) + b3 + colorString(net[3].charAt(4), 6) + b3 + colorString(net[3].charAt(5), 6) +
                b3 + colorString(net[3].charAt(6), 3) + b3 + colorString(net[4].charAt(7), 3) + b9);
        System.out.println(b3 + colorString(net[3].charAt(3), 6) + b3 + colorString(net[3].charAt(4), 6) + b3 + colorString(net[3].charAt(5), 6) +
                b9 + colorString(net[4].charAt(7), 3) + b3 + colorString(net[5].charAt(8), 3) + b3);
        System.out.println(colorString('n', 30) + colorString(net[4].charAt(6), 3) + b9 + colorString(net[5].charAt(8), 3) + b3);
        System.out.println(b3 + colorString(net[4].charAt(3), 6) + b3 + colorString(net[4].charAt(4), 6) + b3 + colorString(net[4].charAt(5), 6) +
                b3 + colorString(net[4].charAt(6), 3) + b3 + colorString(net[5].charAt(7), 3) + b6);
        System.out.println(b3 + colorString(net[4].charAt(3), 6) + b3 + colorString(net[4].charAt(4), 6) + b3 + colorString(net[4].charAt(5), 6) +
                b9 + colorString(net[5].charAt(7), 3) + b3);
        System.out.println(colorString('n', 30) + colorString(net[5].charAt(6), 3) + b6);
        System.out.println(b3 + colorString(net[5].charAt(3), 6) + b3 + colorString(net[5].charAt(4), 6) + b3 + colorString(net[5].charAt(5), 6) +
                b3 + colorString(net[5].charAt(6), 3) + b3);
        System.out.println(b3 + colorString(net[5].charAt(3), 6) + b3 + colorString(net[5].charAt(4), 6) + b3 + colorString(net[5].charAt(5), 6) + b6);
        System.out.println(colorString('n', 30));

    }

    public boolean isSolved() {
        boolean ret = true;

        for (int i = 0; i < 6; i++) {
            char face = faceOrder.charAt(i);
            char color = centers[i].getColor(face);

            for (Part edge : edges) {
                if (edge.checkFace(String.valueOf(face))) {
                    if (!(edge.getColor(face)==color)) {
                        ret = false;
                    }
                }
            }

            for (Part corner : corners) {
                if (corner.checkFace(String.valueOf(face))) {
                    if (!(corner.getColor(face)==color)) {
                        ret = false;
                    }
                }
            }
        }
        if (ret) {
            log.fine("cube is solved");
        } else {
            log.warning("cube is not solved");
        }

        return ret;
    }

    private String colorString(char color, int length) {
        StringBuilder out = new StringBuilder();
        switch(color) {
            case 'r':
                out.append("\u001B[48;2;191;0;0m");
                break;
            case 'g':
                out.append("\u001B[48;2;0;191;0m");
                break;
            case 'y':
                out.append("\u001B[48;2;191;191;0m");
                break;
            case 'o':
                out.append("\u001B[48;2;191;85;0m");
                break;
            case 'b':
                out.append("\u001B[48;2;0;0;191m");
                break;
            case 'w':
                out.append("\u001B[48;2;191;191;191m");
                break;
            case 'n':
                out.append("\u001B[48;2;0;0;0m");
                break;
        }

        for (int i = 0; i < Math.max(0,length); i++) {
            out.append(" ");
        }
        out.append("\u001B[0m");

        return out.toString();
    }
}
