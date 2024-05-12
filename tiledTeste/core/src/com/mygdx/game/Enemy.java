package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Enemy {
	private Texture enemyLixoTexture;
	private TextureRegion[][] enemyFrames;
	private Animation<TextureRegion> enemyLixoAnimation;
	private CircleShape enemyLixo;
	private Body enemyLixoBody;
	private BodyDef enemyLixoBodyDef;
	private FixtureDef enemyLixoFixture;
	private float stateTime;
	private SpriteBatch batch;
	private float radius = 0.45f;
	
	public Enemy() {
		
	}

	public Texture getEnemyLixoTexture() {
		return enemyLixoTexture;
	}

	public void setEnemyLixoTexture(Texture enemyLixoTexture) {
		this.enemyLixoTexture = enemyLixoTexture;
	}

	public TextureRegion[][] getEnemyFrames() {
		return enemyFrames;
	}

	public void setEnemyFrames(TextureRegion[][] enemyFrames) {
		this.enemyFrames = enemyFrames;
	}

	public Animation<TextureRegion> getEnemyLixoAnimation() {
		return enemyLixoAnimation;
	}

	public void setEnemyLixoAnimation(Animation<TextureRegion> enemyLixoAnimation) {
		this.enemyLixoAnimation = enemyLixoAnimation;
	}

	public CircleShape getEnemyLixo() {
		return enemyLixo;
	}

	public void setEnemyLixo(CircleShape enemyLixo) {
		this.enemyLixo = enemyLixo;
	}

	public BodyDef getEnemyLixoBodyDef() {
		return enemyLixoBodyDef;
	}

	public void setEnemyLixoBodyDef(BodyDef enemyLixoBodyDef) {
		this.enemyLixoBodyDef = enemyLixoBodyDef;
	}

	public FixtureDef getEnemyLixoFixture() {
		return enemyLixoFixture;
	}

	public void setEnemyLixoFixture(FixtureDef enemyLixoFixture) {
		this.enemyLixoFixture = enemyLixoFixture;
	}
	
	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}
	
	public Body getBody() {
		return this.enemyLixoBody;
	}
	
	public void setBody(Body enemyLixoBody) {
		this.enemyLixoBody = enemyLixoBody;
	}
	
	public float getRadius() {
		return this.radius;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public void criarEnemy(World mundo, float posX, float posY, float density, float friction, float restitution) {
		enemyLixo = new CircleShape();
		enemyLixoBodyDef = new BodyDef();
		enemyLixoFixture = new FixtureDef();
		batch = new SpriteBatch();
		
		enemyLixo.setRadius(radius);
		enemyLixoBodyDef.type = BodyType.KinematicBody;
		enemyLixoBodyDef.position.set(posX, posY);
		enemyLixoBody = mundo.createBody(enemyLixoBodyDef);
		enemyLixoFixture.shape = enemyLixo;
		enemyLixoFixture.density = density;
		enemyLixoFixture.friction = friction;
		enemyLixoFixture.restitution = restitution;
		enemyLixoBody.createFixture(enemyLixoFixture);
	}
	
	public Animation<TextureRegion> criarAnimacaoEnemy(String CaminhoTexture, int sizeWidth, int sizeHeigth, int sizeLine, int sizeColumn, 
			float velAnimation) {
		enemyLixoTexture = new Texture(Gdx.files.internal(CaminhoTexture));
		TextureRegion[][] tmpEnemyLixo = TextureRegion.split(enemyLixoTexture, 38, 34);
		enemyFrames = new TextureRegion[sizeLine][sizeColumn];
		for(int i = 0; i<sizeLine; i++) {
			for(int j = 0; j< sizeColumn; j++) {
				enemyFrames[i][j] = tmpEnemyLixo[i][j];
			}
		}
		
		enemyLixoAnimation = new Animation<TextureRegion>(velAnimation, enemyFrames[0]);
		
		return enemyLixoAnimation;
		
	}
	
	public void movimentacaoEnemy(float posXLimiteEsquerda, float posXLimiteDireita, float speedPosX, float speedNegX) {
		if( enemyLixoBody.getPosition().x <= posXLimiteEsquerda) {
			enemyLixoBody.setLinearVelocity(speedPosX, 0);
		}else if(enemyLixoBody.getPosition().x >= posXLimiteDireita) {
			enemyLixoBody.setLinearVelocity(-speedNegX, 0);
		}
	}
	
	public TextureRegion quadroAtualDaAnimacao(float statetime, Animation<TextureRegion> enemyAnimation) {
		TextureRegion currentEnemyFrame = enemyAnimation.getKeyFrame(statetime, true);
		return currentEnemyFrame;
	}
	
	public TextureRegion Animatio(float statetime, Animation<TextureRegion> left, Animation<TextureRegion> rigth) {
		if (this.enemyLixoBody.getLinearVelocity().x > 0) {
			TextureRegion currentEnemyFrame = left.getKeyFrame(statetime, true);
			return currentEnemyFrame;
		}else {
			TextureRegion currentEnemyFrame = rigth.getKeyFrame(statetime, true);
			return currentEnemyFrame;
			}
	}
	
}
