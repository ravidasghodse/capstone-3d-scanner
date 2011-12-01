package Capstone.Scanner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

//import test.opengl.PointCloud;
import android.content.Context;
import android.opengl.GLU;
import android.util.Log;

public class CloudRenderer extends AbstractRenderer
{   
    //A raw native buffer to hold the point coordinates
    private FloatBuffer mFVertexBuffer;
    private IntBuffer quaterBuffer;
    
    private int one = 1;
	private int[] quater = new int[] {
		one,one,-one,  
        -one,one,-one,  
        one,one,one,  
        -one,one,one,  
          
        one,-one,one,  
        -one,-one,one,  
        one,-one,-one,  
        -one,-one,-one,  
          
        one,one,one,  
        -one,one,one,  
        one,-one,one,  
        -one,-one,one,  
          
        one,-one,-one,  
        -one,-one,-one,  
        one,one,-one,  
        -one,one,-one,  
          
        -one,one,one,  
        -one,one,-one,  
        -one,-one,one,  
        -one,-one,-one,  
          
        one, one, -one,  
        one, one, one,  
        one, -one, -one,  
        one, -one, one  
	    };  
    
    public CloudRenderer(Context context) 
    {
    	ByteBuffer byteQuaterBuffer = ByteBuffer.allocateDirect(quater.length * 4);
    	byteQuaterBuffer.order(ByteOrder.nativeOrder());
    	quaterBuffer = byteQuaterBuffer.asIntBuffer();
    	quaterBuffer.put(quater);
    	
        ByteBuffer vbb = ByteBuffer.allocateDirect(PointCloud.vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(PointCloud.vertices);
        mFVertexBuffer.position(0);
    }

   //overriden method
    protected void draw(GL10 gl)    
    {
    	//GLU.gluLookAt(gl, 0, 0, -5,	0f, 0f, 0f,		0f, 1.0f, 0.0f);
    	gl.glRotatef(-PointCloud.xlook/10, 0.0f, 1.0f, 0.0f);  
        gl.glRotatef(PointCloud.ylook/10, 1.0f, 0.0f, 0.0f);  
    	gl.glColor4f(0f, 0f, 0, 1f);
//    	gl.glVertexPointer(3, GL10.GL_FIXED, 0, quaterBuffer);
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
    	    	
    	gl.glDrawArrays(GL10.GL_TRIANGLES, 0, PointCloud.cur);
    	Log.i("fuck", "# "+PointCloud.cur);
    	//for(int i = 0; i < PointCloud.cur; i += 3){
    		//gl.glPushMatrix();
    		//gl.glTranslatef(PointCloud.vertices[i], PointCloud.vertices[i+1], PointCloud.vertices[i+2]);    
//    		for (int j = 0; j < 6; j++) {
//                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, j * 4, 4);
            //}
            //gl.glPopMatrix();
//    	}
    	//gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
