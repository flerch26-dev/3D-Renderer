package Scripts;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Model extends Utilities
{
    public Vector3[] points;
    public Vector2[] textureCoords;
    public Vector3[] normals;

    public String textureFileName;
    public BufferedImage texture = null;
    public String filename;

    public float Yaw;
    public float Pitch;
    public float Roll;

    public Vector3 position;
    public Vector3 scale;

    public Model(String filename, String textureFileName, Vector3 position, Vector3 scale, float Yaw, float Pitch, float Roll)
    {
        this.filename = filename;
        this.position = position;
        this.Yaw = Yaw;
        this.Pitch = Pitch;
        this.Roll = Roll;
        this.textureFileName = textureFileName;
        this.scale = scale;
    }

    public void Init(Vector3[] points, Vector2[] textureCoords, Vector3[] normals)
    {
        this.points = points;
        this.textureCoords = textureCoords;
        this.normals = normals;

        try {
            File file = new File(textureFileName);

            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return;
            }

            texture = ImageIO.read(file);
            if (texture == null) {
                System.out.println("Failed to load image: Unsupported format?");
                return;
            }

            //int width = texture.getWidth();
            //int height = texture.getHeight();
            //System.out.println("Loaded image: " + width + "x" + height);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
