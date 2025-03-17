package org.ltae.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * @Auther WenLong
 * @Date 2025/2/19 11:11
 * @Description
 **/
public class TileShapeToB2d {
    private final static String TAG = TileShapeToB2d.class.getSimpleName();
    public static Shape getShape (MapObject mapObject){
        return getShape(mapObject,1);
    }
    public static Shape getShape (MapObject mapObject,float worldScale){
        Shape shape = null;
        if (mapObject instanceof RectangleMapObject shapeObject){
            Rectangle rectangle = shapeObject.getRectangle();
            // 计算矩形的四个顶点
            float x = rectangle.getX();
            float y = rectangle.getY();
            float width = rectangle.getWidth();
            float height = rectangle.getHeight();
            float[] vertices = new float[] {
                x, y,  // 左下角
                x + width, y,  // 右下角
                x + width, y + height,  // 右上角
                x, y + height   // 左上角
            };
//                if (flipX) {
//                    vertices = flipXVertices(vertices,widthDef);
//                }

            for (int i = 0; i < vertices.length; i++) {
                vertices[i]*=worldScale;
            }
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(vertices);

            shape = polygonShape;
        }else if (mapObject instanceof CircleMapObject shapeObject){
            Circle circle = shapeObject.getCircle();

            CircleShape circleShape = new CircleShape();
            circleShape.setPosition(new Vector2(worldScale*circle.x,worldScale*circle.y));
            circleShape.setRadius(worldScale*circle.radius);

            shape = circleShape;
        }else if (mapObject instanceof EllipseMapObject shapeObject){
            Ellipse ellipse = shapeObject.getEllipse();

            CircleShape circleShape = new CircleShape();
            circleShape.setPosition(new Vector2(worldScale*(ellipse.x+ellipse.width/2),worldScale*(ellipse.y+ellipse.height/2)));
            circleShape.setRadius(ellipse.width<ellipse.height?worldScale*(ellipse.width/2):worldScale*(ellipse.height/2));

            shape = circleShape;
        }else if (mapObject instanceof PolygonMapObject shapeObject){
            Polygon polygon = shapeObject.getPolygon();

            for (int i = 0; i < polygon.getVertices().length; i++) {
                polygon.getVertices()[i]*=worldScale;
            }

            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(polygon.getVertices());
            shape = polygonShape;
        }else if (mapObject instanceof PolylineMapObject shapeObject){
        }else {
            Gdx.app.error(TAG,"MapObject is not a shape object!");
        }
        return shape;
    }
}
