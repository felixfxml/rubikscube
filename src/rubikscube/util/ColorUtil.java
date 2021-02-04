package rubikscube.util;

public class ColorUtil {

    public static float[] hexToRGB(int hex) {
        float[] rgba = new float[4];

        int a = (hex & 0xFF000000) >> 24;
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);

        rgba[0] = r / 255.0f;
        rgba[1] = g / 255.0f;
        rgba[2] = b / 255.0f;
        rgba[3] = a / 255.0f;

        return rgba;
    }

}
