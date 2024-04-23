package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Block {
	private BodyDef bodyDef = new BodyDef();
	private Body body;
	private FixtureDef fixtureDef = new FixtureDef();
	private Fixture fixture;
	
	public void createTypeBodyDef(String type) {
		if(type == "dinamico") {
			this.bodyDef.type = BodyType.DynamicBody;
		}else if(type == "cinematico") {
			this.bodyDef.type = BodyType.KinematicBody;
		}else if(type == "estatico") {
			this.bodyDef.type = BodyType.StaticBody;
		}else{
			System.out.println("O tipo " + type + "é incompativel digete dinamico, cinematico ou estatico");
		}
	}
	
	public void setPositionBodydef(int x, int y) {
		this.bodyDef.position.set(x, y);
	}
	
	public void insertBodyInWorld(World world) {
		this.body = world.createBody(this.bodyDef);
	}
	
	public void createFixtureCircle(Shape shape, float density, float friction, float restituition) {
		this.fixtureDef.shape = shape;
		this.fixtureDef.density = density;
		this.fixtureDef.friction = friction;
		this.fixtureDef.restitution = restituition;
		this.fixture = body.createFixture(this.fixtureDef);
	}
	
	public void createImpulso(float forceX, float forceY, boolean VF) {
		this.body.applyForceToCenter(forceX, forceY, VF);
	}
	
}
