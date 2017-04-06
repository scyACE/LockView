package scy.com.a9lockview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 */

public class LockView extends View {

    private boolean isInit, isSelect, isFinish, movingNoPoint;
    private static final int POINT_SIZE = 5;

    /**
     * 自定义的点
     */
    public static class Point {

        //正常状态
        public static int STATE_NORMAL = 0;
        //按下状态
        public static int STATE_PRESSED = 1;
        //错误状态
        public static int STATE_ERROR = 2;
        public int state;
        public float x, y;

        public Point() {
        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public static boolean with(float pointX, float pointY, float r, float movingX, float movingY) {
            return Math.sqrt((pointX - movingX) * (pointX - movingX) + (pointY - movingY) * (pointY - movingY)) < r;
        }
    }


    private Point[][] points = new Point[3][3];
    private float width, height, offsetsX, offsetsY, bitmapR, movingX, movingY;
    private Bitmap pointNormal, pointPressed, pointError;
    private Paint paint;
    private List<Point> pointList = new ArrayList<>();

    public LockView(Context context) {
        super(context);
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initPoints();
            isInit = true;
        }
        points2Cancas(canvas);
    }

    /**
     * 初始化点
     */
    private void initPoints() {

        paint = new Paint();
        //获取布局宽高
        width = getWidth();
        height = getHeight();

        //横屏
        if (width > height) {
            offsetsX = (width - height) / 2;
            width = height;
            //竖屏
        } else {
            height = width;
            offsetsY = (height - width) / 2;
        }

        //初始化资源
        pointNormal = BitmapFactory.decodeResource(getResources(), R.drawable.lock_point_normal);
        pointPressed = BitmapFactory.decodeResource(getResources(), R.drawable.lock_point_press);
        pointError = BitmapFactory.decodeResource(getResources(), R.drawable.lock_point_error);


        points[0][0] = new Point(offsetsX + width / 4, offsetsY + width / 4);
        points[0][1] = new Point(offsetsX + width / 2, offsetsY + width / 4);
        points[0][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 4);

        points[1][0] = new Point(offsetsX + width / 4, offsetsY + width / 2);
        points[1][1] = new Point(offsetsX + width / 2, offsetsY + width / 2);
        points[1][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 2);

        points[2][0] = new Point(offsetsX + width / 4, offsetsY + width - width / 4);
        points[2][1] = new Point(offsetsX + width / 2, offsetsY + width - width / 4);
        points[2][2] = new Point(offsetsX + width - width / 4, offsetsY + width - width / 4);

        bitmapR = pointNormal.getWidth() / 2;


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        movingX = event.getX();
        movingY = event.getY();
        movingNoPoint = false;
        Point point = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                point = checkSelectPoint();
                if (point != null) {
                    isSelect = true;

                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSelect) {
                    point = checkSelectPoint();
                    if (point == null) {
                        movingNoPoint = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFinish = true;
                isSelect = false;
                break;
        }
        //选中重复检查
        if (!isFinish && isSelect && point != null) {
            //交叉点
            if (crossPoint(point)) {
                movingNoPoint = true;
            } else {
                point.state = Point.STATE_PRESSED;
                pointList.add(point);
            }
        }

        //绘制结束
        if (isFinish) {
            //绘制不成立
            if (pointList.size() == 1) {
                resetPoints();
                //绘制失败
            } else if (pointList.size() < POINT_SIZE && pointList.size() > 2) {
                errorPoints();
            }
        }

        postInvalidate();
        return true;
    }

    private boolean crossPoint(Point point) {
        if (pointList.contains(point)) {
            return true;
        } else {
            return false;
        }
    }

    private void resetPoints() {
        pointList.clear();
    }

    private void errorPoints() {
        for (Point p : pointList) {
            p.state = Point.STATE_ERROR;
        }
    }

    private Point checkSelectPoint() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point point = points[i][j];
                if (Point.with(point.x, point.y, bitmapR, movingX, movingY)) {
                    return point;
                }
            }
        }
        return null;
    }

    private void line2Canvas(Canvas canvas, Point a, Point b) {
        if (a.state == Point.STATE_PRESSED) {
            canvas.drawLine(a.x, a.y, b.x, b.y, paint);
        } else {
            canvas.drawLine(a.x, a.y, b.x, b.y, paint);
        }
    }

    private void points2Cancas(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                Point point = points[i][j];
                if (point.state == Point.STATE_PRESSED) {
                    canvas.drawBitmap(pointPressed, point.x - bitmapR, point.y + bitmapR, paint);
                } else if (point.state == Point.STATE_ERROR) {
                    canvas.drawBitmap(pointError, point.x - bitmapR, point.y + bitmapR, paint);
                } else {
                    canvas.drawBitmap(pointNormal, point.x - bitmapR, point.y + bitmapR, paint);
                }
            }
        }
    }
}
