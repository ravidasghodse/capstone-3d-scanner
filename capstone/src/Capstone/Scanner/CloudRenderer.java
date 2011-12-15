package Capstone.Scanner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

public class CloudRenderer extends AbstractRenderer {
	private static final int STANDBY = 0;
	private static final int SCANNING = 1;
	private static final int MENDING = 2;
	// A raw native buffer to hold the point coordinates
	private IntBuffer quaterBuffer;
	private FloatBuffer lineBuffer;
	private FloatBuffer triangleBuffer;

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
			-one, -one, one, one, -one, one, -one, one, one, one, one, one,
			// BACK
			-one, -one, -one, -one, one, -one, one, -one, -one, one, one, -one,
			// LEFT
			-one, -one, one, -one, one, one, -one, -one, -one, -one, one, -one,
			// RIGHT
			one, -one, -one, one, one, -one, one, -one, one, one, one, one,
			// TOP
			-one, one, one, one, one, one, -one, one, -one, one, one, -one,
			// BOTTOM
			-one, -one, one, -one, -one, -one, one, -one, one, one, -one, -one,

	};

	public CloudRenderer(Context context) {

		// Calculate average Z coordinate
		ArrayList<Float> zAL = new ArrayList<Float>();
		float zSum = 0f;
		for (int i = 2; i < PointCloud.curLine; i += 3) {
			for (int j = 0; j < PointCloud.POINTPERLINE; j++) {
				if (PointCloud.vertices[i][j * 3 + 2] != -1f) {
					zAL.add((Float) PointCloud.vertices[i][j * 3 + 2]);
				}
			}
		}
		Float[] zArr = new Float[zAL.size()];
		zAL.toArray(zArr);
		Arrays.sort(zArr);
		for (int i = PointCloud.dump; i < zAL.size() - PointCloud.dump; i++)
			zSum += zArr[i];
		PointCloud.zAvg = zSum / (zAL.size() - PointCloud.dump * 2);

		Log.i("calculate", "calc zAvg = " + PointCloud.zAvg);

		// mending the missing points
		float stepX, stepY, stepZ;
		int scanMode = STANDBY;
		int mendCount, mendBeginPoint = 0;
		for (int i = 0; i < PointCloud.curLine; i++) {
			scanMode = STANDBY;
			mendCount = 0;
			for (int j = 0; j < PointCloud.POINTPERLINE - 1; j++) {
				if (PointCloud.vertices[i][j * 3 + 2] == -1) {
					if (scanMode == SCANNING) {
						scanMode = MENDING;
						mendCount = 0;
						mendBeginPoint = j;
					} else if (scanMode == MENDING) {
						mendCount += 1;
						if (mendCount > PointCloud.MAXMEND)
							scanMode = STANDBY;
					}
				} else { // Point exists
					if (scanMode == STANDBY)
						scanMode = SCANNING; // Enter scanning mode
					else if (scanMode == MENDING) {
						// Calculate the stepping
						stepX = (PointCloud.vertices[i][j * 3] - PointCloud.vertices[i][(mendBeginPoint - 1) * 3])
								/ (j - mendBeginPoint + 1);
						stepY = (PointCloud.vertices[i][j * 3 + 1] - PointCloud.vertices[i][(mendBeginPoint - 1) * 3 + 1])
								/ (j - mendBeginPoint + 1);
						stepZ = (PointCloud.vertices[i][j * 3 + 2] - PointCloud.vertices[i][(mendBeginPoint - 1) * 3 + 2])
								/ (j - mendBeginPoint + 1);

						for (int k = mendBeginPoint; k < j; k++) {
							PointCloud.vertices[i][k * 3] = (k + 1 - mendBeginPoint)
									* stepX
									+ PointCloud.vertices[i][(mendBeginPoint - 1) * 3];
							PointCloud.vertices[i][k * 3 + 1] = (k + 1 - mendBeginPoint)
									* stepY
									+ PointCloud.vertices[i][(mendBeginPoint - 1) * 3 + 1];
							PointCloud.vertices[i][k * 3 + 2] = (k + 1 - mendBeginPoint)
									* stepZ
									+ PointCloud.vertices[i][(mendBeginPoint - 1) * 3 + 2];
							scanMode = SCANNING; // Return scanning mode
						}
					}
				}
			}
		}

		// Cube Buffer
		ByteBuffer byteQuaterBuffer = ByteBuffer
				.allocateDirect(quater.length * 4);
		byteQuaterBuffer.order(ByteOrder.nativeOrder());
		quaterBuffer = byteQuaterBuffer.asIntBuffer();
		quaterBuffer.put(quater);
		quaterBuffer.position(0);

		// Lighting Params
		lightAmbient = new float[] { 0.5f, 0.5f, 0.5f, 1.0f };

		lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

		// lightPosition = new float[] { 0.0f, 0.0f, -300.0f, 1.0f };
		lightPosition = new float[] { 0f, 1f, 1f, 0.0f };

		// lightAmbientBuffer
		ByteBuffer lightAmbientbyteBuffer = ByteBuffer
				.allocateDirect(lightAmbient.length * 4 * 6);
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
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glClearColor(0f, 0f, 0f, 1);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// 设置环境光
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbientBuffer);

		// 设置漫射光
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuseBuffer);

		// 设置光源的位置
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPositionBuffer);

		// 启用一号光源
		gl.glEnable(GL10.GL_LIGHT1);
	}

	private void drawCloud(GL10 gl) {

		// enable light
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glRotatef(-PointCloud.xlook / 10, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(PointCloud.ylook / 10, 1.0f, 0.0f, 0.0f);
		gl.glScalef(0.65f, 0.65f, 0.65f);
		gl.glColor4f(1f, 1f, 1f, 1f);

		gl.glVertexPointer(3, GL10.GL_FIXED, 0, quaterBuffer);

		// gl.glNormal3f(0, 0, -1);
		for (int i = 0; i < PointCloud.curLine; i++) {
			for (int j = 0; j < PointCloud.POINTPERLINE; j++) {
				gl.glPushMatrix();
				gl.glTranslatef(PointCloud.vertices[i][j * 3],
						PointCloud.vertices[i][j * 3 + 1],
						PointCloud.vertices[i][j * 3 + 2] - PointCloud.zAvg);
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
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnable(GL10.GL_LIGHT1);
	}

	private void drawLine(GL10 gl) {
		gl.glDisable(GL10.GL_LIGHT0);
		
		gl.glRotatef(-PointCloud.xlook / 10, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(PointCloud.ylook / 10, 1.0f, 0.0f, 0.0f);
		gl.glColor4f(1f, 1f, 1f, 1f);

		// Initialize triangle buffer
		ByteBuffer byteLineBuffer = ByteBuffer.allocateDirect(2 * 3 * 4);
		byteLineBuffer.order(ByteOrder.nativeOrder());
		lineBuffer = byteLineBuffer.asFloatBuffer();
		float linePoint[] = new float[2 * 3];
		gl.glTranslatef(0, 0, -PointCloud.zAvg);
		for (int i = 0; i < PointCloud.curLine; i++) {
			for (int j = 0; j < PointCloud.POINTPERLINE - 1; j++) {
				if (PointCloud.vertices[i][j * 3] != -1f
						&& PointCloud.vertices[i][(j + 1) * 3] != -1f
						&& PointCloud.calcDist(i, j, i, j + 1) < 20) {
					for (int k = 0; k < 2; k++) {
						linePoint[k * 3] = PointCloud.vertices[i][(j + k) * 3];
						linePoint[k * 3 + 1] = PointCloud.vertices[i][(j + k) * 3 + 1];
						linePoint[k * 3 + 2] = PointCloud.vertices[i][(j + k) * 3 + 2];
					}
					lineBuffer.put(linePoint);
					lineBuffer.position(0);

					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineBuffer);

					gl.glDrawArrays(GL10.GL_LINES, 0, 2);
				}
			}
		}

		for (int j = 0; j < PointCloud.POINTPERLINE; j++) {
			for (int i = 0; i < PointCloud.curLine - 1; i++) {
				if (PointCloud.vertices[i][j * 3] != -1f
						&& PointCloud.vertices[i + 1][j * 3] != -1f
						&& PointCloud.calcDist(i, j, i + 1, j) < PointCloud.legalDist) {
					for (int k = 0; k < 2; k++) {
						linePoint[k * 3] = PointCloud.vertices[i + k][j * 3];
						linePoint[k * 3 + 1] = PointCloud.vertices[i + k][j * 3 + 1];
						linePoint[k * 3 + 2] = PointCloud.vertices[i + k][j * 3 + 2];
					}
					lineBuffer.put(linePoint);
					lineBuffer.position(0);

					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineBuffer);

					gl.glDrawArrays(GL10.GL_LINES, 0, 2);
				}
			}
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnable(GL10.GL_LIGHT1);
	}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
	private void drawSurface(GL10 gl) {
		// enable light
		gl.glEnable(GL10.GL_LIGHTING);

		gl.glRotatef(-PointCloud.xlook / 10, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(PointCloud.ylook / 10, 1.0f, 0.0f, 0.0f);
		gl.glColor4f(1f, 1f, 1f, 1f);

		float triangleVertices[];

		for (int i = 0; i < PointCloud.curLine - 1; i++) {
			for (int j = 0; j < PointCloud.POINTPERLINE - 1; j++) {
				// if four points all exist & their distance is legal
				if (PointCloud.vertices[i][j * 3] != -1f
						&& PointCloud.vertices[i][(j + 1) * 3] != -1.0f
						&& PointCloud.vertices[i + 1][j * 3] != -1f
						&& PointCloud.vertices[i + 1][(j + 1) * 3] != -1f
						&& PointCloud.calcDist(i, j, i, j + 1) < PointCloud.legalDist
						&& PointCloud.calcDist(i, j, i + 1, j) < PointCloud.legalDist
						&& PointCloud.calcDist(i + 1, j, i + 1, j + 1) < PointCloud.legalDist
						&& PointCloud.calcDist(i, j + 1, i + 1, j + 1) < PointCloud.legalDist) {

					triangleVertices = new float[] {
							// top left
							PointCloud.vertices[i][j * 3],
							PointCloud.vertices[i][j * 3 + 1],
							PointCloud.vertices[i][j * 3 + 2],
							// bottom left
							PointCloud.vertices[i][(j + 1) * 3],
							PointCloud.vertices[i][(j + 1) * 3 + 1],
							PointCloud.vertices[i][(j + 1) * 3 + 2],
							// top right
							PointCloud.vertices[i + 1][j * 3],
							PointCloud.vertices[i + 1][j * 3 + 1],
							PointCloud.vertices[i + 1][j * 3 + 2],
							// bottom left
							PointCloud.vertices[i + 1][(j + 1) * 3],
							PointCloud.vertices[i + 1][(j + 1) * 3 + 1],
							PointCloud.vertices[i + 1][(j + 1) * 3 + 2] };

					// Log.i("reconstruction", "i :"+i + ' '+
					// PointCloud.vertices[i][j*3] + " j: "+j);

					// Initialize triangle buffer
					ByteBuffer byteTriangleBuffer = ByteBuffer
							.allocateDirect(triangleVertices.length * 4);
					byteTriangleBuffer.order(ByteOrder.nativeOrder());
					triangleBuffer = byteTriangleBuffer.asFloatBuffer();
					triangleBuffer.put(triangleVertices);
					triangleBuffer.position(0);

					float[] normal = new float[3];
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleBuffer);

					PointCloud.calcNorm(normal, i, j, i, j + 1, i + 1, j);
					gl.glNormal3f(normal[0], normal[1], normal[2]);
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);

					PointCloud.calcNorm(normal, i, j + 1, i + 1, j, i + 1,
							j + 1);
					gl.glNormal3f(normal[0], normal[1], normal[2]);
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 1, 3);

					// Log.i("draw", "draw!");
				}
			}
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnable(GL10.GL_LIGHT1);
	}

	// overriden method
	protected void draw(GL10 gl) {
		int renderMode = OpenGLActivity.renderMode;
		
		Log.d("mode",Integer.toString(renderMode));
		
		switch (renderMode) {
		case OpenGLActivity.MODE_CLOUD:
			drawCloud(gl);
			break;
		case OpenGLActivity.MODE_LINE:
			drawLine(gl);
			break;
		case OpenGLActivity.MODE_SURFACE:
			drawSurface(gl);
			break;
		}
	}
}
