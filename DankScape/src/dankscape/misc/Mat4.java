/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import dankscape.nativeinterface.NativeInterface;


/**
 *
 * @author Pieterjan
 */
public class Mat4
{
    private double[][] data;
    
    private void setZero()
    {
        data = new double[4][];
        for(int i = 0;i < 4;i++)
            data[i] = new double[4];
    }
    
    public Mat4()
    {
        setIdentity();
    }
    
    public Mat4 setIdentity()
    {
        setZero();
        for(int i = 0;i < 4;i++)
            data[i][i] = 1.0;
        return this;
    }
    
    /*public void setLookAt(Vec3 eye, double rotX, double rotY)
    {
        setIdentity();
        
        rotateX(rotX);
        rotateY(rotY);
        
        translate(-eye.x, -eye.y, -eye.z);
        //Vec3 at   = eye.add(t.mult(new Vec3(0, 0, 1)));
        //Vec3 up   = eye.add(t.mult(new Vec3(0, 1, 0)));
        //setLookAt(eye, at, up);
    }

    public void setLookAt(Vec3 eye, Vec3 at, Vec3 up)
    {
        Vec3 zaxis = at.subtract(eye).normalize();
        Vec3 xaxis = up.cross(zaxis).normalize();
        Vec3 yaxis = zaxis.cross(xaxis);
        
        setIdentity();
        data[0][0] = xaxis.x; data[1][0] = yaxis.x; data[2][0] = zaxis.x; data[3][0] = 0;
        data[0][1] = xaxis.y; data[1][1] = yaxis.y; data[2][1] = zaxis.y; data[3][1] = 0;
        data[0][2] = xaxis.z; data[1][2] = yaxis.z; data[2][2] = zaxis.z; data[3][2] = 0;
        data[0][3] = -xaxis.dot(eye); data[1][3] = -yaxis.dot(eye); data[2][3] = -zaxis.dot(eye); data[3][3] = 1.0;
    }*/
    
    public void translate(double x, double y, double z)
    {
        Mat4 mat = new Mat4();
        mat.data[3][0] = x;
        mat.data[3][1] = y;
        mat.data[3][2] = z;
        copy(mult(mat));
    }
    
    public void scale(double x, double y, double z)
    {
        Mat4 mat = new Mat4();
        mat.data[0][0] = x;
        mat.data[1][1] = y;
        mat.data[2][2] = z;
        copy(mult(mat));
    }
    
    public void rotateX(double a)
    {
        Mat4 mat = new Mat4();
        mat.data[1][1] = Math.cos(a);
        mat.data[2][1] = -Math.sin(a);
        mat.data[1][2] = Math.sin(a);
        mat.data[2][2] = Math.cos(a);
        copy(mult(mat));
    }
    
    public void rotateY(double a)
    {
        Mat4 mat = new Mat4();
        mat.data[0][0] = Math.cos(a);
        mat.data[0][2] = -Math.sin(a);
        mat.data[2][0] = Math.sin(a);
        mat.data[2][2] = Math.cos(a);
        copy(mult(mat));
    }
    
    public void rotateZ(double a)
    {
        Mat4 mat = new Mat4();
        mat.data[0][0] = Math.cos(a);
        mat.data[1][0] = -Math.sin(a);
        mat.data[0][1] = Math.sin(a);
        mat.data[1][1] = Math.cos(a);
        copy(mult(mat));
    }
    
    public Mat4 setElem(int x, int y, double val)
    {
        data[x][y] = val;
        return this;
    }
    
    public Mat4 setRow(int row, double x, double y, double z, double w)
    {
        data[0][row] = x;
        data[1][row] = y;
        data[2][row] = z;
        data[3][row] = w;
        return this;
    }
    
    public void copy(Mat4 other)
    {
        for(int i = 0;i < 4;i++)
            for(int j = 0;j < 4;j++)
                data[i][j] = other.data[i][j];
    }
    
    public Mat4 mult(Mat4 other)
    {
        Mat4 result = new Mat4();
        result.setZero();
        for(int i = 0;i < 4;i++)
            for(int j = 0;j < 4;j++)
                for(int k = 0;k < 4;k++)
                    result.data[i][j] += data[k][j] * other.data[i][k];
        return result;
    }
    
    public Vec3 mult(Vec3 other)
    {
        double[] result = { 0.0, 0.0, 0.0 };
        for(int i = 0;i < 3;i++)
            result[i] = data[0][i] * other.x + data[1][i] * other.y + data[2][i] * other.z + data[3][i] * 1.0;
        return new Vec3(result[0], result[1], result[2]);
    }
    
    public void dump()
    {
        String s = "";
        for(int i = 0;i < 4;i++)
        {
            s += "{";
            for(int j = 0;j < 4;j++)
                s += "" + data[j][i] + (j + 1 < 4 ? ", " : "");
            s += "}\n";
        }
        NativeInterface.println(s);
    }
}
