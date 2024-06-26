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
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MyGdxGame extends ApplicationAdapter {
	private float escala = 1/16f;
	private OrthogonalTiledMapRenderer renderer;
	private TiledMap map;
	private OrthographicCamera cam;
	private World mundo;
	private Texture gariStopRight, gariStopLeft, enemyKillAnimation;
	private SpriteBatch batch;
	private boolean isJumping, isDead = false, enemyKill = false, enemyAnimatio = true;
	
	private Texture walkrightSheet, walkleftSheet;
	private TextureRegion[][] walkrightFrames, walkleftFrames;
	private Animation<TextureRegion> walkAnimationright, walkAnimationleft, enemyLixoAnimatioLeft, enemyLixoAnimatioRigth;
	private float statetime;
	
	private BodyDef playerBodyDef, lixoBodyDef;
	private Body playerBody, lixoBody;
	private FixtureDef playerFixture, lixoFixture;
	private Box2DDebugRenderer debug;
	private PolygonShape player;
	private CircleShape lixo;
	private float maxSpeed = 3;
	
	private Enemy enemyLixo, enemyLixo2;
	
	private Viewport viewport;
	private Stage stage;
	private TextureRegion currentEnemyFrame, currentEnemyFrame2;
	
	@Override
	public void create () {
		
		viewport = new FitViewport(700, 600);
		stage = new Stage(viewport);
		Gdx.input.setInputProcessor(stage);
		
		mundo = new World(new Vector2(0, -10), true);
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 20, 20);
		map = new TmxMapLoader().load("Fase1.tmx");
		debug = new Box2DDebugRenderer();
		
		player = new PolygonShape();
		//player = new CircleShape();
		playerBodyDef = new BodyDef();
		playerFixture = new FixtureDef();
		
		lixo = new CircleShape();
		lixoBodyDef = new BodyDef();
		lixoFixture = new FixtureDef();
		
		enemyLixo = new Enemy();
		enemyLixo.criarEnemy(mundo, 27, 4.5f, 1, 0.2f, 0.8f);
		enemyLixoAnimatioLeft = enemyLixo.criarAnimacaoEnemy("texture_pack/Trash_bag/Sprite_format/Trash_bag_walking_left.png", 38, 34, 1, 13, 0.05f);
		enemyLixoAnimatioRigth = enemyLixo.criarAnimacaoEnemy("texture_pack/Trash_bag/Sprite_format/Trash_bag_walking_right.png", 38, 34, 1, 13, 0.05f);
		enemyKillAnimation = new Texture(Gdx.files.internal("texture_pack/Trash_bag/Sprite_format/trash_dieng.png"));

		enemyLixo2 = new Enemy();
		enemyLixo2.criarEnemy(mundo, 5.5f, 21.5f, 1, 0.2f, 0.8f);
		
		//Aplicando animação
		gariStopRight = new Texture(Gdx.files.internal("texture_pack/Gari/Sprite_format/Gari_default.png"));
		gariStopLeft = new Texture(Gdx.files.internal("texture_pack/Gari/Sprite_format/Gari_default_left.png"));
		batch = new SpriteBatch();
		
		walkrightSheet = new Texture(Gdx.files.internal("texture_pack/Gari/Sprite_format/Gari_Walking_Right.png"));
		TextureRegion[][] tmpright = TextureRegion.split(walkrightSheet, 32, 32);
		walkrightFrames = new TextureRegion[1][2];
		for(int i = 0; i<1; i++) {
			for(int j = 0; j<2; j++) {
				walkrightFrames[i][j] = tmpright[i][j];
			}
		}
		walkAnimationright = new Animation<TextureRegion>(0.25f, walkrightFrames[0]);
		statetime = 0f;
		
		walkleftSheet = new Texture(Gdx.files.internal("texture_pack/Gari/Sprite_format/Gari_Walking_Left.png"));
		TextureRegion[][] tmpleft = TextureRegion.split(walkleftSheet, 32, 32);
		walkleftFrames = new TextureRegion[1][2];
		for(int i = 0; i<1; i++) {
			for (int j = 0; j<2; j++) {
				walkleftFrames[i][j] = tmpleft[i][j];
			}
		}
		
		walkAnimationleft = new Animation<TextureRegion>(0.25f, walkleftFrames[0]);
		
		
		MapObjects objects = map.getLayers().get("Chao").getObjects();

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
		
		for (MapObject object : map.getLayers().get("Chao").getObjects()) {
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
		
		for (MapObject object : map.getLayers().get("LinhasBTela").getObjects()) {
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
		        fixture.setUserData("parede");
		        shape.dispose(); 
		    }
		}
		
		MapObjects objectsParede = map.getLayers().get("Paredes").getObjects();

		for (MapObject object : objectsParede) {
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

		        fixture.setUserData("parede");
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
			        isJumping = false;
			    }
				
			}
			
			@Override
			public void beginContact(Contact contact) {
			    Fixture fixtureA = contact.getFixtureA();
			    Fixture fixtureB = contact.getFixtureB();

			    if (fixtureA.getBody() == playerBody && fixtureB.getUserData() != null && fixtureB.getUserData().equals("chao")||
			    	fixtureB.getBody() == playerBody && fixtureA.getUserData() != null && fixtureA.getUserData().equals("chao")){
			        // O jogador colidiu com o chão
			        isJumping = true;
			    }
			    
			    //contact enemey
			    
			    if(!enemyKill) {
				    if ((fixtureA.getBody() == playerBody && fixtureB.getBody() == enemyLixo.getBody())||
				    	(fixtureB.getBody() == playerBody && fixtureA.getBody() == enemyLixo.getBody())){
				    	if (playerBody.getPosition().y > enemyLixo.getBody().getPosition().y + enemyLixo.getRadius()) {
				    		enemyKill = true;
				    	}else {
				    	}
				    }
			    }
			}
		});

		
		player.setAsBox(0.6f, 0.9f);
		//player.setRadius(0.9f);
		playerBodyDef.type = BodyType.DynamicBody;
		playerBodyDef.position.set(1.16f, 3.915f);
		playerBody = mundo.createBody(playerBodyDef);
		playerFixture.shape = player;
		playerFixture.density = 1f;
		playerFixture.friction = 0.2f;
		playerFixture.restitution = 0.0f;
		playerBody.createFixture(playerFixture);
		playerBody.setFixedRotation(true);
		
		lixo.setRadius(0.40f);
		lixoBodyDef.type = BodyType.KinematicBody;
		lixoBodyDef.position.set(45.5f, 6.5f);
		lixoBody = mundo.createBody(lixoBodyDef);
		lixoFixture.shape = lixo;
		lixoFixture.density = 0;
		lixoFixture.friction = 0;
		lixoFixture.restitution = 3;
		lixoBody.createFixture(lixoFixture);
		
		lixo.setRadius(0.40f);
		lixoBodyDef.type = BodyType.KinematicBody;
		lixoBodyDef.position.set(26.5f, 11.5f);
		lixoBody = mundo.createBody(lixoBodyDef);
		lixoFixture.shape = lixo;
		lixoFixture.density = 0;
		lixoFixture.friction = 0;
		lixoFixture.restitution = 1.1f;
		lixoBody.createFixture(lixoFixture);
		
		
		renderer = new OrthogonalTiledMapRenderer(map, escala);
	
	}
	
	@Override
	public void resize(int width, int height){
		viewport.update(width, height, true);
	}

	@Override
	public void render () {
		
		
		statetime += Gdx.graphics.getDeltaTime();
		TextureRegion currentFrame = null;
		
		Vector2 velocity = playerBody.getLinearVelocity();
		
		if(Math.abs(velocity.x) > maxSpeed) {
			velocity.x = Math.signum(velocity.x) * maxSpeed;
			playerBody.setLinearVelocity(velocity.x, velocity.y);
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.W) && isJumping == true) {
			playerBody.applyLinearImpulse(new Vector2(0, 15f), playerBody.getWorldCenter(), true);
			isJumping = false;
		}
		
			
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			playerBody.applyForceToCenter(10f, 0, true);
			// Altera a textura para a animação de caminhada para a direita
			currentFrame = walkAnimationright.getKeyFrame(statetime, true);
		} else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			playerBody.applyForceToCenter(-10f, 0, true);
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
		
		//enemyLixo
		
		enemyLixo.movimentacaoEnemy(27, 40, 1.5f, 1.5f);
		currentEnemyFrame = enemyLixo.Animatio(statetime, enemyLixoAnimatioRigth, enemyLixoAnimatioLeft);
			
		enemyLixo2.movimentacaoEnemy(5.5f, 8, 1.7f, 1.7f);
		currentEnemyFrame2 = enemyLixo2.Animatio(statetime, enemyLixoAnimatioRigth, enemyLixoAnimatioLeft);
		
		
		mundo.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
	    if (enemyKill) {
	        // Destrói o inimigo fora do ciclo de atualização principal do mundo
	        enemyLixo.enemyKill(mundo);
	        enemyKill = false; // Reinicia a flag
	        enemyAnimatio = false;
	    }
		
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
		batch.draw(currentFrame, playerBody.getPosition().x -0.97f, playerBody.getPosition().y -1, 2, 2);
		if(enemyAnimatio) {
			batch.draw(currentEnemyFrame, enemyLixo.getBody().getPosition().x -0.46f, enemyLixo.getBody().getPosition().y -0.5f, 1, 1);
		}
		batch.draw(currentEnemyFrame2, enemyLixo2.getBody().getPosition().x -0.46f, enemyLixo2.getBody().getPosition().y -0.5f, 1, 1);
		batch.end();
		
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

	}
	
	@Override
	public void dispose () {
		stage.dispose();
	}
}
