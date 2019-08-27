package cn.tedu.shoot;
import java.awt.image.BufferedImage;
/** ��л�: �Ƿ����Ҳ�ܵ÷� */
public class BigAirplane extends FlyingObject implements Enemy{
	private static BufferedImage[] images;
	static {
		images = new BufferedImage[5];
		for(int i=0;i<images.length;i++) {
			images[i] = readImage("bigplane"+i+".png");
		}
	}
	
	private int speed; //�ƶ��ٶ�
	/** ���췽�� */
	public BigAirplane(){
		super(69,99);
		speed = 2;
	}
	
	/** ��дstep()�ƶ� */
	public void step() {
		y+=speed; //y+(����)
	}
	
	int index = 1; //�±�
	/** ��дgetImage()��ȡͼƬ */
	public BufferedImage getImage() { //ÿ10������һ��
		if(isLife()) {        //�����ŵ�
			return images[0]; //�򷵻�images[0]
		}else if(isDead()) { //�����˵�
			BufferedImage img = images[index++];
			if(index==images.length) {
				state = REMOVE;
			}
			return img;
		}
		return null; //REMOVE״̬ʱ������null
	}
	
	/** ��дgetScore()�÷� */
	public int getScore() {
		return 3; //�����л�����ҵ�3��
	}
}
















