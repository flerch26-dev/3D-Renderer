package Scripts;
import java.awt.Color;
import java.util.Random;

import javax.swing.JPanel;

public class Utilities extends JPanel
{
    public static class Vector2
    {
        public float x, y;
        public Vector2(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    }

    public static class Vector3
    {
        public float x, y, z;
        public Vector3(float x, float y, float z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static Vector2 addVecs(Vector2 a, Vector2 b) { return new Vector2(a.x + b.x, a.y + b.y); }
    public static Vector3 addVecs(Vector3 a, Vector3 b) { return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z); }
    public static Vector2 subVecs(Vector2 a, Vector2 b) { return new Vector2(a.x - b.x, a.y - b.y); }
    public static Vector3 subVecs(Vector3 a, Vector3 b) { return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z); }
    public static Vector2 multVecByNum(Vector2 a, float num) { return new Vector2(a.x * num, a.y * num); }
    public static Vector3 multVecByNum(Vector3 a, float num) { return new Vector3(a.x * num, a.y * num, a.z * num); }

    public static float clamp(float val, float min, float max) { return Math.max(min, Math.min(max, val)); }

    public static Vector2 RandomVector2(Random rng, int width, int height) { return new Vector2(rng.nextFloat() * width, rng.nextFloat() * height); }
    public static Color RandomColor(Random rng) { return new Color(rng.nextFloat(), rng.nextFloat(), rng.nextFloat()); }

    public static float Dot(Vector2 a, Vector2 b) { return a.x * b.x + a.y * b.y; }
    public static float Dot(Vector3 a, Vector3 b) { return a.x * b.x + a.y * b.y + a.z * b.z; }
    public static Vector2 Perpendicular(Vector2 vec) { return new Vector2(vec.y, -vec.x); }

    public static Vector3 cross(Vector3 a, Vector3 b) 
    {
        return new Vector3(
            a.y * b.z - a.z * b.y,
            a.z * b.x - a.x * b.z,
            a.x * b.y - a.y * b.x
        );
    }

    public static boolean PointInTriangle(Vector2 a, Vector2 b, Vector2 c, Vector2 p)
    {
        float areaABP = SignedTriangleArea(b, a, p);
        float areaBCP = SignedTriangleArea(c, b, p);
        float areaCAP = SignedTriangleArea(a, c, p);
        boolean inTri = areaABP >= 0 && areaBCP >= 0 && areaCAP >= 0;
        float totalArea = (areaABP + areaBCP + areaCAP);

        return inTri && totalArea > 0;
    }

    public static Vector3 GetWeights(Vector2 a, Vector2 b, Vector2 c, Vector2 p)
    {
        float denom = (b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y);

        float u = ((b.y - c.y) * (p.x - c.x) + (c.x - b.x) * (p.y - c.y)) / denom;
        float v = ((c.y - a.y) * (p.x - c.x) + (a.x - c.x) * (p.y - c.y)) / denom;
        float w = 1.0f - u - v;

        return new Vector3(u, v, w);
    }

    public static float SignedTriangleArea(Vector2 a, Vector2 b, Vector2 c)
    {
        Vector2 ac = subVecs(c, a);
        Vector2 abPerp = Perpendicular(subVecs(b, a));
        return Dot(ac, abPerp) / 2;
    }

    public static Vector3 normalize(Vector3 v) 
    {
        float mag = (float)Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        if (mag == 0) return new Vector3(0, 0, 0); // avoid division by zero
        return new Vector3(v.x / mag, v.y / mag, v.z / mag);
    }
}
