package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
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
import com.badlogic.gdx.physics.box2d.ChainShape;
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
	private Texture teste;
	private SpriteBatch batch;
	private boolean isJumping;
	
	
	private BodyDef playerBodyDef;
	private PolygonShape player;
	private Body playerBody;
	private FixtureDef playerFixture;
	private Box2DDebugRenderer debug;
	private float posX = 2, posY = 20;
	
	@Override
	public void create () {
		mundo = new World(new Vector2(0, -10), true);
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 20, 20);
		map = new TmxMapLoader().load("Fase1.tmx");
		debug = new Box2DDebugRenderer();
		
		player = new PolygonShape();
		playerBodyDef = new BodyDef();
		playerFixture = new FixtureDef();
		
		teste = new Texture(Gdx.files.internal("Gari_default.png"));
		batch = new SpriteBatch();
		
		MapObjects objects = map.getLayers().get("Estaticosq").getObjects();

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
		
		for (MapObject object : map.getLayers().get("Estaticosq").getObjects()) {
		    if (object instanceof PolygonMapObject) {
		        PolygonMapObject polygonObject = (PolygonMapObject) object;
		        float[] vertices = polygonObject.getPolygon().getTransformedVertices();
		        Vector2[] worldVertices = new Vector2[vertices.length / 2];

		        for (int i = 0; i < worldVertices.length; i++) {
		            worldVertices[i] = new Vector2(vertices[i * 2] * escala, vertices[i * 2 + 1] * escala);
		        }

		        BodyDef bodyDef = new BodyDef();
		        bodyDef.type = BodyDef.BodyType.StaticBody;
		        Body body = mundo.createBody(bodyDef);

		        ChainShape shape = new ChainShape();
		        shape.createLoop(worldVertices);

		        FixtureDef fixtureDef = new FixtureDef();
		        fixtureDef.shape = shape;

		        body.createFixture(fixtureDef);
		        shape.dispose(); 
		    }
		}

		
		player.setAsBox(0.6f, 0.9f);
		playerBodyDef.type = BodyType.DynamicBody;
		playerBodyDef.position.set(posX, posY);
		playerBody = mundo.createBody(playerBodyDef);
		playerFixture.shape = player;
		playerFixture.density = 1f;
		playerFixture.friction = 0.2f;
		playerFixture.restitution = 0.0f;
		playerBody.createFixture(playerFixture);
		playerBody.setFixedRotation(true);
		
		
		renderer = new OrthogonalTiledMapRenderer(map, escala);
	}

	@Override
	public void render () {
		
		if(Gdx.input.isKeyPressed(Input.Keys.W) && isJumping == false) {
			playerBody.applyLinearImpulse(new Vector2(0, 4f), playerBody.getWorldCenter(), true);
			isJumping = true;
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			playerBody.applyForceToCenter(5.0f, 0.0f, true);
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			playerBody.applyForceToCenter(-5.0f, 0.0f, true);
		}
		
		
		mundo.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
	    float minX = cam.viewportWidth / 2;
	    float minY = cam.viewportHeight / 2;
	    
	    float maxX = 57 - cam.viewportWidth / 2;
	    float maxY = 25 - cam.viewportHeight / 2;
		
	    float cameraX = MathUtils.clamp(playerBody.getPosition().x, minX, maxX);
	    float cameraY = MathUtils.clamp(playerBody.getPosition().y, minY, maxY);
	    cam.position.set(cameraX, cameraY, 0);
	    
		cam.update();
		
		ScreenUtils.clear(.25f,.25f,.25f,1);
		cam.update();
		renderer.setView(cam);
		renderer.render();
		
		debug.render(mundo, cam.combined);
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(teste, playerBody.getPosition().x -0.8f, playerBody.getPosition().y -1, 2, 2);
		batch.end();

	}
	
	@Override
	public void dispose () {

	}
}
