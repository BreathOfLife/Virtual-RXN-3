package Reference;

import java.io.Serializable;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

public class Vector3D  implements Serializable{
	public double x, y, z;

	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector3D(Point3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public Vector3D(Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public Vector3D add(double x, double y, double z) {
		return new Vector3D(this.x + x, this.y + y, this.z + z);
	}

	public Vector3D sub(double x, double y, double z) {
		return new Vector3D(this.x - x, this.y - y, this.z - z);
	}

	public Vector3D mult(double x, double y, double z) {
		return new Vector3D(this.x * x, this.y * y, this.z * z);
	}

	public Vector3D div(double x, double y, double z) {
		return new Vector3D(this.x / x, this.y / y, this.z / z);
	}

	public Vector3D add(Vector3D v) {
		return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
	}

	public Vector3D sub(Vector3D v) {
		return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
	}

	public Vector3D mult(Vector3D v) {
		return new Vector3D(this.x * v.x, this.y * v.y, this.z * v.z);
	}

	public Vector3D div(Vector3D v) {
		return new Vector3D(this.x / v.x, this.y / v.y, this.z / v.z);
	}

	public Vector3D add(double a) {
		return new Vector3D(this.x + a, this.y + a, this.z + a);
	}

	public Vector3D sub(double s) {
		return new Vector3D(this.x - s, this.y - s, this.z - s);
	}

	public Vector3D mult(double m) {
		return new Vector3D(this.x * m, this.y * m, this.z * m);
	}

	public Vector3D div(double d) {
		return new Vector3D(this.x / d, this.y / d, this.z / d);
	}

	public Vector3D clone() {
		return new Vector3D(x, y, z);
	}

	public double[] toArray() {
		return new double[] { x, y, z };
	}

	public boolean equals(Vector3D v) {
		return (x == v.x) && (y == v.y) && (z == v.z);
	}

	public Vector3D crossProd(Vector3D v) {
		double i = y * v.z - z * v.y;
		double j = z * v.x - x * v.z;
		double k = x * v.y - y * v.x;
		return new Vector3D(i, j, k);
	}

	public Vector3d toJ3dVec() {
		return new Vector3d(x, y, z);
	}

	public Point3d toJ3dPnt() {
		return new Point3d(x, y, z);
	}
	
	public double getMagnitude() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public Vector3D getDir() {
		double mag = getMagnitude();
		if (mag != 0) {
			return div(getMagnitude());
		}
		return new Vector3D(); 
	}

	public double dot(Vector3D v) {
		return x*v.x+y*v.y+z*v.z;
	}
	
	public Quat4d toJ3dRotQuat() {

        if (x == 0 && y == 0 && z == 0) {
        	return new Quat4d(1,1,1,1);
        }
		
		Vector3D yAxis = new Vector3D(0,1,0);
		Vector3D axisNotNormal = yAxis.crossProd(this);
		Vector3D axis = axisNotNormal.getDir();
		
		// When the intended direction is a point on the yAxis, rotate on x
        if (Double.isNaN(axis.x) && Double.isNaN(axis.y) && Double.isNaN(axis.z)) 
        {
            axis.x = 1;
            axis.y = 0;
            axis.z = 0;
        }
        // Compute the quaternion transformations
        final double angleX = yAxis.angle(this);
        final double a = axis.x * (double) Math.sin(angleX / 2);
        final double b = axis.y * (double) Math.sin(angleX / 2);
        final double c = axis.z * (double) Math.sin(angleX / 2);
        final double d = (double) Math.cos(angleX / 2);
		return new Quat4d(a, b, c, d);
	}

	public double angle(Vector3D v) {
		return Math.toDegrees(Math.acos(dot(v) / (getMagnitude() * v.getMagnitude())));
	}
	
	public static Vector3D random(double mag) {
		Vector3D vRandNotNormal = new Vector3D(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
		Vector3D vRandNormal = vRandNotNormal.getDir();
		Vector3D vRandFinal = vRandNormal.mult(mag);
		return vRandFinal;
	}

	public Point3f toJ3dPntF() {
		return new Point3f((float)x, (float)y, (float)z);
	}
}
