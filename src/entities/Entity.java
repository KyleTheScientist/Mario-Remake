package entities;

import java.awt.Image;

public class Entity {
	
	private static Entity[] ents = new Entity[0];
	protected double x, y, width, height;
	protected boolean isPlayer = false, exists = true, isVisible = true;
	protected double[] vel = new double[2];
	protected Image image;
	
	public Entity() {
		Entity[] newEnts = new Entity[ents.length + 1];
		for (int i = 0; i < ents.length; i++) {
			newEnts[i] = ents[i];
		}
		newEnts[ents.length] = this;
		ents = newEnts;
	}
	
	public static void reset(){
		ents = new Entity[0];
		Tile.reset();
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public void setVisible(boolean isVisible){
		this.isVisible = isVisible;
	}
	
	public void move(double x, double y){
		this.x += x;
		this.y += y;
	}

	public static Entity[] getEnts() {
		return ents;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	public boolean isPlayer(){
		return isPlayer;
	}
	
	public int getIntX() {
		return (int)x;
	}
	
	public int getIntY() {
		return (int)y;
	}
	
	public int getIntWidth() {
		return (int)width;
	}
	
	public int getIntHeight() {
		return (int)height;
	}
	
	//Player Specific
	public double nextPosX() {
		return x + vel[0];
	}

	public double nextPosY() {
		return y + vel[1];
	}
	
	public Image getImage(){
		return image;
	}
	
	public void setImage(Image image){
		this.image = image;
	}
	
	public boolean exists(){
		return exists;
	}
	
	public void setExists(boolean exists){
		if(!exists){
			this.setVisible(false);
		}
		this.exists = exists;
	}
	
	public void destroy(){
		setExists(false);
	}
	
}
