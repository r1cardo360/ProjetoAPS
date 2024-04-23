package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MyGameAPS extends ApplicationAdapter {
	
	private OrthographicCamera cam;
	private World mundo;
	private Box2DDebugRenderer debugRenderer;
	private CircleShape circle;
	private Block circle1;
	private Texture textura;
	private PolygonSprite spriteGari;
	private SpriteBatch batch;
	
	private TiledMap map;
	private OrthogonalTiledMapRenderer Render;
	private TmxMapLoader loader;
	
	float escalacam = 1/16f;
	
	@Override
	public void create () {
		mundo = new World(new Vector2(0, -10), true);
		debugRenderer = new Box2DDebugRenderer();
		cam = new OrthographicCamera();
		circle1 = new Block();
		circle = new CircleShape();
		batch = new SpriteBatch();
		loader = new TmxMapLoader();
		map = new TiledMap();
		map = loader.load("teste.tmx");
		
		loadStaticBlock();
		
		
		Render = new OrthogonalTiledMapRenderer(map, 1/16f);
		cam.setToOrtho(false, 600/16, 400/16);
		
		batch.setProjectionMatrix(cam.combined);
		
		new PolygonSpriteBatch();
		textura = new Texture(Gdx.files.internal("badlogic.jpg"));
		
		circle.setRadius(0.5f);
		
		circle1.createTypeBodyDef("dinamico");
		circle1.setPositionBodydef(8, 15);
		circle1.insertBodyInWorld(mundo);

		circle1.createFixtureCircle(circle, 0.3f, 0.5f, 1.0f);

	}
	
	private void loadStaticBlock() {
		MapLayer blockLayer = map.getLayers().get("Estaticos");
		
		for(MapObject object: blockLayer.getObjects()) {
			if(object instanceof RectangleMapObject) {
				RectangleMapObject rectangleObject = (RectangleMapObject) object;
				Rectangle rectangle = rectangleObject.getRectangle();
				
				BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((rectangle.x + rectangle.width / 2), (rectangle.y + rectangle.height / 2));
                
                Body body = mundo.createBody(bodyDef);
                
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(rectangle.width / 2, rectangle.height / 2);
                
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                
                body.createFixture(fixtureDef);
                shape.dispose();
			}
		}
	}

	@Override
	public void render () {
		
		Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mundo.step(1/60f, 6, 2);
		
		cam.update();
		
		debugRenderer.render(mundo, cam.combined);
		
		Render.setView(cam);
		Render.render();
		
	}
	
	@Override
	public void dispose () {
		map.dispose();
		mundo.dispose();
	}
}
