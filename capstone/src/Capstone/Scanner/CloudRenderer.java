package Capstone.Scanner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

//import test.opengl.PointCloud;
import android.content.Context;
import android.opengl.GLU;

public class CloudRenderer extends AbstractRenderer
{   
    //A raw native buffer to hold the point coordinates
    private FloatBuffer mFVertexBuffer;
    
    public CloudRenderer(Context context) 
    {
        ByteBuffer vbb = ByteBuffer.allocateDirect(PointCloud.vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(PointCloud.vertices);
        mFVertexBuffer.position(0);
    }

   //overriden method
    protected void draw(GL10 gl)    
    {
//    	GLU.gluLookAt(gl, (float)(-5*Math.cos(PointCloud.ylook/100)*Math.sin(PointCloud.xlook/100)),
//    			(float)(5*Math.sin(PointCloud.ylook/100)),
//    			(float)(-5*Math.cos(PointCloud.xlook/100)*Math.cos(PointCloud.ylook/100)),
//    			0f, 0f, 0f,
//    			0f, 1.0f, 0.0f);
    	GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f,		0f, 1.0f, 0.0f);
    	gl.glRotatef(-PointCloud.xlook/10, 0.0f, 1.0f, 0.0f);  
        gl.glRotatef(PointCloud.ylook/10, 1.0f, 0.0f, 0.0f);  
    	gl.glColor4f(0f, 0f, 0, 1f);
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
    	gl.glDrawArrays(GL10.GL_TRIANGLES, 0, PointCloud.vertices.length);
    	
    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
