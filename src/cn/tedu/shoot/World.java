package cn.tedu.shoot;
import com.tedu.shoot.Sky;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
/** 主窗口 */
public class World extends JPanel {
	public static final int WIDTH = 400;  //窗口的宽
	public static final int HEIGHT = 700; //窗口的高
	
	public static final int START = 0;     //启动状态
	public static final int RUNNING = 1;   //运行状态
	public static final int PAUSE = 2;     //暂停状态
	public static final int GAME_OVER = 3; //游戏结束状态
	private int state = START; //当前状态(默认启动状态)
	
	private static BufferedImage start;    //启动图
	private static BufferedImage pause;    //暂停图
	private static BufferedImage gameover; //游戏结束图
	static { //初始化静态图片
		start = FlyingObject.readImage("start.png");
		pause = FlyingObject.readImage("pause.png");
		gameover = FlyingObject.readImage("gameover.png");
	}
	
	private Sky sky = new Sky();         //天空对象
	private Hero hero = new Hero();      //英雄机对象
	private FlyingObject[] enemies = {}; //敌人(小敌机、大敌机、小蜜蜂)数组
	private Bullet[] bullets = {};       //子弹数组
	
	/** 生成敌人(小敌机、大敌机、小蜜蜂)对象 */
	public FlyingObject nextOne() {
		Random rand = new Random();  //随机数对象
		int type = rand.nextInt(20); //0到19
		if(type<5) { //0到4时，返回小蜜蜂对象
			return new Bee();
		}else if(type<12) { //5到11时，返回小敌机对象
			return new Airplane();
		}else { //12到19时，返回大敌机对象
			return new BigAirplane();
		}
	}
	
	int enterIndex = 0; //敌人入场计数
	/** 敌人(小敌机、大敌机、小蜜蜂)入场 */
	public void enterAction() { //每10毫秒走一次
		enterIndex++; //每10毫秒增1
		if(enterIndex%40==0) { //每400(10*40)毫秒走一次
			FlyingObject obj = nextOne(); //获取敌人对象
			enemies = Arrays.copyOf(enemies,enemies.length+1); //扩容
			enemies[enemies.length-1] = obj; //将敌人对象添加到最后一个元素上
		}
	}
	
	int shootIndex = 0; //射击计数
	/** 子弹入场 */
	public void shootAction() { //每10毫秒走一次
		shootIndex++; //每10毫秒增1
		if(shootIndex%30==0) { //每300(30*10)毫秒走一次
			Bullet[] bs = hero.shoot(); //获取子弹对象
			bullets = Arrays.copyOf(bullets,bullets.length+bs.length); //扩容(bs有几个元素就扩大几个容量)
			System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length); //数组的追加
		}
	}
	
	/** 飞行物移动 */
	public void stepAction() { //每10毫秒走一次
		sky.step(); //天空动
		for(int i=0;i<enemies.length;i++) { //遍历所有敌人
			enemies[i].step(); //敌人动
		}
		for(int i=0;i<bullets.length;i++) { //遍历所有子弹
			bullets[i].step(); //子弹动
		}
	}
	
	/** 删除越界的敌人和子弹----避免内存泄漏 */
	public void outOfBoundsAction() { //每10毫秒走一次
		int index = 0; //1)不越界敌人数组下标  2)不越界敌人个数
		FlyingObject[] enemyLives = new FlyingObject[enemies.length]; //不越界敌人数组
		for(int i=0;i<enemies.length;i++) { //遍历敌人数组
			FlyingObject f = enemies[i]; //获取每一个敌人
			if(!f.outOfBounds() && !f.isRemove()) { //不越界并且非删除状态的
				enemyLives[index] = f; //将不越界敌人装到不越界敌人数组中
				index++; //1)不越界敌人数组下标增一 2)不越界敌人个数增一
			}
		}
		enemies = Arrays.copyOf(enemyLives, index); //将不越界敌人数组复制到enemies中，index为几则enemies的长度为几
		
		index = 0; //1)下标归零  //2)个数归零
		Bullet[] bulletLives = new Bullet[bullets.length];
		for(int i=0;i<bullets.length;i++) {
			Bullet b = bullets[i];
			if(!b.outOfBounds() && !b.isRemove()) {
				bulletLives[index] = b;
				index++;
			}
		}
		bullets = Arrays.copyOf(bulletLives,index);
	}
	
	int score = 0; //玩家的得分
	/** 子弹与敌人的碰撞 */
	public void bulletBangAction() { //每10毫秒走一次
		for(int i=0;i<bullets.length;i++) { //遍历所有子弹
			Bullet b = bullets[i]; //获取每一个子弹
			for(int j=0;j<enemies.length;j++) { //遍历所有敌人
				FlyingObject f = enemies[j]; //获取每一个敌人
				if(b.isLife() && f.isLife() && f.hit(b)) { //若都活着并且撞上了
					b.goDead(); //子弹去死
					f.goDead(); //敌人去死
					
					if(f instanceof Enemy) { //若被撞对象能得分
						Enemy e = (Enemy)f;  //将被撞对象强转为得分接口
						score += e.getScore(); //玩家得分
					}
					if(f instanceof Award) { //若被撞对象为奖励
						Award a = (Award)f;  //将被撞对象强转为奖励接口
						int type = a.getAwardType(); //获取奖励类型
						switch(type) { //根据奖励类型来获取不同的奖励
						case Award.DOUBLE_FIRE:   //若奖励类型为火力值
							hero.addDoubleFire(); //则英雄机增火力
							break;
						case Award.LIFE:    //若奖励类型为命
							hero.addLife(); //则英雄机增命
							break;
						}
					}
				}
			}
		}
	}
	
	/** 英雄机与敌人的碰撞 */
	public void heroBangAction() { //每10毫秒走一次
		for(int i=0;i<enemies.length;i++) { //遍历所有敌人
			FlyingObject f = enemies[i]; //获取每一个敌人
			if(hero.isLife() && f.isLife() && f.hit(hero)) { //撞上了
				f.goDead(); //敌人去死
				hero.subtractLife(); //英雄机减命
				hero.clearDoubleFire(); //英雄机清空火力值
			}
		}
	}
	
	/** 检测游戏结束 */
	public void checkGameOverAction() { //每10毫秒走一次
		if(hero.getLife()<=0) { //游戏结束了
			state = GAME_OVER; //将当前状态修改为游戏结束状态
		}
	}
	
	/** 启动程序的执行 */
	public void action() {
		//创建侦听器对象
		MouseAdapter l = new MouseAdapter() {
			/** 重写鼠标移动事件 */
			public void mouseMoved(MouseEvent e) {
				if(state==RUNNING) { //仅在运行状态下执行
					int x = e.getX(); //获取鼠标的x坐标
					int y = e.getY(); //获取鼠标的y坐标
					hero.moveTo(x, y); //英雄机随着鼠标移动
				}
			}
			/** 重写鼠标点击事件 */
			public void mouseClicked(MouseEvent e) {
				switch(state) { //根据当前状态做不同的处理
				case START:        //启动状态时
					state=RUNNING; //修改为运行状态
					break;
				case GAME_OVER: //游戏结束状态时
					score = 0;  //清理现场
					sky = new Sky();
					hero = new Hero();
					enemies = new FlyingObject[0];
					bullets = new Bullet[0];
					state=START; //修改为启动状态
					break;
				}
			}
			/** 重写鼠标移出事件 */
			public void mouseExited(MouseEvent e) {
				if(state==RUNNING) { //运行状态时
					state=PAUSE;     //修改为暂停状态
				}
			}
			/** 重写鼠标移入事件 */
			public void mouseEntered(MouseEvent e) {
				if(state==PAUSE) { //暂停状态时
					state=RUNNING; //修改为运行状态
				}
			}
		};
		this.addMouseListener(l); //处理鼠标操作事件
		this.addMouseMotionListener(l); //处理鼠标滑动事件
		
		Timer timer = new Timer(); //定时器对象
		int interval = 10; //定时间隔(以毫秒为单位)
		timer.schedule(new TimerTask() {
			public void run() { //定时干的事--每10毫秒走一次
				if(state==RUNNING) { //仅在运行状态下执行
					enterAction();  //敌人(小敌机、大敌机、小蜜蜂)入场
					shootAction();  //子弹入场
					stepAction();   //飞行物移动
					outOfBoundsAction(); //删除越界的敌人和子弹
					bulletBangAction();  //子弹与敌人的碰撞
					heroBangAction();    //英雄机与敌人的碰撞
					checkGameOverAction(); //检测游戏结束
				}
				repaint();      //重画(重新调用paint()方法)
			}
		},interval,interval); //定时计划表
	}
	
	/** 重写paint()画对象  g:画笔 */
	public void paint(Graphics g) {
		sky.paintObject(g);  //画天空
		hero.paintObject(g); //画英雄机
		for(int i=0;i<enemies.length;i++) { //遍历所有敌人
			enemies[i].paintObject(g); //画敌人
		}
		for(int i=0;i<bullets.length;i++) { //遍历所有子弹
			bullets[i].paintObject(g); //画子弹
		}
		
		g.drawString("SCORE: "+score,10,25); //画分
		g.drawString("LIFE: "+hero.getLife(),10,45); //画命
		
		switch(state) { //根据当前状态画不同的图
		case START: //启动状态时画启动图
			g.drawImage(start,0,0,null);
			break;
		case PAUSE: //暂停状态时画暂停图
			g.drawImage(pause,0,0,null);
			break;
		case GAME_OVER: //游戏结束状态时画游戏结束图
			g.drawImage(gameover,0,0,null);
			break;
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		World world = new World();
		frame.add(world);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null); 
		frame.setVisible(true); //1)设置窗口可见  2)尽快调用paint()
		
		world.action(); //启动程序的执行
	}

}




































