package Scripts;

import java.util.ArrayList;
import java.util.List;

public class Chunk extends Utilities
{
    public enum BlockType { Air, Grass };
    public BlockType[][][] blocks;
    public Vector3 chunkPos;

    public Vector3[] points;
    public Vector2[] textureCoords;
    public Vector3[] normals;
    public Vector3 chunkDimensions;

    public Model model;

    public Chunk(Vector3 pos, Vector3 chunkDimensions)
    {
        chunkPos = pos;
        this.chunkDimensions = chunkDimensions;
        InitBlockArray(chunkDimensions);
        GenerateTerrainMeshPoints();

        // Generate simple texture coordinates (0-1) per vertex
        textureCoords = new Vector2[points.length];
        for (int i = 0; i < points.length; i++)
        {
            // Use fractional part of x/z so texture tiles
            textureCoords[i] = new Vector2(points[i].x - (float)Math.floor(points[i].x),
                                        points[i].z - (float)Math.floor(points[i].z));
        }

        // Generate flat normals for each triangle (per 3 vertices)
        normals = new Vector3[points.length];
        for (int i = 0; i < points.length; i += 3)
        {
            Vector3 v0 = points[i];
            Vector3 v1 = points[i+1];
            Vector3 v2 = points[i+2];

            Vector3 edge1 = subVecs(v1, v0);
            Vector3 edge2 = subVecs(v2, v0);
            Vector3 normal = normalize(cross(edge1, edge2));

            normals[i] = normal;
            normals[i+1] = normal;
            normals[i+2] = normal;
        }

        // Now create model using the chunk's data and chunk position.
        // Use the chunk's texture for the chunk model.
        model = new Model("/Users/florianlerch/Full3DRenderer/Models/GrassCube.obj",
                        "/Users/florianlerch/Full3DRenderer/Textures/Green.png",
                        chunkPos, new Vector3(1, 1, 1), 0, 0, 0);

        // Initialize Model with the chunk's mesh data and texture
        model.points = this.points;
        model.textureCoords = this.textureCoords;
        model.normals = this.normals;
        model.Init(model.points, model.textureCoords, model.normals);
    }


    public void GenerateTerrainMeshPoints()
    {
        List<Vector3> verts = new ArrayList<Vector3>();
        for (int x = 1; x < chunkDimensions.x - 1; x++)
        {
            for (int z = 1; z < chunkDimensions.z - 1; z++)
            {
                for (int y = 1; y < chunkDimensions.y - 1; y++)
                {
                    BlockType blockType = blocks[x][y][z];
                    boolean isNotBorderBlock = z < chunkDimensions.z - 1 && z > 1 && x < chunkDimensions.x - 1 && x > 1;
                    Vector3 blockPos = new Vector3(x, y, z);
                    System.out.println(blockPos.x + ", " + blockPos.y + ", " + blockPos.z);

                    if (blockType != BlockType.Air && isNotBorderBlock)
                    {
                        // Front (z+1)
                        if (blocks[x][y][z+1] == BlockType.Air) 
                        {
                            verts.add(addVecs(new Vector3(1, 0, 1), blockPos));
                            verts.add(addVecs(new Vector3(0, 1, 1), blockPos));
                            verts.add(addVecs(new Vector3(0, 0, 1), blockPos));

                            verts.add(addVecs(new Vector3(1, 1, 1), blockPos));
                            verts.add(addVecs(new Vector3(0, 1, 1), blockPos));
                            verts.add(addVecs(new Vector3(1, 0, 1), blockPos));
                        }

                        // Back (z)
                        if (blocks[x][y][z-1] == BlockType.Air) 
                        {
                            verts.add(addVecs(new Vector3(0, 0, 0), blockPos));
                            verts.add(addVecs(new Vector3(1, 1, 0), blockPos));
                            verts.add(addVecs(new Vector3(1, 0, 0), blockPos));

                            verts.add(addVecs(new Vector3(0, 1, 0), blockPos));
                            verts.add(addVecs(new Vector3(1, 1, 0), blockPos));
                            verts.add(addVecs(new Vector3(0, 0, 0), blockPos));
                        }

                        // Top (y+1)
                        if (blocks[x][y+1][z] == BlockType.Air) 
                        {
                            verts.add(addVecs(new Vector3(0, 1, 0), blockPos));
                            verts.add(addVecs(new Vector3(0, 1, 1), blockPos));
                            verts.add(addVecs(new Vector3(1, 1, 0), blockPos));

                            verts.add(addVecs(new Vector3(1, 1, 0), blockPos));
                            verts.add(addVecs(new Vector3(0, 1, 1), blockPos));
                            verts.add(addVecs(new Vector3(1, 1, 1), blockPos));
                        }

                        // Bottom (y)
                        if (blocks[x][y-1][z] == BlockType.Air) 
                        {
                            verts.add(addVecs(new Vector3(0, 0, 0), blockPos));
                            verts.add(addVecs(new Vector3(1, 0, 0), blockPos));
                            verts.add(addVecs(new Vector3(0, 0, 1), blockPos));

                            verts.add(addVecs(new Vector3(1, 0, 0), blockPos));
                            verts.add(addVecs(new Vector3(1, 0, 1), blockPos));
                            verts.add(addVecs(new Vector3(0, 0, 1), blockPos));
                        }

                        // Right (x+1)
                        if (blocks[x+1][y][z] == BlockType.Air) 
                        {
                            verts.add(addVecs(new Vector3(1, 0, 0), blockPos));
                            verts.add(addVecs(new Vector3(1, 1, 0), blockPos));
                            verts.add(addVecs(new Vector3(1, 0, 1), blockPos));

                            verts.add(addVecs(new Vector3(1, 0, 1), blockPos));
                            verts.add(addVecs(new Vector3(1, 1, 0), blockPos));
                            verts.add(addVecs(new Vector3(1, 1, 1), blockPos));
                        }

                        // Left (x)
                        if (blocks[x-1][y][z] == BlockType.Air) 
                        {
                            verts.add(addVecs(new Vector3(0, 0, 1), blockPos));
                            verts.add(addVecs(new Vector3(0, 1, 1), blockPos));
                            verts.add(addVecs(new Vector3(0, 0, 0), blockPos));

                            verts.add(addVecs(new Vector3(0, 0, 0), blockPos));
                            verts.add(addVecs(new Vector3(0, 1, 1), blockPos));
                            verts.add(addVecs(new Vector3(0, 1, 0), blockPos));
                        }
                    }
                }
            }
        }
        points = verts.toArray(new Vector3[verts.size()]);
    }

    public void InitBlockArray(Vector3 chunkDimensions)
    {
        blocks = new BlockType[(int)chunkDimensions.x][(int)chunkDimensions.y][(int)chunkDimensions.z];
        for (int x = 0; x < chunkDimensions.x; x++)
        {
            for (int z = 0; z < chunkDimensions.z; z++)
            {
                for (int y = 0; y < chunkDimensions.y; y++)
                {
                    if ((x + y + z) % 2 == 0) blocks[x][y][z] = BlockType.Grass;
                    else blocks[x][y][z] = BlockType.Air;
                }
            }
        }
    }
}
