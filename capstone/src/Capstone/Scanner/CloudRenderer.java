package test.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.umji.scanner.ImageData;

import android.content.Context;

//filename: SimpleTriangleRenderer.java
public class CloudRenderer extends AbstractRenderer
{   
    //A raw native buffer to hold the point coordinates
    private FloatBuffer mFVertexBuffer;
    
    //A raw native buffer to hold indices
    //allowing a reuse of points.
    private ShortBuffer mIndexBuffer;
    
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
       gl.glColor4f(1.0f, 0, 0, 0.5f);
       gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
		gl.glDrawArrays(GL10.GL_POINTS, 0, PointCloud.vertices.length);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
