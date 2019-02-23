/*
 * RSModelData.java
 * ---------------------------------------------------------------------------
 * Note: this file was automatically generated by the DankScape API Generator,
 * modifications to this file are useless as it will have to be regenerated often in the future.
 */

package dankscape.api.rs;

import dankscape.loader.AppletLoader;

// Original name: dh => eo (Renderable) => gd (CacheableNode) => gw (Node) => java.lang.Object
public class RSModelData extends RSRenderable {

    public RSModelData(Object ref) {
        super(ref);
    }

    public int[] getVertexY() {
        return (int[])AppletLoader.getSingleton().getFieldValue("ModelData", "vertexY", ref);
    }

    public int[] getVertexX() {
        return (int[])AppletLoader.getSingleton().getFieldValue("ModelData", "vertexX", ref);
    }

    public byte[] getFaceRenderType() {
        return (byte[])AppletLoader.getSingleton().getFieldValue("ModelData", "faceRenderType", ref);
    }

    public int[] getVertexZ() {
        return (int[])AppletLoader.getSingleton().getFieldValue("ModelData", "vertexZ", ref);
    }

    public int[] getTriangleSkinValues() {
        return (int[])AppletLoader.getSingleton().getFieldValue("ModelData", "triangleSkinValues", ref);
    }

    public RSFaceNormal[] getFaceNormals() {
        Object[] objects = (Object[])AppletLoader.getSingleton().getFieldValue("ModelData", "faceNormals", ref);
        RSFaceNormal[] wrappers = null;
        if(objects == null)
            return null;
        wrappers = new RSFaceNormal[objects.length];
        for(int i = 0;i < objects.length;i++) {
            if(objects[i] != null)
                wrappers[i] = (RSFaceNormal)getWrapper(objects[i]);
        }
        return wrappers;
    }

    public Object[] getRSRef_FaceNormals() {
        return (Object[])AppletLoader.getSingleton().getFieldValue("ModelData", "faceNormals", ref);
    }

    public int[] getVertexSkins() {
        return (int[])AppletLoader.getSingleton().getFieldValue("ModelData", "vertexSkins", ref);
    }

    public short[] getFaceTextures() {
        return (short[])AppletLoader.getSingleton().getFieldValue("ModelData", "faceTextures", ref);
    }

    public short[] getFaceColor() {
        return (short[])AppletLoader.getSingleton().getFieldValue("ModelData", "faceColor", ref);
    }

    public byte getPriority() {
        return (byte)AppletLoader.getSingleton().getFieldValue("ModelData", "priority", ref);
    }

    public byte[] getTextureRenderTypes() {
        return (byte[])AppletLoader.getSingleton().getFieldValue("ModelData", "textureRenderTypes", ref);
    }

    public byte[] getTextureCoords() {
        return (byte[])AppletLoader.getSingleton().getFieldValue("ModelData", "textureCoords", ref);
    }

    public int getVertexCount() {
        return (int)AppletLoader.getSingleton().getFieldValue("ModelData", "vertexCount", ref);
    }

    public short[] getTexturePrimaryColor() {
        return (short[])AppletLoader.getSingleton().getFieldValue("ModelData", "texturePrimaryColor", ref);
    }

    public int[] getTrianglePointsZ() {
        return (int[])AppletLoader.getSingleton().getFieldValue("ModelData", "trianglePointsZ", ref);
    }

    public int getTriangleFaceCount() {
        return (int)AppletLoader.getSingleton().getFieldValue("ModelData", "triangleFaceCount", ref);
    }

    public int[] getTrianglePointsY() {
        return (int[])AppletLoader.getSingleton().getFieldValue("ModelData", "trianglePointsY", ref);
    }

    public int[] getTrianglePointsX() {
        return (int[])AppletLoader.getSingleton().getFieldValue("ModelData", "trianglePointsX", ref);
    }

    public short getContrast() {
        return (short)AppletLoader.getSingleton().getFieldValue("ModelData", "contrast", ref);
    }

    public short[] getTexTriangleZ() {
        return (short[])AppletLoader.getSingleton().getFieldValue("ModelData", "texTriangleZ", ref);
    }

    public short[] getTexTriangleY() {
        return (short[])AppletLoader.getSingleton().getFieldValue("ModelData", "texTriangleY", ref);
    }

    public short[] getTexTriangleX() {
        return (short[])AppletLoader.getSingleton().getFieldValue("ModelData", "texTriangleX", ref);
    }

    public byte[] getFaceRenderPriorities() {
        return (byte[])AppletLoader.getSingleton().getFieldValue("ModelData", "faceRenderPriorities", ref);
    }

    public RSVertexNormal[] getNormals() {
        Object[] objects = (Object[])AppletLoader.getSingleton().getFieldValue("ModelData", "normals", ref);
        RSVertexNormal[] wrappers = null;
        if(objects == null)
            return null;
        wrappers = new RSVertexNormal[objects.length];
        for(int i = 0;i < objects.length;i++) {
            if(objects[i] != null)
                wrappers[i] = (RSVertexNormal)getWrapper(objects[i]);
        }
        return wrappers;
    }

    public Object[] getRSRef_Normals() {
        return (Object[])AppletLoader.getSingleton().getFieldValue("ModelData", "normals", ref);
    }

    public byte[] getFaceAlphas() {
        return (byte[])AppletLoader.getSingleton().getFieldValue("ModelData", "faceAlphas", ref);
    }

}