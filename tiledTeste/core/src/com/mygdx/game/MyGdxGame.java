package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	private float escala = 1/16f;
	private OrthogonalTiledMapRenderer renderer;
	private TiledMap map;
	private OrthographicCamera cam;
	private World mundo;
	//teste
	
	private BodyDef circleBodyDef;
	private CircleShape circle;
	private Body circleBody;
	private FixtureDef circleFixture;
	private Box2DDebugRenderer debug;
	private float posX = 2, posY = 20;
	
	@Override
	public void create () {
		mundo = new World(new Vector2(0, -10), true);
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 20, 30);
		map = new TmxMapLoader().load("teste.tmx");
		debug = new Box2DDebugRenderer();
		
		circle = new CircleShape();
		circleBodyDef = new BodyDef();
		circleFixture = new FixtureDef();
		
		MapObjects objects = map.getLayers().get("Obj").getObjects();

		for (MapObject object : objects) {
		    if (object instanceof RectangleMapObject) {
		        Rectangle rect = ((RectangleMapObject) object).getRectangle();
		        BodyDef bodyDef = new BodyDef();
		        bodyDef.type = BodyDef.BodyType.StaticBody;
		        bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / 16, (rect.getY() + rect.getHeight() / 2) / 16);
		        Body body = mundo.createBody(bodyDef);

		        PolygonShape shape = new PolygonShape();
		        shape.setAsBox(rect.getWidth() / 2 / 16, rect.getHeight() / 2 / 16);
		        FixtureDef fixtureDef = new FixtureDef();
		        fixtureDef.shape = shape;
		        body.createFixture(fixtureDef);

		        shape.dispose();
		    }
		}
		
		circle.setRadius(5f * escala);
		circleBodyDef.type = BodyType.DynamicBody;
		circleBodyDef.position.set(posX, posY);
		circleBody = mundo.createBody(circleBodyDef);
		circleFixture.shape = circle;
		circleFixture.density = 0.3f;
		circleFixture.friction = 0.2f;
		circleFixture.restitution = 0.6f;
		circleBody.createFixture(circleFixture);
		
		
		renderer = new OrthogonalTiledMapRenderer(map, escala);
	}

	@Override
	public void render () {
		
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			circleBody.applyForceToCenter(0.0f, 2.0f, true);
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			circleBody.applyForceToCenter(1.0f, 0.0f, true);
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			circleBody.applyForceToCenter(-1.0f, 0.0f, true);
		}
		
		mundo.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
	    float minX = cam.viewportWidth / 2;
	    float minY = cam.viewportHeight / 2;
	    
	    float maxX = 512 - cam.viewportWidth / 2;
	    float maxY = 320 - cam.viewportHeight / 2;
		
	    float cameraX = MathUtils.clamp(circleBody.getPosition().x, minX, maxX);
	    float cameraY = MathUtils.clamp(circleBody.getPosition().y, minY, maxY);
	    cam.position.set(cameraX, cameraY, 0);
	    
		cam.update();
		
		ScreenUtils.clear(.25f,.25f,.25f,1);
		cam.update();
		renderer.setView(cam);
		renderer.render();
		
		debug.render(mundo, cam.combined);

	}
	
	@Override
	public void dispose () {

	}
}
