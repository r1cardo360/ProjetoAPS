package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	private float escala = 1/16f;
	private OrthogonalTiledMapRenderer renderer;
	private TiledMap map;
	private OrthographicCamera cam;
	private World mundo;
	private Texture gariStopRight, gariStopLeft;
	private SpriteBatch batch;
	private boolean isJumping;
	
	private Texture walkrightSheet, walkleftSheet;
	private TextureRegion[][] walkrightFrames, walkleftFrames;
	private Animation<TextureRegion> walkAnimationright, walkAnimationleft;
	private float statetime;
	
	private BodyDef playerBodyDef;
	private PolygonShape player;
	private Body playerBody;
	private FixtureDef playerFixture;
	private Box2DDebugRenderer debug;
	
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
		
		//Aplicando animação
		gariStopRight = new Texture(Gdx.files.internal("Character/APS_Java_Sprite_teste/Gari_stop_right.png"));
		gariStopLeft = new Texture(Gdx.files.internal("Character/APS_Java_Sprite_teste/Gari_stop_left.png"));
		batch = new SpriteBatch();
		
		walkrightSheet = new Texture(Gdx.files.internal("Character/APS_Java_Sprite_teste/Gari_walk_right.png"));
		TextureRegion[][] tmpright = TextureRegion.split(walkrightSheet, 32, 32);
		walkrightFrames = new TextureRegion[2][2];
		for(int i = 0; i<2; i++) {
			for(int j = 0; j<2; j++) {
				walkrightFrames[i][j] = tmpright[i][j];
			}
		}
		walkAnimationright = new Animation<TextureRegion>(0.25f, walkrightFrames[0]);
		statetime = 0f;
		
		walkleftSheet = new Texture(Gdx.files.internal("Character/APS_Java_Sprite_teste/Gari_walk_left.png"));
		TextureRegion[][] tmpleft = TextureRegion.split(walkleftSheet, 32, 32);
		walkleftFrames = new TextureRegion[1][3];
		for(int i = 0; i<1; i++) {
			for (int j = 0; j<3; j++) {
				walkleftFrames[i][j] = tmpleft[i][j];
			}
		}
		walkAnimationleft = new Animation<TextureRegion>(0.25f, walkleftFrames[0]);
		
		
		
		
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
		        Fixture fixture = body.createFixture(fixtureDef);

		        fixture.setUserData("chao");
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

		        Fixture fixture = body.createFixture(fixtureDef);
		        fixture.setUserData("chao");
		        shape.dispose(); 
		    }
		}
		
		mundo.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void endContact(Contact contact) {
			    Fixture fixtureA = contact.getFixtureA();
			    Fixture fixtureB = contact.getFixtureB();

			    if ((fixtureA.getBody() == playerBody && fixtureB.getUserData() != null && fixtureB.getUserData().equals("chao")) ||
			        (fixtureB.getBody() == playerBody && fixtureA.getUserData() != null && fixtureA.getUserData().equals("chao"))) {
			        // O jogador deixou de colidir com o chão
			        isJumping = true;
			    }
				
			}
			
			@Override
			public void beginContact(Contact contact) {
			    Fixture fixtureA = contact.getFixtureA();
			    Fixture fixtureB = contact.getFixtureB();

			    if (fixtureA.getBody() == playerBody && fixtureB.getUserData() != null && fixtureB.getUserData().equals("chao")||
			    	fixtureB.getBody() == playerBody && fixtureA.getUserData() != null && fixtureA.getUserData().equals("chao")){
			        // O jogador colidiu com o chão
			        isJumping = false;
			    }
				
			}
		});

		
		player.setAsBox(0.6f, 0.9f);
		playerBodyDef.type = BodyType.DynamicBody;
		playerBodyDef.position.set(1.16f, 3.915f);
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
		
		statetime += Gdx.graphics.getDeltaTime();
		TextureRegion currentFrame = null;
		
		if(Gdx.input.isKeyPressed(Input.Keys.W) && isJumping == false) {
			playerBody.applyLinearImpulse(new Vector2(0, 7f), playerBody.getWorldCenter(), true);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
		    playerBody.setLinearVelocity(2.0f, playerBody.getLinearVelocity().y);
		    // Altera a textura para a animação de caminhada para a direita
		    currentFrame = walkAnimationright.getKeyFrame(statetime, true);
		} else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
		    playerBody.setLinearVelocity(-2.0f, playerBody.getLinearVelocity().y);
		    // Altera a textura para a animação de caminhada para a esquerda
		    currentFrame = walkAnimationleft.getKeyFrame(statetime, true);
		} else {
		    // Se não estiver pressionando A ou D, usa a textura de gari parado
		    if (playerBody.getLinearVelocity().x >= 0) {
		        currentFrame = new TextureRegion(gariStopRight);
		    } else {
		        currentFrame = new TextureRegion(gariStopLeft);
		    }
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
		batch.draw(currentFrame, playerBody.getPosition().x -0.8f, playerBody.getPosition().y -1, 2, 2);
		batch.end();
		

	}
	
	@Override
	public void dispose () {

	}
}
