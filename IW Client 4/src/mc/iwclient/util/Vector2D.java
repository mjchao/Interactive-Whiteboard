package mc.iwclient.util;

public class Vector2D {

	final public double m_x;
	final public double m_y;
	
	public Vector2D( double x , double y ) {
		this.m_x = x;
		this.m_y = y;
	}
	
	public Vector2D subtract( Vector2D vector ) {
		return new Vector2D( this.m_x-vector.m_x , this.m_y-vector.m_y );
	}
	
	public double dot( Vector2D vector ) {
		return this.m_x*vector.m_x + this.m_y*vector.m_y;
	}
	
	public double cross( Vector2D vector ) {
		return this.m_x*vector.m_y - vector.m_x*this.m_y;
	}
	
	public double magnitude() {
		return Math.sqrt( this.m_x*this.m_x + this.m_y*this.m_y );
	}
	
	public double magnitudeSquared() {
		return this.m_x*this.m_x + this.m_y*this.m_y;
	}
}
