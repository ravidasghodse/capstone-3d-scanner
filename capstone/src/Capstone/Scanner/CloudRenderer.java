package Capstone.Scanner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
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
    
    /**
     * @param context
     *            光线
     */
    // lightAmbientBuffer
    private FloatBuffer lightAmbientBuffer;
    // 环境光lightAmbient
    private float[] lightAmbient;
    // lightDiffuseBuffer
    private FloatBuffer lightDiffuseBuffer;
    // 漫射光lightDiffuse
    private float[] lightDiffuse;
    // lightPositionBuffer
    private FloatBuffer lightPositionBuffer;
    // 光源的位置
    private float[] lightPosition; 
    
    int one = 0x10000;
    int quater[] = {
    		
            // FRONT
            -one, -one, one, one, -one, one,
            -one, one, one, one, one, one,
            // BACK
            -one, -one, -one, -one, one, -one,
            one, -one, -one, one, one, -one,
            // LEFT
            -one, -one, one, -one, one, one,
            -one, -one, -one, -one, one, -one,
            // RIGHT
            one, -one, -one, one, one, -one,
            one, -one, one, one, one, one,
            // TOP
            -one, one, one, one, one, one,
            -one, one, -one, one, one, -one,
            // BOTTOM
            -one, -one, one, -one, -one, -one,
            one, -one, one, one, -one, -one,

    };
    
    public CloudRenderer(Context context) 
    {
//        PointCloud.cur = PointCloud.vertices.length;
    	Log.i("calculate","calc zAvg");
        float zCor[] = new float[PointCloud.cur/3];
    	float zSum = 0f;
    	for (int i = 2; i < PointCloud.cur; i += 3){
    		zCor[(i-2)/3] = PointCloud.vertices[i];
    	}
    	Arrays.sort(zCor);
    	for (int i = PointCloud.dump; i < PointCloud.cur/3 - PointCloud.dump; i++)
    		zSum+= zCor[i];
    	PointCloud.zAvg = zSum/((float)PointCloud.cur/3 - PointCloud.dump);
    	
    	ByteBuffer byteQuaterBuffer = ByteBuffer.allocateDirect(quater.length * 4);
    	byteQuaterBuffer.order(ByteOrder.nativeOrder());
    	quaterBuffer = byteQuaterBuffer.asIntBuffer();
    	quaterBuffer.put(quater);
    	quaterBuffer.position(0);
    	
        lightAmbient = new float[] { 0.5f, 0.5f, 0.5f, 1.0f };

        lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

//        lightPosition = new float[] { 0.0f, 0.0f, -300.0f, 1.0f };
        lightPosition = new float[] { 1.0f, 1.0f, 1f, 0.0f };
        
    	 // lightAmbientBuffer
        ByteBuffer lightAmbientbyteBuffer = ByteBuffer.allocateDirect(lightAmbient.length * 4 * 6);
		lightAmbientbyteBuffer.order(ByteOrder.nativeOrder());
		lightAmbientBuffer = lightAmbientbyteBuffer.asFloatBuffer();
		lightAmbientBuffer.put(lightAmbient);
		lightAmbientBuffer.position(0);
		
		// lightAmbientBuffer
		ByteBuffer lightPositionbyteBuffer = ByteBuffer
		        .allocateDirect(lightPosition.length * 4 * 6);
		lightPositionbyteBuffer.order(ByteOrder.nativeOrder());
		lightPositionBuffer = lightPositionbyteBuffer.asFloatBuffer();
		lightPositionBuffer.put(lightPosition);
		lightPositionBuffer.position(0);
		
		// lightAmbientBuffer
		ByteBuffer lightDiffusebyteBuffer = ByteBuffer
		        .allocateDirect(lightDiffuse.length * 4 * 6);
		lightDiffusebyteBuffer.order(ByteOrder.nativeOrder());
		lightDiffuseBuffer = lightDiffusebyteBuffer.asFloatBuffer();
		lightDiffuseBuffer.put(lightDiffuse);
		lightDiffuseBuffer.position(0);
    }
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_FASTEST);
        gl.glClearColor(0f, 0f, 0f, 1);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        //设置环境光
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbientBuffer);

        //设置漫射光
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuseBuffer);

        //设置光源的位置
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPositionBuffer);
        
        //启用一号光源
        gl.glEnable(GL10.GL_LIGHT1); 
    }
    
   //overriden method
    protected void draw(GL10 gl)    
    {
    	//启用光源
        gl.glEnable(GL10.GL_LIGHTING); 
        

    	
    	gl.glRotatef(-PointCloud.xlook/10, 0.0f, 1.0f, 0.0f);  
        gl.glRotatef(PointCloud.ylook/10, 1.0f, 0.0f, 0.0f);
        gl.glScalef(0.65f, 0.65f, 0.65f);
    	gl.glColor4f(1f, 1f, 1f, 1f);
    	gl.glVertexPointer(3, GL10.GL_FIXED, 0, quaterBuffer);

    	for(int i = 0; i < PointCloud.cur; i += 3){
    		gl.glPushMatrix();
    		gl.glTranslatef(PointCloud.vertices[i], PointCloud.vertices[i+1], PointCloud.vertices[i+2]-PointCloud.zAvg);    
//    		for (int j = 0; j < 6; j++) {
//    			gl.glNormal3f(nx, ny, nz)
//                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, j * 4, 4);
//            }
    		gl.glNormal3f(0, 0, -1);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    		gl.glNormal3f(0, 0, +1);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
    		gl.glNormal3f(-1, 0, 0);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
    		gl.glNormal3f(1f, 0, 0);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
    		gl.glNormal3f(0f, 1f, 0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
    		gl.glNormal3f(0, -1, 0);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
            gl.glPopMatrix();
    	}
        
    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    	gl.glEnable(GL10.GL_LIGHT1);
    }
}
