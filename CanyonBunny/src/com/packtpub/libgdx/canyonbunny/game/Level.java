package com.packtpub.libgdx.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.canyonbunny.game.objects.AbstractGameObject;
import com.packtpub.libgdx.canyonbunny.game.objects.BunnyHead;
import com.packtpub.libgdx.canyonbunny.game.objects.Clouds;
import com.packtpub.libgdx.canyonbunny.game.objects.Feather;
import com.packtpub.libgdx.canyonbunny.game.objects.GoldCoin;
import com.packtpub.libgdx.canyonbunny.game.objects.Mountains;
import com.packtpub.libgdx.canyonbunny.game.objects.Rock;
import com.packtpub.libgdx.canyonbunny.game.objects.WaterOverlay;

public class Level {
	
	public static final String TAG = Level.class.getName();
	
	public BunnyHead bunnyHead;
	public Array<GoldCoin> goldCoins;
	public Array<Feather> feathers;
	
	public enum BLOCK_TYPE {
		EMPTY(0,0,0),
		ROCK(0,255,0),
		PLAYER_SPAWNPOINT(255,255,255),
		ITEM_FEATHER(255,0,255),
		ITEM_GOLD_COIN(255,255,0);
	
		private int color;
		private BLOCK_TYPE (int r, int g, int b) {
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}
	
		public boolean sameColor(int color) {
			return this.color == color;
		}
		
		public int getColor() {
			return color;
		}
	}
	
	public Array<Rock> rocks;
	
	public Clouds clouds;
	public Mountains mountains;
	public WaterOverlay waterOverlay;
	
	public Level(String filename) {
		init(filename);
	}
	
	private void init(String filename) {
		bunnyHead = null;
		rocks = new Array<Rock>();
		
		goldCoins = new Array<GoldCoin>();
		feathers = new Array<Feather>();
		
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		int lastPixel = -1;
		for(int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
			for(int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				float baseHeight = pixmap.getHeight() - pixelY;
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				if(BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
					
				} else
				if(BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
					if(lastPixel != currentPixel) {
						obj = new Rock();
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
						rocks.add((Rock) obj);
					} else {
						rocks.get(rocks.size - 1).increaseLength(1);
					}
				} else
				if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
					obj = new BunnyHead();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					bunnyHead = (BunnyHead) obj;
				} else
				if(BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {
					obj = new Feather();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					feathers.add((Feather) obj);
				} else
				if(BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel)) {
					obj = new GoldCoin();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					goldCoins.add((GoldCoin) obj);
				} else {
					int r = 0xff & (currentPixel >>> 24);
					int g = 0xff & (currentPixel >>> 16);
					int b = 0xff & (currentPixel >>> 8);
					int a = 0xff & currentPixel;
					Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelY + ">: r<" + r + "> g<" + g + "> b<" + b + "> a<" + a + ">");
				}
				lastPixel = currentPixel;
			}
		}
		
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0,2);
		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1,-1);
		waterOverlay = new WaterOverlay(pixmap.getWidth());
		waterOverlay.position.set(0, -3.75f);
		
		pixmap.dispose();
		Gdx.app.debug(TAG, "level '" + filename + "' loaded");
	}
	
	public void render(SpriteBatch batch) {
		mountains.render(batch);
		
		for(Rock rock : rocks) {
			rock.render(batch);
		}
		
		for(GoldCoin goldCoin : goldCoins) {
			goldCoin.render(batch);
		}
		
		for(Feather feather: feathers) {
			feather.render(batch);
		}
		
		bunnyHead.render(batch);
		
		waterOverlay.render(batch);
		
		clouds.render(batch);
	}
		
	public void update (float deltaTime) {
		bunnyHead.update(deltaTime);
		for(Rock rock : rocks)
			rock.update(deltaTime);
		for(GoldCoin goldCoin : goldCoins)
			goldCoin.update(deltaTime);
		for(Feather feather : feathers)
			feather.update(deltaTime);
		clouds.update(deltaTime);
	}

}
