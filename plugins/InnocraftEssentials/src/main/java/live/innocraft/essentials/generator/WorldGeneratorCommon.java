package live.innocraft.essentials.generator;

import org.bukkit.util.noise.SimplexNoiseGenerator;

public class WorldGeneratorCommon {

    public static double fBmSimplex(double x, double y, SimplexNoiseGenerator generator, int octaves, double persistance, double lacunarity) {

        double result = 0;
        double l = 1.0;
        double p = 1.0;

        for (int i = 0; i < octaves; i++) {
            result += generator.noise(x * l, y * l) * p;

            l *= lacunarity;
            p *= persistance;
        }

        return result;

    }

}
