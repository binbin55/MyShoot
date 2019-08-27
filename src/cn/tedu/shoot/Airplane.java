package cn.tedu.shoot;
import java.awt.image.BufferedImage;
import java.io.*;

/** 小敌机: 是飞行物，也能得分 */
public class Airplane extends FlyingObject implements Enemy, Serializable {
	/*
	 * images图片属性设计为静态变量:
	 * 1)Airplane a1 = new Airplane();
	 *   1.1)加载Airplane.class到方法区，images到方法区
	 *   1.2)执行静态块读取图片
	 * 2)Airplane a2 = new Airplane();
	 *   2.1)无
	 * 3)Airplane a3 = new Airplane();
	 *   3.1)无
	 */
	/*
	 * images图片属性设计为实例变量:
	 * 1)Airplane a1 = new Airplane();
	 *   1.1)将图片images分配到堆中
	 *   1.2)在构造方法中读取图片到images中
	 * 2)Airplane a2 = new Airplane();
	 *   2.1)将图片images分配到堆中
	 *   2.2)在构造方法中读取图片到images中
	 * 3)Airplane a3 = new Airplane();
	 *   3.1)将图片images分配到堆中
	 *   3.2)在构造方法中读取图片到images中
	 */
	private static BufferedImage[] images;
	static {
		images = new BufferedImage[5];
		for(int i=0;i<images.length;i++) {
			images[i] = readImage("airplane"+i+".png");
		}
	}
	private int speed; //移动速度
	/** 构造方法 */
	public Airplane(){
		super(49,36);
		speed = 2;
	}
	
	/** 重写step()移动 */
	public void step() {
		y+=speed; //y+(向下)
	}
	
	int index = 1; //下标
	/** 重写getImage()获取图片 */
	public BufferedImage getImage() { //每10毫秒走一次
		if(isLife()) {        //若活着的
			return images[0]; //则返回images[0]
		}else if(isDead()) { //若死了的
			BufferedImage img = images[index++];
			if(index==images.length) {
				state = REMOVE;
			}
			return img;
		}
		return null; //REMOVE状态时，返回null
		/*
		 *                   index=1
		 * 10M img=images[1] index=2                   返回images[1]
		 * 20M img=images[2] index=3                   返回images[2]
		 * 30M img=images[3] index=4                   返回images[3]
		 * 40M img=images[4] index=5(REMOVE) 返回images[4]
		 * 50M 
		 */
	}
	
	/** 重写getScore()得分 */
	public int getScore() {
		return 1; //打掉小敌机，玩家得1分
	}


	public static void main(String[] args) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("s.bat"));
		oos.writeObject(images);
		oos.close();
		System.out.println("ok");
	}

}















