package com.gulei.gldemo;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by gl152 on 2019/1/17.
 */

public class Triangle {
    //attribute：用于顶点着色器当中经常修改的参数
    //vec4:4维向量  包含4个基本类型数据
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "varying  vec4 vColor;" +
                    "attribute vec4 aColor;"
                    + "void main() {"
                    + "  gl_Position = uMVPMatrix * vPosition;"
                    + "  vColor=aColor;"
                    + "}";
    //在片元着色器(fragment shader)最开始的地方加上 precision mediump float; 便设定了默认的精度.这样所有没有显式表明精度的变量 都会按照设定好的默认精度来处理.
    //uniform:顶点片元共享，但不常修改的修饰符
    //gl_FragColor:只读输入，窗口的x,y,z和1/w,片元着色器的填充色
    private final String fragmentShaderCode =
            "precision mediump float;"
                    + "varying vec4 vColor;"
                    + "void main() {"
                    + "  gl_FragColor = vColor;"
                    + "}";

    private FloatBuffer vertexBuffer;//float字节缓冲区
    private FloatBuffer colorBuffer;//float字节缓冲区

    // 数组中每个顶点的坐标数量
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            0.0f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f
    };

    float vertexColor[] = {
            1.0f, 0f, 0f, 1.0f,
            0f, 1.0f, 0f, 1.0f,
            0f, 0f, 1.0f, 1.0f
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {255, 255, 255, 1.0f};//argb
    private final int mProgram;
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mColorHandle;
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;//顶点的个数
    private final int vertexStride = COORDS_PER_VERTEX * (Float.SIZE / Byte.SIZE); // 每个顶点的字节大小

    public Triangle() {
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        /**
         ByteBuffer类中的order(ByteOrder bo) 方法可以设置 ByteBuffer 的字节序。
         其中的ByteOrder是枚举：
         ByteOrder BIG_ENDIAN 代表大字节序的 ByteOrder 。
         ByteOrder LITTLE_ENDIAN 代表小字节序的 ByteOrder 。
         ByteOrder nativeOrder() 返回当前硬件平台的字节序。
         Java的缓冲区数据存储结构为大端字节序(BigEdian)，而OpenGl的数据为小端字节序（LittleEdian）,因为数据存储结构的差异，
         所以，在Android中使用OpenGl的时候必须要进行下转换
         */
        bb.order(ByteOrder.nativeOrder());
        //转成floatbuffer
        vertexBuffer = bb.asFloatBuffer();
        //放入字节数组，put会使下标自动前移
        vertexBuffer.put(triangleCoords);
        //重置下标，读取第一个坐标
        vertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(vertexColor.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(vertexColor);
        colorBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        // 创建空的OpenGL ES程序
        mProgram = GLES20.glCreateProgram();
        // 添加顶点着色器到程序中
        GLES20.glAttachShader(mProgram, vertexShader);
        // 添加片段着色器到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        // 创建OpenGL ES程序可执行文件
        GLES20.glLinkProgram(mProgram);

    }

    //创建着色器
    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void draw(float[] mvpMatrix) {
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);
        // 获取顶点着色器的位置的句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 启用三角形顶点位置的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形坐标数据  参数解释：https://blog.csdn.net/flycatdeng/article/details/82667374
        /**
         *index指定要修改的通用顶点属性的索引。
         size指定每个通用顶点属性的组件数。 必须为1,2,3或4.初始值为4。
         type指定数组中每个组件的数据类型。 接受符号常量GL_BYTE，GL_UNSIGNED_BYTE，GL_SHORT，GL_UNSIGNED_SHORT，GL_FIXED或GL_FLOAT。 初始值为GL_FLOAT。
         normalized指定在访问定点数据值时是应将其标准化（GL_TRUE）还是直接转换为定点值（GL_FALSE）。
         stride指定连续通用顶点属性之间的字节偏移量。 如果stride为0，则通用顶点属性被理解为紧密打包在数组中的。 初始值为0。
         pointer指定指向数组中第一个通用顶点属性的第一个组件的指针。 初始值为0。
         */
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
        // 获取片段着色器的颜色的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, colorBuffer);
        // 得到形状的变换矩阵的句柄
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // 将投影和视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        // 绘制三角形:第一个参数表示绘制方式，第二个参数表示偏移量，第三个参数表示顶点个数。
        /**
         * int GL_POINTS //将传入的顶点坐标作为单独的点绘制
         * int GL_LINES //将传入的坐标作为单独线条绘制，ABCDEFG六个顶点，绘制AB、CD、EF三条线
         * int GL_LINE_STRIP //将传入的顶点作为折线绘制，ABCD四个顶点，绘制AB、BC、CD三条线
         * int GL_LINE_LOOP //将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
         * int GL_TRIANGLES //将传入的顶点作为单独的三角形绘制，ABCDEF绘制ABC,DEF两个三角形
         * int GL_TRIANGLE_FAN //将传入的顶点作为扇面绘制，ABCDEF绘制ABC、ACD、ADE、AEF四个三角形
         * int GL_TRIANGLE_STRIP //将传入的顶点作为三角条带绘制，ABCDEF绘制ABC,BCD,CDE,DEF四个三角形
         参数2：从数组缓存中的哪一位开始绘制，一般都定义为0

         参数3：顶点的数量
         */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
