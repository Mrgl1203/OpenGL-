package com.gulei.gldemo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by gl152 on 2019/1/17.
 */

public class Square {
    private FloatBuffer vertexBuffer;
    /**
     * short 数据类型是 16 位、有符号的以二进制补码表示的整数
     * 最小值是 -32768（-2^15）；
     * 最大值是 32767（2^15 - 1）；
     * Short 数据类型也可以像 byte 那样节省空间。一个short变量是int型变量所占空间的二分之一；
     * 默认值是 0；
     */
    private ShortBuffer drawListBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.5f, 0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, 0.5f, 0.0f}; // top right

    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; // 画正方形顶点的顺序，openGL的基础图形为三角形，这里要按照两个三角形的路线去画

    public Square() {
        ByteBuffer vb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer db = ByteBuffer.allocateDirect(drawOrder.length * 2);
        db.order(ByteOrder.nativeOrder());
        drawListBuffer = db.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

}
