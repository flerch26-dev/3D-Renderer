package Scripts;
public class Transforms extends Utilities
{
    public static Vector3 ToWorldPoint(Vector3 p, Vector3 Position, Vector3 scale, Vector3[] basisVectors)
    {
        Vector3 ihat = multVecByNum(basisVectors[0], scale.x);
        Vector3 jhat = multVecByNum(basisVectors[1], scale.y);
        Vector3 khat = multVecByNum(basisVectors[2], scale.z);
        return addVecs(TransformVector(ihat, khat, jhat, p), Position);
    }

    public static Vector3 ToLocalPoint(Vector3 cameraPosition, float cameraYaw, float cameraPitch, float cameraRoll, Vector3 scale, Vector3 worldPoint)
    {
        Vector3[] basis = Transforms.GetBasisVectors(cameraYaw, cameraPitch, cameraRoll);
        Vector3 right = basis[0];
        Vector3 up = basis[1];
        Vector3 forward = basis[2];

        // Move/rotate world space points into camera space:
        Vector3 relativePosition = subVecs(worldPoint, cameraPosition);
        Vector3 transformed = new Vector3(
            Dot(relativePosition, right),
            Dot(relativePosition, up),
            Dot(relativePosition, forward)
        );
        return transformed;
        /*Vector3 local = subVecs(worldPoint, cameraPosition);
        local.x /= scale.x;
        local.y /= scale.y;
        local.z /= scale.z;
        return local;*/
    }

    static Vector3[] GetBasisVectors(float Yaw, float Pitch, float Roll)
    {
        Vector3 ihat_yaw = new Vector3((float)Math.cos(Yaw), 0, (float)Math.sin(Yaw));
        Vector3 jhat_yaw = new Vector3(0,1,0);
        Vector3 khat_yaw = new Vector3((float)-Math.sin(Yaw), 0, (float)Math.cos(Yaw));

        Vector3 ihat_pitch = new Vector3(1,0,0);
        Vector3 jhat_pitch = new Vector3(0,(float)Math.cos(Pitch),(float)-Math.sin(Pitch));
        Vector3 khat_pitch = new Vector3(0, (float)Math.sin(Pitch), (float)Math.cos(Pitch));

        Vector3 ihat_roll = new Vector3((float)Math.cos(Roll),(float)Math.sin(Roll),0);
        Vector3 jhat_roll = new Vector3((float)-Math.sin(Roll),(float)Math.cos(Roll),0);
        Vector3 khat_roll = new Vector3(0, 0, 1);

        Vector3 ihat_pitchYaw = TransformVector(ihat_yaw, jhat_yaw, khat_yaw, ihat_pitch);
        Vector3 jhat_pitchYaw = TransformVector(ihat_yaw, jhat_yaw, khat_yaw, jhat_pitch);
        Vector3 khat_pitchYaw = TransformVector(ihat_yaw, jhat_yaw, khat_yaw, khat_pitch);

        Vector3 ihat = TransformVector(ihat_pitchYaw, jhat_pitchYaw, khat_pitchYaw, ihat_roll);
        Vector3 jhat = TransformVector(ihat_pitchYaw, jhat_pitchYaw, khat_pitchYaw, jhat_roll);
        Vector3 khat = TransformVector(ihat_pitchYaw, jhat_pitchYaw, khat_pitchYaw, khat_roll);

        return new Vector3[] {ihat, jhat, khat};
    }

    static Vector3[] GetInverseBasisVectors(float Yaw, float Pitch, float Roll)
    {
        Vector3[] basisVectors = GetBasisVectors(Yaw, Pitch, Roll);
        Vector3 ihat = basisVectors[0];
        Vector3 jhat = basisVectors[1];
        Vector3 khat = basisVectors[2];

        Vector3 ihat_inverse = new Vector3(ihat.x, jhat.x, khat.x);
        Vector3 jhat_inverse = new Vector3(ihat.y, jhat.y, khat.y);
        Vector3 khat_inverse = new Vector3(ihat.z, jhat.z, khat.z);

        return new Vector3[] {ihat_inverse, jhat_inverse, khat_inverse};
    }

    static Vector3 TransformVector(Vector3 ihat, Vector3 jhat, Vector3 khat, Vector3 p)
    {
        Vector3 x = multVecByNum(ihat, p.x);
        Vector3 y = multVecByNum(jhat, p.y);
        Vector3 z = multVecByNum(khat, p.z);
        return addVecs(addVecs(x, y), z);
    }
}
