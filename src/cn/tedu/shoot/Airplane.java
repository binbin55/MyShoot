package cn.tedu.shoot;
import java.awt.image.BufferedImage;
import java.io.*;

/** С�л�: �Ƿ����Ҳ�ܵ÷� */
public class Airplane extends FlyingObject implements Enemy, Serializable {
	/*
	 * imagesͼƬ�������Ϊ��̬����:
	 * 1)Airplane a1 = new Airplane();
	 *   1.1)����Airplane.class����������images��������
	 *   1.2)ִ�о�̬���ȡͼƬ
	 * 2)Airplane a2 = new Airplane();
	 *   2.1)��
	 * 3)Airplane a3 = new Airplane();
	 *   3.1)��
	 */
	/*
	 * imagesͼƬ�������Ϊʵ������:
	 * 1)Airplane a1 = new Airplane();
	 *   1.1)��ͼƬimages���䵽����
	 *   1.2)�ڹ��췽���ж�ȡͼƬ��images��
	 * 2)Airplane a2 = new Airplane();
	 *   2.1)��ͼƬimages���䵽����
	 *   2.2)�ڹ��췽���ж�ȡͼƬ��images��
	 * 3)Airplane a3 = new Airplane();
	 *   3.1)��ͼƬimages���䵽����
	 *   3.2)�ڹ��췽���ж�ȡͼƬ��images��
	 */
	private static BufferedImage[] images;
	static {
		images = new BufferedImage[5];
		for(int i=0;i<images.length;i++) {
			images[i] = readImage("airplane"+i+".png");
		}
	}
	private int speed; //�ƶ��ٶ�
	/** ���췽�� */
	public Airplane(){
		super(49,36);
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
		/*
		 *                   index=1
		 * 10M img=images[1] index=2                   ����images[1]
		 * 20M img=images[2] index=3                   ����images[2]
		 * 30M img=images[3] index=4                   ����images[3]
		 * 40M img=images[4] index=5(REMOVE) ����images[4]
		 * 50M 
		 */
	}
	
	/** ��дgetScore()�÷� */
	public int getScore() {
		return 1; //���С�л�����ҵ�1��
	}


	public static void main(String[] args) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("s.bat"));
		oos.writeObject(images);
		oos.close();
		System.out.println("ok");
	}

}















