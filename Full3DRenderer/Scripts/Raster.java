package Scripts;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Raster extends Camera implements Runnable
{
    public BufferedImage canvas;
    Thread gameThread;

    static final int CanvasWidth = 1280;
    static final int CanvasHeight = 1080;

    public static float[][] depthBuffer = new float[CanvasWidth][CanvasHeight];

    String modelsPath = "/Users/florianlerch/Full3DRenderer/Models/";
    String texturesPath = "/Users/florianlerch/Full3DRenderer/Textures/";

    Model fox = new Model(modelsPath + "fox.obj", texturesPath + "Orange.png", new Vector3(2, 0, 5f), new Vector3(1, 1, 1), (float)-Math.PI / 2, (float)-Math.PI / 2, 0);
    Model monkey = new Model(modelsPath + "monkey.obj", texturesPath + "Green.png", new Vector3(3, 0, 6f), new Vector3(1, 1, 1), (float)Math.PI, (float)-Math.PI / 2, 0);
    Model cube = new Model(modelsPath + "GrassCube.obj", texturesPath + "GrassTexture.png", new Vector3(-3, 0, 6f), new Vector3(.5f, .5f, .5f), 0, (float)-Math.PI / 2, 0);
    Chunk chunk = new Chunk(new Vector3(0, 0, 10), new Vector3(10 + 2, 2 + 2, 10 + 2));
    Model[] models = new Model[] { fox };

    static Vector3 DirectionToLight = normalize(new Vector3(0, 0, -10));

    KeyPress keyPress = new KeyPress();
    MouseInput mouseInput = new MouseInput();
    public static Vector2 mousePos = new Vector2(0, 0);
    static float mouseSensitivity = .001f;
    float lastMouseX = -1;
    float lastMouseY = -1;    

    public Raster()
    {
        this.setPreferredSize(new Dimension(CanvasWidth,CanvasHeight));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyPress);
        this.addMouseListener(mouseInput);
        this.setDoubleBuffered(false);
    }

    public void startGameThread()
    {
        ResetDepthBuffer();
        gameThread = new Thread(this);
        gameThread.start();
    }

    void ResetDepthBuffer()
    {
        for (int x = 0; x < CanvasWidth; x++)
        {
            for (int y = 0; y < CanvasHeight; y++)
            {
                depthBuffer[x][y] = 10000f;  
            }
        }
    }

    @Override
    public void run() 
    {
        for (Model model : models) {
            LoadModel(model, new Vector3(0, 0, 5), model.filename);
            //model.points = chunk.points;
            //model.normals = chunk.normals;
            //model.textureCoords = chunk.textureCoords;
        }

        // Allocate the canvas ONCE
        canvas = new BufferedImage(CanvasWidth, CanvasHeight, BufferedImage.TYPE_INT_ARGB);

        while (gameThread != null) 
        {
            ResetDepthBuffer();

            // Clear the canvas instead of recreating it
            Graphics2D g2 = canvas.createGraphics();
            g2.setColor(Color.WHITE);  // background color
            g2.fillRect(0, 0, CanvasWidth, CanvasHeight);
            g2.dispose();

            Update();

            // Render models into the canvas
            for (Model model : models) {
                Render(model);
            }

            // Request a repaint on the EDT
            javax.swing.SwingUtilities.invokeLater(this::repaint);

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);

        if (canvas != null) {
            g.drawImage(canvas, 0, 0, null);
        }
        // DO NOT dispose() g — Swing handles it
    }

    void LoadModel(Model model, Vector3 position, String filename)
    {
        Vector3[] points = LoadObjFile(filename);
        Vector2[] textureCoords = LoadTextureCoords(filename);
        Vector3[] normals = LoadNormals(filename);
        model.Init(points, textureCoords, normals);
    }

    void Update()
    {
        //monkey.Pitch += 0.04;
        //cube.Yaw += 0.04;

        int key = keyPress.key;
        //mousePos = GetMousePos();
        int cameraRotationSpeed = 5;
        if (key == KeyEvent.VK_LEFT) { mousePos.x -= cameraRotationSpeed; }
        if (key == KeyEvent.VK_RIGHT) { mousePos.x += cameraRotationSpeed; }
        if (key == KeyEvent.VK_UP) { mousePos.y += cameraRotationSpeed;  }
        if (key == KeyEvent.VK_DOWN) { mousePos.y -= cameraRotationSpeed; }
        int mouseX = (int)mousePos.x;
        int mouseY = (int)mousePos.y;

        if (lastMouseX != -1 && lastMouseY != -1) 
        {
            int deltaX = (int)(mouseX - lastMouseX);
            int deltaY = (int)(mouseY - lastMouseY);

            cameraYaw += deltaX * mouseSensitivity;
            cameraPitch -= deltaY * mouseSensitivity;

            // Clamp pitch to avoid flipping
            cameraPitch = Math.max((float)-Math.PI / 2.0f + 0.01f, Math.min((float)Math.PI / 2.0f - 0.01f, cameraPitch));
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        Vector3[] basis = Transforms.GetBasisVectors(cameraYaw, cameraPitch, cameraRoll);
        Vector3 right = basis[0];
        Vector3 up = basis[1];
        Vector3 forward = basis[2];

        if (key == KeyEvent.VK_W) { cameraPosition = addVecs(cameraPosition, multVecByNum(forward, cameraSpeed)); }
        if (key == KeyEvent.VK_S) { cameraPosition = subVecs(cameraPosition, multVecByNum(forward, cameraSpeed)); }
        if (key == KeyEvent.VK_A) { cameraPosition = subVecs(cameraPosition, multVecByNum(right, cameraSpeed));  }
        if (key == KeyEvent.VK_D) { cameraPosition = addVecs(cameraPosition, multVecByNum(right, cameraSpeed)); }
        if (key == KeyEvent.VK_Q) { cameraPosition = subVecs(cameraPosition, multVecByNum(up, cameraSpeed));  }
        if (key == KeyEvent.VK_E) { cameraPosition = addVecs(cameraPosition, multVecByNum(up, cameraSpeed)); }
    }
    
    static Vector2 GetMousePos()
    {
        Point b = MouseInfo.getPointerInfo().getLocation();
        return new Vector2((int)b.getX(), (int)b.getY());
    }

    public void Render(Model model)
    {
        for (int i = 0; i < model.points.length; i+=3)
        {
            Vector3[] basisVectors = GetBasisVectors(model.Yaw, model.Pitch, model.Roll);
            Vector3 a = WorldToScreen(model.points[i + 0], model.position, model.scale, basisVectors);
            Vector3 b = WorldToScreen(model.points[i + 1], model.position, model.scale, basisVectors);
            Vector3 c = WorldToScreen(model.points[i + 2], model.position, model.scale, basisVectors);
            if (a.z <= 1 || b.z <= 1 || c.z <= 1) continue;
            
            float minX = Math.min(Math.min(a.x, b.x), c.x);
            float minY = Math.min(Math.min(a.y, b.y), c.y);
            float maxX = Math.max(Math.max(a.x, b.x), c.x);
            float maxY = Math.max(Math.max(a.y, b.y), c.y);

            int blockStartX = (int)clamp(minX, 0, CanvasWidth - 1);
            int blockStartY = (int)clamp(minY, 0, CanvasHeight - 1);
            int blockEndX = (int)clamp(maxX, 0, CanvasWidth - 1);
            int blockEndY = (int)clamp(maxY, 0, CanvasHeight - 1);

            for (int x = blockStartX; x <= blockEndX; x++)
            {
                for (int y = blockStartY; y <= blockEndY; y++)
                {
                    Vector2 p = new Vector2(x, y);
                    Vector3 weights;

                    Vector2 a2 = new Vector2(a.x, a.y);
                    Vector2 b2 = new Vector2(b.x, b.y);
                    Vector2 c2 = new Vector2(c.x, c.y);
                    
                    if (PointInTriangle(a2, b2, c2, p))
                    {
                        Vector3 depths = new Vector3(a.z, b.z, c.z);
                        weights = GetWeights(a2, b2, c2, p);
                        Vector3 inverseDepths = new Vector3(1 / depths.x, 1 / depths.y, 1 / depths.z);
                        float depth = 1 / Dot(inverseDepths, weights);

                        if (depth < depthBuffer[x][y])
                        {
                            float u = (model.textureCoords[i].x / depths.x * weights.x + model.textureCoords[i + 1].x / depths.y * weights.y + model.textureCoords[i + 2].x / depths.z * weights.z) * depth;
                            float v = (model.textureCoords[i].y / depths.x * weights.x + model.textureCoords[i + 1].y / depths.y * weights.y + model.textureCoords[i + 2].y / depths.z * weights.z) * depth;
                            int xTexCoord = (int)Math.floor(u * (model.texture.getWidth() - 1));
                            int yTexCoord = model.texture.getHeight() - 1 - (int)Math.floor(v * (model.texture.getHeight() - 1));
                            Vector2 texCoords = new Vector2(xTexCoord, yTexCoord);
                            
                            float normalX = (model.normals[i].x / depths.x * weights.x + model.normals[i + 1].x / depths.y * weights.y + model.normals[i + 2].x / depths.z * weights.z) * depth;
                            float normalY = (model.normals[i].y / depths.x * weights.x + model.normals[i + 1].y / depths.y * weights.y + model.normals[i + 2].y / depths.z * weights.z) * depth;
                            float normalZ = (model.normals[i].z / depths.x * weights.x + model.normals[i + 1].z / depths.y * weights.y + model.normals[i + 2].z / depths.z * weights.z) * depth;
                            Vector3 normal = new Vector3(normalX, normalY, normalZ);
                            //normal = TransformVector(basisVectors[0], basisVectors[1], basisVectors[2], normal);

                            //canvas.setRGB(x, y, model.texture.getRGB(xTexCoord,yTexCoord));
                            canvas.setRGB(x, y, GetPixelColor(model, texCoords, normal, basisVectors));
                            depthBuffer[x][y] = depth;
                        }
                    }
                }
            }
        }
    }

    public static Vector3 WorldToScreen(Vector3 point, Vector3 position, Vector3 scale, Vector3[] basisVectors)
    {
        Vector3 vertexWorld = ToWorldPoint(point, position, scale, basisVectors);
        Vector3 vertexView = ToLocalPoint(cameraPosition, cameraYaw, cameraPitch, cameraRoll, scale, vertexWorld);

        float screenHeightWorld = (float)Math.tan(fov / 2) * 2;
        float pixelsPerWorldUnit = CanvasHeight / screenHeightWorld / vertexView.z;

        Vector2 pixelOffset = new Vector2(vertexView.x * pixelsPerWorldUnit, vertexView.y * pixelsPerWorldUnit);
        Vector2 vertexScreen = new Vector2(pixelOffset.x + CanvasWidth / 2, pixelOffset.y + CanvasHeight / 2);
        return new Vector3(vertexScreen.x, vertexScreen.y, vertexView.z);
    }

    public int GetPixelColor(Model model, Vector2 texCoord, Vector3 normal, Vector3[] basisVectors) 
    {
        //Vector3 transformedLightDir = TransformVector(basisVectors[0], basisVectors[1], basisVectors[2], normalize(DirectionToLight));
        float lightIntensity = Math.max(0, Dot(normal, DirectionToLight) + 1) * 0.5f;

        int textureColor = model.texture.getRGB((int) texCoord.x, (int) texCoord.y);
        Color texColor = new Color(textureColor);

        int r = Math.min(255, (int)(texColor.getRed() * lightIntensity));
        int g = Math.min(255, (int)(texColor.getGreen() * lightIntensity));
        int b = Math.min(255, (int)(texColor.getBlue() * lightIntensity));

        return new Color(r, g, b).getRGB();
    }
}