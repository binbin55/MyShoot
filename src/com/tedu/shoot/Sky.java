package com.tedu.shoot;

import cn.tedu.shoot.FlyingObject;
import cn.tedu.shoot.World;

import java.awt.*;
import java.awt.image.BufferedImage;

import static cn.tedu.shoot.FlyingObject.readImage;

/** ���: �Ƿ����� */
public class Sky extends FlyingObject {
	private static BufferedImage image;
	static {
		image = readImage("background.png");
	}
	
	private int speed; //�ƶ��ٶ�
	private int y1;    //�ڶ���ͼƬ��y����
	/** ���췽�� */
	public Sky(){
		super(World.WIDTH, World.HEIGHT,0,0);
		speed = 1;
		y1 = -World.HEIGHT;
	}
	
	/** ��дstep()�ƶ� */
	public void step() {
		y+=speed;  //y+(����)
		y1+=speed; //y1+(����)
		if(y>=World.HEIGHT) { //��y>=���ڵĸߣ���ζ��y��ȥ��
			y=-World.HEIGHT;  //���޸�yΪ���Ĵ��ڵĸ�(Ų������ȥ)
		}
		if(y1>=World.HEIGHT) { //��y1>=���ڵĸߣ���ζ��y1��ȥ��
			y1=-World.HEIGHT;  //���޸�y1Ϊ���Ĵ��ڵĸ�(Ų������ȥ)
		}
	}
	
	/** ��дgetImage()��ȡͼƬ */
	public BufferedImage getImage() {
		return image; //ֱ�ӷ���image����
	}
	
	/** ��дpaintObject()��ͼƬ */
	public void paintObject(Graphics g) {
		g.drawImage(this.getImage(),this.x,this.y,null);
		g.drawImage(getImage(),x,y1,null);
	}
	
}














