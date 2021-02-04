package rubikscube.solving;

public class Part {

    private final StringBuffer faces;
    private final StringBuffer colors;
    private final int type; //1 = center, 2 = edge, 3 = corner
    private final String faceTest = "FRUBLD";
    private final String colorTest = "rgyobw";

    private final FileLogger log;

    public Part(String facesIn, String colorsIn, FileLogger logIn) {
        log = logIn;

        //getting type
        if(facesIn.length() == colorsIn.length() && facesIn.length() <= 3) {
            type = facesIn.length();
        } else {
            type = 0;
            log.warning("can not create part: " + facesIn + " " + colorsIn);
            faces = null;
            colors = null;
            return;
        }

        //checking faces + colors
        boolean bool = true;
        for(int i = 0; i < type; i++) {
            if(faceTest.indexOf(facesIn.charAt(i)) == -1 || colorTest.indexOf(colorsIn.charAt(i)) == -1) {
                bool = false;
            }
        }
        faces = new StringBuffer(type);
        colors = new StringBuffer(type);

        if(bool) {
            faces.append(facesIn);
            colors.append(colorsIn);
            log.finer("Part created: " + faces + " " + colors + " " + type);
        } else {
            log.warning("can not create part: " + facesIn + " " + colorsIn);
        }
    }

    public void setColors(String colorsIn) {
        if (colorsIn.length() == type) {
            boolean bool = true;
            for(int i = 0; i < colorsIn.length(); i++) {
                if(colorTest.indexOf(colorsIn.charAt(i)) == -1) {
                    bool = false;
                }
            }

            if(bool) {
                colors.delete(0, type);
                colors.append(colorsIn);
                log.finest("set colors");
            } else {
                log.warning("error: unknown colors");
            }
        } else {
            log.warning("error: wrong length");
        }
    }

    public void setColor(char newColor, char face) {
        int a = faces.indexOf(String.valueOf(face));
        if(a != -1 && colorTest.indexOf(newColor) != -1) {
            colors.setCharAt(a, newColor);
            log.finest("set color");
        } else {
            log.warning("can not set color at face");
        }
    }

    public String getFaces() {
        return faces.toString();
    }

    public String getColors() {
        return colors.toString();
    }

    public char getFace(char color) {
        char ret = faces.charAt(colors.indexOf(String.valueOf(color)));
        log.finest("got face: "  + ret);
        return ret;
    }

    public char getColor(char face) {
        char ret = colors.charAt(faces.indexOf(String.valueOf(face)));
        log.finest("got color: "  + ret);
        return ret;
    }

    public int getType() {
        return type;
    }

    public boolean checkFace(String facesCheck) {
        int le = facesCheck.length();

        if(le <= type) {
            boolean bool = true;
            for(int i = 0; i < le; i++) {
                if(faces.indexOf(String.valueOf(facesCheck.charAt(i))) == -1) {
                    bool = false;
                }
            }
            return bool;
        }
        return false;
    }

    public boolean checkColor(String colorsCheck) {
        int le = colorsCheck.length();

        if(le <= type) {
            boolean bool = true;
            for(int i = 0; i < le; i++) {
                if(colors.indexOf(String.valueOf(colorsCheck.charAt(i))) == -1) {
                    bool = false;
                }
            }
            return bool;
        }
        return false;
    }
}
