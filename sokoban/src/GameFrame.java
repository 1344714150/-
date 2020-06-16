

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameFrame extends JFrame implements ActionListener ,MouseListener,KeyListener{

	private static final long serialVersionUID = 1L;
	private int grade = 0;//�ؿ���
	private int row = 7,column = 7,leftX = 0,leftY = 0;//row,column��ʾ�������ꣻleftX,leftY�������Ͻ�ͼƬλ��
	private int mapRow = 0,mapColumn = 0;//��ͼ��������
	private int width = 0,height = 0;//��Ļ��С
	private boolean acceptKey = true;
	private Image pic[] = null;
	private byte[][] map = null;
	private ArrayList list = new ArrayList();//���ڳ��ز���
	Sound sound;
	
	final byte WALL = 1,BOX = 2,BOXONEND = 3,END = 4,MANDOWN = 5,
			MANLEFT = 6,MANRIGHT = 7,MANUP = 8,GRASS = 9,MANDOWNONEND = 10,MANLEFTONEND = 11,
			MANRIGHTONEND = 12,MANUPONEND = 13;
	
	/***
	 * @author īȾ����
	 * 
	 * ���췽������Ҫ�������£�
	 * 1.�Դ��ڽ����������õĳ�ʼ��
	 * 2.���Ӱ�����������ļ����¼�
	 * 3.ͼƬ�Լ����ֵļ���
	 */
	public GameFrame(){
		super("��������Ϸ�����ְ�");
		setSize(600,600);
		setVisible(true);
		setResizable(false);
		setLocation(300,20);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cont = getContentPane();
		cont.setLayout(null);
		cont.setBackground(Color.black);
		getPic();
		width = this.getWidth();
		height = this.getHeight();
		this.setFocusable(true);
		initMap();
		this.addKeyListener(this);
		this.addMouseListener(this);
		//��������
		sound = new Sound();
		sound.loadSound();
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ���ݵ�ǰ�Ĺؿ��������е�ͼ�ĳ�ʼ���������������¼
	 * ע�ʹ�������ӡ��ǰ�ؿ���ͼ���ݣ�ȡ��ע�Ϳ��ڿ���̨������ͼ�������Ϣ
	 * ��󽫵�ͼ��С������λ�õ���Ϣ�洢�����Ա��
	 */
	public void initMap(){
		map = getMap(grade);
		list.clear();
		/*
	    byte[][] temp = map;
		for(int i=0;i<temp.length;i++){
			for(int j=0;j<temp[0].length;j++){
				System.out.print(temp[i][j]+" ");		
			}
			System.out.println();
		}
		*/	
		getMapSizeAndPosition();
		getManPosition();
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ������ͼ���飬��ȡ���ﵱǰλ�ã��洢�������ڵ�������
	 */
	public void getManPosition(){
		for(int i=0;i<map.length;i++){
			for(int j=0;j<map[0].length;j++){
				if(map[i][j]==MANDOWN||map[i][j]==MANUP||map[i][j]==MANLEFT||map[i][j]==MANRIGHT){
					row = i;
					column = j;
					break;
				}			
			}
		}
	}
		
	/***
	 * @author īȾ����
	 * 
	 * ��ȡ��ͼ����������������д洢
	 * ������Ϸ��������������ұ߾�
	 * ���磺��߾� = �����ڿ��� - ���� * ������ȣ�/2
	 */
	public void getMapSizeAndPosition(){
		mapRow = map.length;
		mapColumn = map[0].length;
		leftX = (width - map[0].length * 30)/2;
		leftY = (height - map.length * 30)/2;
//		System.out.println(leftX);
//		System.out.println(leftY);
//		System.out.println(mapRow);
//		System.out.println(mapColumn);
	}
	

	/***
	 * @author īȾ����
	 * 
	 * ��ʼ��ͼƬ��Դ ,����Դ�ļ��ж�ȡͼƬ�����洢��pic������
	 */
	public void getPic(){
		pic = new Image[14];
		for(int i=0;i<=13;i++){
			pic[i] = Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir")+"/GameDrawable/pic"+i+".png");			
		}
	}
	
	/***
	 * @author īȾ����
	 * 
	 * �жϽ����ƶ�ǰ�����ﵱǰ���ڲݵػ���Ŀ�����
	 */
	public byte grassOrEnd(byte man){
		byte result = GRASS;
		if(man == MANLEFTONEND || man == MANRIGHTONEND || man == MANUPONEND || man == MANDOWNONEND){
			result = END;
		}		
		return result;
	}

	/***
	 * @author īȾ����
	 * 
	 * ���Ʋ����������ж������Ϸ�һ���ǲ���ǽ�������ǽreturn;
	 * 
	 * -�����Ϸ������ӻ�λ��Ŀ����ϵ�������
	 * --true:�����Ϸ��ڶ����ǲݵػ��߹�����
	 * -------true:�����ƶ�ǰ�ĵ�ͼ���ݵ�list�У������ƶ���ز���
	 * --false:��ʱ��������һ���ǹ��������յ㣬�����ƶ���ز���
	 */
	private void moveUp(){
		if(map[row-1][column]==WALL)
			return;
		byte tempBox;
		byte tempMan;
		
		if(map[row-1][column]==BOX||map[row-1][column]==BOXONEND){		//�������һ��������
			if(map[row-2][column]==GRASS||map[row-2][column]==END){     //������ϵڶ����ǲݵػ���Ŀ���
				Map currentMap = new Map(row,column,map);
				list.add(currentMap);//���ڳ��ز���
				//��������������Ŀ������ǣ��ݴ���������Ŀ����ϵ�״̬�����ݴ�����ͨ������״̬
				tempBox = map[row-2][column]==END?BOXONEND:BOX;
				//��������һ�������λ��Ŀ��������ǣ��ݴ�������λ��Ŀ����ϲ������ߵ�״̬�����ݴ������������ߵ�״̬
				tempMan = map[row-1][column]==BOXONEND?MANUPONEND:MANUP;
				//���������ƶ�ǰ��״̬��ȥ�����ƶ��󣬸�λ���ǻָ��ɲݵػ���Ŀ��㡣
				map[row][column] = grassOrEnd(map[row][column]);
				//���ƶ�������������״̬���и���
				map[row-2][column] = tempBox;
				map[row-1][column] = tempMan;
				//������������������һ��
				row--;
			}
		}else{//�������һ���ǹ�������Ŀ���
			Map currentMap = new Map(row,column,map);
			list.add(currentMap);//���ڳ��ز���
			//������һ���ǲݵ����ǣ��ݴ����������ߵ�״̬�����ݴ�����λ��Ŀ����ϲ��������ߵ�״̬��
			tempMan = map[row-1][column]==GRASS?MANUP:MANUPONEND;
			//���ƶ��������֮ǰ�������ڵ�����λ�õ�״̬���и���
			map[row][column] = grassOrEnd(map[row][column]);
			map[row-1][column] = tempMan;
			//������������������һ��
			row--;
		}
		
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ���Ʋ���
	 * 
	 * ���Բο����Ʋ�����ע�ͣ����õ��ж��߼���һ��������׸��
	 */
	private void moveDown(){
	
		if(map[row+1][column]==WALL)
			return ;
		
		byte tempBox;
		byte tempMan;
		
		if(map[row+1][column]==BOX||map[row+1][column]==BOXONEND){
			if(map[row+2][column]==END||map[row+2][column]==GRASS){
				Map currentMap = new Map(row,column,map);
				list.add(currentMap);//���ڳ��ز���
				tempBox = map[row+2][column] == END?BOXONEND:BOX;
				tempMan = map[row+1][column] == BOXONEND?MANDOWNONEND:MANDOWN;
				map[row][column] = grassOrEnd(map[row][column]);
				map[row+2][column] = tempBox;
				map[row+1][column] = tempMan;
				row++;
			}
		}else{
			Map currentMap = new Map(row,column,map);
			list.add(currentMap);//���ڳ��ز���
			tempMan = map[row+1][column]==GRASS?MANDOWN:MANDOWNONEND;
			map[row][column] = grassOrEnd(map[row][column]);
			map[row+1][column] = tempMan;
			row++;
		}
		
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ���Ʋ���
	 * 
	 * ���Բο����Ʋ�����ע�ͣ����õ��ж��߼���һ��������׸��
	 */
	private void moveLeft(){
		
		if(map[row][column-1]==WALL)
			return ;
		
		byte tempBox;
		byte tempMan;
		
		if(map[row][column-1]==BOX||map[row][column-1]==BOXONEND){
			if(map[row][column-2]==END||map[row][column-2]==GRASS){
				Map currentMap = new Map(row,column,map);
				list.add(currentMap);//���ڳ��ز���
				tempBox = map[row][column-2] == END?BOXONEND:BOX;
				tempMan = map[row][column-1] == BOXONEND?MANLEFTONEND:MANLEFT;
				map[row][column] = grassOrEnd(map[row][column]);
				map[row][column-2] = tempBox;
				map[row][column-1] = tempMan;
				column--;
			}
		}else{
			Map currentMap = new Map(row,column,map);
			list.add(currentMap);//���ڳ��ز���
			tempMan = map[row][column-1]==GRASS?MANLEFT:MANLEFTONEND;
			map[row][column] = grassOrEnd(map[row][column]);
			map[row][column-1] = tempMan;
			column--;
		}
		
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ���Ʋ���
	 * 
	 * ���Բο����Ʋ�����ע�ͣ����õ��ж��߼���һ��������׸��
	 */
	private void moveRight(){
		
		if(map[row][column+1]==WALL)
			return ;
		
		byte tempBox;
		byte tempMan;
		
		if(map[row][column+1]==BOX||map[row][column+1]==BOXONEND){
			if(map[row][column+2]==END||map[row][column+2]==GRASS){
				Map currentMap = new Map(row,column,map);
				list.add(currentMap);//���ڳ��ز���
				tempBox = map[row][column+2] == END?BOXONEND:BOX;
				tempMan = map[row][column+1] == BOXONEND?MANRIGHTONEND:MANRIGHT;
				map[row][column] = grassOrEnd(map[row][column]);
				map[row][column+2] = tempBox;
				map[row][column+1] = tempMan;
				column++;
			}
		}else{
			Map currentMap = new Map(row,column,map);
			list.add(currentMap);//���ڳ��ز���
			tempMan = map[row][column+1]==GRASS?MANRIGHT:MANRIGHTONEND;
			map[row][column] = grassOrEnd(map[row][column]);
			map[row][column+1] = tempMan;
			column++;
		}
		
	}
	
	/***
	 * @author īȾ����
	 * 
	 * �жϵ�ǰ�ؿ��Ƿ�ͨ�ء�
	 * 
	 * ԭ����
	 * ������ͼ���飬���Ƿ񻹴���û�б����Ӹ��ǵ�Ŀ���
	 * ������ڣ�˵����ǰ�ؿ���û��ͨ�ء�
	 * */
	public boolean isFinished(){
		for(int i=0;i<mapRow;i++){
			for(int j=0;j<mapColumn;j++){
				if(map[i][j]==END||map[i][j]==MANDOWNONEND||map[i][j]==MANUPONEND||
						map[i][j]==MANLEFTONEND||map[i][j]==MANRIGHTONEND){
					return false;
				}
			}			
		}
		return true;
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/***
	 * 
	 * @author īȾ����
	 * 
	 * ʵ�ּ��������¼��ķ���
	 * 
	 * �������Ҽ�ִ�������ƶ��߼�
	 * A����һ�أ�D����һ�أ�B�����ص���һ��
	 * 
	 * �����ƶ������صĲ���ִ���꣬repaint�ػ���Ϸ����
	 * �жϵ�ǰ�ؿ��Ƿ�ͨ�أ����ͨ�أ������ε����������û����ѯ�ʿ���ٻָ���������
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP){
			moveUp();
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			moveDown();
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			moveLeft();
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			moveRight();
		}
		if(e.getKeyCode() == KeyEvent.VK_A){//��һ��
			acceptKey = true;
			priorGrade();
			return ;
		}
		if(e.getKeyCode() == KeyEvent.VK_D){//��һ��
			acceptKey = true;
			nextGrade();
			return ;
		}
		if(e.getKeyCode() == KeyEvent.VK_B){//����
			undo();
		}
		
		repaint();
		
		if(isFinished()){
			//���ð���
			acceptKey = false;
			if(grade==10){
				JOptionPane.showMessageDialog(this, "��ϲͨ�����һ��");
			}else{
				String msg = "��ϲ��ͨ����"+(grade+1)+"�أ�����\n�Ƿ�Ҫ������һ�أ�";
				int type = JOptionPane.YES_NO_OPTION;
				String title = "����";
				int choice = 0;
				choice = JOptionPane.showConfirmDialog(null, msg,title,type);
				if(choice==1){
					System.exit(0);
				}else if(choice == 0){
					acceptKey = true;
					nextGrade();
				}
			}
		}
		
	}

	/***
	 * 
	 * @author īȾ����
	 * 
	 * �滭������ÿ�ε���repaint�󣬻�ͨ��paint���л������
	 * 
	 * ������ͼ���飬���ݲ�ͬ��ֵ���滭��ͬ��ͼƬ��
	 * ���Ͻ�x���� = ��������߾�+��ǰ����*ש��߳�
	 * ���Ͻ�y���� = �������ϱ߾�+��ǰ����*ש��߳�
	 * ש��ı߳���30���ء�
	 */
	public void paint(Graphics g){
		for(int i=0;i<mapRow;i++){
			for(int j=0;j<mapColumn;j++){
				if(map[i][j]!=0){
					g.drawImage(pic[map[i][j]],leftX+j*30,leftY+i*30,30,30,this);
				}
			}
		}
		
		g.setColor(Color.RED);
		g.setFont(new Font("����_2312",Font.BOLD,30));
		g.drawString("�����ǵ�", 150, 140);
		g.drawString(String.valueOf(grade+1),310,140);
		g.drawString("��", 360, 140);
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ��ȡ���ﵱǰ��������
	 */
	public int getManX(){
		return row;
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ��ȡ���ﵱǰ��������
	 */
	public int getManY(){
		return column;
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ��ȡ��ǰ�ؿ���
	 */
	public int getGrade(){
		return grade;
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ����ڼ��صĲ���grade,��������ؿ����ĵ�ͼ����map
	 */
	public byte[][] getMap(int grade){		
		return MapFactory.getMap(grade);
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ����string,���е�����ʾ
	 */
	public void DisplayToast(String str){
		JOptionPane.showMessageDialog(null, str,"��ʾ",JOptionPane.ERROR_MESSAGE);
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ����һ���������ڻ��塣
	 * ԭ���Ǵ�list�ж�ȡ���һ��Ԫ�أ����Ԫ����һ����ͼ���顣
	 * ͨ��������飬���Իָ���ǰ��Ϸ���ݵ���һ����
	 * 
	 */
	public void undo(){
		if(acceptKey){
			if(list.size()>0){
				Map priorMap = (Map)list.get(list.size()-1);
				map = priorMap.getMap();
				row = priorMap.getManX();
				column = priorMap.getManY();
				repaint();
				list.remove(list.size()-1);
			}else{
				DisplayToast("�����ٳ���");
			}
		}else{
			DisplayToast("�˹�����ɣ����ܳ���");
		}
	}
	
	/***
	 * 
	 * @author īȾ����
	 * 
	 * ���ز������ص���һ��
	 */
	public void priorGrade(){
		grade--;
		acceptKey = true;
		if(grade<0)
			grade = 0;
		initMap();
		clearPaint(this.getGraphics());
		repaint();
	}

	/***
	 * 
	 * @author īȾ����
	 * 
	 * ���ز�����������һ��
	 */
	public void nextGrade(){
		if(grade>=MapFactory.getCount()-1){
			DisplayToast("��ϲ��������йؿ�");
			acceptKey = false;
		}else{
			grade++;
			initMap();
			clearPaint(this.getGraphics());
			repaint();
			acceptKey = true;
		}
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ��ջ�������
	 */
	private void clearPaint(Graphics g) {
		g.clearRect(0, 0, width+leftX, height+leftY);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/***
	 * @author īȾ����
	 * 
	 * ����������¼���������Ҽ��ص���һ��
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			undo();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/***
	 * @author īȾ����
	 * 
	 * ��������������Ϸ
	 */
	public static void main(String[] args){
		new GameFrame();
	}
	
}